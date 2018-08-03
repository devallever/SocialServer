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
import com.social.pojo.TPost;
import com.social.servlet.RecruitDataServlet.Root;


/**
 * Servlet implementation class PostListServlet
 */
@WebServlet("/PostListServlet")
public class PostListServlet extends HttpServlet {
	
	private String recruit_id;
	private String user_id;
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
		
		recruit_id = request.getParameter("recruit_id");
		if(!checkParameter(response, "recruit_id", recruit_id, root, pw)) return;
		Post post;
		PostDAO dao = new PostDAO();
		List<TPost> list_tpost;
		List<Post> list_post = new ArrayList<PostListServlet.Post>();
		try {
			list_tpost = dao.getByQuery("recruit_id=" + recruit_id,0, 0);
			for(TPost tpost : list_tpost){
				post = new Post();
				post.id = tpost.getId() + "";
				post.postname = tpost.getPostname();
				post.salary = tpost.getSalary();
				list_post.add(post);
			}
			
			root.list_post = list_post;
			root.success = true;
			root.message = "";
			
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
	
	
	class Root{
		boolean success;
		String message;
		List<Post> list_post;
	}
	
	class Post{
		String id;
		String postname;
		String salary;
	}

}
