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

import org.springframework.web.servlet.theme.ThemeChangeInterceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.NewsDAO;
import com.social.dao.NewsImageDAO;
import com.social.dao.PhotowallDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TPhotowall;
import com.social.pojo.TUser;
import com.social.servlet.AddNewsServlet.Root;
import com.social.util.CommonUtils;


/**
 * Servlet implementation class AddPhotoWallServlet
 */
@WebServlet("/AddPhotoWallServlet")
@MultipartConfig
public class AddPhotoWallServlet extends HttpServlet {
	
	private String user_id;
	private int position;
	private Part part;
	private String image_path;
	
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
		
		//获取参数
		String str_posttion = request.getParameter("position");
		if(!checkParameter(response, "position", str_posttion, root, pw)) return;
		position = Integer.valueOf(str_posttion);
		
		PhotowallDAO dao = new PhotowallDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		TUser tuser;
		TPhotowall tPhotowall;
		try {
			
			tuser = userDAO.getById(Long.parseLong(user_id));
			
			part = request.getPart("part");
			if(part != null){
				if(CommonUtils.getImgByte(part).length>0){
					
					createPhotoWallImageFile(part, position);
					image_path = "/images/photowallimg/" + tuser.getId() +"_" + (position+1) + ".jpg";
					
					
					if(dao.getByQuery("user_id="+ user_id +" and photoposition= " + (position+1) , 0, 0).size() == 0){
						tPhotowall = new TPhotowall();
						tPhotowall.setPath(image_path);
						tPhotowall.setUser(tuser);
						tPhotowall.setPhotoposition(position+1);
						tPhotowall.setOptlockversion1(Long.parseLong("0"));
						tPhotowall.setOptlockversion2(Long.parseLong("0"));
						tPhotowall = dao.add(tPhotowall);
					}else{
						tPhotowall = dao.getByQuery("user_id="+ user_id +" and photoposition = " + (position+1) , 0, 0).get(0);
						tPhotowall.setPath(image_path);
						tPhotowall.setUser(tuser);
						tPhotowall.setPhotoposition(position+1);
						tPhotowall = dao.update(tPhotowall);
					}
					
					
					root.new_photo_path =tPhotowall.getPath();
					root.success = true;
					root.message = "";
					
					pw.print(gson.toJson(root));
					pw.close();
					userDAO.close();
					dao.close();
				}
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
	
	private void createPhotoWallImageFile(Part part, int position){ 
		try {
			String filepath = getServletContext().getRealPath("") + "images/photowallimg";
			String filename = user_id +"_" + (position+1) + ".jpg";
			byte[] b;
			b = CommonUtils.getImgByte(part);
			System.out.println("b.length = " + b.length);
			if(b.length >0){
				CommonUtils.byteToFile(b, filepath, filename);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	class Root{
		boolean success;
		String message;
		String new_photo_path;
	}

}
