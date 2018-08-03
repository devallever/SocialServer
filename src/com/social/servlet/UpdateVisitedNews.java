package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import com.social.dao.UserDAO;
import com.social.dao.VisitedNewsDAO;
import com.social.pojo.TNews;
import com.social.pojo.TVisitednews;
import com.social.servlet.UserDataServlet.Root;


/**
 * Servlet implementation class UpdateVisitedNews
 */
@WebServlet("/UpdateVisitedNews")
public class UpdateVisitedNews extends HttpServlet {
	private String news_id;
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
//		if(session == null || session.getAttribute("id") == null){
//			root.message = "未登录";
//			root.seccess = false;
//			root.user = null;
//			pw.print(gson.toJson(root));
//			pw.close();
//		}
//		user_id = session.getAttribute("id").toString();
		
		
		//获取参数
		news_id = request.getParameter("news_id");
		if(!checkParameter(response, "news_id", news_id, root, pw)) return;
		
		NewsDAO dao = new NewsDAO();
		VisitedNewsDAO visitedNewsDAO = new VisitedNewsDAO(dao.getSession());
		UserDAO userDAO = new UserDAO(dao.getSession());
		TNews tnews;
		TVisitednews tvisitednews;
		List<TVisitednews> list_tvisitednews;
		
		try {
			tnews = dao.getById(Long.parseLong(news_id));
			int count = tnews.getVisited_count();
			count ++ ;
			tnews.setVisited_count(count);
			dao.update(tnews);
			
			if(session.getAttribute("id") != null){
				user_id = session.getAttribute("id").toString();
				list_tvisitednews = visitedNewsDAO.getByQuery("user_id=" + tnews.getUser().getId()+ " and who_id=" + user_id, 0, 0);
				if(list_tvisitednews.size()==0){
					tvisitednews = new TVisitednews();
					tvisitednews.setDate(new Date());
					tvisitednews.setNews(tnews);
					tvisitednews.setWho(userDAO.getById(Long.parseLong(user_id)));
					tvisitednews.setUser(tnews.getUser());
					tvisitednews.setOptlockversion1(Long.parseLong("0"));
					tvisitednews.setOptlockversion2(Long.parseLong("0"));
					visitedNewsDAO.add(tvisitednews);
				}else{
					tvisitednews = list_tvisitednews.get(0);
					tvisitednews.setDate(new Date());
					visitedNewsDAO.update(tvisitednews);
				}
			}
			
			userDAO.close();
			visitedNewsDAO.close();
			dao.close();
			pw.close();
			
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
			pw.close();
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
