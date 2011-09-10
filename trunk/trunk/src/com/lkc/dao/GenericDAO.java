package com.lkc.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

@SuppressWarnings("unchecked")
public class GenericDAO<T extends Serializable> extends HibernateDaoSupport {

	protected Class<T> entityBeanType;

	public GenericDAO(Class<T> entityBeanType) {
		this.entityBeanType = entityBeanType;
	}

	public void save(T entity) {
		getHibernateTemplate().save(entity);
	}

	public void delete(T entity) {
		getHibernateTemplate().delete(entity);
	}

	public void update(T entity) {
		getHibernateTemplate().update(entity);
	}

	public void saveOrUpdate(T entity) {
		getHibernateTemplate().saveOrUpdate(entity);
	}

	public void saveOrUpdateAll(Collection<T> entities) {
		getHibernateTemplate().saveOrUpdateAll(entities);
	}

	public List<T> load(final int offset, final int length) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<T> result = hibernateTemplate.execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("from " + entityBeanType.getSimpleName());
				query.setFirstResult(offset);
				query.setMaxResults(length);
				return query.list();
			}
		});
		return result;
	}

	public List<T> loadSearch(final int offset, final int length, final Map<String, String> search) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<T> result = hibernateTemplate.execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "from " + entityBeanType.getSimpleName() + " et";
				Set<String> fieldSet = search.keySet();
				if (!search.isEmpty()) {
					sql += " where ";
					int i = 0;
					for (String field : fieldSet) {
						if (i > 0) {
							sql += " or ";
						}
						sql += " et." + field + " like :value" + i;
						i++;
					}
				}
				Query query = session.createQuery(sql);
				int i = 0;
				for (String field : fieldSet) {
					query.setString("value" + i, "%" + search.get(field) + "%");
					i++;
				}
				query.setFirstResult(offset);
				query.setMaxResults(length);
				return query.list();
			}
		});
		return result;
	}

	public List<T> load(final int offset, final int length, final Map<String, Boolean> orderField) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<T> result = hibernateTemplate.execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "from " + entityBeanType.getSimpleName();
				if (orderField.size() > 0) {
					Set<String> fieldSet = orderField.keySet();
					sql += " et order by";
					boolean first = true;
					for (String field : fieldSet) {
						if (!first) {
							sql += ",";
						} else {
							first = false;
						}
						sql += " et." + field + " " + (orderField.get(field) ? "asc" : "desc");
					}
				}
				Query query = session.createQuery(sql);
				query.setFirstResult(offset);
				query.setMaxResults(length);
				return query.list();
			}
		});
		return result;
	}

	public List<T> loadSearch(final int offset, final int length, final Map<String, Boolean> orderField, final Map<String, String> search) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<T> result = hibernateTemplate.execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "from " + entityBeanType.getSimpleName() + " et ";
				Set<String> fieldSet = search.keySet();
				if (!search.isEmpty()) {
					sql += " where ";
					int i = 0;
					for (String field : fieldSet) {
						if (i > 0) {
							sql += " or ";
						}
						sql += " et." + field + " like :value" + i;
						i++;
					}
				}
				if (orderField.size() > 0) {
					Set<String> fieldSetOrder = orderField.keySet();
					sql += " order by";
					boolean first = true;
					for (String field : fieldSetOrder) {
						if (!first) {
							sql += ",";
						} else {
							first = false;
						}
						sql += " et." + field + " " + (orderField.get(field) ? "asc" : "desc");
					}
				}
				Query query = session.createQuery(sql);
				int i = 0;
				for (String field : fieldSet) {
					query.setString("value" + i, "%" + search.get(field) + "%");
					i++;
				}
				query.setFirstResult(offset);
				query.setMaxResults(length);
				return query.list();
			}
		});
		return result;
	}

	public long count() {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		long result = hibernateTemplate.execute(new HibernateCallback<Long>() {
			@Override
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("select count(*) from " + entityBeanType.getSimpleName());
				return (Long) query.list().iterator().next();
			}
		});
		return result;
	}

	public long countSearch(final Map<String, String> search) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		long result = hibernateTemplate.execute(new HibernateCallback<Long>() {
			@Override
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "select count(*) from " + entityBeanType.getSimpleName() + " et ";
				Set<String> fieldSet = search.keySet();
				if (!search.isEmpty()) {
					sql += " where ";
					int i = 0;
					for (String field : fieldSet) {
						if (i > 0) {
							sql += " or ";
						}
						sql += " et." + field + " like :value" + i;
						i++;
					}
				}
				Query query = session.createQuery(sql);
				int i = 0;
				for (String field : fieldSet) {
					query.setString("value" + i, "%" + search.get(field) + "%");
					i++;
				}
				return (Long) query.list().iterator().next();
			}
		});
		return result;
	}

	public List<T> loadByField(final String field, final Object value) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<T> result = hibernateTemplate.execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("from " + entityBeanType.getSimpleName() + " b where b." + field + "=:value");
				setData(query, "value", value);
				return query.list();
			}
		});
		return result;
	}

	public boolean checkExistByField(final String field, final Object value) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		Boolean result = hibernateTemplate.execute(new HibernateCallback<Boolean>() {
			@Override
			public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("select count(*) from " + entityBeanType.getSimpleName() + " b where b." + field
						+ "=:value");
				setData(query, "value", value);
				Long temp = (Long) query.list().iterator().next();
				return temp != null && temp.longValue() > 0;
			}
		});
		return result;
	}

	public boolean checkExistByFieldWithCreteria(final String field, final Object value, final String notInField, final Object notInValue) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		Boolean result = hibernateTemplate.execute(new HibernateCallback<Boolean>() {
			@Override
			public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("select count(*) from " + entityBeanType.getSimpleName() + " b where b." + field
						+ "=:value and b." + notInField + "!=:notIn");
				setData(query, "value", value);
				setData(query, "notIn", notInValue);
				Long temp = (Long) query.list().iterator().next();
				return temp != null && temp.longValue() > 0;
			}
		});
		return result;
	}

	protected void setData(Query query, String field, Object value) {
		if (value instanceof Long) {
			query.setLong(field, (Long) value);
			return;
		}
		if (value instanceof Integer) {
			query.setInteger(field, (Integer) value);
			return;
		}
		if (value instanceof Short) {
			query.setShort(field, (Short) value);
			return;
		}
		if (value instanceof Byte) {
			query.setByte(field, (Byte) value);
			return;
		}
		if (value instanceof Double) {
			query.setDouble(field, (Double) value);
			return;
		}
		if (value instanceof Float) {
			query.setFloat(field, (Float) value);
			return;
		}
		if (value instanceof String) {
			query.setString(field, value.toString());
			return;
		}
		if (value instanceof Calendar) {
			query.setCalendar(field, (Calendar) value);
			return;
		}
		if (value instanceof Date) {
			query.setDate(field, (Date) value);
			return;
		}
		query.setEntity(field, value);
	}

	public void refresh(T entity) {
		getHibernateTemplate().refresh(entity);
	}

	public List<T> load() {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<T> result = hibernateTemplate.find("from " + entityBeanType.getSimpleName());
		return result;
	}
}
