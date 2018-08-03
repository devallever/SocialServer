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
import com.social.dao.FriendgroupDAO;
import com.social.pojo.TFriend;
import com.social.pojo.TFriendgroup;

/**
 * Servlet implementation class ModifyUserFriendGroupServlet
 */
@WebServlet("/ModifyUserFriendGroupServlet")
public class ModifyUserFriendGroupServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	private String friend_id;
	private String friendgroup_id;
	
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
		
		friendgroup_id = request.getParameter("friendgroup_id");
		if(!checkParameter(response, "friendgroup_id", friendgroup_id, root, pw)) return;
		FriendDAO dao = new FriendDAO();
		FriendgroupDAO friendgroupDAO = new FriendgroupDAO(dao.getSession());
		TFriend tfriend;
		TFriendgroup tFriendgroup;
		try {
			tfriend = dao.getByQuery("user_id = "+ user_id + " and friend_id = " + friend_id, 0, 0).get(0);
			tFriendgroup = friendgroupDAO.getById(Long.parseLong(friendgroup_id));
			tfriend.setFriendgroup(tFriendgroup);
			dao.update(tfriend);
			
			root.success = true;
			root.message = "";
			pw.print(gson.toJson(root));
			pw.close();
			friendgroupDAO.close();
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
