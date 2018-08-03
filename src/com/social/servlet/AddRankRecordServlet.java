package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.RankDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TRank;

/**
 * Servlet implementation class AddRankRecordServlet
 */
@WebServlet("/AddRankRecordServlet")
public class AddRankRecordServlet extends HttpServlet {
	
	private PrintWriter pw;
	private Root root;
	
	private String chatwith_name;
	private String user_id;
	
	
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Enter AddRankRecordServlet");
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
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		chatwith_name = request.getParameter("chatwith_name");
		if(!checkParameter(response, "chatwith_name", chatwith_name, root, pw)) return;
		
		RankDAO dao = new RankDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		TRank trank;
		String chatwith_id;
		try {
			chatwith_id = userDAO.getByQuery("username='" + chatwith_name + "'", 0, 0).get(0).getId().toString();
			if(dao.getByQuery("chatwith_id=" + chatwith_id + " and user_id=" + user_id, 0, 0).size()>0){
				//有记录不操作
				System.out.println("有记录");
			}else{
				System.out.println("无记录");
				trank = new TRank();
				trank.setUser(userDAO.getById(Long.parseLong(user_id)));
				trank.setChatwith(userDAO.getByQuery("username='" + chatwith_name + "'", 0, 0).get(0));
				trank.setOptlockversion1(Long.parseLong("0"));
				trank.setOptlockversion2(Long.parseLong("0"));
				dao.add(trank);
				
			}
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
	}
	
}
