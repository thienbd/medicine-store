package com.lkc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.lkc.entities.Medicine;

@SuppressWarnings("unchecked")
public class MedicineDAO extends GenericDAO<Medicine> {
	public MedicineDAO() {
		super(Medicine.class);
	}

	public List<String> loadMedicineName() {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<String> result = hibernateTemplate.execute(new HibernateCallback<List<String>>() {
			@Override
			public List<String> doInHibernate(Session session) throws HibernateException, SQLException {
				String field = "name";
				Query query = session.createQuery("select " + field + " from " + entityBeanType.getSimpleName() + " b order by " + field
						+ " asc");
				return query.list();
			}
		});
		return result;
	}
}
