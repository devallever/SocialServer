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
import com.social.dao.GroupDAO;
import com.social.dao.GroupmemberDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TGroup;
import com.social.pojo.TGroupmember;
import com.social.pojo.TUser;


/**
 * Servlet implementation class InviteFriendToGroupServlet
 */
@WebServlet("/InviteFriendToGroupServlet")
public class InviteFriendToGroupServlet extends HttpServlet {
	
	private String user_id;
	private String group_id;
	private PrintWriter pw;
	private Root root;
	private String applyer;
	private String invited_username;
	
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
		System.out.println(session.getId());
		if(session == null || session.getAttribute("id") == null){
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		group_id = request.getParameter("group_id");
		if(!checkParameter(response, "group_id", group_id, root, pw)) return;
		
		applyer = request.getParameter("applyer");//被邀请申请者的username;
		if(!checkParameter(response, "applyer", applyer, root, pw)) return;
		
		invited_username = request.getParameter("invited_username");//邀请者的username;
		if(!checkParameter(response, "invited_username", invited_username, root, pw)) return;
		
		GroupmemberDAO dao = new GroupmemberDAO();
		GroupDAO groupDAO = new GroupDAO(dao.getSession());
		UserDAO userDAO = new UserDAO(dao.getSession());
		TGroupmember tGroupmember;
		TUser tuser;
		TUser tuser_invite;
		List<TGroupmember> list_tgroupmember;
		TGroup tgroup;
		try {
			tuser_invite = userDAO.getByQuery("username='" + invited_username + "'", 0, 0).get(0);
			//判断是否成员
			tuser  = userDAO.getByQuery("username='" + applyer + "'", 0, 0).get(0);
			list_tgroupmember = dao.getByQuery("group_id=" + group_id + " and user_id=" + tuser.getId(), 0, 0);
			if(list_tgroupmember.size()>0){
				//有记录 已经是成员
				root.success = false;
				root.message = "已加入";
				pw.print(gson.toJson(root));
				pw.close();
				groupDAO.close();
				userDAO.close();
				dao.close();
			}else{
				tgroup = groupDAO.getById(Long.parseLong(group_id));
				//无记录。不是成员
				tGroupmember = new TGroupmember();
				tGroupmember.setGroup(groupDAO.getById(Long.parseLong(group_id)));
				tGroupmember.setUser(tuser);
				tGroupmember.setOptlockversion1(Long.parseLong("0"));
				tGroupmember.setOptlockversion2(Long.parseLong("0"));
				dao.add(tGroupmember);
				root.success = true;
				root.message = "";
				
				pw.print(gson.toJson(root));
				pw.close();
				groupDAO.close();
				userDAO.close();
				dao.close();
				
				//极光推送
				JPushClient jpushClient = new JPushClient("ac315c7bded98ea4f4423a50", "24d43240d820aaaf0803626a");
				PushPayload payload = buildPushObject_all_all_alert(applyer, tuser_invite.getNickname(),tgroup.getGroupname());
				try {     
		            PushResult result = jpushClient.sendPush(payload);
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
	
	
	private static PushPayload buildPushObject_all_all_alert(String username,String invited_nickname,String group_name) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username",username);
		map.put("msg_type", "inviet_friend_to_group");
		map.put("group_name", group_name);
		
		return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(username))
                .setNotification(Notification.android(invited_nickname + "邀请您加入群组 " + group_name, "", map))
                .build();
    }
	
	class Root{
		boolean success;
		String message;
	}
}
