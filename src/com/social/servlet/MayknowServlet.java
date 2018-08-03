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
import com.social.dao.UserDAO;
import com.social.pojo.TUser;

/**
 * Servlet implementation class MayknowServlet
 */
@WebServlet("/MayknowServlet")
public class MayknowServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	private String phone_json;
	
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
		System.out.println(session.getId());
		if(session == null || session.getAttribute("id") == null){
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		phone_json = request.getParameter("phone_json");
		if(!checkParameter(response, "phone_json", phone_json, root, pw)) return;
		
		Data data;
		data = gson.fromJson(phone_json, Data.class);
		
		UserDAO dao = new UserDAO();
		List<TUser> list_tuser;
		List<TUser> list_mayknow_tuser = new ArrayList<TUser>();
		List<User> list_user = new ArrayList<MayknowServlet.User>();
		User user;
		try {
			for (int i = 0; i < data.phone.length; i++) {
				list_tuser = dao.getByQuery("phone='" + data.phone[i] + "'", 0, 0);
				for(TUser tuser: list_tuser){
					list_mayknow_tuser.add(tuser);
				}
			}
			
			for(TUser tuser:list_mayknow_tuser){
				user = new User();
				user.id = tuser.getId()+"";
				user.username = tuser.getUsername();
				user.nickname = tuser.getNickname();
				user.head_path = tuser.getHeadpath();
				user.phone = tuser.getPhone();
				list_user.add(user);
			}
			
			
			root.success = true;
			root.message = "";
			root.list_user = list_user;
			
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
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
	
	
	class Root{
		boolean success;
		String message;
		List<User> list_user;
	}
	
	class User{
		String id;
		String nickname;
		String username;
		String head_path;
		String phone;
	}
	
	class Data{
		String[] phone;
	}

}
