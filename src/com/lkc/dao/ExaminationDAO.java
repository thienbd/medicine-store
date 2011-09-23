package com.lkc.dao;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
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

	public long countByDate(final Date fromDate, final Date toDate) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		long result = hibernateTemplate.execute(new HibernateCallback<Long>() {
			@Override
			public Long doInHibernate(Session arg0) throws HibernateException, SQLException {
				String sql = "select count(distinct ex.patient) from " + entityBeanType.getSimpleName()
						+ " ex where ex.examDate>=:fromDate and ex.examDate<=:toDate";
				Query query = arg0.createQuery(sql);
				query.setDate("fromDate", fromDate);
				query.setDate("toDate", toDate);
				return (Long) query.list().get(0);
			}
		});
		return result;
	}

	public long countByCalendar(final Calendar fromDate, final Calendar toDate) {
		return countByDate(fromDate.getTime(), toDate.getTime());
	}
}
