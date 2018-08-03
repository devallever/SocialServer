package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.UserDAO;
import com.social.pojo.TUser;

/**
 * Servlet implementation class GetVideoFeeSettingServlet
 */
@WebServlet("/GetVideoFeeSettingServlet")
public class GetVideoFeeSettingServlet extends HttpServlet {
	
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
		if(session == null || session.getAttribute("id") == null){
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
		}
		user_id = session.getAttribute("id").toString();
		
		//FeeDAO dao = new FeeDAO();
		UserDAO dao = new UserDAO();
		List<TUser> list_tuser;
		TUser tuser;
		Fee fee;
		try {
			list_tuser = dao.getByQuery("id =" + user_id, 0, 0);
			tuser = list_tuser.get(0);
//			if(list_tuser.size()>0){
//				tuser = list_tfee.get(0);
//			}else{
//				tuser = new T();
//				tuser.setAccept_video(1);
//				tuser.setUser(userDAO.getById(Long.parseLong(user_id)));
//				tuser.setVideo_fee(0);
//				tuser.setVoice_fee(0);
//				tuser.setOptlockversion1(Long.parseLong("0"));
//				tuser.setOptlockversion2(Long.parseLong("0"));
//				tuser.add(tfee);
//			}
			
			fee = new Fee();
			fee.accept_video = tuser.getAccept_video();
			fee.video_fee = tuser.getVideo_fee();
			root.fee = fee;
			root.success = true;
			root.message = "";
			
			dao.close();
			//dao.close();
			pw.print(gson.toJson(root));
			pw.close();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
 		
		
		
	}
	
	
	class Root{
		boolean success;
		String message;
		Fee fee;
	}
	class Fee{
		int accept_video;
		int video_fee;
	}
	

}
