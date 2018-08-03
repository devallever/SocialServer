package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
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
import com.social.dao.UserDAO;
import com.social.dao.VipDAO;
import com.social.pojo.TUser;
import com.social.pojo.TVip;

/**
 * Servlet implementation class GiveVipServlet
 */
@WebServlet("/GiveVipServlet")
public class GiveVipServlet extends HttpServlet {
	
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
		
		
		VipDAO dao = new VipDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		TVip tvip;
		TUser tuser;
		int month = 1;
		int type = 3;//赠送会员
		List<TVip> list_tvip;
		try {
			tuser = userDAO.getById(Long.parseLong(user_id));
			
			list_tvip = dao.getByQuery("user_id=" + user_id, 0, 0);
			if(list_tvip.size() == 0){
				//添加记录
				tvip = new TVip();
				tvip.setMonth_count(month);
				tvip.setType(type);
				tvip.setUser(tuser);
				Date start_date = new Date();
				tvip.setStart_date(start_date);
				Date end_date = addDate(start_date, (month*30));
				tvip.setEnd_date(end_date);
				tvip.setOptlockversion1(Long.parseLong("0"));
				tvip.setOptlockversion2(Long.parseLong("0"));
				tvip = dao.add(tvip);
			}else{
				//更新记录
				tvip = list_tvip.get(0);
				Date start_date = new Date();
				tvip.setStart_date(start_date);
				Date end_date = addDate(start_date, (month*30));
				tvip.setEnd_date(end_date);
				tvip.setType(type);
				tvip.setMonth_count(month);
				tvip = dao.update(tvip);
			}
			
			
			//
			tuser.setIs_vip(1);
			tuser.setMessagecount(10);
			tuser = userDAO.update(tuser);
			
			root.success = true;
			root.message = "";
			root.isVip = tuser.getIs_vip();
			userDAO.close();
			dao.close();
			pw.print(gson.toJson(root));
			pw.close();
			return;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	public Date addDate(Date d,long day) throws ParseException {
		  long time = d.getTime(); 
		  day = day*24*60*60*1000; 
		  time+=day; 
		  return new Date(time);
	}

	class Root{
		boolean success;
		String message;
		int isVip;
	}
	
}
