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
import com.social.dao.UserDAO;
import com.social.pojo.TGroup;
import com.social.pojo.TGroupmember;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class RedPocketGroupListServlet
 */
@WebServlet("/RedPocketGroupListServlet")
public class RedPocketGroupListServlet extends HttpServlet {
	
	private String str_longitude;
	private String str_latitude;

	private PrintWriter pw;
	private Root root;
	
	//private String str_page;
	//private int page;
	//private int startCount;
	//private int endCount;
	
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
		
		
		// 获取参数
		str_longitude = request.getParameter("longitude");
		if (!checkParameter(response, "longitude", str_longitude, root, pw))
			return;
		// longitude = Double.parseDouble(str_latitude);

		str_latitude = request.getParameter("latitude");
		if (!checkParameter(response, "latitude", str_latitude, root, pw))
			return;
		
		//str_page = request.getParameter("page");
		//if(str_page == null) str_page = "1";
		//page = Integer.valueOf(str_page);
		//endCount = page * 1 * 10;
		//startCount = endCount - 9;
		
		GroupDAO dao = new GroupDAO();
		GroupmemberDAO memberDAO = new GroupmemberDAO(dao.getSession());
		UserDAO userDAO = new UserDAO(dao.getSession());
		
		List<TGroup> list_tgroup;//获取所有群组
		List<TGroup> displayTGroup_list = new ArrayList<TGroup>();//要显示的附近群组
		
		List<Double> list_distance = new ArrayList<Double>();
		List<Group> list_group = new ArrayList<Group>();
		try {
			list_tgroup = dao.getByQuery("state=1 and red_pocket_count > 0", 0, 0);
			for(TGroup tgroup: list_tgroup){
				if(tgroup.getLongitude()==0 || tgroup.getLatitude()==0){
					
				}else{
					System.out.println("用户1经度：" + str_longitude);
					System.out.println("用户1纬度：" + str_latitude);
					System.out.println("用户2经度："+ tgroup.getLongitude());
					System.out.println("用户2纬度："+ tgroup.getLatitude());
					double distance = CommonUtils.getDistance(str_latitude,
							str_longitude, String.valueOf(tgroup.getLatitude()),
							String.valueOf(tgroup.getLongitude()));
					System.out.println("discance_double = " + distance);
					
					if (distance < 100) {
						displayTGroup_list.add(tgroup);
						String distance_str = distance + "";
						// distance_str = distance_str.substring(0,4);
						distance_str = distance_str.substring(0,
								distance_str.indexOf("."));
						System.out.println("NearbyNews->distance = "
								+ distance_str);
						// list_distance.add(String.valueOf(distance));
						list_distance.add(distance);
					}
					
				}
				
			}
			
			Group group;
			User builder;
			List<TGroupmember> list_tgroupmemnber_list;
			for(int i=0; i<displayTGroup_list.size();i++){
				List<User> list_member = new ArrayList<User>();
				
				TGroup tgroup = displayTGroup_list.get(i);
				group = new Group();
				group.id = tgroup.getId()+"";
				group.groupname= tgroup.getGroupname();
				group.group_img = tgroup.getGroupimg();
				group.latitude  = tgroup.getLatitude();
				group.longitude = tgroup.getLongitude();
				group.group_type = tgroup.getGroup_type();
				
				//群主信息
				builder = new User();
				builder.id = tgroup.getUser().getId()+"";
				builder.headpath = tgroup.getUser().getHeadpath();
				builder.age = tgroup.getUser().getAge();
				builder.sex = tgroup.getUser().getSex();
				builder.nickname = tgroup.getUser().getNickname();
				builder.username = tgroup.getUser().getUsername();
				group.group_bulider = builder;
				
				String dis_with_two = new java.text.DecimalFormat("#.00").format(list_distance.get(i));
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
					member.headpath = tGroupmember.getUser().getHeadpath();
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
			
			quickSortByList(list_group,0,displayTGroup_list.size()-1);
			
//			List<Group> list_group_for_client = new ArrayList<Group>();
//			for(int i=startCount-1; i<(startCount-1+10); i++){
//				if(i<list_group.size()) list_group_for_client.add(list_group.get(i));
//				
//			}
			
			
			root.success = true;
			root.message = "";
			root.group_list = list_group;
			pw.print(gson.toJson(root));
			memberDAO.close();
			userDAO.close();
			dao.close();
			pw.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	
	
	class Root {
		boolean success;
		String message;
		List<Group> group_list;
	}

	class Group {
		String id;
		String groupname;
		String group_img;
		User group_bulider;
		double distance;
		double longitude;
		double latitude;
		String point;
		int is_member;
		int member_count;
		int women_count;
		String hx_group_id;
		String attention;
		int group_type;
		List<User> list_members;
	}
	
	class User{
		String id;
		String username;
		String nickname;
		String headpath;
		int age;
		String sex;
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
