package com.social.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.social.pojo.TAddetail;
import com.social.util.HibernateUtil;

public class AdDetailDAO {
	
	Session session = null;
	boolean commit = false;

	public AdDetailDAO() {
		this.session = HibernateUtil.getSession();
		commit = true;
		HibernateUtil.beginSession(session);
	}
	
	
	public AdDetailDAO(Session session) {
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
	
	
	public TAddetail getById(long id) throws Exception {
		TAddetail d = null;
		try {
			d = (TAddetail) session.get(TAddetail.class, id);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		return d;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TAddetail> getByQuery(String conditions, long start,
			long limit) throws Exception {
		List<TAddetail> dl = null;

		try {
			String hql = "from TAddetail";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " where " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TAddetail>) query.list();
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
	
	public TAddetail add(TAddetail d) throws Exception {
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
		TAddetail d = null;

		try {
			d = (TAddetail) session.get(TAddetail.class, id);
			if (d != null)
				session.delete(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
	}
	
	public TAddetail update(TAddetail d) throws Exception {
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
