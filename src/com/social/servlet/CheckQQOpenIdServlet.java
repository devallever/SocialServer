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
import com.social.bean.QQ;
import com.social.bean.Response;
import com.social.dao.UserDAO;
import com.social.pojo.TUser;
import com.social.servlet.LikeServlet.Root;

/**
 * Servlet implementation class CheckQQOpenIdServlet
 */
@WebServlet("/CheckQQOpenIdServlet")
public class CheckQQOpenIdServlet extends HttpServlet {
	
	private String open_id;
	
	private PrintWriter pw;
	private Response<QQ> root;
	
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		pw = response.getWriter();
		root = new Response<>();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
		//获取参数
		open_id = request.getParameter("open_id");
		if(!checkParameter(response, "open_id", open_id, root, pw)) return;
		
		UserDAO dao = new UserDAO();
		List<TUser> list_tuser;
		try {
			list_tuser = dao.getByQuery("qq_open_id = '" + open_id + "'", 0, 0);
			if(list_tuser.size()>0) root.getData().setExist(1);
			else root.getData().setExist(0);			
			root.setSuccess(true);
			root.setMessage("");
			
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	
	
	private  boolean checkParameter(HttpServletResponse response,String parameterName ,String parameterValue , Response<QQ> root, PrintWriter pw){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		if (parameterValue == null || parameterValue.length() == 0) {
			root.setSuccess(false);
			root.setMessage("没有指定" + parameterName + "参数");
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
