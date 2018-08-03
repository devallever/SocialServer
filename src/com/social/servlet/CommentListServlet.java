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
import com.social.dao.CommentDAO;
import com.social.pojo.TComment;


/**
 * Servlet implementation class CommentListServlet
 */
@WebServlet("/CommentListServlet")
public class CommentListServlet extends HttpServlet {
	private String news_id;
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
//		if(session == null || session.getAttribute("id") == null){
//			root.message = "未登录";
//			root.success = false;
//			pw.print(gson.toJson(root));
//			pw.close();
//		}
		
		//获取参数
		
		news_id = request.getParameter("news_id");
		if(!checkParameter(response, "news_id", news_id, root, pw)) return;
		
		CommentDAO dao = new CommentDAO();
		List<TComment> list_tcomment;
		List<Comment> list_comment = new ArrayList<Comment>();
		try {
			list_tcomment = dao.getByQuery("news_id="+ news_id + "order by date desc", 0, 0);
			if(list_tcomment.size()>0){
				for(TComment tComment : list_tcomment){
					Comment comment = new Comment();
					comment.id = String.valueOf(tComment.getId());
					if(tComment.getComment()!=null){
						comment.comment_id = String.valueOf(tComment.getComment().getId());
					}else{
						comment.comment_id = "-1";//
					}
					
					comment.content = tComment.getContent();
					String all_day = tComment.getDate().toString().split(" ")[0];
					String month = all_day.split("-")[1];
					String day = all_day.split("-")[2];
					String time = tComment.getDate().toString().split(" ")[1];
					String hour = time.split(":")[0];
					String min = time.split(":")[1];
					String date = month+"-"+day + " " + hour + ":" + min;
					comment.date = date;
					comment.nickname = String.valueOf(tComment.getUser().getNickname());
					comment.username = tComment.getUser().getUsername();
					comment.user_head_path = tComment.getUser().getHeadpath();
					comment.user_id = String.valueOf(tComment.getUser().getId());
					comment.comment_voice_path = tComment.getAudio_path();
					list_comment.add(comment);
				}
				root.success = true;
				root.message= "";
				root.list_comment = list_comment;
			}else{
				root.success = false;
				root.message = "无记录";
			}
			
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//longitude = Double.parseDouble(str_latitude);
		
	}
	
	class Root{
		boolean success;
		String message;
		List<Comment> list_comment;
	}
	class Comment{
		String id;
		String content;
		String user_id;
		String nickname;
		String username;
		String user_head_path;
		String date;
		String comment_id;
		String comment_voice_path;
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
