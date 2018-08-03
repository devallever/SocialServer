package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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
import com.social.dao.SignDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TSign;
import com.social.pojo.TUser;
import com.social.servlet.GetCreditServlet.Root;
import com.sun.openpisces.Dasher;


/**
 * Servlet implementation class GetSingDataServlet
 */
@WebServlet("/GetSignDataServlet")
public class GetSignDataServlet extends HttpServlet {
	private String username;
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
		
		username = request.getParameter("username");
		if(!checkParameter(response, "username", username, root, pw)) return;
		
		SignDAO dao = new SignDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		TSign tsign;
		List<TSign> list_tsign;
		TUser tuser;
		Sign sign;
		try {
			tuser = userDAO.getByQuery("username='" + username + "'", 0, 0).get(0);
			list_tsign = dao.getByQuery("user_id=" + user_id, 0, 0);
			sign = new Sign();
			if(list_tsign.size() == 0){
				sign.username = tuser.getUsername();
				sign.is_sign = 0;
				sign.day_count = 0;
			}else{
				tsign = list_tsign.get(0);
				sign.username = tsign.getUser().getUsername();
				Date today_date = new Date();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss ");//转换格式
				String today = sdf.format(today_date).toString().split(" ")[0];
				System.out.println("today = " + today);
				String sign_date = tsign.getSign_date().toString().split(" ")[0];
				System.out.println("sign_date = " + sign_date);
				if(today.equals(sign_date)) sign.is_sign = 1;//已签到
				else sign.is_sign = 0;//未签到
				
				//sign.day_count = tsign.getDay_count();
				
				if(tsign.getDay_count()>=100) sign.day_count=0;
				else sign.day_count = tsign.getDay_count();
				
			}
			
			root.sign = sign;
			root.success = true;
			root.message = "";
			pw.print(gson.toJson(root));
			pw.close();
			userDAO.close();
			dao.close();
			
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
		Sign sign;
		
	}
	
	class Sign{
		String username;
		int is_sign;
		int day_count;
	}
}
