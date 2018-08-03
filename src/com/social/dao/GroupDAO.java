package com.social.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.social.pojo.TGroup;
import com.social.util.HibernateUtil;


public class GroupDAO {
	
	Session session = null;
	boolean commit = false;

	public GroupDAO() {
		this.session = HibernateUtil.getSession();
		commit = true;
		HibernateUtil.beginSession(session);
	}
	
	
	public GroupDAO(Session session) {
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
	
	
	public TGroup getById(long id) throws Exception {
		TGroup d = null;
		try {
			d = (TGroup) session.get(TGroup.class, id);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		return d;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TGroup> getByQuery(String conditions, long start,
			long limit) throws Exception {
		List<TGroup> dl = null;

		try {
			String hql = "from TGroup";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " where " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TGroup>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	public TGroup add(TGroup d) throws Exception {
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
		TGroup d = null;

		try {
			d = (TGroup) session.get(TGroup.class, id);
			if (d != null)
				session.delete(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
	}
	
	public TGroup update(TGroup d) throws Exception {
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
