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
import com.social.dao.GroupDAO;
import com.social.dao.GroupmemberDAO;


/**
 * Servlet implementation class DropGroupServlet
 */
@WebServlet("/DropGroupServlet")
public class DropGroupServlet extends HttpServlet {
	
	private String user_id;
	private String group_id;
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
		System.out.println(session.getId());
		if(session == null || session.getAttribute("id") == null){
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		group_id = request.getParameter("group_id");
		if(!checkParameter(response, "group_id", group_id, root, pw)) return;
		
		GroupmemberDAO dao = new GroupmemberDAO();
		GroupDAO groupDAO = new GroupDAO(dao.getSession());
		
		try {
			Long id = dao.getByQuery("group_id=" + group_id + " and user_id = " + user_id, 0, 0).get(0).getId();
			dao.deleteById(id);
			
			root.success = true;
			root.message = "";
			pw.print(gson.toJson(root));
			pw.close();
			groupDAO.close();
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
	
	private class Root{
		boolean success;
		String message;
	}

}
