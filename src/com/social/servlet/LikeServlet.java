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
import com.social.dao.LikeDAO;
import com.social.dao.NewsDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TLike;
import com.social.pojo.TNews;
import com.social.pojo.TUser;
import com.social.servlet.AddNewsServlet.CustomeMessage;

/**
 * Servlet implementation class LikeServlet
 */
@WebServlet("/LikeServlet")
public class LikeServlet extends HttpServlet {
	private String user_id;
	private String news_id;
	
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
		news_id = request.getParameter("news_id");
		if(!checkParameter(response, "news_id", news_id, root, pw)) return;
//		
//		user_id = request.getParameter("user_id");
//		if(!checkParameter(response, "user_id", user_id, root, pw)) return;

		LikeDAO dao = new LikeDAO();
		NewsDAO newsDAO = new NewsDAO(dao.getSession());
		UserDAO userDAO = new UserDAO(dao.getSession());
		TLike tLike = new TLike();
		TNews tNews;
		TUser tuser;
		int count;
		try {
			if (dao.getByQuery("news_id=" + news_id + " and user_id=" + user_id, 0,0).size()==0){//点赞
				

				tNews = newsDAO.getById(Long.parseLong(news_id));
				tLike.setNews(tNews);
				tLike.setOptlockversion1(Long.parseLong("0"));
				tuser = userDAO.getById(Long.parseLong(user_id));
				tLike.setUser(tuser);
				dao.add(tLike);
				int count_add = tNews.getLickcount() + 1;
				tNews.setLickcount(count_add);
				newsDAO.update(tNews);
				
				root.likeCount = count_add;
				root.success = true;
				root.message = "点赞成功";
				root.islike = 1;
				
				//SECRET //APPKEY
				JPushClient jpushClient = new JPushClient("ac315c7bded98ea4f4423a50", "24d43240d820aaaf0803626a");
				PushPayload payload = buildPushObject_all_all_alert(tNews.getUser().getUsername(), tuser.getNickname(), tNews.getId()+"");
//				newsDAO.close();
//				userDAO.close();
//				dao.close();
				
				try {     
		            //PushResult result = jpushClient.sendPush(payload);
					PushResult result;
		            CustomeMessage message = new CustomeMessage();
		        	message.username = tuser.getUsername();
		        	message.title = "互信";
		            message.content = tuser.getNickname() + "：赞了你的动态";
		            message.msg_type = "like_news";
		            message.news_id = tNews.getId()+"";
		            result = jpushClient.sendMessageWithRegistrationID("互信", gson.toJson(message), tNews.getUser().getJpush_registration_id());
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
				
			}else{//取消
				dao.deleteById(dao.getByQuery("news_id=" + news_id + " and user_id=" + user_id, 0,0).get(0).getId());
				tNews = newsDAO.getById(Long.parseLong(news_id));
				int count_cancle = tNews.getLickcount() - 1;
				tNews.setLickcount(count_cancle);
				newsDAO.update(tNews);
				newsDAO.close();
				userDAO.close();
				dao.close();
				root.likeCount = count_cancle;
				root.success = true;
				root.message = "取消成功";
				root.islike = 0;
			}
			
			pw.print(gson.toJson(root));
			pw.close();
			userDAO.close();
			newsDAO.close();
			dao.close();
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			newsDAO.close();
			userDAO.close();
			dao.close();
		}
		
		
	}
	
	class Root{
		boolean success;
		String message;
		int likeCount;
		int islike;
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
	
	
	//username 被推送的用户
	private static PushPayload buildPushObject_all_all_alert(String username,String nickname,String news_id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username",username);
		map.put("msg_type", "like_news");
		map.put("news_id", news_id);
		
		return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(username))
                .setNotification(Notification.android(nickname+":赞了你的动态", "", map))
                .build();
    }
	
	
	class CustomeMessage{
		String username;
		String title;
		String content;
		String msg_type;
		
		String news_id;
	}

}
