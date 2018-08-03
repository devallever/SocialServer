package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.SharerankDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TSharerank;
import com.social.pojo.TUser;
import com.social.servlet.MyChatRankServlet.Root;
import com.social.servlet.MyChatRankServlet.UserRank;

/**
 * Servlet implementation class MyShareRankServlet
 */
@WebServlet("/MyShareRankServlet")
public class MyShareRankServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	
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
		if(session == null || session.getAttribute("id") == null){
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
		}
		user_id = session.getAttribute("id").toString();
		
		SharerankDAO dao = new SharerankDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		List<TSharerank> list_tsharerank;
		int position = -1;
		TUser tuser_rank;
		UserRank user_rank;
		try {
			list_tsharerank = dao.getRank("order by share_count desc", 0, 0);
			
			for(int i=0;i<list_tsharerank.size();i++){
				if (list_tsharerank.get(i).getUser().getId()==Long.parseLong(user_id)) {
					position = i;
				}
			}
			
			if(position == -1){
				tuser_rank = userDAO.getById(Long.parseLong(user_id));
			}else{
				tuser_rank = list_tsharerank.get(position).getUser();
			}
			
			user_rank = new UserRank();
			user_rank.username = tuser_rank.getUsername();
			user_rank.nickname = tuser_rank.getNickname();
			user_rank.user_head_path = tuser_rank.getHeadpath();
			user_rank.rank = position+1;
			if(position>=0) user_rank.sharecount = list_tsharerank.get(position).getShare_count();
			else user_rank.sharecount = 0;
			
			root.success = true;
			root.message = "";
			root.userrank = user_rank;
			//root.list_userrank = list_userrank;
			
			pw.print(gson.toJson(root));
			pw.close();
			userDAO.close();
			dao.close();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	
	class Root{
		boolean success;
		String message;
		UserRank userrank;
	}
	class UserRank{
		String username;
		String nickname;
		int rank;
		String user_head_path;
		int sharecount;
	}

}
