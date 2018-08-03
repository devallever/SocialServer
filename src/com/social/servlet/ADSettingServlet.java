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
import com.social.dao.AdDAO;
import com.social.pojo.TAd;
import com.social.servlet.ConnectionServlet.Root;

/**
 * Servlet implementation class ADSettingServlet
 */
@WebServlet("/ADSettingServlet")
public class ADSettingServlet extends HttpServlet {
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
		
		AdDAO dao = new AdDAO();
		TAd tAD;
		ADSetting setting = new ADSetting();
		try {
			tAD = dao.getById(Long.parseLong("1"));
			if(tAD== null){
				root.success = false;
				root.message = "";
				root.ad_setting = null;
				pw.print(gson.toJson(root));
				pw.close();
				dao.close();
				return;
			}
			setting.id = String.valueOf(tAD.getId());
			setting.day_space = tAD.getDay_space();
			setting.count = tAD.getCount();
			setting.isshow = tAD.getIsshow();
			root.success = true;
			root.message = "";
			root.ad_setting = setting;
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			root.success =false;
			root.message = "未知错误";
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
		}
		
		
		
		//String user_id = session.getAttribute("id").toString();
	}
	
	class Root{
		boolean success;
		String message;
		ADSetting ad_setting;
		
	}
	class ADSetting{
		String id;
		int day_space;
		int count;
		int isshow;
	}

}
