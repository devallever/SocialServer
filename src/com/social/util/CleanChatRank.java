package com.social.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.social.dao.RankDAO;
import com.social.pojo.TRank;


public class CleanChatRank {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while(true){
						
						Date date = new Date();
						String hour = df.format(date).split(" ")[1].split(":")[0];
						String min = df.format(date).split(" ")[1].split(":")[1];
						if(hour.equals("00") && min.equals("00")){
							//Çå¿Õ
							List<TRank> list_trank;
							RankDAO dao = new RankDAO();
							list_trank = dao.getByQuery("", 0, 0);
							dao.close();
							for(TRank trank:list_trank){
								dao = new RankDAO();
								dao.deleteById(trank.getId());
								dao.close();
							}
							
						}
						Thread.sleep(1000);
						
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}

}
