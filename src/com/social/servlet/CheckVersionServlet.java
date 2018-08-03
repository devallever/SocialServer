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
import com.social.dao.VersionDAO;
import com.social.pojo.TVersion;


/**
 * Servlet implementation class CheckVersionServlet
 */
@WebServlet("/CheckVersionServlet")
public class CheckVersionServlet extends HttpServlet {
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
		if(session == null || session.getAttribute("id") == null){
			root.message = "未登录";
			root.code = -1;
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
			return;
		}
		
		int version_code = Integer.valueOf(request.getParameter("version_code"));
		System.out.println(version_code+"");
		
		VersionDAO dao = new VersionDAO();
		List<TVersion> list_tversion;
		Version version;
		try {
			//list_tversion = dao.getByQuery("order by id asc", 0, 0);
			list_tversion = dao.getUpdateVersion(0, 0);
			if(list_tversion.size()>0){
				TVersion tVersion = list_tversion.get(0);
				if (tVersion.getVersion_code() > version_code) {
					root.code = 1;
					root.success = true;
					root.message = "";
					version = new Version();
					version.description = tVersion.getDescription();
					version.id = String.valueOf(tVersion.getId());
					version.version_code = tVersion.getVersion_code();
					version.version_name = tVersion.getVersion_name();
					
					
					//version.app_path = tVersion.getApp_path();
					
					String filePath = "/DownloadSocial";
					version.app_path = filePath;
					
					root.version = version;
					pw.print(gson.toJson(root));
					pw.close();
					dao.close();
				}else{
					root.code = 2;
					root.success = false;
					root.message = "无可更新版本";
					root.code = 0;
					root.version = null;
					pw.print(gson.toJson(root));
					pw.close();
					dao.close();
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//String user_id = session.getAttribute("id").toString();
	}
	
	class Root{
		boolean success;
		int code;
		String message;
		Version version;
	}
	
	class Version{
		String id;
		int version_code;
		String version_name;
		String description;
		String app_path;
	}

}
