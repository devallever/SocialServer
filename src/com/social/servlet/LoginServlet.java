package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.text.StrLookup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.AutoreactionDAO;
import com.social.dao.RecommendDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TAutoreaction;
import com.social.pojo.TRecommend;
import com.social.pojo.TUser;



/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	
	private String qq_open_id;
	
	private String username;
	private String password;
	private Root root;
	private PrintWriter pw;
	private String jpush_registration_id;
	
	private String str_longitude;
	private String str_latitude;
	private String address;
	
	
	
	
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
		
		//获取参数
		username = request.getParameter("username");
		if(!checkParameter(response, "username", username, root, pw)) return;
		
		 password = request.getParameter("password");
		if(!checkParameter(response, "password", password, root, pw)) return;
		
		//获取参数
		str_longitude = request.getParameter("longitude");
		if(!checkParameter(response, "longitude", str_longitude, root, pw)) return;
		//longitude = Double.parseDouble(str_latitude);
				
		str_latitude = request.getParameter("latitude");
		if(!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		
		address = request.getParameter("address");
		
		jpush_registration_id = request.getParameter("jpush_registration_id");
		if(jpush_registration_id==null) jpush_registration_id = "";
		//if(!checkParameter(response, "jpush_registration_id", jpush_registration_id, root, pw)) return;
		
		//latitude = Double.parseDouble(str_latitude);
		
		UserDAO dao = new UserDAO();
		AutoreactionDAO autoreactionDAO = new AutoreactionDAO(dao.getSession());
		RecommendDAO recommendDAO = new RecommendDAO(dao.getSession());
		List<TAutoreaction> list_tautoreaction;
		List<TUser> ll;
		User user = new User();
		List<TRecommend> list_trecommend;
		
		TUser tu = null;
		
		try {
			//new add----------------------------
			ll = dao.getByQuery("username = '" + username + "'", 0, 0);
			System.out.println("users_size = " + ll.size());
			if(ll==null || ll.size()==0){
				root.seccess =false;
				root.message = "账号不存在";
				root.user = null;
				pw.print(gson.toJson(root));
				pw.close();
				return;
			}else{
				tu = ll.get(0);
			}
			
			ll= null;
			
			//new add----------------------------
			if(password.equals(tu.getPassword())){
				
				user.id = String.valueOf(tu.getId());
				user.username = tu.getUsername();
				user.nickname = tu.getNickname();
				user.longitude = tu.getLongitude();
				user.latiaude = tu.getLatitude();
				user.phone = tu.getPhone();
				user.user_head_path = tu.getHeadpath();
				user.signature = tu.getSignature();
				user.email = tu.getEmail();
				user.city = tu.getCity();
				user.sex = tu.getSex();
				user.age = tu.getAge();
				user.occupation = tu.getOccupation();
				user.constellation = tu.getConstellation();
				user.hight = tu.getHight();
				user.weight = tu.getWeight();
				user.figure = tu.getFigure();
				user.emotion = tu.getEmotion();
				user.is_vip = tu.getIs_vip();
				user.onlinestate = tu.getOnlinestate();
				//user.user_id = String.valueOf(tu.getId());
				list_tautoreaction = autoreactionDAO.getByQuery("user_id=" + tu.getId(), 0, 0);
				if (list_tautoreaction.size()>0) {
					user.autoreaction = list_tautoreaction.get(0).getContent();
				}else{
					user.autoreaction = "我在忙，待会儿回复。";
				}
				autoreactionDAO.close();
				//
				list_trecommend = recommendDAO.getByQuery("recommended_id=" + tu.getId(), 0, 0);
				if(list_trecommend.size()>0){
					//所登录的账号已被推荐过，，已完成新手任务
					user.is_recommended = 1;
				}else{
					//所登录的账号没有被推荐过，，，未完成新手任务
					user.is_recommended = 0;
				}
				recommendDAO.close();
				
				root.seccess = true;
				root.message = "";
				root.user = user;
				dao.close();
				
				//更新user表的经纬度和登录次数
				dao = new UserDAO();
				TUser tuser = dao.getById(tu.getId());
				tuser.setLongitude(Double.parseDouble(str_longitude));
				tuser.setLatitude(Double.parseDouble(str_latitude));
				tuser.setJpush_registration_id(jpush_registration_id);
				if (address!=null) {
					tuser.setAddress(address);
				}
				long login_count = tuser.getLogin_count();
				login_count ++;
				tuser.setLogin_count(login_count);
				dao.update(tuser);
				dao.close();
				

				HttpSession session = request.getSession(true);
				session.setAttribute("id", user.id);
				System.out.println("Session.id = " + session.getId());
				System.out.println("session.getAttribute(id) = "+ session.getAttribute("id"));

				System.out.println("Session.id = " + session.getId());
				System.out.println("session.getAttribute(id) = " + session.getAttribute("id"));
				
//				
				Cookie id = new Cookie("id", user.id.toString());
				response.addCookie(id);
				root.session_id = session.getId();
				pw.print(gson.toJson(root));
				//return gson.toJson(result);
			}else{
				root.seccess = false;
				root.message = "密码错误";
				root.user = null;
				pw.print(gson.toJson(root));
				return;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pw.close();
		
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
		String imagepath;
		double longitude;
		double latiaude;
		String phone;
		String email;
		String user_head_path;
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
		int is_vip;
		int is_recommended;
		String autoreaction;
		String onlinestate;
	}
	
	private  boolean checkParameter(HttpServletResponse response,String parameterName ,String parameterValue , Root root, PrintWriter pw){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		if (parameterValue == null || parameterValue.length() == 0) {
			root.seccess = false;
			root.message = "没有指定" + parameterName + "参数";
			response.setContentType("text/html;charset=utf-8");
			try {
				pw = response.getWriter();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pw.print(gson.toJson(root));
			pw.close();
			return false;
		}
		System.out.println(parameterValue);
		return true;
	}

}
