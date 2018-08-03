package com.social.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.social.pojo.TFollow;
import com.social.util.HibernateUtil;

public class FollowDAO {
	
	Session session = null;
	boolean commit = false;

	public FollowDAO() {
		this.session = HibernateUtil.getSession();
		commit = true;
		HibernateUtil.beginSession(session);
	}
	
	
	public FollowDAO(Session session) {
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
	
	
	public TFollow getById(long id) throws Exception {
		TFollow d = null;
		try {
			d = (TFollow) session.get(TFollow.class, id);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		return d;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TFollow> getByQuery(String conditions, long start,
			long limit) throws Exception {
		List<TFollow> dl = null;

		try {
			String hql = "from TFollow";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " where " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TFollow>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	@SuppressWarnings("unchecked")
	public List<TFollow> getByHQL(String hql, long start,
			long limit) throws Exception {
		List<TFollow> dl = null;

		try {
			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TFollow>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
//	@SuppressWarnings("unchecked")
//	public List<TAd> getUpdateVersion(long start,long limit) throws Exception {
//		List<TAd> dl = null;
//
//		try {
//			String hql = "from TAd ORDER BY version_code DESC";
//
//			Query query = session.createQuery(hql);
//			if (limit > 0) {
//				query.setFirstResult((int) 0);
//				query.setMaxResults((int) 0);
//			}
//			dl = (List<TAd>) query.list();
//		} catch (Exception e) {
//			HibernateUtil.rollbackTransaction(session);
//			throw e;
//		}
//
//		return dl;
//	}
	
	public TFollow add(TFollow d) throws Exception {
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
		TFollow d = null;

		try {
			d = (TFollow) session.get(TFollow.class, id);
			if (d != null)
				session.delete(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
	}
	
	public TFollow update(TFollow d) throws Exception {
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
