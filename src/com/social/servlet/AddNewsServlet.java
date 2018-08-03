package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.FollowDAO;
import com.social.dao.FriendDAO;
import com.social.dao.NewsDAO;
import com.social.dao.NewsImageDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TFollow;
import com.social.pojo.TFriend;
import com.social.pojo.TNews;
import com.social.pojo.TNewsImage;
import com.social.pojo.TUser;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class AddNewsServlet
 */
@WebServlet("/AddNewsServlet")
@MultipartConfig
public class AddNewsServlet extends HttpServlet {
	private String user_id;
	private String city;
	private Date date;
	private String content;
	private double longitude;
	private double latitude;
	private String str_longitude;
	private String str_latitude;
	private Part part1;
	private Part part2;
	private Part part3;
	private Part part4;
	private Part part5;
	private Part part6;
	private Part audio_part;
	private int i = 0;
	private String newsImage1;
	private String newsImage2;
	private String newsImage3;
	private String newsImage4;
	private String newsImage5;
	private String newsImage6;
	
	private String user_nickname;
	
	
	
	private News resultNews;
	
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
		System.out.println(session.getId());
		if(session == null || session.getAttribute("id") == null){
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		//获取参数
//		user_id = request.getParameter("user_id");
//		if(!checkParameter(response, "user_id", user_id, root, pw)) return;
		
		content = request.getParameter("content");
		if(!checkParameter(response, "content", content, root, pw)) return;
		
		city = request.getParameter("city");
		if(city==null){
			city = "";
		}
		//if(!checkParameter(response, "city", city, root, pw)) return;
		
		
		str_latitude = request.getParameter("latitude");
		System.out.println("str_latitude = "  + str_latitude);
		if(str_latitude == null){
			System.out.println(str_latitude);
			str_latitude = "0";
		}
		//if(!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		//latitude = Double.valueOf(str_latitude);
		
		str_longitude = request.getParameter("longitude");
		if(str_longitude == null){
			str_longitude = "0";
		}
		//if(!checkParameter(response, "longitude", str_longitude, root, pw)) return;
		//longitude = Double.valueOf(str_longitude);
		
		date = new Date();
		
		
		NewsDAO dao = new NewsDAO();
		UserDAO userDao = new UserDAO(dao.getSession());
		TNews tNews;
		resultNews = new News();
		try {
			tNews = new TNews();
			tNews.setContent(content);
			tNews.setCity(city);
			tNews.setDate(date);
			tNews.setLatitude(Double.parseDouble(str_latitude));
			tNews.setLongitude(Double.parseDouble(str_longitude));
			tNews.setOptlockversion1(Long.parseLong("0"));
			tNews.setUser(userDao.getById(Long.parseLong(user_id)));
			tNews.setCommentcount(0);
			tNews.setLickcount(0);
			
			tNews =  dao.add(tNews);
			resultNews.id = String.valueOf(tNews.getId());
			resultNews.content = tNews.getContent();
			resultNews.user_id = String.valueOf(tNews.getUser().getId());
			resultNews.date = tNews.getDate().toString();
			resultNews.longitude = String.valueOf(tNews.getLongitude());
			resultNews.latitude = String.valueOf(tNews.getLatitude());
			resultNews.city = tNews.getCity();
			resultNews.lickcount = tNews.getLickcount();
			resultNews.commentcount = tNews.getCommentcount();
			resultNews.is_commented = 0;
			userDao.close();
			dao.close();
			
			
			//
			dao = new NewsDAO();
			List<String> img_list = new ArrayList<String>();
			NewsImageDAO newsImageDAO = new NewsImageDAO(dao.getSession());
			TNewsImage tNewsImage = new TNewsImage();
			tNewsImage.setNews(tNews);
			tNewsImage.setOptlockversion1(Long.parseLong("0"));
			part1 = request.getPart("part1");
			System.out.println();
			if(part1 != null){
				if(CommonUtils.getImgByte(part1).length>0){
					createNewsImageFile(part1,1);
					newsImage1 = "/images/newsimg/" + tNews.getId() +"_" + 1 + ".jpg";
					img_list.add(newsImage1);
					tNewsImage.setPath(newsImage1);
					tNewsImage = newsImageDAO.add(tNewsImage);
					newsImageDAO.close();
					dao.close();
				}
				
			}
			part2 = request.getPart("part2");
			if(part2 != null){
				if(CommonUtils.getImgByte(part2).length>0){
					dao = new NewsDAO();
					NewsImageDAO newsImageDAO2 = new NewsImageDAO(dao.getSession());
					createNewsImageFile(part2,2);
					newsImage2 = "/images/newsimg/" + tNews.getId() +"_" + 2 + ".jpg";
					img_list.add(newsImage2);
					tNewsImage.setPath(newsImage2);
					tNewsImage = newsImageDAO2.add(tNewsImage);
					newsImageDAO2.close();
					dao.close();
				}
				
			}
			part3 = request.getPart("part3");
			if(part3 != null){
				if(CommonUtils.getImgByte(part3).length>0){
					dao = new NewsDAO();
					newsImageDAO = new NewsImageDAO(dao.getSession());
					createNewsImageFile(part3,3);
					newsImage3 = "/images/newsimg/" + tNews.getId() +"_" + 3 + ".jpg";
					img_list.add(newsImage3);
					tNewsImage.setPath(newsImage3);
					tNewsImage = newsImageDAO.add(tNewsImage);
					newsImageDAO.close();
					dao.close();
				}
				
			}
			part4 = request.getPart("part4");
			if(part4 != null){
				if(CommonUtils.getImgByte(part4).length>0){
					dao = new NewsDAO();
					newsImageDAO = new NewsImageDAO(dao.getSession());
					createNewsImageFile(part4,4);
					newsImage4 = "/images/newsimg/" + tNews.getId() +"_" + 4 + ".jpg";
					img_list.add(newsImage4);
					tNewsImage.setPath(newsImage4);
					tNewsImage = newsImageDAO.add(tNewsImage);
					newsImageDAO.close();
					dao.close();
				}
				
			}
			part5 = request.getPart("part5");
			if(part5 != null){
				if(CommonUtils.getImgByte(part5).length>0){
				dao = new NewsDAO();
				newsImageDAO = new NewsImageDAO(dao.getSession());
				createNewsImageFile(part5,5);
				newsImage5 = "/images/newsimg/" + tNews.getId() +"_" + 5 + ".jpg";
				img_list.add(newsImage5);
				tNewsImage.setPath(newsImage5);
				tNewsImage = newsImageDAO.add(tNewsImage);
				newsImageDAO.close();
				dao.close();
				}
			}
			part6 = request.getPart("part6");
			if(part6 != null){
				if(CommonUtils.getImgByte(part6).length>0){
					dao = new NewsDAO();
					newsImageDAO = new NewsImageDAO(dao.getSession());
					createNewsImageFile(part6,6);
					newsImage6 = "/images/newsimg/" + tNews.getId() +"_" + 6 + ".jpg";
					img_list.add(newsImage6);
					tNewsImage.setPath(newsImage6);
					tNewsImage = newsImageDAO.add(tNewsImage);
					newsImageDAO.close();
					dao.close();
				}
			}
			
			audio_part = request.getPart("audio_part");
			if(audio_part!=null){
				if(CommonUtils.getImgByte(audio_part).length>0){
					dao = new NewsDAO();
					TNews tnews = dao.getById(Long.parseLong(resultNews.id));
					createNewsVoiceFile(audio_part);
					String news_voice_path = "/audio/news_voice/" + tnews.getId() + ".mp3";
					tnews.setAudio_path(news_voice_path);
					dao.close();
				}
			}
			
			
			
			resultNews.news_image_path = img_list;

			root.success = true;
			root.message = "";
			root.news = resultNews;
			
			pw.print(gson.toJson(root));
			pw.close();
			img_list.clear();
			
			
			JPushClient jpushClient = new JPushClient("ac315c7bded98ea4f4423a50", "24d43240d820aaaf0803626a");

	        // For push, all you need do is to build PushPayload object.
			
			FriendDAO friendDAO = new FriendDAO();
			UserDAO user__dao= new UserDAO(friendDAO.getSession());
			FollowDAO followDAO = new FollowDAO(dao.getSession());
			List<TFollow> list_fans;
			List<TFriend> list_tfriend;
			List<TUser> list_push_tuser = new ArrayList<TUser>();
			TUser tusr = user__dao.getById(Long.parseLong(user_id));
			user_nickname = tusr.getNickname();
			
			list_tfriend = friendDAO.getByQuery("user_id="+ user_id, 0, 0);
			for(TFriend tfriend:list_tfriend){
				list_push_tuser.add(tfriend.getFriend());
			}
			
			list_fans = followDAO.getByQuery("follow_id=" + user_id, 0, 0);
			for(TFollow tfollow: list_fans){
				list_push_tuser.add(tfollow.getUser());
			}
			
			
			String[] arr_friend_username = new String[list_push_tuser.size()];
			String[] arr_friend_nickname = new String[list_push_tuser.size()];
			String[] arr_registration_id = new String[list_push_tuser.size()];
			for(int i=0;i<list_push_tuser.size();i++){
				arr_friend_username[i] = list_push_tuser.get(i).getUsername();
				arr_friend_nickname[i] = list_push_tuser.get(i).getUsername();
				arr_registration_id[i] = list_push_tuser.get(i).getJpush_registration_id();
			}
			dao.close();
			
	        PushPayload payload = buildPushObject_all_all_alert(arr_friend_username,user_nickname,tusr.getUsername());
	        
	        try {     
	        	PushResult result;
	            //result = jpushClient.sendPush(payload);
	        	CustomeMessage message = new CustomeMessage();
	        	message.username = tusr.getUsername();
	        	message.title = "互信";
	            message.content = tusr.getNickname() + "：发布了动态";
	            message.msg_type = "add_news";
	            message.news_id = tNews.getId()+"";
	            result = jpushClient.sendMessageWithRegistrationID("互信", gson.toJson(message), arr_registration_id);
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
			
			
			//userDao.close();
			//dao.close();
			
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(GenericJDBCException e){
			root.success= false;
			root.message = "暂时不支持表情";
			//root.comment = comment;
			
			pw.print(gson.toJson(root));
			pw.close();
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
		News news;
	}
	
	class News{
		String id;
		String content;
		String user_id;
		String date;
		String longitude;
		String latitude;
		String city;
		int is_commented;
		int commentcount;
		int lickcount;
		List<String> news_image_path;
		
		
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
	
	private void createNewsImageFile(Part part, int position){ 
		try {
			String filepath = getServletContext().getRealPath("") + "images/newsimg";
			String filename = resultNews.id +"_" + position + ".jpg";
			byte[] b;
			b = CommonUtils.getImgByte(part);
			System.out.println("b.length = " + b.length);
			if(b.length >0){
				CommonUtils.byteToFile(b, filepath, filename);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void createNewsVoiceFile(Part part){ 
		try {
			String filepath = getServletContext().getRealPath("") + "audio/news_voice";
			String filename = resultNews.id + ".mp3";
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
	
	
	private static PushPayload buildPushObject_all_all_alert(String[] friend_username,String nickname,String username) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("msg_type", "add_news");
		
		return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(friend_username))
                .setNotification(Notification.android(nickname + "：发布了动态", "", map))
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
