package com.lkc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.lkc.entities.Examination;
import com.lkc.entities.Patient;

@SuppressWarnings("unchecked")
public class ExaminationDAO extends GenericDAO<Examination> {
	public ExaminationDAO() {
		super(Examination.class);
	}

	public List<Examination> loadByPatient(final Patient patient) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<Examination> result = hibernateTemplate.execute(new HibernateCallback<List<Examination>>() {
			@Override
			public List<Examination> doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "from " + entityBeanType.getSimpleName() + " ex where ex.patient=:patient order by ex.examDate asc";
				Query query = session.createQuery(sql);
				query.setEntity("patient", patient);
				return query.list();
			}
		});
		return result;
	}
}
