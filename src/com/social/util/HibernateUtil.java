package com.social.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


@SuppressWarnings("deprecation")
public class HibernateUtil {
	private static SessionFactory sessionFactory;
	static {
		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		// Alternatively, you could look up in JNDI here
		return sessionFactory;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}
	
	public static Session getSession()
	{
		Session session = sessionFactory.openSession();
		return session;
	}
	
	public static void beginSession(Session session)
	{
		session.getTransaction().begin();
	}
	
	public static void commitTransaction(Session session)
	{
		session.getTransaction().commit();
	}
	
	public static void rollbackTransaction(Session session)
	{
		Transaction tx = session.getTransaction();
		if (tx.isActive())
			tx.rollback();
	}
}