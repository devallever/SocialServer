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
import com.social.dao.UserDAO;
import com.social.dao.WebcollectionDAO;
import com.social.pojo.TUser;
import com.social.pojo.TWebcollection;

/**
 * Servlet implementation class AddWebCollectionServlet
 */
@WebServlet("/AddWebCollectionServlet")
public class AddWebCollectionServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	
	private String title;
	private String url;
	
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
		
		
		title = request.getParameter("title");
		if(!checkParameter(response, "title", title, root, pw)) return;
		
		
		url = request.getParameter("url");
		if(!checkParameter(response, "url", url, root, pw)) return;
		
		WebcollectionDAO dao = new WebcollectionDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		List<TWebcollection> list_twebcollection;
		TWebcollection twebcollection;
		TUser tuser;
		
		try {
			list_twebcollection = dao.getByQuery("user_id=" + user_id +  " and url='" + url + "'", 0, 0);
			if (list_twebcollection.size()>0) {
				//有记录 不需要天机
				root.success = false;
				root.message = "已存在";
			}else{
				//无记录 添加操作
				tuser = userDAO.getById(Long.parseLong(user_id));
				
				twebcollection = new TWebcollection();
				twebcollection.setDate(new Date());
				twebcollection.setTitle(title);
				twebcollection.setUrl(url);
				twebcollection.setUser(tuser);
				twebcollection.setOptlockversion1(Long.parseLong("0"));
				twebcollection.setOptlockversion2(Long.parseLong("0"));
				dao.add(twebcollection);
				
				root.success = true;
				root.message = "";
			}
			
			pw.print(gson.toJson(root));
			pw.close();
			userDAO.close();
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
