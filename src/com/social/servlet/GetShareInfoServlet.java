package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.ShareDAO;
import com.social.pojo.TShare;


/**
 * Servlet implementation class GetShareInfoServlet
 */
@WebServlet("/GetShareInfoServlet")
public class GetShareInfoServlet extends HttpServlet {
	
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
		
		ShareDAO dao = new ShareDAO();
		TShare tShare;
		List<TShare> list_tshare;
		try {
			list_tshare = dao.getByQuery("", 0, 0);
			if (list_tshare!=null && list_tshare.size()>0) {
				tShare = list_tshare.get(0);
			}else{
				tShare = null;
				root.success = true;
				root.message = "";
				root.content = "我获得一个新人红包，邀请你也一起来领取哦！";
				root.img_url = "http://27.54.249.252:8080/SocialServer/images/share/red_pocket.png";
				root.url = "http://27.54.249.252:8080/SocialServer/apk/social_0.15.06.apk";
				pw.print(gson.toJson(root));
				pw.close();
				dao.close();
				return;
			}
			
			
			root.content = tShare.getContent();
			root.url = tShare.getUrl();
			
			root.img_url = tShare.getImg_url();
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	class Root{
		boolean success;
		String message;
		String content;
		String url;
		String img_url;
	}
	
}
