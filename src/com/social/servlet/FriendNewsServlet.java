package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.social.dao.LikeDAO;
import com.social.dao.NewsDAO;
import com.social.dao.NewsImageDAO;
import com.social.pojo.TFollow;
import com.social.pojo.TFriend;
import com.social.pojo.TNews;
import com.social.pojo.TNewsImage;
import com.social.servlet.FriendListServlet.Friend;
import com.social.servlet.HostNewsListServlet.News;
import com.social.servlet.HostNewsListServlet.Root;
import com.social.util.CommonUtils;
import com.social.util.DateTimeUtils;

/**
 * Servlet implementation class FriendNewsServlet
 */
@WebServlet("/FriendNewsServlet")
public class FriendNewsServlet extends HttpServlet {
	
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
		if(session == null || session.getAttribute("id") == null){
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
		}
		user_id = session.getAttribute("id").toString();
		
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
		FriendDAO friendDAO = new FriendDAO(dao.getSession());
		LikeDAO likeDAO = new LikeDAO(dao.getSession());
		List<TFriend> list_tfriend;
		List<TNews> list_tnews_all = new ArrayList<TNews>();
		List<TNews> list_tnews_friend;
		List<News> list_news = new ArrayList<News>();
		News news;
		Date p_date;
		Date date_date = new Date();
		String diaplay_date;
		
		FollowDAO followDAO = new FollowDAO(dao.getSession());
		List<TFollow> list_tfollow;
		List<TNews> list_tnews_follow;
		try {
			list_tfriend = friendDAO.getByQuery("user_id=" + user_id, 0, 0);
			
			for(TFriend tfriend: list_tfriend){
				list_tnews_friend = dao.getByQuery("user_id=" + tfriend.getFriend().getId(), 0, 0);//单个朋友的全部动态
				for(TNews tNews: list_tnews_friend){
					list_tnews_all.add(tNews);
				}
			}
			
			long[] arr_friend_id = new long[list_tfriend.size()];
			for (int i = 0; i < list_tfriend.size(); i++) {
				arr_friend_id[i] = list_tfriend.get(i).getId();
			}
			StringBuilder friend_id_builder = new StringBuilder();
			for (int i = 0; i < arr_friend_id.length; i++) {
				friend_id_builder.append(arr_friend_id[i]);
				if (i == arr_friend_id.length - 1) {
					
				}else {
					friend_id_builder.append(",");
				}
			}
			String str_friend_id_in_collection = friend_id_builder.toString();
			System.out.println(str_friend_id_in_collection);
			
			String hql;
			if(str_friend_id_in_collection.length() == 0){
				hql = "from TFollow where user_id=" + user_id;
			}else{
				hql = "from TFollow where user_id=" + user_id + " and follow_id not in(" + str_friend_id_in_collection + ")";
			}
			
			list_tfollow = followDAO.getByHQL(hql, 0, 0);
			for(TFollow tfollow: list_tfollow){
				list_tnews_follow = dao.getByQuery("user_id=" + tfollow.getFollow().getId(), 0, 0);//单个朋友的全部动态
				for(TNews tNews: list_tnews_follow){
					list_tnews_all.add(tNews);
				}
			}
			
			
			
			
			for(TNews tnews: list_tnews_all){
				news = new News();
				news.id = String.valueOf(tnews.getId());
				news.city = tnews.getCity();
				news.commentcount = tnews.getCommentcount();
				news.content = tnews.getContent();
				news.dateDate = tnews.getDate();
				
				
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
			
			
			sortByMethod(list_news, "getDateDate", true);
			
			List<News> list_news_for_client = new ArrayList<News>();
			for(int i=startCount-1; i<(startCount-1+10); i++){
				if(i<list_news.size()) list_news_for_client.add(list_news.get(i));
				
			}
			
			root.success = true;
			root.message = "";
			root.news_list = list_news_for_client;
			
			pw.print(gson.toJson(root));
			likeDAO.close();
			friendDAO.close();
			pw.close();
			dao.close();
			
			//sortByMethod(list_tnews_all, method, reverseFlag);
			
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
	
	
	@SuppressWarnings("unused")
	private void sortByMethod(List<News> list, final String method,
            final boolean reverseFlag) {
        Collections.sort(list, new Comparator<Object>() {
            public int compare(Object arg1, Object arg2) {
                int result = 0;
                try {
                    Method m1 = ((News) arg1).getClass().getMethod(method, null);
                    Method m2 = ((News) arg2).getClass().getMethod(method, null);
                    Object obj1 = m1.invoke(((News)arg1), null);
                    Object obj2 = m2.invoke(((News)arg2), null);
                    if(obj1 instanceof String) {
                        // 字符串
                        result = obj1.toString().compareTo(obj2.toString());
                    }else if(obj1 instanceof Date) {
                        // 日期
                        long l = ((Date)obj1).getTime() - ((Date)obj2).getTime();
                        if(l > 0) {
                            result = 1;
                        }else if(l < 0) {
                            result = -1;
                        }else {
                            result = 0;
                        }
                    }else if(obj1 instanceof Integer) {
                        // 整型（Method的返回参数可以是int的，因为JDK1.5之后，Integer与int可以自动转换了）
                        result = (Integer)obj1 - (Integer)obj2;
                    }else {
                        // 目前尚不支持的对象，直接转换为String，然后比较，后果未知
                        result = obj1.toString().compareTo(obj2.toString());
                        
                        System.err.println("MySortList.sortByMethod方法接受到不可识别的对象类型，转换为字符串后比较返回...");
                    }
                    
                    if (reverseFlag) {
                        // 倒序
                        result = -result;
                    }
                } catch (NoSuchMethodException nsme) {
                    nsme.printStackTrace();
                } catch (IllegalAccessException iae) {
                    iae.printStackTrace();
                } catch (InvocationTargetException ite) {
                    ite.printStackTrace();
                }

                return result;
            }
        });
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
		Date dateDate;
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
		
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getUser_id() {
			return user_id;
		}
		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getLongitude() {
			return longitude;
		}
		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
		public String getNickname() {
			return nickname;
		}
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getSex() {
			return sex;
		}
		public void setSex(String sex) {
			this.sex = sex;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public String getLatitude() {
			return latitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		public String getUser_head_path() {
			return user_head_path;
		}
		public void setUser_head_path(String user_head_path) {
			this.user_head_path = user_head_path;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public double getDistance() {
			return distance;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
		public int getIsLiked() {
			return isLiked;
		}
		public void setIsLiked(int isLiked) {
			this.isLiked = isLiked;
		}
		public int getCommentcount() {
			return commentcount;
		}
		public void setCommentcount(int commentcount) {
			this.commentcount = commentcount;
		}
		public int getLickcount() {
			return lickcount;
		}
		public void setLickcount(int lickcount) {
			this.lickcount = lickcount;
		}
		public List<String> getNews_image_path() {
			return news_image_path;
		}
		public void setNews_image_path(List<String> news_image_path) {
			this.news_image_path = news_image_path;
		}
		public Date getDateDate() {
			return dateDate;
		}
		public void setDateDate(Date dateDate) {
			this.dateDate = dateDate;
		}
		
		
		
		
		
		
	}
	
}
