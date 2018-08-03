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
import com.mysql.fabric.xmlrpc.base.Array;
import com.social.dao.GroupDAO;
import com.social.dao.GroupmemberDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TGroup;
import com.social.pojo.TGroupmember;
import com.social.pojo.TUser;
import com.social.servlet.ConnectionServlet.Root;
import com.social.servlet.NearbyGroupListServlet.Group;
import com.social.servlet.NearbyGroupListServlet.User;
import com.social.util.CommonUtils;



/**
 * Servlet implementation class GetRecommendGroupInfo
 */
@WebServlet("/GetRecommendGroupInfo")
public class GetRecommendGroupInfo extends HttpServlet {
	private PrintWriter pw;
	private Root root;
	private String user_id;
	
	private String str_longitude;
	private String str_latitude;
	
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	@SuppressWarnings("unused")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		pw = response.getWriter();
		root = new Root();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		HttpSession session = request.getSession();
		if (session == null || session.getAttribute("id") == null) {
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		// 获取参数
		str_longitude = request.getParameter("longitude");
		if(str_longitude==null) str_longitude = "0";
		//if (!checkParameter(response, "longitude", str_longitude, root, pw))return;
		// longitude = Double.parseDouble(str_latitude);

		str_latitude = request.getParameter("latitude");
		if(str_latitude==null) str_latitude = "0";
		//if (!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		
		UserDAO dao = new UserDAO();
		TUser tuser = null;
		GroupDAO groupDAO = new GroupDAO(dao.getSession());
		GroupmemberDAO groupmemberDAO = new GroupmemberDAO(dao.getSession());
		List<TGroup> list_tgroup;
		
		try {
			tuser = dao.getById(Long.parseLong(user_id));
			if(tuser == null){
				root.success = false;
				root.message = "";
				groupmemberDAO.close();
				groupDAO.close();
				dao.close();
				return;
			}
			if (tuser.getLogin_count() <= 3) {
				//第一次登录
				root.is_first_login = 1;
				//第一次登录就不会加入任何群组
				list_tgroup = groupDAO.getByQuery("group_type=4", 0, 0);
				List<Group> list_group = new ArrayList<GetRecommendGroupInfo.Group>();
				Group group;
				List<TGroupmember> list_tgroupmemnber_list;
				System.out.println("list_tgroup.size = " + list_tgroup.size());
				if (list_group ==null && list_group.size()==0) {
					root.group = null;
				}else{
					for(TGroup tgroup:list_tgroup){
						
						list_tgroupmemnber_list = groupmemberDAO.getByQuery("group_id=" + tgroup.getId() + " and user_id="+user_id, 0, 0);
						
						if(list_tgroupmemnber_list == null || list_tgroupmemnber_list.size() == 0){
							//不是成员
							group = new Group();
							group.id = tgroup.getId()+"";
							group.groupname = tgroup.getGroupname();
							group.group_img = tgroup.getGroupimg();
							group.hx_group_id = tgroup.getHx_group_id();
							
							double distance = CommonUtils.getDistance(	str_latitude,
																		str_longitude, 
																		String.valueOf(tgroup.getLatitude()),
																		String.valueOf(tgroup.getLongitude()));
							//String dis_with_two = new java.text.DecimalFormat("#.00").format(list_distance.get(i));
							String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
							group.distance = Double.parseDouble(dis_with_two);
							
							//group.distance= tgroup.get
							//群主人数
							list_tgroupmemnber_list = groupmemberDAO.getByQuery("group_id=" + tgroup.getId(), 0, 0);
							int group_member_count = list_tgroupmemnber_list.size();
							group.member_count = group_member_count;
							
							//女生人数
							int women_count = 0;
							for(TGroupmember tGroupmember: list_tgroupmemnber_list){
								if(tGroupmember.getUser().getSex().equals("女")){
									women_count++;
								}
								
							}
							group.women_count = women_count;
							
							list_group.add(group);
						}else{
							//是成员
							root.group = null;
						}
						
						
					}
					
					//根据distance进行排序
					quickSortByList(list_group,0,list_group.size()-1);
					
					if(list_group.size()>0)
						root.group = list_group.get(0);
					root.success = true;
					root.message = "";
				}
				
				
			}else{
				//非第一次登录
				root.success = true;
				root.message = "不是第一次登录";
				root.is_first_login = 0;
				root.group = null;
			}
			
		
			pw.print(gson.toJson(root));
			groupDAO.close();
			groupmemberDAO.close();
			dao.close();
			pw.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}

	class Root{
		boolean success;
		String message;
		int is_first_login;
		Group group;
	}
	
	class Group{
		String id;
		String groupname;
		String group_img;
		double distance;
		int member_count;
		int women_count;
		String hx_group_id;
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
