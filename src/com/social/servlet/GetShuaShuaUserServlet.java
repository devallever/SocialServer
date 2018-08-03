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

import sun.net.www.protocol.http.HttpURLConnection.TunnelState;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.fabric.xmlrpc.base.Array;
import com.social.dao.PhotowallDAO;
import com.social.dao.ShuaShuaDAO;
import com.social.dao.UserDAO;
import com.social.pojo.TPhotowall;
import com.social.pojo.TShuaShua;
import com.social.pojo.TUser;
import com.social.servlet.MayknowServlet.User;
import com.social.util.CommonUtils;

/**
 * Servlet implementation class GetShuaShuaUserServlet
 */
@WebServlet("/GetShuaShuaUserServlet")
public class GetShuaShuaUserServlet extends HttpServlet {
	
	private String str_longitude;
	private String str_latitude;
	
	private PrintWriter pw;
	private Root root;
	private String user_id;
	
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
			root.success = false;
			pw.print(gson.toJson(root));
			pw.close();
		}
		user_id = session.getAttribute("id").toString();
		
		//获取参数
		str_longitude = request.getParameter("longitude");
		if(!checkParameter(response, "longitude", str_longitude, root, pw)) return;
		//longitude = Double.parseDouble(str_latitude);
		
		str_latitude = request.getParameter("latitude");
		if(!checkParameter(response, "latitude", str_latitude, root, pw)) return;
		//latitude = Double.parseDouble(str_latitude);
		TUser tuser = null;
		List<TUser> list_tuser;
		UserDAO dao = new UserDAO();
		ShuaShuaDAO shuashuaDAO = new ShuaShuaDAO(dao.getSession());
		List<TShuaShua> list_tshuashua;
		try {
			
			if(tuser == null){
				root.list_shuashuaUser = null;
				root.success = false;
				root.message = "";
				//root.list_tuser = list_tuser;
				pw.print(gson.toJson(root));
				pw.close();
				shuashuaDAO.close();
				dao.close();
				return;
			}
			
			tuser = dao.getById(Long.parseLong(user_id));
			String sex = tuser.getSex();
			
			list_tshuashua = shuashuaDAO.getByQuery("my_id=" + user_id, 0, 0);
			
			//String[] not_include_username = {"allever", "baobao","yc_been"};
			String[] arr_not_include_username = new String[list_tshuashua.size()];
			for(int i = 0; i<list_tshuashua.size(); i++){
				arr_not_include_username[i] = list_tshuashua.get(i).getOther().getUsername();
			}
			
			
			String not_include = "";
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i<arr_not_include_username.length; i++){
				builder.append("'" + arr_not_include_username[i] + "'");
				if (i < arr_not_include_username.length - 1) {
					builder.append(",");
				}
			}
			not_include = builder.toString();
			System.out.println(not_include);
			
			//String hql = "from TUser where sex !='" + sex + "'";//限制性别
			//String hql = "from TUser where sex !='" + sex + "'" + "and username not in('allever','baobao')";
			String hql;
			if(not_include.equals("")){
				hql = "from TUser where sex !='" + sex + "'";
			}else{
				hql = "from TUser where sex !='" + sex + "'" + "and username not in("+ not_include +")";
			}
			
			list_tuser = dao.getUserListByHql(hql, 0, 0);
			
			
			ShuaShuaUser shuashuaUser;
			List<ShuaShuaUser> list_shuashuaUser = new ArrayList<GetShuaShuaUserServlet.ShuaShuaUser>();
			PhotowallDAO photowallDAO = new PhotowallDAO(dao.getSession());
			List<TPhotowall> list_photowList;
			for(TUser result_tuser : list_tuser){
				list_photowList = photowallDAO.getByQuery("user_id="+result_tuser.getId(), 0, 0);
				if(list_photowList!=null && list_photowList.size()>=1){
					//进一步操作
					shuashuaUser = new ShuaShuaUser();
					shuashuaUser.username = result_tuser.getUsername();
					shuashuaUser.nickanme = result_tuser.getNickname();
					shuashuaUser.sex = result_tuser.getSex();
					shuashuaUser.occupation = result_tuser.getOccupation();
					shuashuaUser.age = result_tuser.getAge();
					shuashuaUser.signature = result_tuser.getSignature();
					double distance = CommonUtils.getDistance(str_latitude, str_longitude, String.valueOf(result_tuser.getLatitude()), String.valueOf(result_tuser.getLongitude()));
					String dis_with_two = new java.text.DecimalFormat("#.00").format(distance);
					shuashuaUser.distance = Double.parseDouble(dis_with_two);
					List<String> list_photowall_img_urls = new ArrayList<String>();
					for(TPhotowall tphotowall: list_photowList){
						list_photowall_img_urls.add(tphotowall.getPath());
					}
					switch (list_photowList.size()) {
					case 1:
						list_photowall_img_urls.add(result_tuser.getHeadpath());
						list_photowall_img_urls.add(result_tuser.getHeadpath());
						list_photowall_img_urls.add(result_tuser.getHeadpath());
						break;
					case 2:
						list_photowall_img_urls.add(result_tuser.getHeadpath());
						list_photowall_img_urls.add(result_tuser.getHeadpath());
						break;
					case 3:
						list_photowall_img_urls.add(result_tuser.getHeadpath());
						break;
					default:
						break;
					}
					shuashuaUser.photo_wall_img_urls = list_photowall_img_urls;
					list_shuashuaUser.add(shuashuaUser);
					
					
				}
			}
			root.list_shuashuaUser = list_shuashuaUser;
			root.success = true;
			root.message = "";
			//root.list_tuser = list_tuser;
			pw.print(gson.toJson(root));
			pw.close();
			photowallDAO.close();
			shuashuaDAO.close();
			dao.close();
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
		List<ShuaShuaUser> list_shuashuaUser;
		//List<TUser> list_tuser;
	}
	
	class ShuaShuaUser{
		String username;
		String nickanme;
		String sex;
		String occupation;
		int age;
		double distance;
		String signature;
		List<String> photo_wall_img_urls;
	}
	
	
}
