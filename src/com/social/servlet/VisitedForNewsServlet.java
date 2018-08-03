package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.social.dao.VisitedNewsDAO;
import com.social.pojo.TUser;
import com.social.pojo.TVisitednews;
import com.social.util.CommonUtils;
import com.social.util.DateTimeUtils;

/**
 * Servlet implementation class VisitedForNewsServlet
 */
@WebServlet("/VisitedForNewsServlet")
public class VisitedForNewsServlet extends HttpServlet {
	
	private String str_longitude;
	private String str_latitude;
	
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
		
		str_page = request.getParameter("page");
		if(str_page == null) str_page = "1";
		page = Integer.valueOf(str_page);
		endCount = page * 1 * 10;
		startCount = endCount - 9;
		
		
		VisitedNewsDAO dao = new VisitedNewsDAO();
		List<TVisitednews> list_tnews_visited;
		
		List<User> list_visited_news_user = new ArrayList<User>();
		User user;
		try {

			
			list_tnews_visited = dao.getByQuery("user_id=" + user_id+ "order by date desc", 0, 0);
			
			Date p_date;
			Date date_date = new Date();
			String diaplay_date;
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
				
				p_date = tVisitednews.getDate();
				//date_date = 
				diaplay_date = DateTimeUtils.getDisplayTime(p_date, date_date);
				user.date = diaplay_date;
				
				list_visited_news_user.add(user);
			}
			
			
			List<User> list_user_for_client = new ArrayList<User>();
			for(int i=startCount-1; i<(startCount-1+10); i++){
				if(i<list_visited_news_user.size()) list_user_for_client.add(list_visited_news_user.get(i));
				
			}
			
			root.visitednews_list = list_user_for_client;
			root.success = true;
			root.message = "";
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
		//List<User> visiteduser_list;
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
		String date;
	}

}
