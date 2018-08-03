package com.social.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.social.pojo.TNewsImage;
import com.social.util.HibernateUtil;

public class NewsImageDAO {
	
	Session session = null;
	boolean commit = false;

	public NewsImageDAO() {
		this.session = HibernateUtil.getSession();
		commit = true;
		HibernateUtil.beginSession(session);
	}
	
	
	public NewsImageDAO(Session session) {
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
	
	
	public TNewsImage getById(long id) throws Exception {
		TNewsImage d = null;
		try {
			d = (TNewsImage) session.get(TNewsImage.class, id);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		return d;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TNewsImage> getByQuery(String conditions, long start,
			long limit) throws Exception {
		List<TNewsImage> dl = null;

		try {
			String hql = "from TNewsImage";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " where " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TNewsImage>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	public TNewsImage add(TNewsImage d) throws Exception {
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
		TNewsImage d = null;

		try {
			d = (TNewsImage) session.get(TNewsImage.class, id);
			if (d != null)
				session.delete(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
	}
	
	public TNewsImage update(TNewsImage d) throws Exception {
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
