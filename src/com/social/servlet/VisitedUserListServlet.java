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
import com.social.dao.VisitedNewsDAO;
import com.social.dao.VisitedUserDAO;
import com.social.pojo.TUser;
import com.social.pojo.TVisitednews;
import com.social.pojo.TVisiteduser;
import com.social.servlet.SearchUserServlet.User;
import com.social.util.CommonUtils;


/**
 * Servlet implementation class VisitedUserListServlet
 */
@WebServlet("/VisitedUserListServlet")
public class VisitedUserListServlet extends HttpServlet {
	
	private String str_longitude;
	private String str_latitude;
	
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
		}
		String user_id = session.getAttribute("id").toString();
		
		//获取参数
		str_longitude = request.getParameter("longitude");
		if(!checkParameter(response, "longitude", str_longitude, root, pw)) return;
		//longitude = Double.parseDouble(str_latitude);
		
		str_latitude = request.getParameter("latitude");
		if(!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		
		
		VisitedUserDAO dao = new VisitedUserDAO();
		VisitedNewsDAO visitedNewsDAO = new VisitedNewsDAO(dao.getSession());
		List<TVisiteduser> list_tuser_visited;
		List<TVisitednews> list_tnews_visited;
		
		List<User> list_visited_user_user = new ArrayList<VisitedUserListServlet.User>();
		List<User> list_visited_news_user = new ArrayList<VisitedUserListServlet.User>();
		User user;
		try {
			list_tuser_visited = dao.getByQuery("user_id=" + user_id, 0, 0);
			for(TVisiteduser tVisiteduser: list_tuser_visited){
				TUser tuser = tVisiteduser.getWho();
				user = new User();
				user.id = String.valueOf(tuser.getId());
				user.username = String.valueOf(tuser.getUsername());
				user.nickname = tuser.getNickname();
				user.user_head_path = tuser.getHeadpath();
				double distance = CommonUtils.getDistance(str_latitude, str_longitude, String.valueOf(tuser.getLatitude()), String.valueOf(tuser.getLongitude()));
				String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
				user.distance = Double.parseDouble(dis_with_two);
				user.signature = tuser.getSignature();
				user.age = tuser.getAge();
				user.sex = tuser.getSex();
				user.occupation = tuser.getOccupation();
				user.constellation = tuser.getConstellation();
				user.is_vip = tuser.getIs_vip();
				list_visited_user_user.add(user);
			}
			
			
			list_tnews_visited = visitedNewsDAO.getByQuery("user_id=" + user_id, 0, 0);
			for(TVisitednews tVisitednews: list_tnews_visited){
				TUser tuser = tVisitednews.getWho();
				user = new User();
				user.id = String.valueOf(tuser.getId());
				user.username = String.valueOf(tuser.getUsername());
				user.nickname = tuser.getNickname();
				user.user_head_path = tuser.getHeadpath();
				double distance = CommonUtils.getDistance(str_latitude, str_longitude, String.valueOf(tuser.getLatitude()), String.valueOf(tuser.getLongitude()));
				String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
				user.distance = Double.parseDouble(dis_with_two);
				user.signature = tuser.getSignature();
				user.age = tuser.getAge();
				user.sex = tuser.getSex();
				user.occupation = tuser.getOccupation();
				user.constellation = tuser.getConstellation();
				user.is_vip = tuser.getIs_vip();
				list_visited_news_user.add(user);
			}
			
			
			root.visitednews_list = list_visited_news_user;
			root.visiteduser_list = list_visited_user_user;
			root.visitednews_count = list_visited_news_user.size();
			root.visiteduser_count = list_visited_user_user.size();
			root.success = true;
			root.message = "";
			visitedNewsDAO.close();
			dao.close();
			pw.print(gson.toJson(root));
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
			//pw.close();
			return false;
		}
		System.out.println(parameterValue);
		return true;
	}
	
	class Root{
		boolean success;
		String message;
		int visiteduser_count;
		int visitednews_count;
		List<User> visiteduser_list;
		List<User> visitednews_list;
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
		String constellation;
		String occupation;
		int is_vip;
	}

}
