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
import com.social.dao.WithdrawDAO;
import com.social.pojo.TWithdraw;

/**
 * Servlet implementation class WithdrawLogServlet
 */
@WebServlet("/WithdrawLogServlet")
public class WithdrawLogServlet extends HttpServlet {
	
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
		System.out.println(session.getId());
		if(session == null || session.getAttribute("id") == null){
			root.message = "未登录";
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		WithdrawDAO dao = new WithdrawDAO();
		List<TWithdraw> list_twithdraw;
		List<Withdraw> list_withdraw = new ArrayList<WithdrawLogServlet.Withdraw>();
		Withdraw withdraw;
		
		try {
			list_twithdraw = dao.getByQuery("user_id="+ user_id, 0, 0);
			for(TWithdraw tWithdraw : list_twithdraw){
				withdraw = new Withdraw();
				withdraw.id = tWithdraw.getId()+"";
				withdraw.date = tWithdraw.getDate().toString();
				withdraw.money = tWithdraw.getMoney()+"";
				withdraw.state = tWithdraw.getState()+"";
				withdraw.account = tWithdraw.getAccount();
				list_withdraw.add(withdraw);
			}
			
			
			root.success = true;
			root.message = "";
			root.list_withdraw = list_withdraw;
			dao.close();
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
		List<Withdraw> list_withdraw;
	}
	class Withdraw{
		String id;
		String money;
		String date;
		String state;
		String account;
	}
}
