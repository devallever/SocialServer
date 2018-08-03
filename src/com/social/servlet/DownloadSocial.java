package com.social.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.transform.RootEntityResultTransformer;

import com.social.dao.VersionDAO;
import com.social.pojo.TVersion;
import com.social.servlet.CheckVersionServlet.Version;

/**
 * Servlet implementation class DownloadServlet
 */
@WebServlet("/DownloadSocial")
public class DownloadSocial extends HttpServlet {
	
	private String contentType;
	private String enc="UTF-8";
	//private String rootUrl = "http://27.54.249.252:8080/SocialServer";

	private static final long serialVersionUID = 1L;
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String fileRoot = this.getServletContext().getContextPath();
		
		contentType = this.getInitParameter("contentType");

		VersionDAO dao = new VersionDAO();
		TVersion tversion;
		String filePath;
		String fileName;
		try {
			tversion = dao.getUpdateVersion(0, 0).get(0);
			// System.out.println(fileRoot + tversion.getApp_path());
			//filePath = fileRoot + tversion.getApp_path();
			//filePath = ".\\apk\\social_0.18.01.apk";
			
			System.out.println(this.getServletContext().getRealPath(""));
			String realContextPath = this.getServletContext().getRealPath("");
			if(realContextPath.endsWith("\\")){
				realContextPath = realContextPath.substring(0,realContextPath.length()-1);
			}
			
			System.out.println(realContextPath);
			System.out.println(this.getServletContext().getContextPath().split("/")[1]);
			
			filePath = realContextPath + tversion.getApp_path();
			
			File downloadFile=new File(filePath);
			
			response.setContentType(contentType);
			Long length = downloadFile.length();
			response.setContentLength(length.intValue());
			fileName = URLEncoder.encode(downloadFile.getName(), enc);
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);

			ServletOutputStream servletOutputStream = response.getOutputStream();
			FileInputStream fileInputStream = new FileInputStream(downloadFile);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			int size = 0;
			byte[] b = new byte[4096];
			while ((size = bufferedInputStream.read(b)) != -1) {
				servletOutputStream.write(b, 0, size);
			}
			servletOutputStream.flush();
			servletOutputStream.close();
			bufferedInputStream.close();
			
			int download_count = tversion.getDownload_count();
			download_count ++ ;
			tversion.setDownload_count(download_count);
			dao.update(tversion);

			dao.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// System.out.println(fileRoot);
	}

}
