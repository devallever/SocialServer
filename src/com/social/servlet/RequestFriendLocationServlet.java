package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
/**
 * Servlet implementation class RequestFriendLocationServlet
 */
@WebServlet("/RequestFriendLocationServlet")
public class RequestFriendLocationServlet extends HttpServlet {
	private String from_user_id;
	private String to_username;
	
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
		from_user_id = session.getAttribute("id").toString();
		
		
		//获取参数
		to_username = request.getParameter("to_username");
		if(!checkParameter(response, "to_username", to_username, root, pw)) return;
		
		UserDAO dao = new UserDAO();
		TUser tuser_from;
		TUser tuser_to;
		try{
			tuser_to = dao.getByQuery("username='" + to_username + "'", 0, 0).get(0);
			tuser_from = dao.getById(Long.parseLong(from_user_id)); 
			
			JPushClient jpushClient = new JPushClient("ac315c7bded98ea4f4423a50", "24d43240d820aaaf0803626a");
			//PushPayload payload = buildPushObject_all_all_alert(tuser.getUsername(),tuser.getNickname());
			
			try {     
	            //PushResult result = jpushClient.sendPush(payload);
				PushResult result;
	            CustomeMessage message = new CustomeMessage();
	        	message.username = tuser_from.getUsername();
	        	message.title = "互信";
	            message.content = tuser_from.getNickname() + "：请求获取你的位置信息";
	            message.msg_type = "request_friend_location";
	            result = jpushClient.sendMessageWithRegistrationID("互信", gson.toJson(message), tuser_to.getJpush_registration_id());
	            System.out.println("Got result - " + result);

	        } catch (APIConnectionException e) {
	            // Connection error, should retry later
	        	System.out.println("Connection error, should retry later\n" +  e);

	        } catch (APIRequestException e) {
	            // Should review the error, and fix the request
	        	System.out.println("Should review the error, and fix the request\n" +  e);
	        	System.out.println("HTTP Status: " + e.getStatus());
	        	System.out.println("Error Code: " + e.getErrorCode());
	        	System.out.println("Error Message: " + e.getErrorMessage());
	        }
			
			
			root.message = "";
			root.success = true;
			
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
		}catch (Exception e) {
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
	}
	
	
	class CustomeMessage{
		String username;
		String title;
		String content;
		String msg_type;
	}

}
