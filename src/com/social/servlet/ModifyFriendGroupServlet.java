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
import com.social.dao.FriendgroupDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TFriendgroup;
import com.social.servlet.AddFriendGroupServlet.Root;

/**
 * Servlet implementation class ModifyFriendGroupServlet
 */
@WebServlet("/ModifyFriendGroupServlet")
public class ModifyFriendGroupServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	private String friendgroup_id;
	private String friendgroup_name;
	
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
		
		friendgroup_id = request.getParameter("friendgroup_id");
		if(!checkParameter(response, "friendgroup_id", friendgroup_id, root, pw)) return;
		
		friendgroup_name = request.getParameter("friendgroup_name");
		if(!checkParameter(response, "friendgroup_name", friendgroup_name, root, pw)) return;
		
		FriendgroupDAO dao = new FriendgroupDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		TFriendgroup tfriendgroup;
		List<TFriendgroup> list_tfriendgroup;
		try {
			list_tfriendgroup = dao.getByQuery("user_id=" + user_id + " and friendgroup_name='" + friendgroup_name + "'", 0, 0);
			if(list_tfriendgroup.size()>0){
				root.success = false;
				root.message = "分组已存在";
				
			}else{
				tfriendgroup = dao.getById(Long.parseLong(friendgroup_id));
				tfriendgroup.setFriendgroup_name(friendgroup_name);
				dao.update(tfriendgroup);
				root.success = true;
				root.message = "";
			}
			
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
