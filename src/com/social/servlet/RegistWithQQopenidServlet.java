package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javassist.bytecode.Opcode;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.easemob.server.example.api.IMUserAPI;
import com.easemob.server.example.comm.ClientContext;
import com.easemob.server.example.comm.EasemobRestAPIFactory;
import com.easemob.server.example.comm.body.IMUserBody;
import com.easemob.server.example.comm.wrapper.BodyWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.FriendgroupDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TFriendgroup;
import com.social.pojo.TUser;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class RegistWithQQopenidServlet
 */
@WebServlet("/RegistWithQQopenidServlet")
@MultipartConfig
public class RegistWithQQopenidServlet extends HttpServlet {
	private static final String HX_DEFAULT_PASSWORD = "123456";
	
	private String username;
	private String qq_open_id;
	private String nickname;
	private String password;
	private String phone;
	private String sex;
	private String age;
//	private String email;
//	private String longitude_str;
//	private String latitude_str;
//	private double longitude;
//	private double latitude;
	private Part part;
	private String city;
	private String recommend_name;//推荐人互信号
	private String jpush_registration_id;
	
	private PrintWriter pw;
	private Root root;
	
	private TUser register_user;
	private User user;
	
	
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
		
		nickname = request.getParameter("nickname");
		if(!checkParameter(response, "nickname", nickname, root, pw)) return;
		
		qq_open_id = request.getParameter("qq_open_id");
		if(!checkParameter(response, "qq_open_id", qq_open_id, root, pw)) return;
		
		 password = request.getParameter("password");
		 if(password==null) password = "";
		
		 phone = request.getParameter("phone");
			if(phone==null) phone = "";
			
		sex = request.getParameter("sex");
		if(sex==null || sex.length()==0) sex = "女";
		//if(!checkParameter(response, "sex", sex, root, pw)) return;
			
		age = request.getParameter("age");
		if(age==null || age.length()==0) age = "18";
		
//		longitude_str = request.getParameter("longitude");
//		if(longitude_str==null) longitude = 0.00;
//		else longitude = Double.parseDouble(longitude_str);
//		
//		 latitude_str = request.getParameter("latitude");
//		if(latitude_str==null) latitude = 0.00;
//		else latitude = Double.parseDouble(latitude_str);
		
		
//		
//		 email = request.getParameter("email");
//		if(email==null) email = "";
//		
		 city = request.getParameter("city");
		if(city==null || city.equals("")) city = "北京 海淀";
		
		recommend_name = request.getParameter("recommend_name");
		if(recommend_name == null || recommend_name.length()==0) recommend_name= "xm";
		System.out.println("recommend_name = " + recommend_name);
		
		jpush_registration_id = request.getParameter("jpush_registration_id");
		if(jpush_registration_id==null) jpush_registration_id = "";
		//if(!checkParameter(response, "jpush_registration_id", jpush_registration_id, root, pw)) return;
		
		
		part = request.getPart("photo");
		if(!checkParameterPart(response, "photo", part, root, pw)) return;
		
		//检验用户名合法性
		UserDAO dao = new UserDAO();
		//FeeDAO feeDAO = new FeeDAO(dao.getSession());
		FriendgroupDAO friendgroupDAO = new FriendgroupDAO(dao.getSession());
		
		List<TUser> list_user;
		try{
			list_user = dao.getByQuery("username = '" + username + "'", 0, 0);
			if(list_user!=null && list_user.size()>0){
				root.seccess = false;
				root.message = "用户名已被注册";
				root.user = null;
				pw.print(gson.toJson(root));
				pw.close();
				return;
			}
		}catch(Exception e){
			
		}
		
//		InputStream is = part.getInputStream();
//		String root_path = this.getServletContext().getRealPath("/");
//		String path = root_path + "/images/head/" + username+ ".jpg"; 
//		FileOutputStream fos = new FileOutputStream(path);
//		byte[] b = new byte[1024];
//		int count = 0;
//		while ((count = is.read(b)) > 0) {
//			fos.write(b, 0, count);
//		}
//		fos.close();
		
		
		String filepath = getServletContext().getRealPath("") + "\\images\\head";
		String filename = username + ".jpg";
		byte[] b = CommonUtils.getImgByte(part);
		CommonUtils.byteToFile(b, filepath, filename);
		
		System.out.println(getServletContext().toString());
		
