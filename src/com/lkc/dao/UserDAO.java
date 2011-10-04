package com.lkc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.lkc.entities.MyUser;

@SuppressWarnings("unchecked")
public class UserDAO extends GenericDAO<MyUser> {

	public UserDAO() {
		super(MyUser.class);
	}

	public MyUser login(final String userName, final String password) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		MyUser result = hibernateTemplate.execute(new HibernateCallback<MyUser>() {
			@Override
			public MyUser doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "from " + entityBeanType.getSimpleName() + " usr where usr.userName=:userName and usr.password=:password";
				Query query = session.createQuery(sql);
				query.setString("userName", userName);
				query.setString("password", password);
				List<MyUser> users = query.list();
				if (users.size() == 1) {
					return users.get(0);
				}
				return null;
			}
		});
		return result;
	}

	public MyUser login(final long userId, final String password) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		MyUser result = hibernateTemplate.execute(new HibernateCallback<MyUser>() {
			@Override
			public MyUser doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "from " + entityBeanType.getSimpleName() + " usr where usr.id=:userId and usr.password=:password";
				Query query = session.createQuery(sql);
				query.setLong("userId", userId);
				query.setString("password", password);
				List<MyUser> users = query.list();
				if (users.size() == 1) {
					return users.get(0);
				}
				return null;
			}
		});
		return result;
	}
}
