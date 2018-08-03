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
import com.social.dao.UserDAO;
import com.social.pojo.TUser;


/**
 * Servlet implementation class CheckPhoneServlet
 */
@WebServlet("/CheckPhoneServlet")
public class CheckPhoneServlet extends HttpServlet {
	
	private PrintWriter pw;
	private Root root;
	private String phone;
	
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
//			root.message = "未登录";
//			root.success = false;
//			pw.print(gson.toJson(root));
//			pw.close();
//			return;
		}
		//user_id = session.getAttribute("id").toString();
		
		
		phone = request.getParameter("phone");
		System.out.println("phone = " + phone);
		if(!checkParameter(response, "phone", phone, root, pw)) return;
		
		UserDAO dao = new UserDAO();
		List<TUser> list_tuser;
		try {
			list_tuser = dao.getByQuery("phone='" + phone + "'", 0, 0);
			if(list_tuser.size()==0){
				root.success=true;
				root.message="";
			}else{
				root.success=false;
				root.message="该手机号已被注册";
			}
			
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
		} catch (Exception e) {
			// TODO: handle exception
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
	}

}
