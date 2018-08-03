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
import javax.swing.plaf.ListUI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.GroupDAO;
import com.social.dao.GroupmemberDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TGroup;
import com.social.pojo.TGroupmember;
import com.social.pojo.TUser;
import com.social.servlet.NearbyGroupListServlet.Group;
import com.social.servlet.NearbyUserListServlet.NearbyUserRoot;
import com.social.servlet.NearbyUserListServlet.User;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class SearchUserServlet
 */
@WebServlet("/SearchUserServlet")
public class SearchUserServlet extends HttpServlet {
	
	private String str_longitude;
	private String str_latitude;
	
	private PrintWriter pw;
	private Root root;
	private String key;
	
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
//			root.success = false;
//			pw.print(gson.toJson(root));
//			pw.close();
//		}
		
		//获取参数
		str_longitude = request.getParameter("longitude");
		if(!checkParameter(response, "longitude", str_longitude, root, pw)) return;
		//longitude = Double.parseDouble(str_latitude);
		
		str_latitude = request.getParameter("latitude");
		if(!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		//latitude = Double.parseDouble(str_latitude);
		
		key = request.getParameter("key");
		if(!checkParameter(response, "key", key, root, pw)) return;
		
		UserDAO dao = new UserDAO();
		List<TUser> list_tuser;
		List<User> list_user = new ArrayList<SearchUserServlet.User>();
		User user;
		
		GroupDAO groupdao = new GroupDAO(dao.getSession());
		List<TGroup> list_tgroup;
		List<Group> list_group = new ArrayList<SearchUserServlet.Group>();
		Group group;
		GroupmemberDAO memberDAO= new GroupmemberDAO(dao.getSession());
		
		try {
			//list_tuser = dao.getByQuery("username like  '%" + key + "%' or nickname like '%" + key + "%'", 0, 0);
			list_tuser = dao.getByQuery("username =  '" + key + "' or nickname = '" + key + "'", 0, 0);
			for(TUser tuser: list_tuser){
				user = new User();
				user.id = String.valueOf(tuser.getId());
				user.username = String.valueOf(tuser.getUsername());
				user.nickname = tuser.getNickname();
				user.user_head_path = tuser.getHeadpath();
				double distance = CommonUtils.getDistance(str_latitude, str_longitude, String.valueOf(tuser.getLatitude()), String.valueOf(tuser.getLongitude()));
				String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
				user.distance = Double.parseDouble(dis_with_two);
				user.signature = tuser.getSignature();
				user.age = tuser.getAge();
				user.sex = tuser.getSex();
				user.occupation = tuser.getOccupation();
				user.constellation = tuser.getConstellation();
				
				list_user.add(user);
			}
			
			
			list_tgroup = groupdao.getByQuery("id like  '%" + key + "%' or groupname like '%" + key + "%'", 0, 0);
			User builder;
			List<TGroupmember> list_tgroupmemnber_list;
			for(int i=0; i<list_tgroup.size();i++){
				List<User> list_member = new ArrayList<User>();
				
				TGroup tgroup = list_tgroup.get(i);
				group = new Group();
				group.id = tgroup.getId()+"";
				group.groupname= tgroup.getGroupname();
				group.group_img = tgroup.getGroupimg();
				
				//群主信息
				builder = new User();
				builder.id = tgroup.getUser().getId()+"";
				builder.user_head_path = tgroup.getUser().getHeadpath();
				builder.age = tgroup.getUser().getAge();
				builder.sex = tgroup.getUser().getSex();
				builder.nickname = tgroup.getUser().getNickname();
				builder.username = tgroup.getUser().getUsername();
				group.group_bulider = builder;
				double distance = CommonUtils.getDistance(str_latitude, str_longitude, String.valueOf(tgroup.getLatitude()), String.valueOf(tgroup.getLongitude()));
				String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
				group.distance = Double.parseDouble(dis_with_two);
				group.point = tgroup.getPoint();
				
				//群主人数
				list_tgroupmemnber_list = memberDAO.getByQuery("group_id=" + tgroup.getId(), 0, 0);
				int group_member_count = list_tgroupmemnber_list.size();
				group.member_count = group_member_count;
				
				
				//女生人数
				int women_count = 0;
				User member;
				for(TGroupmember tGroupmember: list_tgroupmemnber_list){
					if(tGroupmember.getUser().getSex().equals("女")){
						women_count++;
					}
					
					member = new User();
					member.id = tGroupmember.getUser().getId()+"";
					member.nickname = tGroupmember.getUser().getNickname();
					member.username = tGroupmember.getUser().getUsername();
					member.age = tGroupmember.getUser().getAge();
					member.sex = tGroupmember.getUser().getSex();
					member.user_head_path = tGroupmember.getUser().getHeadpath();
					list_member.add(member);
					
				}
				group.list_members = list_member;
				group.women_count = women_count;
				group.attention = tgroup.getAttention();
				group.hx_group_id = tgroup.getHx_group_id();
				
				if (session == null || session.getAttribute("id") == null) {
					// 未登录
					group.is_member = 0;// 不是成员
				} else {
					String user_id = session.getAttribute("id").toString();// 登录用户的id
					System.out.println("session_id = " + session.getId());
					if (memberDAO.getByQuery(
							"user_id=" + user_id + " and group_id= "
									+ tgroup.getId(), 0, 0).size() > 0) {
						group.is_member = 1;//是成员
					} else {
						group.is_member = 0;// 不是
					}
				}
				
				
				list_group.add(group);
			}
			
			
			
			
			root.success = true;
			root.message = "";
			root.user_list = list_user;
			root.group_list = list_group;
			pw.print(gson.toJson(root));
			pw.close();
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
	
	
	
	class Root{
		boolean success;
		String message;
		List<User> user_list;
		List<Group> group_list;
	}
	
	class User{
		String id;
		String username;
		String nickname;
		String sex;
		double distance;
		String user_head_path;
		String signature;
		int age;
		String constellation;
		String occupation;
	}
	
	class Group {
		String id;
		String groupname;
		String group_img;
		User group_bulider;
		double distance;
		String point;
		int is_member;
		int member_count;
		int women_count;
		String hx_group_id;
		String attention;
		List<User> list_members;
	}
}
