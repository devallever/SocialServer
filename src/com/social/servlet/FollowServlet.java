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
import com.social.dao.FollowDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TFollow;
import com.social.pojo.TUser;
import com.social.servlet.FansCountServlet.Root;
import com.social.servlet.LikeServlet.CustomeMessage;

/**
 * Servlet implementation class FollowServlet
 */
@WebServlet("/FollowServlet")
public class FollowServlet extends HttpServlet {
	
	private String user_id;
	private String follow_username;
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
		user_id = session.getAttribute("id").toString();
		
		//获取参数
		follow_username = request.getParameter("follow_username");
		if(!checkParameter(response, "follow_username", follow_username, root, pw)) return;
		
		FollowDAO dao = new FollowDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		List<TFollow> list_tfollow;
		TUser follow_user;
		TUser user;
		TFollow tfollow;
		try {
			follow_user = userDAO.getByQuery("username='" + follow_username + "'", 0, 0).get(0);
			user = userDAO.getById(Long.parseLong(user_id));
			list_tfollow = dao.getByQuery("user_id=" + user.getId() + " and follow_id=" + follow_user.getId(), 0, 0);
			if(list_tfollow.size()>0){
				//有记录 删除操作
				//tfollow = dao.getByQuery("user_id=" + user.getId() + " and follow_id=" + follow_user.getId(), 0, 0).get(0);
				//dao.deleteById(tfollow.getId());
				//root.is_follow = 0;
				root.is_follow = 1;
			}else{
				//没记录 添加操作
				tfollow = new TFollow();
				tfollow.setUser(user);
				tfollow.setFollow(follow_user);
				tfollow.setOptlockversion1(Long.parseLong("0"));
				tfollow.setOptlockversion2(Long.parseLong("0"));
				dao.add(tfollow);
				root.is_follow = 1;
				
				JPushClient jpushClient = new JPushClient("ac315c7bded98ea4f4423a50", "24d43240d820aaaf0803626a");
				try {     
		            //PushResult result = jpushClient.sendPush(payload);
					PushResult result;
		            CustomeMessage message = new CustomeMessage();
		        	message.username = user.getUsername();
		        	message.title = "互信";
		            message.content = user.getNickname() + "：关注了你";
		            message.msg_type = "follow";
		            result = jpushClient.sendMessageWithRegistrationID("互信", gson.toJson(message), follow_user.getJpush_registration_id());
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
				
			}
			
			root.success = true;
			root.message = "";
			
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
	
	private static PushPayload buildPushObject_all_all_alert(String[] friend_username,String nickname,String username) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("msg_type", "follow");
		
		return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(friend_username))
                .setNotification(Notification.android(nickname + "关注了你", "", map))
                .build();
    }
	
	class Root{
		boolean success;
		String message;
		int is_follow;
	}
	
	class CustomeMessage{
		String username;
		String title;
		String content;
		String msg_type;
		
	}

}
