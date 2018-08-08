package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;




import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;











import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.bean.Response;
import com.social.dao.UserDAO;
import com.social.pojo.TUser;


/**
 * Servlet implementation class PullRefresherServlet
 */
@WebServlet("/PullRefresherServlet")
public class PullRefresherServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Response<String[]> root;
	
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
		System.out.println(session.getId());
		if(session == null || session.getAttribute("id") == null){
			root.setMessage("未登录");
			root.setSuccess(false);
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		
		List<TUser> list_tuser;
		UserDAO dao = new UserDAO();
		TUser tuser = null;
		try {
			list_tuser = dao.getByQuery("id !=" + user_id + " and jpush_registration_id!=''", 0, 0);
			tuser = dao.getById(Long.parseLong(user_id));
			
			if(tuser == null){
				root.setSuccess(true);
				root.setMessage("");
				root.setData(null);
				
				pw.print(gson.toJson(root));
				pw.close();
				return;
			}
		
			
			dao.close();
			
			String[] arr_username = new String[list_tuser.size()];
			String[] arr_nickname = new String[list_tuser.size()];
			String[] arr_registration_ids = new String[list_tuser.size()];
			for(int i=0;i<list_tuser.size();i++){
				arr_username[i] = list_tuser.get(i).getUsername();
				arr_nickname[i] = list_tuser.get(i).getNickname();
				arr_registration_ids[i] = list_tuser.get(i).getJpush_registration_id();
				
				
			}
			JPushClient jpushClient = new JPushClient("ac315c7bded98ea4f4423a50", "24d43240d820aaaf0803626a");
			PushPayload payload = buildPushObject_all_all_alert(arr_username,tuser.getNickname(),tuser.getUsername());
			try {
	            //PushResult result = jpushClient.sendPush(payload);
	            
	            CustomeMessage message = new CustomeMessage();
	            message.username = tuser.getUsername();
	            message.title = "互信";
	            message.content = tuser.getNickname() + "：刷新了附近人";
	            message.msg_type = "refresh_user";
	            
	            //PushResult result = jpushClient.sendMessageAll(gson.toJson(message));
	            PushResult result = jpushClient.sendMessageWithRegistrationID("互信", gson.toJson(message), arr_registration_ids);
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
			
			
			root.setSuccess(true);
			root.setMessage("");
			root.setData(arr_nickname);
			
			pw.print(gson.toJson(root));
			pw.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	
	private static PushPayload buildPushObject_all_all_alert(String[] arr_username,String nickname,String username) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("msg_type", "refresh_user");
		
		return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(arr_username))
                .setNotification(Notification.android(nickname + "：刷新了附近人", "", map))
                .build();
    }
	
	
	class Root{
		boolean success;
		String message;
		String[] arr_username;
	}
	
	class CustomeMessage{
		String username;
		String title;
		String content;
		String msg_type;
	}

}
