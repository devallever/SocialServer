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
import com.social.dao.RecruitDAO;
import com.social.pojo.TPost;


/**
 * Servlet implementation class AddPostServlet
 */
@WebServlet("/AddPostServlet")
public class AddPostServlet extends HttpServlet {
	
	private String user_id;
	private String postname;
	private String salary;
	private String requirement;
	private String description;
	private PrintWriter pw;
	private Root root;
	private String recruit_id;
	private String str_longitude;
	private String str_latitude;
	
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
		
		
		postname = request.getParameter("postname");
		if(!checkParameter(response, "postname", postname, root, pw)) return;
		
		recruit_id = request.getParameter("recruit_id");
		if(!checkParameter(response, "recruit_id", recruit_id, root, pw)) return;
		
		salary = request.getParameter("salary");
		if(!checkParameter(response, "salary", salary, root, pw)) return;
		
		requirement = request.getParameter("requirement");
		if(!checkParameter(response, "requirement", requirement, root, pw)) return;
		
		description = request.getParameter("description");
		if(!checkParameter(response, "description", description, root, pw)) return;
		
		str_longitude = request.getParameter("longitude");
		if(!checkParameter(response, "longitude", str_longitude, root, pw)) return;
		
		str_latitude = request.getParameter("latitude");
		if(!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		
		PostDAO dao = new PostDAO();
		TPost tpost;
		RecruitDAO recruitDAO = new RecruitDAO(dao.getSession());
		try {
			tpost = new TPost();
			tpost.setOptlockversion1(Long.parseLong("0"));
			tpost.setOptlockversion2(Long.parseLong("0"));
			tpost.setPostname(postname);
			tpost.setSalary(salary);
			tpost.setRecruit(recruitDAO.getById(Long.parseLong(recruit_id)));
			tpost.setRequirement(requirement);
			tpost.setDescription(description);
			tpost.setLatitude(Double.parseDouble(str_latitude));
			tpost.setLongitude(Double.parseDouble(str_longitude));
			tpost = dao.add(tpost);
			
			root.success = true;
			root.message = "";
			pw.print(gson.toJson(root));
			pw.close();
			recruitDAO.close();
			dao.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	class Root{
		boolean success;
		String message;
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

}
