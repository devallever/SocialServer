package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.hibernate.exception.GenericJDBCException;

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
import com.social.dao.CommentDAO;
import com.social.dao.NewsDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TComment;
import com.social.pojo.TNews;
import com.social.pojo.TUser;
import com.social.servlet.LikeServlet.CustomeMessage;
import com.social.util.CommonUtils;
import com.sun.xml.internal.ws.api.pipe.Tube;


/**
 * Servlet implementation class AddCommentServlet
 */
@WebServlet("/AddCommentServlet")
@MultipartConfig
public class AddCommentServlet extends HttpServlet {
	private String user_id;
	private String news_id;
	private String comment_id;
	private String content;
	private PrintWriter pw;
	private Root root;
	private Part audio_part;
	
	private TComment resultTComment;
	private TComment from_comment;
	
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
		
//		user_id = request.getParameter("user_id");
//		if(!checkParameter(response, "user_id", user_id, root, pw)) return;
		
		comment_id = request.getParameter("comment_id");
		if(comment_id.equals("")) comment_id = null;
		
		
		content = request.getParameter("content");
		//if(!checkParameter(response, "content", content, root, pw)) return;
		
		CommentDAO dao = new CommentDAO();
		NewsDAO newsDAO = new NewsDAO(dao.getSession());
		UserDAO userDAO = new UserDAO(dao.getSession());
		TUser tuser;
		TComment tComment = new TComment();
		//TComment resultTComment;
		Comment comment = new Comment();
		TNews tnews;
		
		try{
			
			tuser = userDAO.getById(Long.parseLong(user_id));
			if(comment_id == null ){
				tComment.setComment(null);
			}else{
				from_comment = dao.getById(Long.parseLong(comment_id));
				tComment.setComment(from_comment);
			}
			tComment.setContent(content);
			tComment.setDate(new Date());
			tComment.setNews(newsDAO.getById(Long.parseLong(news_id)));
			tComment.setOptlockversion1(Long.parseLong("0"));
			tComment.setUser(userDAO.getById(Long.parseLong(user_id)));
			resultTComment = dao.add(tComment);
			
			audio_part = request.getPart("audio_part");
			if(audio_part!=null){
				if(CommonUtils.getImgByte(audio_part).length>0){
					createNewsVoiceFile(audio_part);
					String comment_voice_path = "/audio/comment_voice/" + resultTComment.getId() + ".mp3";
					resultTComment.setAudio_path(comment_voice_path);
					dao.update(resultTComment);
					//dao.close();
				}
			}else{
				resultTComment.setAudio_path(null);
				dao.update(resultTComment);
			}
			
			
			
			
			tnews = newsDAO.getById(Long.parseLong(news_id));
			int count_add = tnews.getCommentcount() + 1;
			tnews.setCommentcount(count_add);
			newsDAO.update(tnews);
			newsDAO.close();
			
			comment.id = String.valueOf(resultTComment.getId());
			comment.content = resultTComment.getContent();
			comment.date = resultTComment.getDate().toString();
			if(resultTComment.getComment() == null){
				comment.comment_id = "-1";
			}else{
				comment.comment_id = String.valueOf(resultTComment.getComment().getId());
			}
			comment.nickname = resultTComment.getUser().getNickname();
			comment.user_head_path = resultTComment.getUser().getHeadpath();
			comment.user_id = String.valueOf(resultTComment.getUser().getId());
			
			root.success= true;
			root.message = "";
			root.comment_count = tnews.getCommentcount();
			root.comment = comment;
			
			pw.print(gson.toJson(root));
			pw.close();
			
			JPushClient jpushClient = new JPushClient("ac315c7bded98ea4f4423a50", "24d43240d820aaaf0803626a");
			PushPayload payload;
			System.out.println("comment_id = " + comment_id);
			if(comment_id == null){
				//jpushClient= new JPushClient("9c7a70e2d65ee8aa19fa32cb", "7de63bf34d6e021ea47996f5");
				payload = buildPushObject_all_all_alert(tnews.getUser().getUsername(), tuser.getNickname(), tnews.getId()+"");
			}else{
				//jpushClient = new JPushClient("9c7a70e2d65ee8aa19fa32cb", "7de63bf34d6e021ea47996f5");
				payload= buildPushObject_all_all_alert(from_comment.getUser().getUsername(), tuser.getNickname(), tnews.getId()+"");
			}
			
			
			userDAO.close();
			newsDAO.close();
			dao.close();
			
			
			try {     
	            //PushResult result = jpushClient.sendPush(payload);
				PushResult result;
	            CustomeMessage message = new CustomeMessage();
	        	message.username = tuser.getUsername();
	        	message.title = "互信";
	        	message.msg_type = "comment_news";
	        	message.news_id = tnews.getId()+"";
				if(comment_id== null){
					message.content = tuser.getNickname() + "：评论了你的动态";
					result = jpushClient.sendMessageWithRegistrationID("互信", gson.toJson(message), tnews.getUser().getJpush_registration_id());
				}else{
					message.content = tuser.getNickname() + "：回复了你的评论";
					result = jpushClient.sendMessageWithRegistrationID("互信", gson.toJson(message), from_comment.getUser().getJpush_registration_id());
				}
	            //message.content = tuser.getNickname() + "：赞了你的动态";
	           //result = jpushClient.sendMessageWithRegistrationID("互信", gson.toJson(message), tnews.getUser().getJpush_registration_id());
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
			
			
		}catch(NumberFormatException e ){
			e.printStackTrace();
		}catch(GenericJDBCException e){
			root.success= false;
			root.message = "暂时不支持表情";
			//root.comment = comment;
			
			pw.print(gson.toJson(root));
			pw.close();
			userDAO.close();
			newsDAO.close();
			dao.close();
			System.out.println("okkkk");
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	class Root{
		boolean success;
		String message;
		int comment_count;
		Comment comment;
	}
	
	class Comment{
		String id;
		String content;
		String user_id;
		String nickname;
		String user_head_path;
		String date;
		String comment_id;
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
	
	
	private void createNewsVoiceFile(Part part){ 
		try {
			String filepath = getServletContext().getRealPath("") + "\\audio\\comment_voice";
			String filename = resultTComment.getId() + ".mp3";
			byte[] b;
			b = CommonUtils.getImgByte(part);//也使用于其他对象
			System.out.println("b.length = " + b.length);
			if(b.length >0){
				CommonUtils.byteToFile(b, filepath, filename);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	//username 被推送的用户
		private PushPayload buildPushObject_all_all_alert(String username,String nickname,String news_id) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("username",username);
			map.put("msg_type", "comment_news");
			map.put("news_id", news_id);
			
			String msg_content;
			if(comment_id== null){
				msg_content = nickname + "评论了你的动态";
			}else{
				msg_content = nickname + "回复了你的评论";
			}
			
			return PushPayload.newBuilder()
	                .setPlatform(Platform.all())
	                .setAudience(Audience.alias(username))
	                .setNotification(Notification.android(msg_content, "", map))
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
