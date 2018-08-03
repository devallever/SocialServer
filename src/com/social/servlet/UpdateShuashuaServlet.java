package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sun.net.www.protocol.http.HttpURLConnection.TunnelState;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.ShuaShuaDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TShuaShua;
import com.social.pojo.TUser;
import com.social.servlet.GetShuaShuaUserServlet.Root;


/**
 * Servlet implementation class UpdateShuashuaServlet
 */
@WebServlet("/UpdateShuashuaServlet")
public class UpdateShuashuaServlet extends HttpServlet {
	
	private PrintWriter pw;
	private Root root;
	private String user_id;
	private String str_flag;
	private int flag;
	private String other_username;
	
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
		

		//获取参数
		str_flag = request.getParameter("flag");
		if(!checkParameter(response, "flag", str_flag, root, pw)) return;
		flag = Integer.valueOf(str_flag);
		
		other_username = request.getParameter("other_username");
		if(!checkParameter(response, "other_username", other_username, root, pw)) return;
		
		ShuaShuaDAO dao = new ShuaShuaDAO();
		TShuaShua tshuashua;
		UserDAO userDAO = new UserDAO(dao.getSession());
		TUser tuser;
		TUser other_tuser;
		try {
			tuser = userDAO.getById(Long.parseLong(user_id));
			other_tuser = userDAO.getByQuery("username='" + other_username +"'", 0, 0).get(0);
			tshuashua = new TShuaShua();
			tshuashua.setMy(tuser);
			tshuashua.setOther(other_tuser);
			tshuashua.setFlag(flag);
			tshuashua.setOptlockversion1(Long.parseLong("0"));
			tshuashua.setOptlockversion2(Long.parseLong("0"));
			dao.add(tshuashua);
			root.success = true;
			root.message = "";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		pw.print(gson.toJson(root));
		pw.close();
		userDAO.close();
		dao.close();
		
		
		
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
		
	}
}
