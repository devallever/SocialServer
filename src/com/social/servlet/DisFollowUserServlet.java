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
import com.social.dao.FollowDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TFollow;
import com.social.pojo.TUser;
import com.social.servlet.FollowServlet.Root;

/**
 * Servlet implementation class DisFollowUserServlet
 */
@WebServlet("/DisFollowUserServlet")
public class DisFollowUserServlet extends HttpServlet {
	
	private String user_id;
	private String dis_follow_username;
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
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		//获取参数
		dis_follow_username = request.getParameter("dis_follow_username");
		if(!checkParameter(response, "dis_follow_username", dis_follow_username, root, pw)) return;
		
		FollowDAO dao = new FollowDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		List<TFollow> list_tfollow;
		TFollow tfollow;
		TUser dis_follow_tuser;
		try {
			dis_follow_tuser = userDAO.getByQuery("username='" + dis_follow_username + "'", 0, 0).get(0);
			list_tfollow = dao.getByQuery("user_id=" + user_id + "and follow_id=" + dis_follow_tuser.getId(), 0, 0);
			if(list_tfollow!=null && list_tfollow.size()>0) {
				//删除该记录
				tfollow = list_tfollow.get(0);
				dao.deleteById(tfollow.getId());
				
			}else{
				
			}
			
			root.success = true;
			root.message = "";
			root.is_follow = 0;
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
		int is_follow;
	}

}
