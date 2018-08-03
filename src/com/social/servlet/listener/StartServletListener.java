package com.social.servlet.listener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.social.dao.RankDAO;
import com.social.pojo.TRank;

/**
 * Application Lifecycle Listener implementation class StartServletListener
 *
 */
@WebListener
public class StartServletListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public StartServletListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    	System.out.println("Servlet已启动");
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
							//清空
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
						System.out.println(df.format(date));
						Thread.sleep(1000*60);
						
					}
					
					
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
    }
	
}
