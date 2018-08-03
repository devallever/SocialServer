package com.social.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.social.pojo.TVersion;
import com.social.util.HibernateUtil;

public class VersionDAO {
	
	Session session = null;
	boolean commit = false;

	public VersionDAO() {
		this.session = HibernateUtil.getSession();
		commit = true;
		HibernateUtil.beginSession(session);
	}
	
	
	public VersionDAO(Session session) {
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
	
	
	public TVersion getById(long id) throws Exception {
		TVersion d = null;
		try {
			d = (TVersion) session.get(TVersion.class, id);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		return d;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TVersion> getByQuery(String conditions, long start,
			long limit) throws Exception {
		List<TVersion> dl = null;

		try {
			String hql = "from TVersion";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " where " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TVersion>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	@SuppressWarnings("unchecked")
	public List<TVersion> getUpdateVersion(long start,long limit) throws Exception {
		List<TVersion> dl = null;

		try {
			String hql = "from TVersion ORDER BY version_code DESC";

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) 0);
				query.setMaxResults((int) 0);
			}
			dl = (List<TVersion>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	public TVersion add(TVersion d) throws Exception {
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
		TVersion d = null;

		try {
			d = (TVersion) session.get(TVersion.class, id);
			if (d != null)
				session.delete(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
	}
	
	public TVersion update(TVersion d) throws Exception {
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
