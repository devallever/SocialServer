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
import com.social.dao.PostDAO;
import com.social.pojo.TPost;
import com.social.servlet.AddPostServlet.Root;

/**
 * Servlet implementation class DeletePostServlet
 */
@WebServlet("/ModifyPostServlet")
public class ModifyPostServlet extends HttpServlet {
	private String post_id;
	private String user_id;
	private String postname;
	private String salary;
	private String requirement;
	private String description;
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
		
		
		post_id = request.getParameter("post_id");
		if(!checkParameter(response, "post_id", post_id, root, pw)) return;
		
		postname = request.getParameter("postname");
		if(!checkParameter(response, "postname", postname, root, pw)) return;
		
		salary = request.getParameter("salary");
		if(!checkParameter(response, "salary", salary, root, pw)) return;
		
		description = request.getParameter("description");
		if(!checkParameter(response, "description", description, root, pw)) return;
		
		requirement = request.getParameter("requirement");
		if(!checkParameter(response, "requirement", requirement, root, pw)) return;
		
		PostDAO dao = new PostDAO();
		TPost tpost;
		try {
			tpost = dao.getById(Long.parseLong(post_id));
			tpost.setPostname(postname);
			tpost.setSalary(salary);
			tpost.setDescription(description);
			tpost.setRequirement(requirement);
			dao.update(tpost);
			
			
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
