package com.social.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.dao.RecruitDAO;
import com.social.dao.RecruitImageDAO;
import com.social.pojo.TRecruit;
import com.social.pojo.TRecruitImage;
import com.social.servlet.GroupDataServlet.Root;
import com.social.util.CommonUtils;


/**
 * Servlet implementation class RecruitDataServlet
 */
@WebServlet("/RecruitDataServlet")
public class RecruitDataServlet extends HttpServlet {
	
	private String recruit_id;
	private String user_id;
	private PrintWriter pw;
	private Root root;
	
	private String str_longitude;
	private String str_latitude;
	
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
		
		recruit_id = request.getParameter("recruit_id");
		if(!checkParameter(response, "recruit_id", recruit_id, root, pw)) return;
		
		str_longitude = request.getParameter("longitude");
		if (!checkParameter(response, "longitude", str_longitude, root, pw))
			return;
		// longitude = Double.parseDouble(str_latitude);

		str_latitude = request.getParameter("latitude");
		if (!checkParameter(response, "latitude", str_latitude, root, pw))
			return;
		
		TRecruit trecruit;
		RecruitDAO dao = new RecruitDAO();
		RecruitImageDAO recruitImageDAO = new RecruitImageDAO(dao.getSession());
		List<TRecruitImage> list_tImages;
		Recruit recruit;
		List<String> list_img = new ArrayList<String>();
		TRecruitImage trcruitImg;
		try {
			trecruit = dao.getById(Long.parseLong(recruit_id));
			recruit = new Recruit();
			recruit.id = trecruit.getId()+"";
			recruit.companyname = trecruit.getCompanyname();
			
			double distance = CommonUtils.getDistance(str_latitude,
					str_longitude, String.valueOf(trecruit.getLatitude()),
					String.valueOf(trecruit.getLongitude()));
			String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
			recruit.distance = Double.parseDouble(dis_with_two);
			
			recruit.link = trecruit.getLink();
			recruit.phone = trecruit.getPhone();
			recruit.requirement = trecruit.getRequirement();
			recruit.user_name = trecruit.getUser().getUsername();
			recruit.head_path = trecruit.getUser().getHeadpath();
			recruit.address = trecruit.getAddress();
			
			list_tImages = recruitImageDAO.getByQuery("recruit_id=" + trecruit.getId(), 0, 0);
			for(TRecruitImage tRecruitImage : list_tImages){
				list_img.add(tRecruitImage.getPath());
			}
			recruit.list_recruit_img = list_img;
			
			if (session == null || session.getAttribute("id") == null) {
				// 未登录
				//news.isLiked = 0;// 未赞过
				recruit.is_owner = 0;
			} else {
				String user_id = session.getAttribute("id").toString();// 登录用户的id
				if(user_id.equals(trecruit.getUser().getId()+"")){
					recruit.is_owner = 1;
				}else{
					recruit.is_owner = 0;
				}
			}
			
			root.recruit = recruit;
			root.success = true;
			root.message = "";
			
			pw.print(gson.toJson(root));
			dao.close();
			pw.close();
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
		Recruit recruit;
	}
	class Recruit{
		String id;
		String companyname;
		double distance;
		String link;
		String phone;
		String address;
		String requirement;
		String user_name;
		String head_path;
		int is_owner;
		List<String> list_recruit_img;
	}

}
