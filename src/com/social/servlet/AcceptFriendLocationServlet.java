package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.FriendDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TFriend;
import com.social.pojo.TUser;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;


/**
 * Servlet implementation class AcceptFriendLocationServlet
 */
@WebServlet("/AcceptFriendLocationServlet")
public class AcceptFriendLocationServlet extends HttpServlet {
	private String from_user_id;//我
	private String to_username;//需要推送消息的用户、原来请求用户
	private PrintWriter pw;
	private Root root;
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	@SuppressWarnings("null")
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
		
		FriendDAO dao = new FriendDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		TUser from_tuser;
		TUser to_tuser;
		
		TFriend tfriend;
		List<TFriend> list_tfriend = null;
		try {
			to_tuser = userDAO.getByQuery("username='" + to_username + "'", 0, 0).get(0);
			from_tuser = userDAO.getById(Long.parseLong(from_user_id));
			userDAO.close();
			
			list_tfriend = dao.getByQuery("user_id=" + from_tuser.getId() + " and friend_id=" + to_tuser.getId(), 0, 0);
			if (list_tfriend ==null &list_tfriend.size()==0) {
				
			}else{
				tfriend = list_tfriend.get(0);
				tfriend.setShow_location(1);
				dao.update(tfriend);
				dao.close();
				
				dao = new FriendDAO();
				list_tfriend = null;
				list_tfriend = dao.getByQuery("user_id=" + to_tuser.getId() + " and friend_id=" + from_tuser.getId(), 0, 0);
				if (list_tfriend == null && list_tfriend.size()==0) {
					
				}else{
					tfriend = list_tfriend.get(0);
					tfriend.setShow_location(1);
					dao.update(tfriend);
					dao.close();
				}
				
				root.message = "";
				root.success = true;
				
				pw.print(gson.toJson(root));
				pw.close();
				
				
				try {     
					JPushClient jpushClient = new JPushClient("ac315c7bded98ea4f4423a50", "24d43240d820aaaf0803626a");
		            //PushResult result = jpushClient.sendPush(payload);
					PushResult result;
		            CustomeMessage message = new CustomeMessage();
		        	message.username = from_tuser.getUsername();
		        	message.title = "互信";
		            message.content = from_tuser.getNickname() + "同意你的请求.";
		            message.msg_type = "accept_friend_location";
		            result = jpushClient.sendMessageWithRegistrationID("互信", gson.toJson(message), to_tuser.getJpush_registration_id());
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
			
			
			//dao.close();
			
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
	}
	
	
	class CustomeMessage{
		String username;
		String title;
		String content;
		String msg_type;
	}
	
}
