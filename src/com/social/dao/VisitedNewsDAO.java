package com.social.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.social.pojo.TVisitednews;
import com.social.util.HibernateUtil;

public class VisitedNewsDAO {
	
	Session session = null;
	boolean commit = false;

	public VisitedNewsDAO() {
		this.session = HibernateUtil.getSession();
		commit = true;
		HibernateUtil.beginSession(session);
	}
	
	
	public VisitedNewsDAO(Session session) {
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
	
	
	public TVisitednews getById(long id) throws Exception {
		TVisitednews d = null;
		try {
			d = (TVisitednews) session.get(TVisitednews.class, id);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		return d;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TVisitednews> getByQuery(String conditions, long start,
			long limit) throws Exception {
		List<TVisitednews> dl = null;

		try {
			String hql = "from TVisitednews";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " where " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TVisitednews>) query.list();
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
	
	public TVisitednews add(TVisitednews d) throws Exception {
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
		TVisitednews d = null;

		try {
			d = (TVisitednews) session.get(TVisitednews.class, id);
			if (d != null)
				session.delete(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
	}
	
	public TVisitednews update(TVisitednews d) throws Exception {
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
