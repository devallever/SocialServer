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
import com.social.dao.FriendgroupDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TFriend;
import com.social.pojo.TFriendgroup;

/**
 * Servlet implementation class AddFriendServlet
 */
@WebServlet("/AddFriendServlet")
public class AddFriendServlet extends HttpServlet {
	private String user_id;
	private String friend_id;
	private String is_share;
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
//		user_id = request.getParameter("user_id");
//		if(!checkParameter(response, "user_id", user_id, root, pw)) return;
		
		friend_id = request.getParameter("friend_id");//username
		if(!checkParameter(response, "friend_id", friend_id, root, pw)) return;
		
		is_share = request.getParameter("is_share");//
		if (is_share == null) {
			is_share = "0";
		}
		
		
		
		FriendDAO dao = new FriendDAO();
		UserDAO userDao = new UserDAO(dao.getSession());
		FriendgroupDAO friendgroupDAO = new FriendgroupDAO(dao.getSession());
		TFriend tFriend = new TFriend();
		TFriendgroup tFriendgroup;
		try {
			
			Long friendId = userDao.getByQuery("username = '" + friend_id + "'", 0, 0).get(0).getId();
			
			List<TFriend> list = dao.getByQuery("user_id="+user_id+" and friend_id=" + friendId, 0, 0);
			if(list.size()>0){
				root.success = false;
				root.message = list.get(0).getFriend().getNickname() + "已经是你好友";
				pw.print(gson.toJson(root));
				pw.close();
				userDao.close();
				dao.close();
				return;
			}
			
			tFriend.setUser(userDao.getById(Long.parseLong(user_id)));
			tFriendgroup = friendgroupDAO.getByQuery("user_id=" + user_id + " and friendgroup_name = '我的好友'", 0, 0).get(0);
			tFriend.setFriend(userDao.getByQuery("username = '" + friend_id + "'", 0, 0).get(0));
			tFriend.setFriendgroup(tFriendgroup);
			tFriend.setOptlockversion1(Long.valueOf("1"));
			tFriend.setState(1);
			tFriend.setSecond_name("");
			tFriend.setShow_location(Integer.valueOf(is_share));
			dao.add(tFriend);
			userDao.close();
			dao.close();
			
			dao = new FriendDAO();
			userDao = new UserDAO(dao.getSession());
			friendgroupDAO = new FriendgroupDAO(dao.getSession());
			tFriendgroup = friendgroupDAO.getByQuery("user_id=" + userDao.getByQuery("username = '" + friend_id + "'", 0, 0).get(0).getId() + " and friendgroup_name = '我的好友'", 0, 0).get(0);
			tFriend.setUser(userDao.getByQuery("username = '" + friend_id + "'", 0, 0).get(0));
			tFriend.setFriend(userDao.getById(Long.parseLong(user_id)));
			tFriend.setFriendgroup(tFriendgroup);
			tFriend.setOptlockversion1(Long.valueOf("1"));
			tFriend.setState(1);
			tFriend.setSecond_name("");
			tFriend.setShow_location(Integer.valueOf(is_share));
			dao.add(tFriend);
			
			root.success = true;
			root.message = "添加成功";
			pw.print(gson.toJson(root));
			pw.close();
			userDao.close();
			dao.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	class Root{
		boolean success;
		String message;
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
	

}
