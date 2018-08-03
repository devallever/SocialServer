package com.social.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.social.pojo.TRank;
import com.social.util.HibernateUtil;


public class RankDAO {
	
	Session session = null;
	boolean commit = false;

	public RankDAO() {
		this.session = HibernateUtil.getSession();
		commit = true;
		HibernateUtil.beginSession(session);
	}
	
	
	public RankDAO(Session session) {
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
	
	
	public TRank getById(long id) throws Exception {
		TRank d = null;
		try {
			d = (TRank) session.get(TRank.class, id);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
		return d;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TRank> getByQuery(String conditions, long start,
			long limit) throws Exception {
		List<TRank> dl = null;

		try {
			String hql = "from TRank";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " where " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TRank>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TRank> getMyChatRank(String user_id, long start,
			long limit) throws Exception {
		List<TRank> dl = null;

		try {
			String hql = "select TRank.user_id, count(TRank.chatrank) from TRank where TRank.user_id=" + user_id + " group by TRank.user_id";
			System.out.println(hql);
			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TRank>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	@SuppressWarnings("unchecked")
	public List<TRank> getByQueryWithGroupBy(String conditions, long start,
			long limit) throws Exception {
		List<TRank> dl = null;

		try {
			String hql = "from TRank";
			if ((conditions != null) && (conditions.length() > 0))
				hql += " " + conditions;

			Query query = session.createQuery(hql);
			if (limit > 0) {
				query.setFirstResult((int) start);
				query.setMaxResults((int) limit);
			}
			dl = (List<TRank>) query.list();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}

		return dl;
	}
	
	public TRank add(TRank d) throws Exception {
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
		TRank d = null;

		try {
			d = (TRank) session.get(TRank.class, id);
			if (d != null)
				session.delete(d);
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(session);
			throw e;
		}
	}
	
	public TRank update(TRank d) throws Exception {
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
