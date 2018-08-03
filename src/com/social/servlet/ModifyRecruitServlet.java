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
import com.social.dao.RecruitDAO;
import com.social.pojo.TRecruit;
import com.social.servlet.AddRecruitServlet.Root;

/**
 * Servlet implementation class ModifyRecruitServlet
 */
@WebServlet("/ModifyRecruitServlet")
public class ModifyRecruitServlet extends HttpServlet {
	
	private String recruit_id;
	private String user_id;
	private String companyname;
	private String link;
	private String phone;
	private String str_longitude;
	private String str_latitude;
	private String requirement;
	private String address;
	
	private PrintWriter pw;
	private Root root;
	private TRecruit tRecruit;
	
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
			root.success= false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		recruit_id = request.getParameter("recruit_id");
		if(!checkParameter(response, "recruit_id", recruit_id, root, pw)) return;
		
		companyname = request.getParameter("companyname");
		if(!checkParameter(response, "companyname", companyname, root, pw)) return;
		
		link = request.getParameter("link");
		if(!checkParameter(response, "link", link, root, pw)) return;
		
		phone = request.getParameter("phone");
		if(!checkParameter(response, "phone", phone, root, pw)) return;
		
		address = request.getParameter("address");
		if(!checkParameter(response, "address", address, root, pw)) return;
		
		requirement = request.getParameter("requirement");
		if(!checkParameter(response, "requirement", requirement, root, pw)) return;
		
//		str_longitude = request.getParameter("longitude");
//		if(!checkParameter(response, "longitude", str_longitude, root, pw)) return;
//		
//		str_latitude = request.getParameter("latitude");
//		if(!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		
		
		RecruitDAO dao = new RecruitDAO();
		TRecruit trecruit;
		try {
			trecruit = dao.getById(Long.parseLong(recruit_id));
			trecruit.setCompanyname(companyname);
			trecruit.setLink(link);
			trecruit.setPhone(phone);
			trecruit.setAddress(address);
			trecruit.setRequirement(requirement);
			//trecruit.setLongitude(Double.valueOf(str_longitude));
			//trecruit.setLatitude(Double.valueOf(str_latitude));
			
			dao.update(trecruit);
			
			
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
