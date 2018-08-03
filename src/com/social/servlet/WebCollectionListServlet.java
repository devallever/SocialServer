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
import com.social.dao.UserDAO;
import com.social.dao.WebcollectionDAO;
import com.social.pojo.TWebcollection;
import com.social.servlet.AddWebCollectionServlet.Root;
import com.social.servlet.ShareRankServlet.UserRank;

/**
 * Servlet implementation class WebCollectionListServlet
 */
@WebServlet("/WebCollectionListServlet")
public class WebCollectionListServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	
	private String str_page;
	private int page;
	private int startCount;
	private int endCount;
	
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
		
		str_page = request.getParameter("page");
		if(str_page == null) str_page = "1";
		page = Integer.valueOf(str_page);
		endCount = page * 1 * 10;
		startCount = endCount - 9;
		
		WebcollectionDAO dao = new WebcollectionDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		List<TWebcollection> list_twebcollection;
		List<WebCollection> list_webcollection = new ArrayList<WebCollectionListServlet.WebCollection>();
		WebCollection webcollection;
		try {
			list_twebcollection = dao.getByQuery("user_id=" + user_id, 0, 0);
			for(TWebcollection twebcollection:list_twebcollection){
				webcollection = new WebCollection();
				webcollection.id = twebcollection.getId()+"";
				webcollection.title = twebcollection.getTitle();
				webcollection.url = twebcollection.getUrl();
				list_webcollection.add(webcollection);
			}
			
			//分页加载
			List<WebCollection> list_webcollection_for_client = new ArrayList<WebCollection>();
			for(int i=startCount-1; i<(startCount-1+10); i++){
				if(i<list_webcollection.size()) list_webcollection_for_client.add(list_webcollection.get(i));
				
			}
			
			
			
			root.success = true;
			root.message = "";
			root.list_webcollection = list_webcollection_for_client;//分页加载
			root.list_webcollection = list_webcollection;//全部加载
			
			pw.print(gson.toJson(root));
			pw.close();
			userDAO.close();
			dao.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	class Root{
		boolean success;
		String message;
		List<WebCollection> list_webcollection;
	}

	class WebCollection{
		String id;
		String title;
		String url;
	}
}
