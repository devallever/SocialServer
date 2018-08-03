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
 * Servlet implementation class ModifyUserDataServlet
 */
@WebServlet("/ModifyUserDataServlet")
public class ModifyUserDataServlet extends HttpServlet {
	private String user_id;
	private String nickname;
	//private String phone;
	private String email;
	private String longitude_str;
	private String latitude_str;
	private String city;
	private String sex;
	private String signature;
	private String str_age;
	private String occupation;
	private String constellation;
	private String hight;
	private String weight;
	private String figure;
	private String emotion;
	
	
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
			root.seccess = false;
			root.user = null;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		//获取参数
		
		nickname = request.getParameter("nickname");
		if(nickname==null) nickname = "";
//		
//		phone = request.getParameter("phone");
//		if(phone==null) phone = "";
//		
		email = request.getParameter("email");
		if(email==null) email = "";
//		
//		longitude_str = request.getParameter("longitude");
//		if(longitude_str==null) longitude_str = "0";
//		
//		latitude_str = request.getParameter("latitude");
//		if(latitude_str==null) latitude_str = "0";
		
		city = request.getParameter("city");
		if(city==null) city = "";
		
		str_age = request.getParameter("age");
		if(str_age==null) str_age = "0";
		
		occupation = request.getParameter("occupation");
		if(occupation==null) occupation = "学生";
		
		constellation = request.getParameter("constellation");
		if(constellation==null) constellation = "白羊座";
		
		sex = request.getParameter("sex");
		if(sex==null) sex = "男";
		
		signature = request.getParameter("signature");
		if(signature==null) signature = "他很懒，什么也没写";
		
		hight = request.getParameter("hight");
		if(hight==null) hight = "160";
		
		weight = request.getParameter("weight");
		if(weight==null) weight = "45";
		
		figure = request.getParameter("figure");
		if(figure==null) figure = "匀称";
		
		emotion = request.getParameter("emotion");
		if(emotion==null) emotion = "单身";
		
		
//		user_id = request.getParameter("user_id");
//		if(!checkParameter(response, "user_id", user_id, root, pw)) return;
		
		TUser tuser;
		
		
		UserDAO dao = new UserDAO();
		TUser result_tuser = new TUser();
		User user = new User();
		try {
			tuser = dao.getById(Long.parseLong(user_id));
			if(!nickname.equals("")) tuser.setNickname(nickname);
			//if(!phone.equals("")) tuser.setPhone(phone);
			if(!email.equals("")) tuser.setEmail(email);
			if(!city.equals("")) tuser.setCity(city);
			if(!sex.equals("")) tuser.setSex(sex);
			if(!signature.equals("")) tuser.setSignature(signature);
			if(!str_age.equals("")) tuser.setAge(Integer.parseInt(str_age));
			if(!occupation.equals("")) tuser.setOccupation(occupation);
			if(!constellation.equals("")) tuser.setConstellation(constellation);
			if(!hight.equals("")) tuser.setHight(hight);
			if(!weight.equals("")) tuser.setWeight(weight);
			if(!figure.equals("")) tuser.setFigure(figure);
			if(!emotion.equals("")) tuser.setEmotion(emotion);
			result_tuser = dao.update(tuser);
			
			user.id = String.valueOf(result_tuser.getId());
			user.username = result_tuser.getUsername();
			user.nickname = result_tuser.getNickname();
			user.user_head_path = result_tuser.getHeadpath();
			user.longitude = result_tuser.getLongitude();
			user.latiaude = result_tuser.getLatitude();
			user.phone = result_tuser.getPhone();
			user.email = result_tuser.getEmail();
			user.city = result_tuser.getCity();
			user.sex = result_tuser.getSex();
			user.age = result_tuser.getAge();
			user.occupation = result_tuser.getOccupation();
			user.constellation = result_tuser.getConstellation();
			user.signature = result_tuser.getSignature();
			user.hight = result_tuser.getHight();
			user.weight = result_tuser.getWeight();
			user.figure = result_tuser.getFigure();
			user.emotion = result_tuser.getEmotion();
			root.seccess = true;
			root.message = "";
			root.user = user;
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	class Root{
		boolean seccess;
		String message;
		String session_id;
		User user;
	}
	
	class User{
		String id;
		String username;
		String nickname;
		double longitude;
		double latiaude;
		String phone;
		String user_head_path;
		String email;
		String signature;
		String city;
		String sex;
		int age;
		String occupation;
		String constellation;
		String hight;
        String weight;
        String figure;
        String emotion;
	}
	
	
//	private  boolean checkParameter(HttpServletResponse response,String parameterName ,String parameterValue , Root root, PrintWriter pw){
//		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//		if (parameterValue == null || parameterValue.length() == 0) {
//			root.seccess = false;
//			root.message = "没有指定" + parameterName + "参数";
//			response.setContentType("text/html;charset=utf-8");
//			try {
//				pw = response.getWriter();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			pw.print(gson.toJson(root));
//			pw.close();
//			return false;
//		}
//		System.out.println(parameterValue);
//		return true;
//	}

}
