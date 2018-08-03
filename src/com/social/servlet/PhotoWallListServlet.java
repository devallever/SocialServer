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
import com.social.dao.PhotowallDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TPhotowall;
import com.social.servlet.AddPhotoWallServlet.Root;


/**
 * Servlet implementation class PhotoWallListServlet
 */
@WebServlet("/PhotoWallListServlet")
public class PhotoWallListServlet extends HttpServlet {
	
	//private String user_id;
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
//			root.message = "未登录";
//			root.success = false;
//			pw.print(gson.toJson(root));
//			pw.close();
//			return;
		}
//		user_id = session.getAttribute("id").toString();
		
		String username = request.getParameter("username");
		if(!checkParameter(response, "username", username, root, pw)) return;
		
		PhotowallDAO dao = new PhotowallDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		List<TPhotowall> list_photowall;
		List<String> list_photowallpath;
		try {
			Long user_id = userDAO.getByQuery("username='" + username + "'", 0, 0).get(0).getId();
			list_photowall = dao.getByQuery("user_id=" + user_id +" order by photoposition asc", 0, 0);
			list_photowallpath = new ArrayList<String>();
			for(TPhotowall tPhotowall : list_photowall){
				list_photowallpath.add(tPhotowall.getPath());
			}
			
			root.photowalllist = list_photowallpath;
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
		List<String> photowalllist;
	}
	
}
