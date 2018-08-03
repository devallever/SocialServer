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
import com.social.pojo.TGroup;
import com.social.pojo.TGroupmember;
import com.social.servlet.NearbyGroupListServlet.Group;
import com.social.servlet.NearbyGroupListServlet.Root;
import com.social.servlet.NearbyGroupListServlet.User;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class GroupDataServlet
 */
@WebServlet("/GroupDataServlet")
public class GroupDataServlet extends HttpServlet {
	private String group_id;
	private String user_id;
	private PrintWriter pw;
	private Root root;
	
	private String str_longitude;
	private String str_latitude;
	
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
		
		group_id = request.getParameter("group_id");
		if(!checkParameter(response, "group_id", group_id, root, pw)) return;
		
		str_longitude = request.getParameter("longitude");
		if (!checkParameter(response, "longitude", str_longitude, root, pw))
			return;
		// longitude = Double.parseDouble(str_latitude);

		str_latitude = request.getParameter("latitude");
		if (!checkParameter(response, "latitude", str_latitude, root, pw))
			return;
		
		GroupDAO dao = new GroupDAO();
		GroupmemberDAO memberDAO = new GroupmemberDAO(dao.getSession());
		TGroup tgroup;
		Group group;
		List<User> list_member;
		List<TGroupmember> list_tgroupmemnber_list;
		try {
			tgroup = dao.getById(Long.parseLong(group_id));
			group = new Group();
			group.id = tgroup.getId()+"";
			group.groupname = tgroup.getGroupname();
			group.group_img = tgroup.getGroupimg();
			group.description = tgroup.getDescription();;
			group.attention = tgroup.getAttention();
			group.level = tgroup.getGrouplevel();
			group.point = tgroup.getPoint();
			double distance = CommonUtils.getDistance(str_latitude,
					str_longitude, String.valueOf(tgroup.getLatitude()),
					String.valueOf(tgroup.getLongitude()));
			String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
			group.distance = Double.parseDouble(dis_with_two);
			
			User builder = new User();
			builder.username = tgroup.getUser().getUsername();
			builder.nickname = tgroup.getUser().getNickname();
			builder.headpath = tgroup.getUser().getHeadpath();
			group.group_bulider = builder;
			group.date = tgroup.getDate().toString().split(" ")[0];
			group.hx_group_id = tgroup.getHx_group_id();
			group.group_type = tgroup.getGroup_type();
			
			list_tgroupmemnber_list = memberDAO.getByQuery("group_id=" + tgroup.getId(), 0, 0);
			int group_member_count = list_tgroupmemnber_list.size();
			group.member_count = group_member_count;
			
			//女生人数
			int women_count = 0;
			User member;
			list_member = new ArrayList<GroupDataServlet.User>();
			for(TGroupmember tGroupmember: list_tgroupmemnber_list){
				if(tGroupmember.getUser().getSex().equals("女")){
					women_count++;
				}
				
				member = new User();
				member.nickname = tGroupmember.getUser().getNickname();
				member.username = tGroupmember.getUser().getUsername();
				member.headpath = tGroupmember.getUser().getHeadpath();
				list_member.add(member);
				
			}
			group.list_members = list_member;
			group.women_count = women_count;
			
			
			if (session == null || session.getAttribute("id") == null) {
				// 未登录
				group.is_member = 0;// 不是成员
			} else {
				String user_id = session.getAttribute("id").toString();// 登录用户的id
				System.out.println("session_id = " + session.getId());
				if (memberDAO.getByQuery("user_id=" + user_id + " and group_id= "+ tgroup.getId(), 0, 0).size() > 0) {
					group.is_member = 1;//是成员
				} else {
					group.is_member = 0;// 不是
				}
			}
			
			root.success = true;
			root.message = "";
			root.group = group;
			pw.print(gson.toJson(root));
			memberDAO.close();
			dao.close();
			pw.close();
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	class Root {
		boolean success;
		String message;
		Group group;
	}
	
	class Group {
		String id;
		String groupname;
		String group_img;
		User group_bulider;
		String description;
		double distance;
		String point;
		int is_member;
		int member_count;
		int women_count;
		int level;
		String date;
		String attention;
		String hx_group_id;
		int group_type;
		List<User> list_members;
	}
	
	class User{
		String username;
		String nickname;
		String headpath;
	}
	
	private boolean checkParameter(HttpServletResponse response,
			String parameterName, String parameterValue, Root root,
			PrintWriter pw) {
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
			// pw.close();
			return false;
		}
		System.out.println(parameterValue);
		return true;
	}

	/** 快速排序方法（列表） */
	private void quickSortByList(List<Group> list, int lo0, int hi0) {
		int lo = lo0;
		int hi = hi0;
		if (lo >= hi)
			return;
		// 确定指针方向的逻辑变量
		boolean transfer = true;

		while (lo != hi) {
			if (list.get(lo).distance > list.get(hi).distance) {
				// 交换
				Group temp = list.get(lo);
				list.set(lo, list.get(hi));
				list.set(hi, temp);
				// 决定下标移动，还是上标移动
				transfer = (transfer == true) ? false : true;
			}

			// 将指针向前或者向后移动
			if (transfer)
				hi--;
			else
				lo++;
		}

		// 将数组分开两半，确定每个数字的正确位置
		lo--;
		hi++;
		quickSortByList(list, lo0, lo);
		quickSortByList(list, hi, hi0);
	}

}
