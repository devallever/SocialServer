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
import com.social.dao.PostDAO;
import com.social.pojo.TPost;
import com.social.servlet.NearbyRecruitListServlet.Recruit;
import com.social.servlet.NearbyRecruitListServlet.Root;
import com.social.servlet.NearbyUserListServlet.User;
import com.social.util.CommonUtils;


/**
 * Servlet implementation class NearbyPostListServlet
 */
@WebServlet("/NearbyPostListServlet")
public class NearbyPostListServlet extends HttpServlet {
	
	private String str_longitude;
	private String str_latitude;

	private PrintWriter pw;
	private Root root;
	
	private String str_page;
	private int page;
	private int startCount;
	private int endCount;
	
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
		// if(session == null || session.getAttribute("id") == null){
		// root.message = "未登录";
		// root.success = false;
		// pw.print(gson.toJson(root));
		// pw.close();
		// }

		// 获取参数
		str_longitude = request.getParameter("longitude");
		if (!checkParameter(response, "longitude", str_longitude, root, pw))
			return;
		// longitude = Double.parseDouble(str_latitude);

		str_latitude = request.getParameter("latitude");
		if (!checkParameter(response, "latitude", str_latitude, root, pw))
			return;
		// latitude = Double.parseDouble(str_latitude);
		
		
		str_page = request.getParameter("page");
		if(str_page == null) str_page = "1";
		page = Integer.valueOf(str_page);
		endCount = page * 1 * 10;
		startCount = endCount - 9;
		
		PostDAO dao = new PostDAO();
		List<TPost> list_tpost;
		List<TPost> display_TPost_list = new ArrayList<TPost>();
		List<Double> list_distance = new ArrayList<Double>();
		List<Post> list_post = new ArrayList<NearbyPostListServlet.Post>();
		try {
			list_tpost = dao.getByQuery("", 0, 0);
			for(TPost tpost : list_tpost){
				if(tpost.getLongitude() == 0 || tpost.getLatitude() ==0){
					
				}else{
					double distance = CommonUtils.getDistance(str_latitude,
							str_longitude, String.valueOf(tpost.getLatitude()),
							String.valueOf(tpost.getLongitude()));
					System.out.println("discance_double = " + distance);
					if(distance < 100){
						display_TPost_list.add(tpost);
						String distance_str = distance + "";
						distance_str = distance_str.substring(0,distance_str.indexOf("."));
						list_distance.add(distance);
					}
				}
			}
			
			Post post;
			for(int i=0; i<display_TPost_list.size(); i++){
				TPost tpost = display_TPost_list.get(i);
				post = new Post();
				post.id = tpost.getId()+"";
				post.postname = tpost.getPostname();
				post.salary = tpost.getSalary();
				post.user_id = tpost.getRecruit().getUser().getId()+ "";
				post.user_head_path = tpost.getRecruit().getUser().getHeadpath();
				post.recruit_id = tpost.getRecruit().getId()+"";
				post.phone = tpost.getRecruit().getPhone();
				
				if (session == null || session.getAttribute("id") == null) {
					// 未登录
					//news.isLiked = 0;// 未赞过
					post.is_owner = 0;
				} else {
					String user_id = session.getAttribute("id").toString();// 登录用户的id
					if(user_id.equals(tpost.getRecruit().getUser().getId()+"")){
						post.is_owner = 1;
					}else{
						post.is_owner = 0;
					}
				}
				
				String dis_with_two = new java.text.DecimalFormat("#.00").format(list_distance.get(i));
				post.distance = Double.parseDouble(dis_with_two);
				
				list_post.add(post);
			}
			quickSortByList(list_post,0,display_TPost_list.size()-1);
			
			List<Post> list_post_for_client = new ArrayList<Post>();
			for(int i=startCount-1; i<(startCount-1+10); i++){
				if(i<list_post.size()) list_post_for_client.add(list_post.get(i));
				
			}
			
			root.success = true;
			root.message = "";
			root.list_post= list_post_for_client;
			pw.print(gson.toJson(root));
			dao.close();
			pw.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
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
	private void quickSortByList(List<Post> list, int lo0, int hi0) {
		int lo = lo0;
		int hi = hi0;
		if (lo >= hi)
			return;
		// 确定指针方向的逻辑变量
		boolean transfer = true;

		while (lo != hi) {
			if (list.get(lo).distance > list.get(hi).distance) {
				// 交换
				Post temp = list.get(lo);
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

	
	
	class Root{
		boolean success;
		String message;
		List<Post> list_post;
	}
	
	class Post{
		String id;
		String postname;
		double distance;
		String salary;
		String user_id;
		String user_head_path;
		String recruit_id;
		int is_owner;
		String phone;
	}

}
