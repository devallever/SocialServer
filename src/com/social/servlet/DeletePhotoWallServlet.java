package com.social.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import com.social.dao.PhotowallDAO;
import com.social.pojo.TPhotowall;
import com.social.servlet.AddPhotoWallServlet.Root;


/**
 * Servlet implementation class DeletePhotoWallServlet
 */
@WebServlet("/DeletePhotoWallServlet")
public class DeletePhotoWallServlet extends HttpServlet {
	
	private String user_id;
	private int position;
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
		TPhotowall tPhotowall;
		List<TPhotowall> list_photo_wall;
		try {
			list_photo_wall = dao.getByQuery("user_id="+ user_id +" and photoposition = " + (position+1), 0, 0);
			if(list_photo_wall.size()==1){
				tPhotowall = list_photo_wall.get(0);
				String delete_path = getServletContext().getRealPath("") + "\\images\\photowallimg\\" + user_id + "_" + tPhotowall.getPhotoposition() + ".jpg";
				dao.deleteById(tPhotowall.getId());
				//删除文件
				System.out.println("delete_path = " + delete_path);
				boolean flag = deleteFile(delete_path);
				if(flag){
					System.out.println("删除成功");

					for(int i = position+1, j=0;  j<=dao.getByQuery("user_id="+ user_id +" and photoposition >" + (position+1), 0, 0).size(); i++,j++){
						System.out.println("Enter 循环"); 
						
						File oldFile = new File(getServletContext().getRealPath("") + "\\images\\photowallimg\\" + user_id + "_" + (i+1) + ".jpg");
						  if(!oldFile.exists())
						  {
						   oldFile.createNewFile();
						  }
						  System.out.println("修改前文件名称是："+oldFile.getName());
						  String rootPath = oldFile.getParent();
						  System.out.println("根路径是："+rootPath);
						  File newFile = new File(rootPath + File.separator + user_id + "_" + i + ".jpg");
						  System.out.println("修改后文件名称是："+newFile.getName());
						  if (oldFile.renameTo(newFile)) 
						  {
						   System.out.println("修改成功!");
						   System.out.println("success");
				             list_photo_wall = dao.getByQuery("user_id="+ user_id +" and photoposition=" + (i+1), 0, 0);
				             if(list_photo_wall.size()==1){
				            	 tPhotowall = list_photo_wall.get(0);
				            	 tPhotowall.setPath("/images/photowallimg/" + user_id +"_" + i + ".jpg");
				            	 tPhotowall.setPhotoposition(i);
				            	 dao.update(tPhotowall);
				             }
						  } 
						  else 
						  {
						   System.out.println("修改失败");
						  }
						
//						// 源文件
//				           final File src = new File(getServletContext().getRealPath("") + "\\images\\photowallimg\\" + user_id + "_" + (i+1) + ".jpg");
//						// 目标文件
//				           final File dest = new File(getServletContext().getRealPath("") + "\\images\\photowallimg\\" + user_id + "_" + i + ".jpg");
//				         // 如果文件有人在读取它，则不能被重命名
//				         final InputStream in = new FileInputStream(dest);
//				         final boolean con = src.renameTo(dest);
//				         if (con) {
//				             System.out.println("success");
//				             list_photo_wall = dao.getByQuery("photoposition=" + (i+1), 0, 0);
//				             if(list_photo_wall.size()==1){
//				            	 tPhotowall = list_photo_wall.get(0);
//				            	 tPhotowall.setPath("/images/photowallimg/" + user_id +"_" + i + ".jpg");
//				            	 tPhotowall.setPhotoposition(i);
//				             }
//				             //重命名成功就修改数据库
//				        } else {
//				             System.out.println("fail");
//				         }
					}
					root.success = true;
	            	 root.message = "";
						
	            	 pw.print(gson.toJson(root));
	            	 pw.close();
	            	 dao.close();
					
					
				}else{
					System.out.println("删除失败");
				}
			}
			
			//dao.deleteById(tPhotowall.getId());
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
	}
	
	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */  
	private boolean deleteFile(String sPath) {  
	    boolean flag = false;  
	    File file = new File(sPath);  
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	}  

}
