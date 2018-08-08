package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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
import com.social.bean.Response;
import com.social.bean.User;
import com.social.dao.UserDAO;
import com.social.pojo.TUser;
import com.social.util.CommonUtils;
import com.social.util.DateTimeUtils;


/**
 * Servlet implementation class NearbyUserListServlet
 */
@WebServlet("/UserListServlet")
public class UserListServlet extends HttpServlet {
	private String str_longitude;
	private String str_latitude;
	
	private PrintWriter pw;
	private Response<List<User>> root;
	
	private String str_page;
	private int page;
	private int startCount;
	private int endCount;
	
	private String selected_min_age="0";
	private String selected_max_age="99";
	private String selected_sex = "全部";
	private String selected_distance = "1000";
	
	
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		pw = response.getWriter();
		root = new Response<>();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
		HttpSession session = request.getSession();
//		if(session == null || session.getAttribute("id") == null){
//			root.message = "未登录";
//			root.success = false;
//			pw.print(gson.toJson(root));
//			pw.close();
//		}
		
		selected_sex = request.getParameter("selected_sex"); 
		if(selected_sex==null) selected_sex = "全部";
		selected_min_age = request.getParameter("selected_min_age");
		if(selected_min_age==null) selected_min_age = "0";
		selected_max_age = request.getParameter("selected_max_age");
		if(selected_max_age==null) selected_max_age = "99";
		selected_distance = request.getParameter("selected_distance");
		if(selected_distance==null) selected_distance = "1000000000000000";
		
		str_page = request.getParameter("page");
		if(str_page == null) str_page = "1";
		page = Integer.valueOf(str_page);
		endCount = page * 1 * 10;
		startCount = endCount - 9;
		
		//查询第n行到第m行记录 
		//第2页
		//n = 21;	//startCount
		//m = 40   	//endCount
		//select * from table1 limit n-1,m-n; 
		
		UserDAO dao = new UserDAO();
		List<TUser> list_tuser;
		List<User> list_user = new ArrayList<>();
		User user;
		try{
			if(selected_sex.equals("全部")){
				list_tuser = dao.getByQuery("age>"+ selected_min_age +" and age<" + selected_max_age + " order by login_time desc", startCount-1, endCount - startCount + 1);
			}else{
				list_tuser = dao.getByQuery("age>"+ selected_min_age +" and age<" + selected_max_age + " and sex='" +  selected_sex +"'" + " order by login_time desc", startCount-1, endCount - startCount + 1);
			}
			
			Date endDate = new Date();
			for(TUser tuser: list_tuser){
				user = new User();
				user.setId(tuser.getId()+"");
				user.setUsername(tuser.getUsername());
				user.setNickname(tuser.getNickname());
				user.setSex(tuser.getSex());
				user.setUser_head_path(tuser.getHeadpath());
				user.setSignature(tuser.getSignature());
				user.setAge(tuser.getAge());
				user.setConstellation(tuser.getConstellation());
				user.setOccupation(tuser.getOccupation());
				user.setIs_vip(tuser.getIs_vip());
				user.setVideo_fee(tuser.getVideo_fee());
				user.setAccetp_video(tuser.getAccept_video());
				Date startDate = tuser.getLogin_time();
				String resultTime = DateTimeUtils.getDatePoor(endDate,startDate);
				user.setLogin_time(resultTime);
				list_user.add(user);
			}
			
			root.setSuccess(true);
			root.setMessage("");
			root.setData(list_user);
			dao.close();
			String rootJson = gson.toJson(root);
			System.out.println("rootJson =======================  " + rootJson);
			pw.print(gson.toJson(root));
			pw.close();
			
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
	}
	
/*	class NearbyUserRoot{
		boolean success;
		String message;
		List<User> user_list;
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
		int video_fee;
		int accetp_video;
		String login_time;
	}*/
	
	private  boolean checkParameter(HttpServletResponse response,String parameterName ,String parameterValue , Response<User> root, PrintWriter pw){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		if (parameterValue == null || parameterValue.length() == 0) {
			root.setSuccess(false);
			root.setMessage("没有指定" + parameterName + "参数");
			response.setContentType("text/html;charset=utf-8");
			try {
				pw = response.getWriter();
			} catch (IOException e) {
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
