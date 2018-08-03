package com.social.util;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class DDLCreator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Configuration cfg = new Configuration().configure();
		SchemaExport se = new SchemaExport(cfg);
		se.drop(true, true);    //删除表
		se.create(true, true);  //创建表
	}
}
