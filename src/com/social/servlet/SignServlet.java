package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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

/**
 * Servlet implementation class SignServlet
 */
@WebServlet("/SignServlet")
public class SignServlet extends HttpServlet {
	
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
		TUser tuser;
		List<TSign> list_tsign;
		TSign tsign;
		Sign sign;
		try {
			list_tsign = dao.getByQuery("user_id=" + user_id, 0, 0);
			tuser = userDAO.getById(Long.parseLong(user_id));
			if(list_tsign.size()==0){
				//add
				tsign = new TSign();
				tsign.setDay_count(1);
				tsign.setOptlockversion1(Long.parseLong("0"));
				tsign.setOptlockversion1(Long.parseLong("0"));
				tsign.setSign_date(new Date());
				tsign.setUser(tuser);
				dao.add(tsign);
			}else{
				//update
				tsign = list_tsign.get(0);
				int day_count = tsign.getDay_count();
				day_count = day_count + 1;
				if(day_count>100) day_count = 1;
				tsign.setDay_count(day_count);
				tsign.setSign_date(new Date());
				
				if(day_count==100){
					int credit = tuser.getCredit();
					credit = credit + 1;
					tuser.setCredit(credit);
					userDAO.update(tuser);
				}
				
				dao.update(tsign);
			}
			
			sign = new Sign();
			sign.day_count = tsign.getDay_count();
			sign.is_sign = 1;
			sign.username = tsign.getUser().getUsername();
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
