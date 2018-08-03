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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.FriendgroupDAO;
import com.social.pojo.TFriendgroup;


/**
 * Servlet implementation class FriendGroupNameListServlet
 */
@WebServlet("/FriendGroupNameListServlet")
public class FriendGroupNameListServlet extends HttpServlet {
	
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
		
		FriendgroupDAO dao = new FriendgroupDAO();
		List<TFriendgroup> list_tfriendgroup;
		List<FriendGroup> list_friendgroup = new ArrayList<FriendGroupNameListServlet.FriendGroup>();
		FriendGroup friendgroup;
		try {
			list_tfriendgroup = dao.getByQuery("user_id=" + user_id, 0, 0);
			for(TFriendgroup tfriendgroup: list_tfriendgroup){
				friendgroup = new FriendGroup();
				friendgroup.id = tfriendgroup.getId() + "";
				friendgroup.friendgroup_name = tfriendgroup.getFriendgroup_name();
				list_friendgroup.add(friendgroup);
			}
			
			
			root.success = true;
			root.message = "";
			root.list_friendgroup = list_friendgroup;
			
			
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
		List<FriendGroup> list_friendgroup;
	}
	
	class FriendGroup{
		String id;
		String friendgroup_name;
	}

}
