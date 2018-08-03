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
import com.social.dao.AutoreactionDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TAutoreaction;
import com.social.pojo.TUser;

/**
 * Servlet implementation class SaveAutoReactionServlet
 */
@WebServlet("/SaveAutoReactionServlet")
public class SaveAutoReactionServlet extends HttpServlet {
	
	private String user_id;
	private PrintWriter pw;
	private Root root;
	private String content;
	
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
		
		content = request.getParameter("content");
		if(!checkParameter(response, "content", content, root, pw)) return;
		
		AutoreactionDAO dao = new AutoreactionDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		List<TAutoreaction> list_tautoreaction;
		TUser tuser;
		TAutoreaction tautoreaction;
		try {
			list_tautoreaction = dao.getByQuery("user_id=" + user_id, 0, 0);
			if (list_tautoreaction.size()>0) {
				//有记录， 更新操作
				tautoreaction = list_tautoreaction.get(0);
				tautoreaction.setContent(content);
				tautoreaction = dao.update(tautoreaction);
			}else{
				//无记录 插入操作
				tautoreaction = new TAutoreaction();
				tuser = userDAO.getById(Long.parseLong(user_id));
				tautoreaction.setUser(tuser);
				tautoreaction.setContent(content);
				tautoreaction.setOptlockversion1(Long.parseLong("0"));
				tautoreaction.setOptlockversion2(Long.parseLong("0"));
				tautoreaction = dao.add(tautoreaction);
			}
			
			root.content = tautoreaction.getContent();
			root.success = true;
			root.message = "";
			
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
			
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
		String content;
	}


}
