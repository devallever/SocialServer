package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.RankDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TRank;
import com.social.pojo.TUser;

/**
 * Servlet implementation class ChatRankListServlet
 */
@WebServlet("/ChatRankListServlet")
public class ChatRankListServlet extends HttpServlet {
	
	private PrintWriter pw;
	private Root root;
	
	private String str_page;
	private int page;
	private int startCount;
	private int endCount;
	
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		pw = response.getWriter();
		root = new Root();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
		HttpSession session = request.getSession();
//		if(session == null || session.getAttribute("id") == null){
//			root.message = "未登录";
//			root.success = false;
//			pw.print(gson.toJson(root));
//			pw.close();
//		}
		
		str_page = request.getParameter("page");
		if(str_page == null) str_page = "1";
		page = Integer.valueOf(str_page);
		endCount = page * 1 * 10;
		startCount = endCount - 9;
		
		RankDAO dao = new RankDAO();
		List<TRank> list_trank;
		List<UserRank> list_userrank = new ArrayList<ChatRankListServlet.UserRank>();
		List<String> list_user_id = new ArrayList<String>();
		UserRank userrank;
		List<TRank> list_query_trank;
		try {
			list_trank = dao.getByQueryWithGroupBy("group by user_id", 0, 0);
			for(TRank trank:list_trank){
				TUser tuser = trank.getUser();
				list_user_id.add(tuser.getId()+"");
				System.out.println(trank.getUser().getId()+"");
				
				userrank = new UserRank();
				userrank.username = tuser.getUsername();
				userrank.user_head_path = tuser.getHeadpath();
				userrank.nickname = tuser.getNickname();
				
				list_query_trank = dao.getByQuery("user_id=" + tuser.getId(), 0, 0);
				userrank.chatcount = list_query_trank.size();
				
				list_userrank.add(userrank);
				
			}
			
			quickSortByList(list_userrank, 0, list_userrank.size()-1);
			Collections.reverse(list_userrank);
			
			List<UserRank> list_userrank_for_client = new ArrayList<UserRank>();
			for(int i=startCount-1; i<(startCount-1+10); i++){
				if(i<list_userrank.size()) list_userrank_for_client.add(list_userrank.get(i));
				
			}
			
			root.success = true;
			root.message = "";
			root.list_userrank = list_userrank_for_client;
			
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
					
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	
	/** 快速排序方法（列表） */
	private void quickSortByList(List<UserRank> list, int lo0, int hi0) {
		int lo = lo0;
		int hi = hi0;
		if (lo >= hi)
			return;
		// 确定指针方向的逻辑变量
		boolean transfer = true;

		while (lo != hi) {
			if (list.get(lo).chatcount > list.get(hi).chatcount) {
				// 交换
				UserRank temp = list.get(lo);
				list.set(lo, list.get(hi));
				list.set(hi, temp);
				// 决定下标移动，还是上标移动
				transfer = (transfer == true) ? false : true;
			}

			// 将指针向前或者向后移动
			if (transfer)
				hi--;
			else
				lo++;
		}

		// 将数组分开两半，确定每个数字的正确位置
		lo--;
		hi++;
		quickSortByList(list, lo0, lo);
		quickSortByList(list, hi, hi0);
	}

	
	
	
	class Root{
		boolean success;
		String message;
		List<UserRank> list_userrank;
	}
	class UserRank{
		String username;
		String nickname;
		int rank;
		String user_head_path;
		int chatcount;
	}
	
	

}
