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
import com.social.dao.UserDAO;
import com.social.pojo.TFriend;

/**
 * Servlet implementation class FriendNotLocationListServlet
 */
@WebServlet("/FriendNotLocationListServlet")
public class FriendNotLocationListServlet extends HttpServlet {
	private String user_id;
	private PrintWriter pw;
	private Root root;
	private List<Friend> friends_lit;
	
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
			root.friends_list = null;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		if(session.getAttribute("id")==null){
			root.message = "未登录";
			root.success = false;
			root.friends_list = null;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}else{
			user_id = session.getAttribute("id").toString();
		}
		//user_id = session.getAttribute("id").toString();
		
		//获取参数
//		id = request.getParameter("id");
//		if(!checkParameter(response, "id", id, root, pw)) return;
		
		
		FriendDAO dao = new FriendDAO();
		UserDAO userDao = new UserDAO(dao.getSession());
		List<TFriend> list_tFriends = null;
		
		try {
			list_tFriends = dao.getByQuery("user_id=" + user_id + " and show_location=0", 0, 0);
			if(list_tFriends.size()==0 || list_tFriends==null){
				root.success = false;
				root.message = "无记录";
				root.friends_list = null;
				pw.print(gson.toJson(root));
				pw.close();
				//dao.close();
			}
			root.success = true;
			root.message = "";
			friends_lit = new ArrayList<Friend>();
			for(int i=0; i<list_tFriends.size(); i++){
				Friend friend = new Friend();
				friend.id = String.valueOf(userDao.getById(list_tFriends.get(i).getFriend().getId()).getId());
				friend.head_path = userDao.getById(list_tFriends.get(i).getFriend().getId()).getHeadpath();
				friend.nickname = userDao.getById(list_tFriends.get(i).getFriend().getId()).getNickname();
				friend.username = userDao.getById(list_tFriends.get(i).getFriend().getId()).getUsername();
				friend.signature = userDao.getById(list_tFriends.get(i).getFriend().getId()).getSignature();
				friends_lit.add(friend);
			}
			root.friends_list = friends_lit;
			pw.print(gson.toJson(root));
			pw.close();
			userDao.close();
			dao.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	class Root{
		boolean success;
		String message;
		List<Friend> friends_list;
	}
	
	class Friend{
		String id;
		String nickname;
		String username;
		String head_path;
		String signature;
	}

}
