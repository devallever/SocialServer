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
import com.social.bean.News;
import com.social.bean.Response;
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
	private Response<List<News>> root;
	
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
		root = new Response<>();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		HttpSession session = request.getSession();
		
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

			Date endDate = new Date();
			
			for (TNews tnews: list_tnews) {
				news = new News();
				news.setId(String.valueOf(tnews.getId()));
				news.setCity(tnews.getCity());
				news.setCommentcount(tnews.getCommentcount());
				news.setContent(tnews.getContent());
				
				news.setUsername(tnews.getUser().getUsername());
				news.setSex(tnews.getUser().getSex());
				news.setAge(tnews.getUser().getAge());
				news.setLickcount(tnews.getLickcount());
				news.setUser_id(String.valueOf(tnews.getUser().getId()));// 发布者的id
				news.setUser_head_path(tnews.getUser().getHeadpath());
				System.out.println(news.getUser_head_path());
				news.setNickname(tnews.getUser().getNickname());
				news.setNews_voice_path(tnews.getAudio_path());
				Date startDate = tnews.getDate();
				String result = DateTimeUtils.getDatePoor(endDate,startDate);
				System.out.println("result ============================================ "+ result);
				
				//news.date = DateTimeUtils.getTime2(old_date, date);
				news.setDate(result);
				
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
					news.setIsLiked(0);// 未赞过
				} else {
					String user_id = session.getAttribute("id").toString();// 登录用户的id
					System.out.println("session_id = " + session.getId());
					if (likeDAO.getByQuery(
							"user_id=" + user_id + " and news_id= "
									+ tnews.getId(), 0, 0).size() > 0) {
						news.setIsLiked(1);// 赞过
					} else {
						news.setIsLiked(0);// 未赞过
					}
				}

				news.setNews_image_path(list_news_img);
				list_news.add(news);

			}
			
			root.setSuccess(true);
			root.setMessage("");
			root.setData(list_news);
			pw.print(gson.toJson(root));

			likeDAO.close();
			pw.close();
			dao.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			likeDAO.close();
			pw.close();
			dao.close();
		}

	}
}
