package ecc.cords;

import java.util.Arrays;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;

public class Dao{

	private final Statistics stats = HibernateUtil.getSessionFactory().getStatistics();

 	public Session initSession() {
 		stats.setStatisticsEnabled(true);
		Session session = HibernateUtil.getSessionFactory().openSession();
		if(!session.getTransaction().isActive()) {
			session.beginTransaction();
		}
		return session;
	}

  	public <T> void delete(final T t) {
	   	Session session = initSession();
	   	session.delete(t);
		session.getTransaction().commit();
	    session.close();
 	}

	public <T> T get(final long id, final Class<T> type) {
		Session session = initSession();
		T t = (T) session.get(type, id);
		showStatistics("from get(id,type)",stats);
		session.close();
		return t;
	}

	public <T> T get(T t) {
		Session session = initSession();
		List<T> list = session.createCriteria(t.getClass()).setCacheable(true).list();
		session.getTransaction().commit();
		showStatistics("from get(t)",stats);
		session.close();
		return (T) list.get(list.indexOf((T)t));
	}

	public <T> List<T> getAll(final Class<T> type) {
	   	Session session = initSession();
	   	List<T> list = session.createCriteria(type).setCacheable(true).list();
	   	showStatistics("from getAll(type)",stats);
	   	session.close();
	   	return list;
 	}

 	public <T> List getAllSimplified(String order, final Class<T> type) {
 		Session session = initSession();
 		Criteria criteria = session.createCriteria(type);
 		criteria.setCacheable(true);
 		criteria.setProjection(Projections.projectionList()
 										  .add(Projections.property("empId"))
 										  .add(Projections.property("name"))
 										  .add(Projections.property("hireDate"))
 										  .add(Projections.property("gwa"))
 		);
 		criteria.addOrder(Property.forName(order).asc());
 		List list = criteria.list();
 		showStatistics("from getAllSimplified(type)",stats);
 		session.close();
 		return list;
 	}

 	public <T> List getByCriteria(String order, final Class<T> type) {
  		Session session = initSession();
  		Criteria criteria = session.createCriteria(type)
  						   .addOrder(Property.forName(order).asc());	  
		criteria.setCacheable(true);  						   
		List list = criteria.list();
		showStatistics("from getByCriteria(order,type)",stats);
  		session.close();
  		return list;	
  	}

  	public <T> void save(final T t) {
	   	Session session = initSession();
	   	session.save(t);
		session.getTransaction().commit();
	   	session.close();
  	}

	public <T> void saveOrUpdate(final T t) {
	  	Session session = initSession();
	   	session.saveOrUpdate(t);
	   	session.getTransaction().commit();
	   	session.close();
	}

	private void showStatistics(String from, Statistics stats) {
		System.out.println("***********************************");
		//System.out.println(HibernateUtil.getSessionFactory().getStatistics());
		System.out.println(from);
        System.out.println("Entity fetch count :" + stats.getEntityFetchCount());
        System.out.println("Second level cache hit count : "+ stats.getSecondLevelCacheHitCount());
        System.out.println("Second level cache put count : " + stats.getSecondLevelCachePutCount());
        System.out.println("Second level cache miss count : " + stats.getSecondLevelCacheMissCount());
        System.out.println("***********************************");
	}
}