package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.social.dao.FriendgroupDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TFriend;
import com.social.pojo.TFriendgroup;
import com.social.pojo.TUser;


/**
 * Servlet implementation class FriendGroupListServlet
 */
@WebServlet("/FriendGroupListServlet")
public class FriendGroupListServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	
	private String phone_json;
	
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
			root.list_friendgroup = null;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		if(session.getAttribute("id")==null){
			root.message = "未登录";
			root.success = false;
			root.list_friendgroup = null;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}else{
			user_id = session.getAttribute("id").toString();
		}
		
		phone_json = request.getParameter("phone_json");
		
		
		FriendgroupDAO dao = new FriendgroupDAO();
		FriendDAO friendDAO = new FriendDAO(dao.getSession());
		UserDAO userDAO = new UserDAO(dao.getSession());
		List<TFriendgroup> list_tfriendgroup;
		List<FriendGroup> list_friendgroup = new ArrayList<FriendGroupListServlet.FriendGroup>();
		FriendGroup friendgroup;
		List<String> list_friendgroup_name = new ArrayList<String>();
		
		List<TFriend> list_tfriend;
		Friend friend;
		try {
			list_tfriendgroup = dao.getByQuery("user_id=" + user_id, 0, 0);
			for(TFriendgroup tfriendgroup: list_tfriendgroup){
				list_friendgroup_name.add(tfriendgroup.getFriendgroup_name());
				friendgroup = new FriendGroup();
				friendgroup.id = tfriendgroup.getId() + "";
				friendgroup.friendgroup_name = tfriendgroup.getFriendgroup_name();
				list_tfriend = friendDAO.getByQuery("friendgroup_id=" + tfriendgroup.getId(), 0, 0);
				List<Friend> list_friend = new ArrayList<FriendGroupListServlet.Friend>();
				TUser tuser;
				for(TFriend tfriend: list_tfriend){
					friend = new Friend();
					tuser = tfriend.getFriend();
					friend.id = tuser.getId()+"";
					friend.head_path = tuser.getHeadpath();
					friend.nickname = tuser.getNickname();
					friend.signature = tuser.getSignature();
					friend.username = tuser.getUsername();
					list_friend.add(friend);
				}
				friendgroup.list_friend = list_friend;
				list_friendgroup.add(friendgroup);
			}
			
			//添加可能认识的人
			if(phone_json!=null){
				
				Data data;
				data = gson.fromJson(phone_json, Data.class);
				
				List<TUser> list_tuser;
				List<TUser> list_mayknow_tuser = new ArrayList<TUser>();
				List<Friend> list_user = new ArrayList<Friend>();
				Friend user;
				for (int i = 0; i < data.phone.length; i++) {
					list_tuser = userDAO.getByQuery("phone='" + data.phone[i] + "'", 0, 0);
					for(TUser tuser: list_tuser){
						list_mayknow_tuser.add(tuser);
					}
				}
				
				List<Friend> list_friend = new ArrayList<FriendGroupListServlet.Friend>();
				friendgroup = new FriendGroup();
				friendgroup.id = "-1";
				friendgroup.friendgroup_name = "可能认识的人";
				for(TUser tuser: list_mayknow_tuser){
					friend = new Friend();
					friend.id = tuser.getId()+"";
					friend.head_path = tuser.getHeadpath();
					friend.nickname = tuser.getNickname();
					friend.signature = tuser.getPhone();
					friend.username = tuser.getUsername();
					list_friend.add(friend);
				}
				friendgroup.list_friend = list_friend;
				list_friendgroup.add(friendgroup);
			}
			
			root.success = true;
			root.message = "";
			root.list_friendgroup = list_friendgroup;
			
			pw.print(gson.toJson(root));
			pw.close();
			userDAO.close();
			friendDAO.close();
			dao.close();
			
			
//			for(String name: list_friendgroup_name){
//				list_tfriend = friendDAO.getByQuery("user_id", start, limit)
//			}
//			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
		
	}
	
	class Root{
		boolean success;
		String message;
		List<FriendGroup> list_friendgroup;
	}
	
	class FriendGroup{
		String id;
		String friendgroup_name;
		List<Friend> list_friend;
	}
	
	class Friend{
		String id;
		String nickname;
		String username;
		String head_path;
		String signature;
	}
	
	
	class Data{
		String[] phone;
	}

}
