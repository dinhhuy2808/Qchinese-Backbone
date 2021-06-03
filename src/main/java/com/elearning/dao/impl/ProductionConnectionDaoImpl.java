package com.elearning.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.elearning.dao.ConnectionDao;

public class ProductionConnectionDaoImpl implements ConnectionDao {

	@Override
	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/" + "elearning"
							+ "?serverTimezone=UTC&useLegacyDatetimeCode=false&useUnicode=true&characterEncoding=UTF-8&useSSL=false",
					"root", "9203140Huy!@#");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;
	}

}
