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
import com.social.dao.LikeDAO;
import com.social.dao.NewsDAO;
import com.social.dao.NewsImageDAO;
import com.social.pojo.TNews;
import com.social.pojo.TNewsImage;
import com.social.servlet.NearbyNewsListServlet.News;
import com.social.util.CommonUtils;
import com.social.util.DateTimeUtils;

/**
 * Servlet implementation class HostNewsListServlet
 */
@WebServlet("/HostNewsListServlet")
public class HostNewsListServlet extends HttpServlet {
	private String user_id;
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
//		if(session == null || session.getAttribute("id") == null){
//			root.message = "未登录";
//			root.success = false;
//			pw.print(gson.toJson(root));
//			pw.close();
//		}
//		user_id = session.getAttribute("id").toString();
		
		//获取参数
		str_longitude = request.getParameter("longitude");
		if(!checkParameter(response, "longitude", str_longitude, root, pw)) return;
		//longitude = Double.parseDouble(str_latitude);
		
		str_latitude = request.getParameter("latitude");
		if(!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		//latitude = Double.parseDouble(str_latitude);
		
		str_page = request.getParameter("page");
		if(str_page == null) str_page = "1";
		page = Integer.valueOf(str_page);
		endCount = page * 1 * 10;
		startCount = endCount - 9;
		
		NewsDAO dao = new NewsDAO();
		LikeDAO likeDAO = new LikeDAO(dao.getSession());
		List<TNews> list_all_tnews;
		List<News> list_news = new ArrayList<News>();
		News news;
		Date p_date;
		Date date_date = new Date();
		String diaplay_date;
		
		try {
			list_all_tnews = dao.getHotNewsList(0, 0);//浏览数降序排列
			//list_all_tnews = dao.getByQuery("order by visited_count desc", 0, 0);
			for(TNews tnews : list_all_tnews){
				news = new News();
				news.id = String.valueOf(tnews.getId());
				news.city = tnews.getCity();
				news.commentcount = tnews.getCommentcount();
				news.content = tnews.getContent();
				String all_day = tnews.getDate().toString().split(" ")[0];
				String month = all_day.split("-")[1];
				String day = all_day.split("-")[2];
				String time = tnews.getDate().toString().split(" ")[1];
				String hour = time.split(":")[0];
				String min = time.split(":")[1];
				String date = month+"-"+day + " " + hour + ":" + min;
				news.date = date;
				
				p_date = tnews.getDate();
				//date_date = 
				diaplay_date = DateTimeUtils.getDisplayTime(p_date, date_date);
				news.date = diaplay_date;
				
				news.latitude = String.valueOf(tnews.getLatitude());
				
				if(tnews.getLongitude()==0 && tnews.getLatitude()==0){
					news.distance= -1;//未知距离
				}else{
					news.distance= CommonUtils.getDistance(str_latitude, str_longitude, String.valueOf(tnews.getLatitude()), String.valueOf(tnews.getLongitude()));
				}
				
				String dis_with_two = new java.text.DecimalFormat("#.00").format(news.distance);
				news.distance = Double.parseDouble(dis_with_two);
				news.longitude = String.valueOf(tnews.getLongitude());
				news.lickcount = tnews.getLickcount();
				news.user_id = String.valueOf(tnews.getUser().getId());
				news.nickname = tnews.getUser().getNickname();
				news.username = tnews.getUser().getUsername();
				news.sex = tnews.getUser().getSex();
				news.age = tnews.getUser().getAge();
				news.user_head_path = tnews.getUser().getHeadpath();
				news.news_voice_path = tnews.getAudio_path();
				
				
				if(session == null || session.getAttribute("id") == null){
					//未登录
					news.isLiked = 0;//未赞过
				}else{
					String user_id = session.getAttribute("id").toString();
					if(likeDAO.getByQuery("user_id=" + user_id+ " and news_id= " + tnews.getId(), 0, 0).size()>0){
						news.isLiked = 1;//赞过
					}else{
						news.isLiked = 0;//未赞过
					}
				}
				
				
				NewsImageDAO newsImageDAO = new NewsImageDAO(dao.getSession());
				List<TNewsImage> list_image;
				List<String> list_news_img = new ArrayList<String>();
				list_image = newsImageDAO.getByQuery("news_id=" + tnews.getId(), 0, 0);
				newsImageDAO.close();
				for(TNewsImage newsImage : list_image){
					list_news_img.add(newsImage.getPath());
				}
				
				
				news.news_image_path = list_news_img;
				list_news.add(news);
				
			}
			
			List<News> list_news_for_client = new ArrayList<News>();
			for(int i=startCount-1; i<(startCount-1+10); i++){
				if(i<list_news.size()) list_news_for_client.add(list_news.get(i));
				
			}
			
			root.success = true;
			root.message = "";
			root.news_list = list_news_for_client;
			pw.print(gson.toJson(root));
			likeDAO.close();
			pw.close();
			dao.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	class Root{
		boolean success;
		String message;
		List<News> news_list;
	}
	
	class News{
		String id;
		String content;
		String user_id;
		String date;
		String longitude;
		String nickname;
		String username;
		String sex;
		int age;
		String latitude;
		String user_head_path;
		String city;
		double distance;
		int isLiked;
		int commentcount;
		int lickcount;
		String news_voice_path;
		List<String> news_image_path;
		
		
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
