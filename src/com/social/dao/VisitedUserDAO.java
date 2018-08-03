package com.social.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.social.pojo.TVisiteduser;
import com.social.util.HibernateUtil;

public class VisitedUserDAO {
	
	Session session = null;
	boolean commit = false;

	public VisitedUserDAO() {
		this.session = HibernateUtil.getSession();
		commit = true;
		HibernateUtil.beginSession(session);
	}
	
	
	public VisitedUserDAO(Session session) {
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
	
	
	public TVisiteduser getById(long id) throws Exception {
		TVisiteduser d = null;
		try {
			d = (TVisiteduser) session.get(TVisiteduser.class, id);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		return d;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TVisiteduser> getByQuery(String conditions, long start,
			long limit) throws Exception {
		List<TVisiteduser> dl = null;

		try {
			String hql = "from TVisiteduser";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " where " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TVisiteduser>) query.list();
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
	
	public TVisiteduser add(TVisiteduser d) throws Exception {
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
		TVisiteduser d = null;

		try {
			d = (TVisiteduser) session.get(TVisiteduser.class, id);
			if (d != null)
				session.delete(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
	}
	
	public TVisiteduser update(TVisiteduser d) throws Exception {
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
