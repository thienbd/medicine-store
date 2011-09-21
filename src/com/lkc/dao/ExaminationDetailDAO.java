package com.lkc.dao;

import java.util.List;

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
}