		TUser new_user = new TUser();
		new_user.setUsername(username);
		new_user.setQq_open_id(qq_open_id);
		new_user.setPassword(HX_DEFAULT_PASSWORD);
		new_user.setNickname(nickname);
		new_user.setHeadpath("/images/head/" + filename);
		new_user.setPhone(phone);
		new_user.setOptlockversion1(Long.valueOf("1"));
		new_user.setEmail("");
		new_user.setLatitude(0.0);
		new_user.setLongitude(0.0);
		new_user.setSex(sex);
		new_user.setAge(Integer.valueOf(age));
		new_user.setCity(city);
		new_user.setSignature("他很懒，什么也没写。");
		new_user.setOccupation("学生");
		new_user.setConstellation("白羊座");
		new_user.setHight("160");
		new_user.setWeight("45");
		new_user.setFigure("匀称");
		new_user.setEmotion("单身");
		new_user.setIs_vip(0);
		new_user.setCredit(0);
		new_user.setVisited_count(0);
		new_user.setJpush_registration_id(jpush_registration_id);
		new_user.setMessagecount(10);
		new_user.setOnlinestate("在线");
		new_user.setLogin_count(0);
		new_user.setAddress(city);
		new_user.setLogin_time(new Date());
		new_user.setVideo_fee(1);
		new_user.setVoice_fee(1);
		new_user.setAccept_video(0);
		
		try {
//			register_user =  dao.add(new_user);
//			
//			TFee tfee = new TFee();
//			tfee.setAccept_video(0);
//			tfee.setUser(register_user);
//			tfee.setVideo_fee(1);
//			tfee.setVoice_fee(1);
//			tfee.setOptlockversion1(Long.parseLong("0"));
//			tfee.setOptlockversion2(Long.parseLong("0"));	
//			feeDAO.add(tfee);
			
			
			TFriendgroup tFriendgroup = new TFriendgroup();
			tFriendgroup.setFriendgroup_name("我的好友");
			tFriendgroup.setUser(register_user);
			tFriendgroup.setOptlockversion1(Long.parseLong("0"));
			tFriendgroup.setOptlockversion2(Long.parseLong("0"));
			friendgroupDAO.add(tFriendgroup);
			
			
			EasemobRestAPIFactory factory = ClientContext.getInstance().init(ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
			IMUserAPI imuser = (IMUserAPI)factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
			BodyWrapper userBody = new IMUserBody(register_user.getUsername(), HX_DEFAULT_PASSWORD, register_user.getNickname());
			Object imuserObject = imuser.createNewIMUserSingle(userBody);
			System.out.println(imuserObject.toString());
			
			
			user = new User();
			user.id = String.valueOf(register_user.getId());
			user.username = register_user.getUsername();
			user.nickname = register_user.getNickname();
			user.longitude = register_user.getLongitude();
			user.latiaude = register_user.getLatitude();
			user.phone = register_user.getPhone();
			user.user_head_path = register_user.getHeadpath();
			user.signature = register_user.getSignature();
			user.email = register_user.getEmail();
			user.city = register_user.getCity();
			user.sex = register_user.getSex();
			user.qq_open_id = register_user.getQq_open_id();
			root.seccess = true;
			root.message = "";
			root.user = user;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		System.out.println("user.name = " + register_user.getUsername());
//		
//		HttpSession session = request.getSession(true);
//		session.setAttribute("id", user.id);
//		
//		System.out.println("Session.id = " + session.getId());
//		System.out.println("session.getAttribute(id) = " + session.getAttribute("id"));
//		
//		
//		Cookie id = new Cookie("id", user.id.toString());
//		response.addCookie(id);
		//root.session_id = session.getId();
		pw.print(gson.toJson(root));
		pw.close();
		//feeDAO.close();
		friendgroupDAO.close();
		dao.close();
		
		//注册成功为推荐账号增加信用
		try {
			dao = new UserDAO();
			list_user = dao.getByQuery("username = '" + recommend_name + "'", 0, 0);
			if(list_user!=null && list_user.size()>0){
				//该用户存在
				System.out.println("被推荐用户存在");
				TUser tuser = list_user.get(0);
				int credit = tuser.getCredit();
				credit = credit + 1;
				tuser.setCredit(credit);
				
				dao.update(tuser);
				dao.close();
			}else{
				System.out.println("被推荐用户不存在");
				TUser tuser = dao.getByQuery("username = 'xm'", 0, 0).get(0);
				int credit = tuser.getCredit();
				credit = credit + 1;
				tuser.setCredit(credit);
				dao.update(tuser);
				dao.close();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	class Root{
		boolean seccess;
		String message;
		//String session_id;
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
		String occupation;
		String constellation;
		String qq_open_id;
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
	
	
	private boolean checkParameterPart(HttpServletResponse response,String parameterName, Part parameterValue, Root root, PrintWriter pw)
			throws IOException {
		System.out.println(parameterName + " = " + parameterValue);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		if (parameterValue == null) {
			root.seccess = false;
			root.message = "请选择文件";
			response.setContentType("text/html;charset=utf-8");
			pw = response.getWriter();
			pw.print(gson.toJson(root));
			pw.close();
			return false;
		}
		return true;
	}
}
