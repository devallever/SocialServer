package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.FollowDAO;
import com.social.dao.FriendDAO;
import com.social.dao.NewsDAO;
import com.social.dao.NewsImageDAO;
import com.social.dao.UserDAO;
import com.social.dao.VisitedUserDAO;
import com.social.pojo.TFriend;
import com.social.pojo.TNews;
import com.social.pojo.TNewsImage;
import com.social.pojo.TUser;
import com.social.pojo.TVisiteduser;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class UserDataServlet
 */
@WebServlet("/UserDataServlet")
public class UserDataServlet extends HttpServlet {
	private String check_user_id;
	private String user_id;
	
	private String str_longitude;
	private String str_latitude;
	
	private PrintWriter pw;
	private Root root;
	private User user;
	
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
//		if(session == null || session.getAttribute("id") == null){
//			root.message = "未登录";
//			root.seccess = false;
//			root.user = null;
//			pw.print(gson.toJson(root));
//			pw.close();
//		}
		//user_id = session.getAttribute("id").toString();
		
		
		//获取参数
		check_user_id = request.getParameter("check_user_id");
		if(!checkParameter(response, "check_user_id", check_user_id, root, pw)) return;
		
		str_longitude = request.getParameter("longitude");
		if(str_longitude==null) str_longitude="0"; 
		str_latitude = request.getParameter("latitude");
		if(str_latitude==null) str_latitude="0";
		
		
		UserDAO dao = new UserDAO();
		FriendDAO friendDAO = new FriendDAO(dao.getSession());
		//FeeDAO feeDAO = new FeeDAO(dao.getSession());
		List<TUser> list = new ArrayList<TUser>();
		TUser tu;
		//TFee tfee;
		try {
			Long id = dao.getByQuery("username = '" + check_user_id + "'", 0, 0).get(0).getId();
			list = dao.getByQuery("id=" + id , 0, 0);
			if (list.size()>0) {
				tu = list.get(0);
				user = new User();
				
				user.id = String.valueOf(tu.getId());
				user.username = tu.getUsername();
				user.nickname = tu.getNickname();
				user.longitude = tu.getLongitude();
				user.latiaude = tu.getLatitude();
				double distance = CommonUtils.getDistance(str_latitude, str_longitude, String.valueOf(tu.getLatitude()), String.valueOf(tu.getLongitude()));
				String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
				user.distance = Double.parseDouble(dis_with_two);
				user.phone = tu.getPhone();
				user.user_head_path = tu.getHeadpath();
				user.signature = tu.getSignature();
				user.email = tu.getEmail();
				user.city = tu.getCity();
				user.sex = tu.getSex();
				user.age = tu.getAge();
				user.occupation = tu.getOccupation();
				user.constellation = tu.getConstellation();
				user.hight = tu.getHight();
				user.weight = tu.getWeight();
				user.figure = tu.getFigure();
				user.emotion = tu.getEmotion();
				user.is_vip = tu.getIs_vip();
				//user.second_name = tu.getSecond_name();
				
				//tfee = feeDAO.getByQuery("user_id=" + user.id, 0, 0).get(0);
				user.accept_video = tu.getAccept_video();
				user.video_fee = tu.getVideo_fee();
				
				int visited_count = tu.getVisited_count();
				visited_count++;
				tu.setVisited_count(visited_count);
				dao.update(tu);
				
				if(session.getAttribute("id") != null){
					user_id = session.getAttribute("id").toString();
					VisitedUserDAO visitedUserDAO = new VisitedUserDAO(dao.getSession());
					TVisiteduser tVisiteduser;
					List<TVisiteduser> list_TVisitedusers;
					list_TVisitedusers = visitedUserDAO.getByQuery("who=" + user_id + " and user_id=" + id , 0, 0);
					if(list_TVisitedusers.size()==0){
						//没记录,添加
						tVisiteduser = new TVisiteduser();
						tVisiteduser.setDate(new Date());
						tVisiteduser.setOptlockversion1(Long.parseLong("0"));
						tVisiteduser.setOptlockversion2(Long.parseLong("0"));
						tVisiteduser.setUser(dao.getByQuery("username='" + check_user_id + "'", 0, 0).get(0));
						tVisiteduser.setWho(dao.getById(Long.parseLong(user_id)));
						visitedUserDAO.add(tVisiteduser);
					}else{
						//有记录，更新
						tVisiteduser = list_TVisitedusers.get(0);
						tVisiteduser.setDate(new Date());
						visitedUserDAO.update(tVisiteduser);
					}
					visitedUserDAO.close();
				}
				
				if(session.getAttribute("id") != null){
					user_id = session.getAttribute("id").toString();
					if(friendDAO.getByQuery("user_id="+ user_id + " and friend_id =" + id, 0, 0).size()>0 
							//&& friendDAO.getByQuery("user_id="+ check_user_id + " and friend_id =" + user_id, 0, 0).size()>0
							){
						user.is_friend = 1;//是好友//单向关注
						TFriend tfriend = friendDAO.getByQuery("user_id=" + user_id + " and friend_id =" + user.id, 0, 0).get(0);
						user.second_name = tfriend.getSecond_name();
						user.friendgroup_name = tfriend.getFriendgroup().getFriendgroup_name();
						
					}else{
						user.is_friend = 0;//非好友
						user.second_name = null;
						user.friendgroup_name = null;
					}
					
					FollowDAO followDAO = new FollowDAO(dao.getSession());
					if(followDAO.getByQuery("user_id="+user_id+" and follow_id=" + user.id, 0, 0).size()==1){
						user.is_follow = 1;
					}else{
						user.is_follow = 0;
					}
				}else{
					user.is_friend = 0;
					user.second_name = null;
					user.friendgroup_name = null;
					user.is_follow = 0;
				}
				
				//找该用户的最新三张图片
				NewsDAO newsdao = new NewsDAO(dao.getSession());
				NewsImageDAO newsimgdao = new NewsImageDAO(dao.getSession());
				List<TNews> list_tnews = newsdao.getByQuery("user_id=" + id +" order by date desc", 0, 0);
				List<TNewsImage> list_news_img;
				List<String> list_imgs = new ArrayList<String>();
				for(TNews tnews : list_tnews){
					list_news_img = newsimgdao.getByQuery("news_id = "  + tnews.getId() , 0, 0);
					for(TNewsImage tNewsImage : list_news_img){
						list_imgs.add(tNewsImage.getPath());
					}
					
				}
				
//				dao.close();
//				dao = new UserDAO();
//				List<TUser> list_tuser;
//				list_tuser = dao.getByQuery("username='" + check_user_id + "'", 0, 0);
//				if(){}
				
				
				
				user.list_news_img = list_imgs;
				
				root.seccess = true;
				root.message = "";
				root.user = user;
				newsdao.close();
				newsimgdao.close();
				
				dao.close();
				pw.print(gson.toJson(root));
				pw.close();
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dao.close();
			pw.close();
		}
		
	}
	
	class Root{
		boolean seccess;
		String message;
		String session_id;
		User user;
	}
	
	class User{
		String id;
		String username;
		String nickname;
		double longitude;
		double latiaude;
		double distance;
		int is_friend;
		String phone;
		String user_head_path;
		String email;
		String signature;
		String city;
		String sex;
		int age;
		String occupation;
		String constellation;
		String hight;
		String weight;
		String figure;
		String emotion;
		int is_vip;
		int accept_video;
		int video_fee;
		int is_follow;
		String second_name;
		String friendgroup_name;
		List<String> list_news_img;
	}
	
	private  boolean checkParameter(HttpServletResponse response,String parameterName ,String parameterValue , Root root, PrintWriter pw){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		if (parameterValue == null || parameterValue.length() == 0) {
			root.seccess = false;
			root.message = "没有指定" + parameterName + "参数";
			root.user = null;
			response.setContentType("text/html;charset=utf-8");
			try {
				pw = response.getWriter();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pw.print(gson.toJson(root));
			pw.close();
			return false;
		}
		System.out.println(parameterValue);
		return true;
	}

}
