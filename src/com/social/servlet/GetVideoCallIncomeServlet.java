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
import com.social.servlet.SaveVideoFeeSettingServlet.Root;

/**
 * Servlet implementation class GetVideoCallIncomeServlet
 */
@WebServlet("/GetVideoCallIncomeServlet")
public class GetVideoCallIncomeServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	
	private String from_username;
	private String min_count;
	
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
		
		
		from_username = request.getParameter("from_username");
		if(!checkParameter(response, "from_username", from_username, root, pw)) return;
		
		min_count = request.getParameter("min_count");
		if(!checkParameter(response, "min_count", min_count, root, pw)) return;
		
		
		UserDAO dao = new UserDAO();
		//FeeDAO feedao = new FeeDAO(dao.getSession());
		List<TUser> list_tuser;
		//List<TFee> list_fee;
		TUser tuser_from;
		int video_fee;
		int all_get;
		int from_user_credit;
		TUser tuser_mine;
		int my_credit;
		try {
			
			//减少发起视频聊天用户的信用
			list_tuser = dao.getByQuery("username='" + from_username + "'", 0, 0);
			tuser_from = list_tuser.get(0);
			video_fee = dao.getByQuery("id =" + user_id, 0, 0).get(0).getVideo_fee();
			all_get = video_fee*Integer.valueOf(min_count);
			
			from_user_credit = tuser_from.getCredit();
			from_user_credit = from_user_credit - all_get;
			if(from_user_credit<0) from_user_credit = 0;
			tuser_from.setCredit(from_user_credit);
			dao.update(tuser_from);
			
			//增加接听方的信用
			tuser_mine = dao.getById(Long.parseLong(user_id));
			my_credit = tuser_mine.getCredit();
			my_credit = my_credit + all_get;
			tuser_mine.setCredit(my_credit);
			dao.update(tuser_mine);
			
			root.success = true;
			root.message = "";
			root.income_credit = all_get;
			
			//feedao.close();
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
		int income_credit;
	}

}
