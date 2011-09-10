package com.lkc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.lkc.entities.User;

@SuppressWarnings("unchecked")
public class UserDAO extends GenericDAO<User> {

	public UserDAO() {
		super(User.class);
	}

	public User login(final String userName, final String password) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		User result = hibernateTemplate.execute(new HibernateCallback<User>() {
			@Override
			public User doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "from User where userName=:userName and password=:password";
				Query query = session.createQuery(sql);
				query.setString("userName", userName);
				query.setString("password", password);
				List<User> users = query.list();
				if (users.size() == 1) {
					return users.get(0);
				}
				return null;
			}
		});
		return result;
	}

	public User login(final long userId, final String password) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		User result = hibernateTemplate.execute(new HibernateCallback<User>() {
			@Override
			public User doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "from User where id=:userId and password=:password";
				Query query = session.createQuery(sql);
				query.setLong("userId", userId);
				query.setString("password", password);
				List<User> users = query.list();
				if (users.size() == 1) {
					return users.get(0);
				}
				return null;
			}
		});
		return result;
	}
}
