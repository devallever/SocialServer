package com.social.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.social.pojo.TNews;
import com.social.util.HibernateUtil;

public class NewsDAO {
	
	Session session = null;
	boolean commit = false;

	public NewsDAO() {
		this.session = HibernateUtil.getSession();
		commit = true;
		HibernateUtil.beginSession(session);
	}
	
	
	public NewsDAO(Session session) {
		this.session = session;
		commit = false;
	}
	
	public void close() {
		if (commit == true) {
			HibernateUtil.commitTransaction(session);
			session.close();
		}
	}

	public Session getSession() {
		return session;
	}
	
	
	public TNews getById(long id) throws Exception {
		TNews d = null;
		try {
			d = (TNews) session.get(TNews.class, id);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		return d;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TNews> getByQuery(String conditions, long start,
			long limit) throws Exception {
		List<TNews> dl = null;

		try {
			String hql = "from TNews";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " where " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TNews>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	@SuppressWarnings("unchecked")
	public List<TNews> getByQueryOrderByDateDesc(long start,
			long limit) throws Exception {
		List<TNews> dl = null;

		try {
			String hql = "from TNews order by date desc";

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TNews>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	@SuppressWarnings("unchecked")
	public List<TNews> getHotNewsList(long start,
			long limit) throws Exception {
		List<TNews> dl = null;

		try {
			String hql = "from TNews WHERE visited_count>0 ORDER BY visited_count DESC";

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) 0);
				query.setMaxResults((int) 0);
			}
			dl = (List<TNews>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	public TNews add(TNews d) throws Exception {
		Long id = null;

		try {
			id = (Long) session.save(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return getById(id);
	}
	
	public void deleteById(long id) throws Exception {
		TNews d = null;

		try {
			d = (TNews) session.get(TNews.class, id);
			if (d != null)
				session.delete(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
	}
	
	public TNews update(TNews d) throws Exception {
		try {
			session.update(d);
		}
		catch(RuntimeException e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		
		return getById(d.getId());
	}


}
