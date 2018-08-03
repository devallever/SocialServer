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
 * Servlet implementation class SaveVideoFeeSettingServlet
 */
@WebServlet("/SaveVideoFeeSettingServlet")
public class SaveVideoFeeSettingServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	private String accept_video;
	private String video_fee;
	
	
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
		
		accept_video = request.getParameter("accept_video");//
		video_fee = request.getParameter("video_fee");//
		
		UserDAO dao = new UserDAO();
		//UserDAO userDAO = new UserDAO(dao.getSession());
		List<TUser> list_tuser;
		TUser tuser;
		try {
			list_tuser = dao.getByQuery("id =" + user_id, 0, 0);
			
			tuser = list_tuser.get(0);
			tuser.setAccept_video(Integer.valueOf(accept_video));
			tuser.setVideo_fee(Integer.valueOf(video_fee));
			dao.update(tuser);
			
//			if(list_tfee.size()>0){
//				
//			}else{
//				tfee = new TFee();
//				tfee.setAccept_video(Integer.valueOf(accept_video));
//				tfee.setUser(userDAO.getById(Long.parseLong(user_id)));
//				tfee.setVideo_fee(Integer.valueOf(video_fee));
//				tfee.setVoice_fee(1);
//				tfee.setOptlockversion1(Long.parseLong("0"));
//				tfee.setOptlockversion2(Long.parseLong("0"));
//				tfee = dao.add(tfee);
//			}
			
			
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
	}

}
