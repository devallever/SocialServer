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
import com.social.dao.NewsDAO;
import com.social.pojo.TNews;

/**
 * Servlet implementation class DeleteNewsServlet
 */
@WebServlet("/DeleteNewsServlet")
public class DeleteNewsServlet extends HttpServlet {
	private String user_id;
	private String news_id;
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
		if(session == null || session.getAttribute("id") == null){
			root.messgae = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		//获取参数
//		user_id = request.getParameter("user_id");
//		if(!checkParameter(response, "user_id", user_id, root, pw)) return;
		
		news_id = request.getParameter("news_id");
		if(!checkParameter(response, "news_id", news_id, root, pw)) return;
		
		NewsDAO dao = new NewsDAO();
		List<TNews> list_tnews;
		try {
			list_tnews = dao.getByQuery("user_id="+user_id+ " and id="+ news_id, 0, 0);
			if(list_tnews.size()>0){
				dao.deleteById(Long.parseLong(news_id));
				root.success = true;
				root.messgae = "删除成功";
			}else{
				root.success = false;
				root.messgae = "该动态不存在或者已删除";
				
			}
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class Root{
		boolean success;
		String messgae;
	}
	
	private  boolean checkParameter(HttpServletResponse response,String parameterName ,String parameterValue , Root root, PrintWriter pw){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		if (parameterValue == null || parameterValue.length() == 0) {
			root.success = false;
			root.messgae = "没有指定" + parameterName + "参数";
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
