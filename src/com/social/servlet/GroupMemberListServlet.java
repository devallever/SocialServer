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
import com.social.dao.GroupDAO;
import com.social.dao.GroupmemberDAO;
import com.social.pojo.TGroupmember;
import com.social.pojo.TUser;
import com.social.servlet.NearbyUserListServlet.NearbyUserRoot;
import com.social.servlet.NearbyUserListServlet.User;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class GroupMemberListServlet
 */
@WebServlet("/GroupMemberListServlet")
public class GroupMemberListServlet extends HttpServlet {
	
	private String str_longitude;
	private String str_latitude;
	private String group_id;
	
	private PrintWriter pw;
	private GroupMemberRoot root;
	
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		pw = response.getWriter();
		root = new GroupMemberRoot();
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
		
		group_id = request.getParameter("group_id");
		if(!checkParameter(response, "group_id", group_id, root, pw)) return;
		//latitude = Double.parseDouble(str_latitude);
		
		
			
		GroupmemberDAO dao = new GroupmemberDAO();
		GroupDAO groupDAO = new GroupDAO(dao.getSession());
		List<TGroupmember> list_tgroupmember;
		User owner;
		User member;
		List<User> list_member  = new ArrayList<GroupMemberListServlet.User>();
		try {
			//群主
			TUser towner = groupDAO.getById(Long.parseLong(group_id)).getUser();
			owner = new User();
			owner.id = String.valueOf(towner.getId());
			owner.username = String.valueOf(towner.getUsername());
			owner.nickname = towner.getNickname();
			owner.user_head_path = towner.getHeadpath();
			double distance = CommonUtils.getDistance(str_latitude, str_longitude, String.valueOf(towner.getLatitude()), String.valueOf(towner.getLongitude()));
			String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
			owner.distance = Double.parseDouble(dis_with_two);
			owner.signature = towner.getSignature();
			owner.age = towner.getAge();
			owner.sex = towner.getSex();
			owner.occupation = towner.getOccupation();
			owner.constellation = towner.getConstellation();
			root.group_owner = owner;
			
			
			
			//群成员
			list_tgroupmember = dao.getByQuery("group_id=" + group_id, 0, 0);
			for(TGroupmember tGroupmember : list_tgroupmember){
				TUser tmember = tGroupmember.getUser();
				if(tmember.getId() == Long.parseLong(owner.id)){
					continue;
				}
				member = new User();
				member.id = String.valueOf(tmember.getId());
				member.username = String.valueOf(tmember.getUsername());
				member.nickname = tmember.getNickname();
				member.user_head_path = tmember.getHeadpath();
				distance = CommonUtils.getDistance(str_latitude, str_longitude, String.valueOf(tmember.getLatitude()), String.valueOf(tmember.getLongitude()));
				dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
				member.distance = Double.parseDouble(dis_with_two);
				member.signature = tmember.getSignature();
				member.age = tmember.getAge();
				member.sex = tmember.getSex();
				member.occupation = tmember.getOccupation();
				member.constellation = tmember.getConstellation();
				list_member.add(member);
			}
			root.member_list = list_member;
			
			if (session == null || session.getAttribute("id") == null) {
				// 未登录
				root.is_member = 0;// 不是成员
			} else {
				String user_id = session.getAttribute("id").toString();// 登录用户的id
				System.out.println("session_id = " + session.getId());
				if (dao.getByQuery("user_id=" + user_id + " and group_id= "+group_id, 0, 0).size() > 0) {
					root.is_member = 1;//是成员
				} else {
					root.is_member = 0;// 不是
				}
			}
			
			root.success = true;
			root.message = "";
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
	}
	
	private  boolean checkParameter(HttpServletResponse response,String parameterName ,String parameterValue , GroupMemberRoot root, PrintWriter pw){
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

	
	class GroupMemberRoot{
		boolean success;
		String message;
		User group_owner;
		int is_member;
		List<User> member_list;
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
	
}
