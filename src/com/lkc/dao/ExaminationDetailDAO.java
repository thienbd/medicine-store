package com.lkc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.lkc.entities.Examination;
import com.lkc.entities.ExaminationDetail;

@SuppressWarnings("unchecked")
public class ExaminationDetailDAO extends GenericDAO<ExaminationDetail> {

	public ExaminationDetailDAO() {
		super(ExaminationDetail.class);
	}

	public List<ExaminationDetail> loadByExamination(final Examination examination) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<ExaminationDetail> result = hibernateTemplate.findByNamedParam("from " + entityBeanType.getSimpleName()
				+ " exd where exd.examination=:examination", "examination", examination);
		return result;
	}

	public double sumByExamination(final Examination examination) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		double result = hibernateTemplate.execute(new HibernateCallback<Double>() {
			@Override
			public Double doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "select sum(exd.medicine.price) from " + entityBeanType.getSimpleName()
						+ " exd where exd.examination=:examination";
				Query query = session.createQuery(sql);
				query.setEntity("examination", examination);
				return (Double) query.list().get(0);
			}
		});
		return result;
	}

	public int deleteAllByExamination(final Examination examination) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		int result = hibernateTemplate.execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session arg0) throws HibernateException, SQLException {
				String sql = "delete from " + entityBeanType.getSimpleName() + " where examination_id=:examination_id";
				SQLQuery query = arg0.createSQLQuery(sql);
				query.setLong("examination_id", examination.getId());
				return query.executeUpdate();
			}
		});
		return result;
	}
}
