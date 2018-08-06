package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.text.StrLookup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.RecommendDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TRecommend;
import com.social.pojo.TUser;

/**
 * Servlet implementation class ConnectionServlet
 */
@WebServlet("/ConnectionServlet")
public class ConnectionServlet extends HttpServlet {
	private PrintWriter pw;
	private Root root;
	
	private String jpush_registration_id;
	
	private String str_longitude;
	private String str_latitude;
	private String address;
	
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		pw = response.getWriter();
		root = new Root();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		HttpSession session = request.getSession();
		if (session == null || session.getAttribute("id") == null) {
			root.message = "未登录";
			root.code = -1;
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}

		// 获取参数
		str_longitude = request.getParameter("longitude");
		if(str_longitude==null) str_longitude = "0";
		//if (!checkParameter(response, "longitude", str_longitude, root, pw))return;
		// longitude = Double.parseDouble(str_latitude);

		str_latitude = request.getParameter("latitude");
		if(str_latitude==null) str_latitude = "0";
		//if (!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		
		address = request.getParameter("address");
		if(address==null) address = "广州市天河";
		
		
		jpush_registration_id = request.getParameter("jpush_registration_id");
		if(jpush_registration_id==null) jpush_registration_id = "";
		//if(!checkParameter(response, "jpush_registration_id", jpush_registration_id, root, pw)) return;

		String user_id = session.getAttribute("id").toString();
		UserDAO dao = new UserDAO();
		RecommendDAO recommendDAO = new RecommendDAO(dao.getSession());
		TUser tuser = null;
		List<TRecommend> list_trecommend;
		try {
			tuser = dao.getById(Long.parseLong(user_id));
			if(tuser == null){
				root.code = -1;
				root.success = false;
				root.message = "";
				pw.print(gson.toJson(root));
				pw.close();
				recommendDAO.close();
				dao.close();
				return;
			}
			tuser.setLongitude(Double.valueOf(str_longitude));
			tuser.setLatitude(Double.valueOf(str_latitude));
			tuser.setJpush_registration_id(jpush_registration_id);
			tuser.setLogin_time(new Date());
			tuser.setAddress(address);
			tuser = dao.update(tuser);
			if (tuser != null)
				root.is_vip = tuser.getIs_vip();
			
			list_trecommend = recommendDAO.getByQuery("recommended_id=" + tuser.getId(), 0, 0);
			if(list_trecommend.size()>0){
				root.is_recommended = 1;
			}else{
				root.is_recommended = 0;
			}
			
			root.code = 1;
			root.success = true;
			root.message = "";
			pw.print(gson.toJson(root));
			pw.close();
			recommendDAO.close();
			dao.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// String user_id = session.getAttribute("id").toString();

	}
	
	@SuppressWarnings("unused")
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
			pw.close();
			return false;
		}
		System.out.println(parameterValue);
		return true;
	}
	

	class Root {
		boolean success;
		int code;
		String message;
		int is_vip;
		int is_recommended;
	}

}
