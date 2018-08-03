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
import com.social.dao.PostDAO;
import com.social.dao.RecruitDAO;
import com.social.dao.RecruitImageDAO;
import com.social.pojo.TPost;
import com.social.pojo.TRecruit;
import com.social.pojo.TRecruitImage;
import com.social.servlet.NearbyNewsListServlet.News;
import com.social.servlet.NearbyNewsListServlet.Root;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class NearbyRecruitListServlet
 */
@WebServlet("/NearbyRecruitListServlet")
public class NearbyRecruitListServlet extends HttpServlet {
	
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
			
			
			RecruitDAO dao = new RecruitDAO();
			RecruitImageDAO recruitImageDAO = new RecruitImageDAO(dao.getSession());
			PostDAO postDAO = new PostDAO(dao.getSession());
			
			List<TRecruit> list_trecruit;
			List<TRecruit> displayTRecruit_list = new ArrayList<TRecruit>();
			List<Double> list_distance = new ArrayList<Double>();
			List<Recruit> list_recruit = new ArrayList<NearbyRecruitListServlet.Recruit>();
			try {
				list_trecruit = dao.getByQuery("", 0, 0);
				for(TRecruit tRecruit : list_trecruit){
					if(tRecruit.getLongitude()==0
							|| tRecruit.getLatitude()==0){
						
					}else{
						double distance = CommonUtils.getDistance(str_latitude,
								str_longitude, String.valueOf(tRecruit.getLatitude()),
								String.valueOf(tRecruit.getLongitude()));
						System.out.println("discance_double = " + distance);
						if(distance < 100){
							displayTRecruit_list.add(tRecruit);
							String distance_str = distance + "";
							distance_str = distance_str.substring(0,distance_str.indexOf("."));
							list_distance.add(distance);
						}
					}
				}
				
				Recruit recruit;
				for(int i=0; i< displayTRecruit_list.size(); i++){
					TRecruit tRecruit = displayTRecruit_list.get(i);
					recruit = new Recruit();
					recruit.id = tRecruit.getId()  + "";
					recruit.companyname = tRecruit.getCompanyname();
					recruit.requirement = tRecruit.getRequirement();
					
					recruit.link = tRecruit.getLink();
					recruit.phone = tRecruit.getPhone();
					recruit.date = tRecruit.getDate().toString().split(" ")[0];
					if (session == null || session.getAttribute("id") == null) {
						// 未登录
						//news.isLiked = 0;// 未赞过
						recruit.is_owner = 0;
					} else {
						String user_id = session.getAttribute("id").toString();// 登录用户的id
						if(user_id.equals(tRecruit.getUser().getId()+"")){
							recruit.is_owner = 1;
						}else{
							recruit.is_owner = 0;
						}
					}
					
					String dis_with_two = new java.text.DecimalFormat("#.00").format(list_distance.get(i));
					recruit.distance = Double.parseDouble(dis_with_two);
					recruit.user_head_path = tRecruit.getUser().getHeadpath();
					
					List<TRecruitImage> list_iamge;
					List<String> list_recruit_img = new ArrayList<String>();
					list_iamge = recruitImageDAO.getByQuery("recruit_id=" + tRecruit.getId(), 0, 0);
					for(TRecruitImage tRecruitImage : list_iamge){
						list_recruit_img.add(tRecruitImage.getPath());
					}
					recruit.recruit_image_path = list_recruit_img;
					
					List<TPost> list_tPost;
					List<Post> list_post = new ArrayList<Post>();
					Post post;
					list_tPost = postDAO.getByQuery("recruit_id = " + tRecruit.getId(), 0, 0);
					for(TPost tpost : list_tPost){
						post = new Post();
						post.id = tpost.getId() + "";
						post.postname = tpost.getPostname();
						post.salary = tpost.getSalary();
						list_post.add(post);
					}
					recruit.list_post = list_post;
					list_recruit.add(recruit);
				}
				
				
				quickSortByList(list_recruit, 0, displayTRecruit_list.size()-1);
				List<Recruit> list_recruit_for_client = new ArrayList<NearbyRecruitListServlet.Recruit>();
				for(int i=startCount-1; i<(startCount-1+10); i++){
					if(i<list_recruit.size()) list_recruit_for_client.add(list_recruit.get(i));
				}
				
				root.success  =true;
				root.message = "";
				root.list_recruit = list_recruit;//未改前
				pw.print(gson.toJson(root));
				recruitImageDAO.close();
				postDAO.close();
				dao.close();
				pw.close();
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
	}
	
	
	
	class Root{
		boolean success;
		String message;
		List<Recruit> list_recruit;
		
	}
	
	class Recruit{
		String id;
		String companyname;
		double distance;
		String requirement;
		String date;
		String user_head_path;
		String link;
		String phone;
		int is_owner;
		List<String> recruit_image_path;
		List<Post> list_post;
	}
	
	class Post{
		String id;
		String postname;
		String salary;
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
	private void quickSortByList(List<Recruit> list, int lo0, int hi0) {
		int lo = lo0;
		int hi = hi0;
		if (lo >= hi)
			return;
		// 确定指针方向的逻辑变量
		boolean transfer = true;

		while (lo != hi) {
			if (list.get(lo).distance > list.get(hi).distance) {
				// 交换
				Recruit temp = list.get(lo);
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
