package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.mysql.fabric.xmlrpc.base.Array;
import com.social.dao.RecruitDAO;
import com.social.dao.RecruitImageDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TRecruit;
import com.social.pojo.TRecruitImage;
import com.social.servlet.AddNewsServlet.Root;
import com.social.util.CommonUtils;


/**
 * Servlet implementation class AddRecruitServlet
 */
@WebServlet("/AddRecruitServlet")
@MultipartConfig
public class AddRecruitServlet extends HttpServlet {
	private String user_id;
	private String companyname;
	private String link;
	private String phone;
	private String str_longitude;
	private String str_latitude;
	private String requirement;
	private String address;
	
	private Part part1;
	private Part part2;
	private Part part3;
	private Part part4;
	private Part part5;
	private Part part6;
	
	private String image1;
	private String image2;
	private String image3;
	private String image4;
	private String image5;
	private String image6;
	
	
	private PrintWriter pw;
	private Root root;
	private TRecruit tRecruit;
	
	
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
			root.success= false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		user_id = session.getAttribute("id").toString();
		
		companyname = request.getParameter("companyname");
		if(!checkParameter(response, "companyname", companyname, root, pw)) return;
		
		requirement = request.getParameter("requirement");
		if(!checkParameter(response, "requirement", requirement, root, pw)) return;
		
		link = request.getParameter("link");
		if(!checkParameter(response, "link", link, root, pw)) return;
		
		phone = request.getParameter("phone");
		if(!checkParameter(response, "phone", phone, root, pw)) return;
		
		address = request.getParameter("address");
		if(!checkParameter(response, "address", address, root, pw)) return;
		
		str_longitude = request.getParameter("longitude");
		if(!checkParameter(response, "longitude", str_longitude, root, pw)) return;
		
		str_latitude = request.getParameter("latitude");
		if(!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		
		RecruitDAO dao = new RecruitDAO();
		//RecruitImageDAO recruitImageDAO =  new RecruitImageDAO(dao.getSession());
		UserDAO userDAO = new UserDAO(dao.getSession());
		//TRecruit tRecruit;
		try {
			tRecruit = new TRecruit();
			tRecruit.setCompanyname(companyname);
			tRecruit.setDate(new Date());
			tRecruit.setLatitude(Double.parseDouble(str_latitude));
			tRecruit.setLongitude(Double.parseDouble(str_longitude));
			tRecruit.setLink(link);
			tRecruit.setPhone(phone);
			tRecruit.setAddress(address);
			tRecruit.setRequirement(requirement);
			tRecruit.setUser(userDAO.getById(Long.parseLong(user_id)));
			tRecruit.setOptlockversion1(Long.parseLong("0"));
			tRecruit.setOptlockversion2(Long.parseLong("0"));
			
			tRecruit = dao.add(tRecruit);
			userDAO.close();
			dao.close();
			
			dao = new RecruitDAO();
			List<String> img_list = new ArrayList<String>();
			RecruitImageDAO recruitImageDAO = new RecruitImageDAO(dao.getSession());
			TRecruitImage tRecruitImage = new TRecruitImage();
			tRecruitImage.setRecruit(tRecruit);
			tRecruitImage.setOptlockversion1(Long.parseLong("0"));
			tRecruitImage.setOptlockversion2(Long.parseLong("0"));
			
			part1 = request.getPart("part1");
			System.out.println();
			if(part1 != null){
				if(CommonUtils.getImgByte(part1).length>0){
					createNewsImageFile(part1,1);
					image1 = "/images/recruitimg/" + tRecruit.getId() +"_" + 1 + ".jpg";
					img_list.add(image1);
					tRecruitImage.setPath(image1);
					tRecruitImage = recruitImageDAO.add(tRecruitImage);
					recruitImageDAO.close();
					dao.close();
				}
				
			}
			
			part2 = request.getPart("part2");
			System.out.println();
			if(part2 != null){
				if(CommonUtils.getImgByte(part2).length>0){
					dao = new RecruitDAO();
					RecruitImageDAO recruitImageDAO2 = new RecruitImageDAO(dao.getSession());
					createNewsImageFile(part2,2);
					image2 = "/images/recruitimg/" + tRecruit.getId() +"_" + 2 + ".jpg";
					img_list.add(image2);
					tRecruitImage.setPath(image2);
					tRecruitImage = recruitImageDAO2.add(tRecruitImage);
					recruitImageDAO2.close();
					dao.close();
				}
				
			}
			
			part3 = request.getPart("part3");
			System.out.println();
			if(part3 != null){
				if(CommonUtils.getImgByte(part3).length>0){
					dao = new RecruitDAO();
					recruitImageDAO = new RecruitImageDAO(dao.getSession());
					createNewsImageFile(part3,3);
					image3 = "/images/recruitimg/" + tRecruit.getId() +"_" + 3 + ".jpg";
					img_list.add(image3);
					tRecruitImage.setPath(image3);
					tRecruitImage = recruitImageDAO.add(tRecruitImage);
					recruitImageDAO.close();
					dao.close();
				}
				
			}
			
			part4 = request.getPart("part4");
			System.out.println();
			if(part4 != null){
				if(CommonUtils.getImgByte(part4).length>0){
					dao = new RecruitDAO();
					recruitImageDAO = new RecruitImageDAO(dao.getSession());
					createNewsImageFile(part4,4);
					image4 = "/images/recruitimg/" + tRecruit.getId() +"_" + 4 + ".jpg";
					img_list.add(image4);
					tRecruitImage.setPath(image4);
					tRecruitImage = recruitImageDAO.add(tRecruitImage);
					recruitImageDAO.close();
					dao.close();
				}
				
			}
			
			
			part5 = request.getPart("part5");
			System.out.println();
			if(part5 != null){
				if(CommonUtils.getImgByte(part5).length>0){
					dao = new RecruitDAO();
					recruitImageDAO = new RecruitImageDAO(dao.getSession());
					createNewsImageFile(part5,5);
					image5 = "/images/recruitimg/" + tRecruit.getId() +"_" + 5 + ".jpg";
					img_list.add(image5);
					tRecruitImage.setPath(image5);
					tRecruitImage = recruitImageDAO.add(tRecruitImage);
					recruitImageDAO.close();
					dao.close();
				}
				
			}
			
			part6 = request.getPart("part6");
			System.out.println();
			if(part6 != null){
				if(CommonUtils.getImgByte(part6).length>0){
					dao = new RecruitDAO();
					recruitImageDAO = new RecruitImageDAO(dao.getSession());
					createNewsImageFile(part4,4);
					image6 = "/images/recruitimg/" + tRecruit.getId() +"_" + 6 + ".jpg";
					img_list.add(image6);
					tRecruitImage.setPath(image6);
					tRecruitImage = recruitImageDAO.add(tRecruitImage);
					recruitImageDAO.close();
					dao.close();
				}
				
			}
			
			root.recruit_id = tRecruit.getId()+"";
			root.success = true;
			root.message = "";
			pw.print(gson.toJson(root));
			pw.close();
			img_list.clear();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	
	
	}
	
	class Root{
		boolean success;
		String message;
		String recruit_id;
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
	
	private void createNewsImageFile(Part part, int position){ 
		try {
			String filepath = getServletContext().getRealPath("") + "images/recruitimg";
			String filename = tRecruit.getId() +"_" + position + ".jpg";
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
