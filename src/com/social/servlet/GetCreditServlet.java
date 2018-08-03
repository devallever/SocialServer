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
 * Servlet implementation class GetCreditServlet
 */
@WebServlet("/GetCreditServlet")
public class GetCreditServlet extends HttpServlet {
	
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
		
		UserDAO dao = new UserDAO();
		TUser tuser;
		try {
			tuser = dao.getById(Long.parseLong(user_id));
			if(tuser != null){
				root.credit = tuser.getCredit();
				root.success = true;
				root.message = "";
				dao.close();
				pw.print(gson.toJson(root));
				pw.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	class Root{
		boolean success;
		String message;
		int credit;
	}

}
