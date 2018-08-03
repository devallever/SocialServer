package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

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
import com.social.dao.GroupDAO;
import com.social.dao.GroupmemberDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TGroup;
import com.social.pojo.TGroupmember;
import com.social.servlet.AddNewsServlet.Root;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class AddGroupServlet
 */
@WebServlet("/AddGroupServlet")
@MultipartConfig
public class AddGroupServlet extends HttpServlet {
	private String user_id;
	private String point;
	private String groupname;
	private String description;
	private String str_longitude;
	private String str_latitude;
	private Part part_group_img;
	private String hx_group_id;
	private String str_group_type;
	
	private PrintWriter pw;
	private Root root;
	private String new_group_id;
	
	TGroup group_added;
	
	
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
		
		
		groupname = request.getParameter("groupname");
		System.out.println("groupname = " + groupname);
		if(!checkParameter(response, "groupname", groupname, root, pw)) return;
		
		point = request.getParameter("point");
		if(!checkParameter(response, "point", point, root, pw)) return;
		
		description = request.getParameter("description");
		if(!checkParameter(response, "description", description, root, pw)) return;
		
		hx_group_id = request.getParameter("hx_group_id");
		if(!checkParameter(response, "hx_group_id", hx_group_id, root, pw)) return;
		
		
		str_group_type = request.getParameter("group_type");
		if(!checkParameter(response, "group_type", str_group_type, root, pw)) return;
		int group_type = Integer.parseInt(str_group_type);
		
		str_longitude = request.getParameter("longitude");
		if(str_longitude == null){
			str_longitude = "0";
		}
		str_latitude = request.getParameter("latitude");
		if(str_latitude == null){
			System.out.println(str_latitude);
			str_latitude = "0";
		}
		
		part_group_img =  request.getPart("group_img_part");
		if(!checkParameterPart(response, "part_group_img", part_group_img, root, pw)) return;
		
		
		
		
		GroupDAO dao = new GroupDAO();
		UserDAO userDAO = new UserDAO(dao.getSession());
		GroupmemberDAO memberDAO  = new GroupmemberDAO(dao.getSession());
		
		
		try {
			
			TGroup tGroup = new TGroup();
			tGroup.setAttention("入群须注意");
			tGroup.setDate(new Date());
			tGroup.setDescription(description);
			tGroup.setGroupimg("");
			tGroup.setGrouplevel(1);
			tGroup.setGroupname(groupname);
			tGroup.setLatitude(Double.parseDouble(str_latitude));
			tGroup.setLongitude(Double.parseDouble(str_longitude));
			tGroup.setLimited("");
			tGroup.setOptlockversion1(Long.parseLong("0"));
			tGroup.setOptlockversion2(Long.parseLong("0"));
			tGroup.setPoint(point);
			tGroup.setState(1);
			tGroup.setHx_group_id(hx_group_id);
			tGroup.setGroup_type(group_type);
			tGroup.setRed_pocket_count(0);
			tGroup.setUser(userDAO.getById(Long.parseLong(user_id)));
			group_added = dao.add(tGroup);
			
			
			System.out.println();
			if(part_group_img != null){
				if(CommonUtils.getImgByte(part_group_img).length>0){
					createGroupImageFile(part_group_img);
					String filename = "/images/group/" + group_added.getId()+ ".jpg";
					group_added.setGroupimg(filename);
					dao.update(group_added);
				}
				
			}
			
			TGroupmember tGroupmember = new TGroupmember();
			tGroupmember.setGroup(group_added);
			tGroupmember.setUser(userDAO.getById(Long.parseLong(user_id)));
			tGroupmember.setOptlockversion1(Long.parseLong("0"));
			tGroupmember.setOptlockversion2(Long.parseLong("0"));
			
			memberDAO.add(tGroupmember);
			root.success = true;
			root.message = "";
			pw.print(gson.toJson(root));
			pw.close();
			memberDAO.close();
			userDAO.close();
			dao.close();
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
	}
	
	private class Root{
		boolean success;
		String message;
	}
	
	private boolean checkParameterPart(HttpServletResponse response,String parameterName, Part parameterValue, Root root, PrintWriter pw)
			throws IOException {
		System.out.println(parameterName + " = " + parameterValue);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		if (parameterValue == null) {
			root.success = false;
			root.message = "请选择文件";
			response.setContentType("text/html;charset=utf-8");
			pw = response.getWriter();
			pw.print(gson.toJson(root));
			pw.close();
			return false;
		}
		return true;
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
	
	private void createGroupImageFile(Part part){ 
		try {
			String filepath = getServletContext().getRealPath("") + "images/group";
			String filename = group_added.getId() + ".jpg";
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

}
