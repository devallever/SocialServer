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
import com.social.dao.RecruitImageDAO;
import com.social.pojo.TPost;
import com.social.pojo.TRecruit;
import com.social.pojo.TRecruitImage;
import com.social.util.CommonUtils;


/**
 * Servlet implementation class PostDetailServlet
 */
@WebServlet("/PostDetailServlet")
public class PostDetailServlet extends HttpServlet {
	
	private String post_id;
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
		System.out.println(session.getId());
		
		post_id = request.getParameter("post_id");
		if(!checkParameter(response, "post_id", post_id, root, pw)) return;
		System.out.println("post_id = " + post_id);
		
		String str_longitude = request.getParameter("longitude");
		if(!checkParameter(response, "str_longitude", str_longitude, root, pw)) return;
		
		String str_latitude = request.getParameter("latitude");
		if(!checkParameter(response, "str_latitude", str_latitude, root, pw)) return;
		
		
		PostDAO dao = new PostDAO();
		TPost tpost;
		TRecruit trecruit;
		Post post;
		Recruit recruit;
		
		RecruitImageDAO recruitImageDAO = new RecruitImageDAO(dao.getSession());
		List<TRecruitImage> list_tImages;
		List<String> list_img = new ArrayList<String>();
		
		try {
			tpost = dao.getById(Long.parseLong(post_id));
			trecruit = tpost.getRecruit();
			post = new Post();
			recruit = new Recruit();
			
			post.id = tpost.getId()+ "";
			post.description = tpost.getDescription();
			post.postname = tpost.getPostname();
			post.salary = tpost.getSalary();
			post.requirement = tpost.getRequirement();
			root.post = post;
			
			recruit.address = trecruit.getAddress();
			recruit.companyname = trecruit.getCompanyname();
			recruit.date = trecruit.getDate().toString().split(" ")[0];
			
			
			if (session == null || session.getAttribute("id") == null) {
				// 未登录
				//news.isLiked = 0;// 未赞过
				recruit.is_owner = 0;
			} else {
				String user_id = session.getAttribute("id").toString();// 登录用户的id
				if(user_id.equals(trecruit.getUser().getId()+"")){
					recruit.is_owner = 1;
				}else{
					recruit.is_owner = 0;
				}
			}
			
			double distance = CommonUtils.getDistance(str_latitude,
					str_longitude, String.valueOf(trecruit.getLatitude()),
					String.valueOf(trecruit.getLongitude()));
			String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
			recruit.distance = Double.parseDouble(dis_with_two);
			
			recruit.id = trecruit.getId() + "";
			recruit.latitude = trecruit.getLatitude() + "";
			recruit.longitude = trecruit.getLongitude() + "";
			recruit.link = trecruit.getLink();
			recruit.phone = trecruit.getPhone();
			recruit.username = trecruit.getUser().getUsername();
			recruit.user_head_path = trecruit.getUser().getHeadpath();
			
			list_tImages = recruitImageDAO.getByQuery("recruit_id=" + trecruit.getId(), 0, 0);
			for(TRecruitImage tRecruitImage : list_tImages){
				list_img.add(tRecruitImage.getPath());
			}
			recruit.list_recruit_img = list_img;
			
			
			root.recruit = recruit;
			
			
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
		Post post;
		Recruit recruit;
	}
	
	class Post{
		String id;
		String postname;
		String salary;
		String requirement;
		String description;
	}
	
	class Recruit{
		 String id;
		 String companyname;
		 String date;
		 double distance;
		 String longitude;
		 String latitude;
		 String phone;
		 String link;
		 String address;
		 int is_owner;
		 String username;
		 String user_head_path;
		 List<String> list_recruit_img;
	}

}
