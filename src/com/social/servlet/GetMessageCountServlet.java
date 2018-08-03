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
import com.social.dao.UserDAO;
import com.social.pojo.TUser;

/**
 * Servlet implementation class GetMessageCountServlet
 */
@WebServlet("/GetMessageCountServlet")
public class GetMessageCountServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("GetMessageCountServlet");
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
		
		UserDAO dao = new UserDAO();
		TUser tuser;
		try {
			tuser = dao.getById(Long.parseLong(user_id));
			root.success = true;
			root.message = "";
			root.messageCount = tuser.getMessagecount();
			root.isVip = tuser.getIs_vip();
			
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
		int messageCount;
		int isVip;
	}
}
