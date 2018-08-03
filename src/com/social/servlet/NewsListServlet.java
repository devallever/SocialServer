package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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
import com.social.util.DateTimeUtils;

/**
 * Servlet implementation class NewsListServlet
 */
@WebServlet("/NewsListServlet")
public class NewsListServlet extends HttpServlet {

	private PrintWriter pw;
	private Root root;
	
	private String str_page;
	private int page;
	private int startCount;
	private int endCount;

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
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

		
		str_page = request.getParameter("page");
		if(str_page == null) str_page = "1";
		page = Integer.valueOf(str_page);
		endCount = page * 1 * 10;
		startCount = endCount - 9;
	
		
		NewsDAO dao = new NewsDAO();
		LikeDAO likeDAO = new LikeDAO(dao.getSession());
		List<TNews> list_tnews;
		List<News> list_news = new ArrayList<News>();
		try {
			list_tnews = dao.getByQueryOrderByDateDesc(startCount-1, endCount - startCount + 1);
			News news;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = format.format(new Date());
			for (TNews tnews: list_tnews) {
				news = new News();
				news.id = String.valueOf(tnews.getId());
				news.city = tnews.getCity();
				news.commentcount = tnews.getCommentcount();
				news.content = tnews.getContent();
				
				news.username = tnews.getUser().getUsername();
				news.sex = tnews.getUser().getSex();
				news.age= tnews.getUser().getAge();
				news.lickcount = tnews.getLickcount();
				news.user_id = String.valueOf(tnews.getUser().getId());// 发布者的id
				news.user_head_path = tnews.getUser().getHeadpath();
				System.out.println(news.user_head_path);
				news.nickname = tnews.getUser().getNickname();
				news.news_voice_path = tnews.getAudio_path();
				
				String old_date  = format.format(tnews.getDate());
				news.date = DateTimeUtils.getTime2(old_date, date);
				
				NewsImageDAO newsImageDAO = new NewsImageDAO(dao.getSession());
				List<TNewsImage> list_image;
				List<String> list_news_img = new ArrayList<String>();
				list_image = newsImageDAO.getByQuery(
						"news_id=" + tnews.getId(), 0, 0);
				newsImageDAO.close();
				for (TNewsImage newsImage : list_image) {
					list_news_img.add(newsImage.getPath());
				}
				
				if (session == null || session.getAttribute("id") == null) {
					// 未登录
					news.isLiked = 0;// 未赞过
				} else {
					String user_id = session.getAttribute("id").toString();// 登录用户的id
					System.out.println("session_id = " + session.getId());
					if (likeDAO.getByQuery(
							"user_id=" + user_id + " and news_id= "
									+ tnews.getId(), 0, 0).size() > 0) {
						news.isLiked = 1;// 赞过
					} else {
						news.isLiked = 0;// 未赞过
					}
				}

				news.news_image_path = list_news_img;
				list_news.add(news);

			}
			
			root.success = true;
			root.message = "";
			root.news_list = list_news;
			pw.print(gson.toJson(root));

			likeDAO.close();
			pw.close();
			dao.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class Root {
		boolean success;
		String message;
		List<News> news_list;
	}

	class News {
		String id;
		String content;
		String user_id;
		String nickname;
		String username;
		String sex;
		int age;
		String date;
		String longitude;
		String latitude;
		String city;
		double distance;
		String user_head_path;
		int commentcount;
		int lickcount;
		int isLiked;
		String news_voice_path;
		List<String> news_image_path;
	}
//
//	private boolean checkParameter(HttpServletResponse response,
//			String parameterName, String parameterValue, Root root,
//			PrintWriter pw) {
//		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//		if (parameterValue == null || parameterValue.length() == 0) {
//			root.success = false;
//			root.message = "没有指定" + parameterName + "参数";
//			response.setContentType("text/html;charset=utf-8");
//			try {
//				pw = response.getWriter();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			pw.print(gson.toJson(root));
//			// pw.close();
//			return false;
//		}
//		System.out.println(parameterValue);
//		return true;
//	}



}
