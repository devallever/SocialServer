package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.text.TextFlow;

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
import com.social.servlet.NearbyUserListServlet.User;
import com.social.servlet.VisitedForUserServlet.Root;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class FollowUserServlet
 */
@WebServlet("/FollowUserServlet")
public class FollowUserServlet extends HttpServlet {
	
	private String str_longitude;
	private String str_latitude;
	
	private PrintWriter pw;
	private Root root;
	
	private String str_page;
	private int page;
	private int startCount;
	private int endCount;
	
	private String username;
	
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
//			root.success = false;
//			pw.print(gson.toJson(root));
//			pw.close();
//		}
		String user_id = session.getAttribute("id").toString();
		
		//获取参数
		str_longitude = request.getParameter("longitude");
		if(!checkParameter(response, "longitude", str_longitude, root, pw)) return;
		//longitude = Double.parseDouble(str_latitude);
		
		str_latitude = request.getParameter("latitude");
		if(!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		
		username = request.getParameter("username");
		
		str_page = request.getParameter("page");
		if(str_page == null) str_page = "1";
		page = Integer.valueOf(str_page);
		endCount = page * 1 * 10;
		startCount = endCount - 9;
		
		FollowDAO dao = new FollowDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		List<TFollow> list_follow;
		List<User> list_user = new ArrayList<FollowUserServlet.User>();
		TUser tfollow_user;
		User user;
		String id;
		try {
			
			if(username == null){
				id = user_id;
			}else{
				id = userDAO.getByQuery("username='" + username + "'", 0, 0).get(0).getId()+"";
			}
			
			list_follow = dao.getByQuery("user_id=" + id + "order by id desc", 0, 0);
			for(TFollow tfollow: list_follow){
				tfollow_user = tfollow.getFollow();
				user = new User();
				user.id = tfollow_user.getId()+"";
				user.username = tfollow_user.getUsername();
				user.nickname = tfollow_user.getNickname();
				user.user_head_path = tfollow_user.getHeadpath();
				user.age = tfollow_user.getAge();
				user.sex = tfollow_user.getSex();
				user.signature = tfollow_user.getSignature();
				user.occupation = tfollow_user.getOccupation();
				double distance = CommonUtils.getDistance(str_latitude, str_longitude, String.valueOf(tfollow_user.getLatitude()), String.valueOf(tfollow_user.getLongitude()));
				String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
				user.distance = Double.parseDouble(dis_with_two);
				user.is_vip = tfollow_user.getIs_vip();
				list_user.add(user);
			}
			
			
			List<User> list_user_for_client = new ArrayList<User>();
			for(int i=startCount-1; i<(startCount-1+10); i++){
				if(i<list_user.size()) list_user_for_client.add(list_user.get(i));
				
			}
			
			root.success = true;
			root.message = "";
			root.list_follow_user = list_user_for_client;
			
			
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
		List<User> list_follow_user;
	}
	class User{
		String id;
		String username;
		String nickname;
		String sex;
		double distance;
		String user_head_path;
		String signature;
		int age;
		String occupation;
		int is_vip;
	}

}
