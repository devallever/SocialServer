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
import com.social.dao.RecruitDAO;
import com.social.pojo.TRecruit;
import com.social.servlet.MyGroupListServlet.Root;


/**
 * Servlet implementation class MyRecruitListServlet
 */
@WebServlet("/MyRecruitListServlet")
public class MyRecruitListServlet extends HttpServlet {
	
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
			root.list_recruit = null;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		if(session.getAttribute("id")==null){
			root.message = "未登录";
			root.success = false;
			root.list_recruit = null;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}else{
			user_id = session.getAttribute("id").toString();
		}
		
		RecruitDAO dao = new RecruitDAO();
		List<TRecruit> list_trecruit;
		Recruit  recruit;
		List<Recruit> list_recruit = new ArrayList<MyRecruitListServlet.Recruit>();
		try {
			list_trecruit = dao.getByQuery("user_id=" + user_id, 0, 0);
			for(TRecruit trecruit:list_trecruit){
				recruit = new Recruit();
				recruit.id = trecruit.getId()+"";
				recruit.companyname = trecruit.getCompanyname();
				recruit.date = trecruit.getDate().toString().split(" ")[0];
				recruit.requirement = trecruit.getRequirement();
				recruit.user_id = trecruit.getUser().getId()+"";
				recruit.user_head_path = trecruit.getUser().getHeadpath();
				list_recruit.add(recruit);
			}
			
			root.success = true;
			root.message = "";
			root.list_recruit = list_recruit;
			pw.print(gson.toJson(root));
			dao.close();
			pw.close();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	
	class Root{
		boolean success;
		String message;
		List<Recruit> list_recruit;
	}
	
	class Recruit{
		String id;
		String companyname;
		String date;
		String requirement;
		String user_id;
		String user_head_path;
	}

}
