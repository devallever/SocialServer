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
import com.social.dao.UserDAO;
import com.social.pojo.TUser;

/**
 * Servlet implementation class GetVideoCallMinCountServlet
 */
@WebServlet("/GetVideoCallMinCountServlet")
public class GetVideoCallMinCountServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	private String to_username;
	
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
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		to_username = request.getParameter("to_username");
		if(!checkParameter(response, "to_username", to_username, root, pw)) return;
		
		UserDAO dao = new UserDAO();
		//FeeDAO feeDAO = new FeeDAO(dao.getSession());
		
		int my_credit;
		int to_user_video_fee;
		int minCount;
		TUser tuser;
		TUser to_tuser;
		
		try {
			root.success = true;
			root.message = "";
			
			tuser = dao.getById(Long.parseLong(user_id));
			to_tuser = dao.getByQuery("username='" + to_username + "'", 0, 0).get(0);
			to_user_video_fee = dao.getByQuery("id =" + to_tuser.getId(), 0, 0).get(0).getVideo_fee();
			my_credit = tuser.getCredit();
			
			if (to_user_video_fee == 0) {
				root.minCount = 999;
				root.video_fee = 0;
				pw.print(gson.toJson(root));
				pw.close();
				//feeDAO.close();
				dao.close();
				return;
			}else{
				//to_user_video_fee > 0
			}
			
			if (my_credit < to_user_video_fee) {
				root.minCount = 0;
				root.video_fee = to_user_video_fee;
				pw.print(gson.toJson(root));
				pw.close();
				//feeDAO.close();
				dao.close();
				return;
			}else{
				
			}
			
			minCount = my_credit / to_user_video_fee;
			root.minCount = minCount;
			root.video_fee = to_user_video_fee;
			pw.print(gson.toJson(root));
			pw.close();
			//feeDAO.close();
			dao.close();
			return;
			
		} catch (Exception e) {
			// TODO: handle exception
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
		int video_fee;
		int minCount;
	}

}
