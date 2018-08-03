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
import com.social.dao.AdDetailDAO;
import com.social.pojo.TAddetail;
import com.social.servlet.CheckPhoneServlet.Root;


/**
 * Servlet implementation class AdDetailServlet
 */
@WebServlet("/AdDetailServlet")
public class AdDetailServlet extends HttpServlet {
	
	private PrintWriter pw;
	private Root root;
	private String str_type;
	private int type;
	
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
//			root.message = "未登录";
//			root.success = false;
//			pw.print(gson.toJson(root));
//			pw.close();
//			return;
		}
		//user_id = session.getAttribute("id").toString();
		
		
		str_type = request.getParameter("type");
		System.out.println("type = " + str_type);
		if(!checkParameter(response, "type", str_type, root, pw)) return;
		type = Integer.valueOf(str_type);
		
		AdDetailDAO dao = new AdDetailDAO();
		List<TAddetail> list_taddetail;
		List<AdDetail> list_addetail = new ArrayList<AdDetailServlet.AdDetail>();
		AdDetail addetail;
		try {
			list_taddetail = dao.getByQuery("type=" + type, 0, 0);
			for(TAddetail taddetail: list_taddetail){
				addetail = new AdDetail();
				addetail.id = taddetail.getId()+"";
				addetail.ad_path = taddetail.getPath();
				addetail.url = taddetail.getUrl();
				list_addetail.add(addetail);
			}
			
			root.addetail_list = list_addetail;
			root.success = true;
			root.message="";
			pw.print(gson.toJson(root));
			pw.close();
			dao.close();
			
		} catch (Exception e) {
			// TODO: handle exception
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
		List<AdDetail> addetail_list;
	}

	class AdDetail{
		String id;
		String ad_path;
		String url;
	}
}
