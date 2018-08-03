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
import com.social.dao.FollowDAO;
import com.social.dao.NewsDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TNews;


/**
 * Servlet implementation class FansCountServlet
 */
@WebServlet("/FansCountServlet")
public class FansCountServlet extends HttpServlet {
	
	private String user_id;
	
	private PrintWriter pw;
	private Root root;
	
	private String username;
	
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
//			return;
//		}
		user_id = session.getAttribute("id").toString();
		
		username = request.getParameter("username");
		
		FollowDAO dao = new FollowDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		NewsDAO newsDAO = new NewsDAO(dao.getSession());
		List<TNews> list_tnews;
		String id;
		try {
			if(username == null){
				id = user_id;
			}else{
				id = userDAO.getByQuery("username='" + username + "'", 0, 0).get(0).getId()+"";
			}
			
			list_tnews = newsDAO.getByQuery("user_id=" + id, 0, 0);
			root.news_count = list_tnews.size();
			root.fans_count = dao.getByQuery("follow_id=" + id, 0, 0).size();
			root.follow_count = dao.getByQuery("user_id=" + id, 0, 0).size();
			
			root.success = true;
			root.message = "";
			
			pw.print(gson.toJson(root));
			pw.close();
			newsDAO.close();
			dao.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
	}

	class Root{
		boolean success;
		String message;
		int fans_count;//粉丝数
		int follow_count;//关注数
		int news_count;//动态数
	}
	
}
