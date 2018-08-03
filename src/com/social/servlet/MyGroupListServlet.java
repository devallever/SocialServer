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
import com.social.dao.GroupDAO;
import com.social.dao.GroupmemberDAO;
import com.social.pojo.TGroupmember;
import com.social.servlet.NearbyGroupListServlet.Root;
import com.social.servlet.NearbyGroupListServlet.User;


/**
 * Servlet implementation class MyGroupListServlet
 */
@WebServlet("/MyGroupListServlet")
public class MyGroupListServlet extends HttpServlet {
	private PrintWriter pw;
	private Root root;
	private String user_id;
	
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
			root.group_list = null;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		if(session.getAttribute("id")==null){
			root.message = "未登录";
			root.success = false;
			root.group_list = null;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}else{
			user_id = session.getAttribute("id").toString();
		}
		
		GroupmemberDAO dao = new GroupmemberDAO();
		List<TGroupmember> list_tgroupmember = null;
		List<Group> list_group = new ArrayList<Group>();
		Group group;
		try{
			list_tgroupmember = dao.getByQuery("user_id=" + Long.parseLong(user_id), 0, 0);
			for(TGroupmember tGroupmember: list_tgroupmember){
				group = new Group();
				group.id = tGroupmember.getGroup().getId()+"";
				group.groupname = tGroupmember.getGroup().getGroupname();
				group.group_img = tGroupmember.getGroup().getGroupimg();
				group.description = tGroupmember.getGroup().getDescription();
				group.state = tGroupmember.getGroup().getState();
				group.hx_group_id = tGroupmember.getGroup().getHx_group_id();
				if((tGroupmember.getGroup().getUser().getId()+"").equals(user_id)){
					group.is_my_group = 1;
				}else{
					group.is_my_group = 0;
				}
				list_group.add(group);
			}
			
			root.success = true;
			root.message = "";
			root.group_list = list_group;
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}
	
	class Root {
		boolean success;
		String message;
		List<Group> group_list;
	}
	
	class Group {
		String id;
		String groupname;
		String group_img;
		String hx_group_id;
		int state;
		int is_my_group;
		String description;
	}

}
