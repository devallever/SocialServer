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
import com.social.dao.SharerankDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TSharerank;
import com.social.pojo.TUser;

/**
 * Servlet implementation class AddShareRecordServlet
 */
@WebServlet("/AddShareRecordServlet")
public class AddShareRecordServlet extends HttpServlet {
	
	private PrintWriter pw;
	private Root root;
	
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
		
		SharerankDAO dao = new SharerankDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		List<TSharerank> list_tsharerank;
		TUser share_tuser;
		int share_count;
		TSharerank tsharerank;
		try {
			list_tsharerank = dao.getByQuery("user_id=" + user_id, 0, 0);
			if(list_tsharerank.size()>0){
				//有记录 更新操作
				tsharerank = list_tsharerank.get(0);
				share_count = tsharerank.getShare_count();
				share_count++;
				tsharerank.setShare_count(share_count);
				dao.update(tsharerank);
			}else{
				//没记录 插入操作
				tsharerank = new TSharerank();
				share_tuser = userDAO.getById(Long.parseLong(user_id));
				tsharerank.setUser(share_tuser);
				tsharerank.setShare_count(1);
				tsharerank.setOptlockversion1(Long.parseLong("0"));
				tsharerank.setOptlockversion2(Long.parseLong("0"));
				dao.add(tsharerank);
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
	
	class Root{
		boolean success;
		String message;
	}

}
