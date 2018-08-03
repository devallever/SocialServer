package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.UserDAO;
import com.social.pojo.TUser;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class ModifyHeadServlet
 */
@WebServlet("/ModifyHeadServlet")
@MultipartConfig
public class ModifyHeadServlet extends HttpServlet {
	private String user_id;
	private Part head_img;
	private PrintWriter pw;
	private Root root;
	private String description;
	
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
			root.seccess = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
//		description = request.getParameter("description");
//		if(description == null) System.out.println("desc = null");
//		else System.out.println(description);
		
		//获取参数
//		user_id = request.getParameter("user_id");
//		if(!checkParameter(response, "user_id", user_id, root, pw)) return;
		
		TUser tuser;
		UserDAO dao = new UserDAO();
		try {
			tuser = dao.getById(Long.parseLong(user_id));
			head_img = request.getPart("head_img");
			if(!checkParameterPart(response, "head_img", head_img, root, pw)) return;
			String filepath = getServletContext().getRealPath("") + "images/head";
			String filename = tuser.getUsername() + ".jpg";
			byte[] b = CommonUtils.getImgByte(head_img);
			CommonUtils.byteToFile(b, filepath, filename);
			
			root.seccess = true;
			root.message = "";
			root.head_path = tuser.getHeadpath();
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
	}
	
	class Root{
		boolean seccess;
		String message;
		String head_path;
	}
	
//	private  boolean checkParameter(HttpServletResponse response,String parameterName ,String parameterValue , Root root, PrintWriter pw){
//		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//		if (parameterValue == null || parameterValue.length() == 0) {
//			root.seccess = false;
//			root.message = "没有指定" + parameterName + "参数";
//			response.setContentType("text/html;charset=utf-8");
//			try {
//				pw = response.getWriter();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			pw.print(gson.toJson(root));
//			//pw.close();
//			return false;
//		}
//		System.out.println(parameterValue);
//		return true;
//	}
	
	private boolean checkParameterPart(HttpServletResponse response,String parameterName, Part parameterValue, Root root, PrintWriter pw)
			throws IOException {
		System.out.println(parameterName + " = " + parameterValue);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		if (parameterValue == null) {
			root.seccess = false;
			root.message = "请选择文件";
			response.setContentType("text/html;charset=utf-8");
			pw = response.getWriter();
			pw.print(gson.toJson(root));
			pw.close();
			return false;
		}
		return true;
	}
	

}
