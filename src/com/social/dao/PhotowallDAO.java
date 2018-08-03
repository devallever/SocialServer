package com.social.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.social.pojo.TPhotowall;
import com.social.util.HibernateUtil;


public class PhotowallDAO {
	
	Session session = null;
	boolean commit = false;

	public PhotowallDAO() {
		this.session = HibernateUtil.getSession();
		commit = true;
		HibernateUtil.beginSession(session);
	}
	
	
	public PhotowallDAO(Session session) {
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
	
	
	public TPhotowall getById(long id) throws Exception {
		TPhotowall d = null;
		try {
			d = (TPhotowall) session.get(TPhotowall.class, id);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		return d;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TPhotowall> getByQuery(String conditions, long start,
			long limit) throws Exception {
		List<TPhotowall> dl = null;

		try {
			String hql = "from TPhotowall";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " where " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TPhotowall>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	public TPhotowall add(TPhotowall d) throws Exception {
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
		TPhotowall d = null;

		try {
			d = (TPhotowall) session.get(TPhotowall.class, id);
			if (d != null)
				session.delete(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
	}
	
	public TPhotowall update(TPhotowall d) throws Exception {
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
