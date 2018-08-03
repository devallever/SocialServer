package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.FriendDAO;
import com.social.dao.UserDAO;


/**
 * Servlet implementation class DeleteFriendServlet
 */
@WebServlet("/DeleteFriendServlet")
public class DeleteFriendServlet extends HttpServlet {
	private String user_id;
	private String friend_id;
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
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		//获取参数
//		user_id = request.getParameter("user_id");
//		if(!checkParameter(response, "user_id", user_id, root, pw)) return;
		
		friend_id = request.getParameter("friend_id");
		if(!checkParameter(response, "friend_id", friend_id, root, pw)) return;
		
		FriendDAO dao = new FriendDAO();
		UserDAO userDao = new UserDAO(dao.getSession());
		try {
			Long friendId = userDao.getByQuery("username = '" + friend_id + "'", 0, 0).get(0).getId();
			dao.deleteById(dao.getByQuery("user_id="+user_id+" and friend_id=" + friendId, 0, 0).get(0).getId());
			userDao.close();
			dao.close();
			
			dao = new FriendDAO();
			userDao = new UserDAO(dao.getSession());
			dao.deleteById(dao.getByQuery("user_id="+friendId+" and friend_id=" + user_id, 0, 0).get(0).getId());
			root.success = true;
			root.message = "删除成功";
			pw.print(gson.toJson(root));
			userDao.close();
			dao.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class Root{
		boolean success;
		String message;
	}
	
	private  boolean checkParameter(HttpServletResponse response,String parameterName ,String parameterValue , Root root, PrintWriter pw){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		if (parameterValue == null || parameterValue.length() == 0) {
			root.success = false;
			root.message = "没有指定" + parameterName + "参数";
			response.setContentType("text/html;charset=utf-8");
			try {
				pw = response.getWriter();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pw.print(gson.toJson(root));
			//pw.close();
			return false;
		}
		System.out.println(parameterValue);
		return true;
	}

}
