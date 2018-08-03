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
import com.social.pojo.TFriend;

/**
 * Servlet implementation class ModifySecondNameServlet
 */
@WebServlet("/ModifySecondNameServlet")
public class ModifySecondNameServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	private String friend_id;
	private String second_name;
	
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
		
		friend_id = request.getParameter("friend_id");
		if(!checkParameter(response, "friend_id", friend_id, root, pw)) return;
		
		second_name = request.getParameter("second_name");
		if(!checkParameter(response, "second_name", second_name, root, pw)) return;
		
		FriendDAO dao = new FriendDAO();
		
		TFriend tfriend;
		
		try {
			tfriend = dao.getByQuery("user_id = "+ user_id + " and friend_id = " + friend_id, 0, 0).get(0);
			tfriend.setSecond_name(second_name);
			dao.update(tfriend);
			
			
			root.success = true;
			root.message = "";
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
		
		
	}
	
	
	private boolean checkParameter(HttpServletResponse response,
			String parameterName, String parameterValue, Root root,
			PrintWriter pw) {
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
			// pw.close();
			return false;
		}
		System.out.println(parameterValue);
		return true;
	}
	
	
	class Root{
		boolean success;
		String message;
	}

}
