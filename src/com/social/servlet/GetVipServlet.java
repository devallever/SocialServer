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
 * Servlet implementation class GetVipServlet
 */
@WebServlet("/GetVipServlet")
public class GetVipServlet extends HttpServlet {
	
	private String user_id;
	private String type;
	private String month_count;
	
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
		
		type = request.getParameter("type");
		if(!checkParameter(response, "type", type, root, pw)) return;
		
		month_count = request.getParameter("month_count");
		if(!checkParameter(response, "month_count", month_count, root, pw)) return;
		
		VipDAO dao = new VipDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		
		TVip tvip;
		TUser tuser;
		
		try {
			if(type.equals("1")){
				//支付宝支付
				root.success = false;
				root.message = "暂时不支持支付宝支付";
				userDAO.close();
				dao.close();
				pw.print(gson.toJson(root));
				pw.close();
				return;
			}else if(type.equals("2")){
				//信用支付
				int rest_credit = userDAO.getById(Long.parseLong(user_id)).getCredit();
				int month = Integer.parseInt(month_count);
				switch (month) {
				case 1:
					rest_credit = rest_credit - 10;
					break;
				case 3:
					rest_credit = rest_credit - 28;
					break;
				case 6:
					rest_credit = rest_credit - 50;
					break;
				case 12:
					rest_credit = rest_credit - 100;
					break;
				default:
					break;
				}
				
				if(rest_credit<0){
					root.success = false;
					root.message = "余额不足,请充值";
					userDAO.close();
					dao.close();
					pw.print(gson.toJson(root));
					pw.close();
					return;
				}
				
				tuser = userDAO.getById(Long.parseLong(user_id));
				tuser.setIs_vip(1);
				tuser.setCredit(rest_credit);
				userDAO.update(tuser);
				//userDAO.close();
				
				List<TVip> list_tvip;
				list_tvip = dao.getByQuery("user_id=" + user_id, 0, 0);
				if(list_tvip.size() == 0){
					//添加记录
					tvip = new TVip();
					tvip.setMonth_count(month);
					tvip.setType(Integer.valueOf(type));
					tvip.setUser(tuser);
					Date start_date = new Date();
					tvip.setStart_date(start_date);
					Date end_date = addDate(start_date, (month*30));
					tvip.setEnd_date(end_date);
					tvip.setOptlockversion1(Long.parseLong("0"));
					tvip.setOptlockversion2(Long.parseLong("0"));
					dao.add(tvip);
				}else{
					//更新记录
					tvip = list_tvip.get(0);
					Date start_date = new Date();
					tvip.setStart_date(start_date);
					Date end_date = addDate(start_date, (month*30));
					tvip.setEnd_date(end_date);
					dao.update(tvip);
				}
				
				root.success = true;
				root.message = "";
				userDAO.close();
				dao.close();
				pw.print(gson.toJson(root));
				pw.close();
				return;
				
				
				
			}
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
	
	
	public Date addDate(Date d,long day) throws ParseException {
		  long time = d.getTime(); 
		  day = day*24*60*60*1000; 
		  time+=day; 
		  return new Date(time);
	}

}
