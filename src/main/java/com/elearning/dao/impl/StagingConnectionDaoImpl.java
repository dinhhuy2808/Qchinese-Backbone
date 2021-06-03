package com.elearning.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.elearning.dao.ConnectionDao;

public class StagingConnectionDaoImpl implements ConnectionDao {

	@Override
	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://103.92.29.71:3306/" + "staging"
							+ "?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&useLegacyDatetimeCode=false&useUnicode=true&characterEncoding=UTF-8",
					"root", "9203140Huy!@#");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;
	}

}
