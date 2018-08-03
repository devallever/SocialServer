package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.UserDAO;
import com.social.dao.WithdrawDAO;
import com.social.pojo.TUser;
import com.social.pojo.TWithdraw;


/**
 * Servlet implementation class AddWithdrawServlet
 */
@WebServlet("/AddWithdrawServlet")
public class AddWithdrawServlet extends HttpServlet {
	
	private String user_id;
	private String account;
	private String money;
	
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
		System.out.println(session.getId());
		if(session == null || session.getAttribute("id") == null){
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		account = request.getParameter("account");
		if(!checkParameter(response, "account", account, root, pw)) return;
		
		money = request.getParameter("money");
		if(!checkParameter(response, "money", money, root, pw)) return;
		
		WithdrawDAO dao = new WithdrawDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		TWithdraw twithdraw;
		TUser tuser;
		try {
			tuser = userDAO.getById(Long.parseLong(user_id));
			twithdraw = new TWithdraw();
			twithdraw.setAccount(account);
			twithdraw.setMoney(Integer.valueOf(money));
			twithdraw.setUser(tuser);
			twithdraw.setState(0);
			twithdraw.setDate(new Date());
			twithdraw.setOptlockversion1(Long.parseLong("0"));
			twithdraw.setOptlockversion2(Long.parseLong("0"));
			dao.add(twithdraw);
			
			//
			int credit = tuser.getCredit();
			credit = credit - Integer.valueOf(money);
			if(credit<0) credit = 0;
			tuser.setCredit(credit);
			userDAO.update(tuser);
			
			root.success = true;
			root.message = "";
			root.credit = credit;
			
			userDAO.close();
			dao.close();
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
		int credit;
	}

}
