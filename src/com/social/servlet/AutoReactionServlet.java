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
import com.social.dao.AutoreactionDAO;
import com.social.pojo.TAutoreaction;

/**
 * Servlet implementation class AutoReactionServlet
 */
@WebServlet("/AutoReactionServlet")
public class AutoReactionServlet extends HttpServlet {
	
	private String user_id;
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
		
		AutoreactionDAO dao = new AutoreactionDAO();
		List<TAutoreaction> list_tautoreaction;
		
		try {
			list_tautoreaction = dao.getByQuery("user_id=" + user_id, 0, 0);
			if (list_tautoreaction.size()>0) {
				//有记录
				root.content = list_tautoreaction.get(0).getContent();
			}else{
				root.content = "我在忙，待会儿回复。";
			}
			
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
		String content;
	}

}
