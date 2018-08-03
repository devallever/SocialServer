package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.SharerankDAO;
import com.social.pojo.TSharerank;

/**
 * Servlet implementation class ShareRankServlet
 */
@WebServlet("/ShareRankServlet")
public class ShareRankServlet extends HttpServlet {
	
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
		
//		HttpSession session = request.getSession();
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
		
		SharerankDAO dao = new SharerankDAO();
		List<TSharerank> list_tsharerank;
		List<UserRank> list_userrank = new ArrayList<ShareRankServlet.UserRank>();
		UserRank userrank;
		try {
			list_tsharerank = dao.getRank("order by share_count desc", 0, 0);
			for(TSharerank tsharerank: list_tsharerank){
				userrank = new UserRank();
				userrank.username = tsharerank.getUser().getUsername();
				userrank.nickname = tsharerank.getUser().getNickname();
				userrank.user_head_path = tsharerank.getUser().getHeadpath();
				userrank.sharecount = tsharerank.getShare_count();
				list_userrank.add(userrank);
			}
			
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
		int sharecount;
	}

}
