/*
 * Copyright (c) 2016 Aberger Software GmbH. All Rights Reserved.
 *               http://www.aberger.at
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.elearning.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.elearning.annotation.KeyColumn;
import com.elearning.dao.BaseDao;
import com.elearning.dao.ConnectionDao;

/**
 * A DataAccessObject has the responsibility to do all SQL for us.
 */
public class BaseDaoImpl<T extends Object> implements BaseDao<T> {
	@Autowired
	private ConnectionDao connectionDAO;
	
	protected  PreparedStatement getPreparedStatement(String sql) {
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ps;
	}

	protected  Connection getConnection() {
		return connectionDAO.getConnection();
	}
	
	@Override
	public List<?> findByKey(Object t) {
		StringBuilder sql = new StringBuilder("SELECT * FROM " + t.getClass().getSimpleName() + getSqlQueryForCondition(t));
		Connection conn = getConnection();
		List<T> results = new ArrayList<>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			while (rs.next()) {

				results.add(setToInstane(rs, t));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				rs.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return results;
	}
	@Override
	public List<?> findByGivenKey(Object t,String key) {
		StringBuilder sql = new StringBuilder("SELECT * FROM " + t.getClass().getSimpleName() + getSqlQueryForCondition(t,Arrays.asList(key.split(","))));
		Connection conn = getConnection();
		PreparedStatement ps = null;
		List<T> results = new ArrayList<>();
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			while (rs.next()) {

				results.add(setToInstane(rs, t));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				rs.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		return results;
	}
	@SuppressWarnings("finally")
	@Override
	public String addThenReturnId(Object t) {
		StringBuilder sql = new StringBuilder("INSERT INTO " + t.getClass().getSimpleName() + " ( " + getListOfFieldWithoutKey(t)
		+ " ) VALUES ( " + getListOfValueWithoutKey(t) + " )");
		Connection conn = getConnection();
		PreparedStatement ps = null;
		String key = "";
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate(sql.toString(),Statement.RETURN_GENERATED_KEYS);
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
			    key = rs.getString(1);
			    
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "none";
		} finally {

			try {
				ps.close();
				rs.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			return key;
		}
	}
	@SuppressWarnings("finally")
	@Override
	public boolean add(Object t) {
		StringBuilder sql = new StringBuilder("INSERT INTO " + t.getClass().getSimpleName() + " ( " + getListOfFieldWithoutKey(t)
		+ " ) VALUES ( " + getListOfValueWithoutKey(t) + " )");
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {

			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			return true;
		}
	}
	@SuppressWarnings("finally")
	@Override
	public boolean addList(List t) {
		
		
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			String temp = Arrays.asList(t.get(0).getClass().getDeclaredFields()).stream()
					.filter(field -> !field.isAnnotationPresent(KeyColumn.class)).map(field -> "?")
					.collect(Collectors.joining(","));
			StringBuilder sql = new StringBuilder("INSERT INTO " + t.get(0).getClass().getSimpleName() + " ( " + getListOfFieldWithoutKey(t.get(0))
			+ " ) VALUES ( " + /* getListOfValueWithoutKey(obj) */temp + " )");
			ps = conn.prepareStatement(sql.toString());
			for(Object obj : t) {
				getListOfValueWithoutKeyToPreparedstatement(obj, ps);
				ps.addBatch();

			}
			ps.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {

			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			return true;
		}
	}
	@Override
	public boolean deleteByKey(Object t) {
		StringBuilder sql = new StringBuilder("DELETE FROM " + t.getClass().getSimpleName() + " WHERE ");
		Class targetClass = t.getClass();
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql.toString() + String.join(" AND ", getKeyFieldsEqualValue(t)));
			ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {

			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			return true;
		}
	}
	@Override
	public boolean deleteByGivenValue(Object t) {
		StringBuilder sql = new StringBuilder("DELETE FROM " + t.getClass().getSimpleName());
		Class targetClass = t.getClass();
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql.toString() + getSqlQueryForCondition(t));
			ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {

			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			return true;
		}
	}
	@Override
	public boolean updateByKey(Object t) {
		StringBuilder sql = new StringBuilder("UPDATE " + t.getClass().getSimpleName() + " SET "
				+ String.join(" , ", getNonKeyFieldsEqualValue(t)) + " WHERE "
				+ String.join(" AND ", getKeyFieldsEqualValue(t)));
		Class targetClass = t.getClass();
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {

			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			return true;
		}
	}
	
	/*return list of non-key-field with value*/ 
	private List<String> getNonKeyFieldsEqualValue(Object t) {

		List<String> keyFieldsEqualValue = Arrays.asList(t.getClass().getDeclaredFields()).stream()
				.filter(field -> !field.isAnnotationPresent(KeyColumn.class)).map(field -> {
					String fieldName = field.getName();
					String methodName = "get" + String.valueOf(fieldName.charAt(0)).toUpperCase()
							+ fieldName.substring(1);
					String value = "";
					Method method;
					try {
						method = t.getClass().getDeclaredMethod(methodName);
						method.setAccessible(true);
						if (field.getType().getSimpleName().equals("String")
								|| field.getType().getSimpleName().equals("Date")) {
							value = "'" + method.invoke(t).toString() + "'";
						} else {
							value = method.invoke(t).toString();
						}
					} catch (NoSuchMethodException e) {
		
						e.printStackTrace();
					} catch (SecurityException e) {
		
						e.printStackTrace();
					} catch (IllegalAccessException e) {
		
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
		
						e.printStackTrace();
					} catch (InvocationTargetException e) {
		
						e.printStackTrace();
					} catch (NullPointerException e) {
		
						if (field.getType().getSimpleName().equals("String")) {
							return "`"+fieldName+"`" + " = ''"  ;
						} else {
							return "`"+fieldName+"`" + " = 0"  ;
						}
						
					}

					return "`"+fieldName+"`" + " = " + value;
				}).collect(Collectors.toList());
		return keyFieldsEqualValue;
	}

	/*return list of non-key-field with value*/ 
	private List<String> getNonKeyFieldsEqualNotNullValue(Object t) {

		List<String> keyFieldsEqualValue = Arrays.asList(t.getClass().getDeclaredFields()).stream()
				.filter(field -> !field.isAnnotationPresent(KeyColumn.class)).map(field -> {
					String fieldName = field.getName();
					String methodName = "get" + String.valueOf(fieldName.charAt(0)).toUpperCase()
							+ fieldName.substring(1);
					String value = "";
					Method method;
					try {
						method = t.getClass().getDeclaredMethod(methodName);
						method.setAccessible(true);
						if (field.getType().getSimpleName().equals("String")
								|| field.getType().getSimpleName().equals("Date")
								||field.getType().isEnum()) {
							value = "'" + method.invoke(t).toString() + "'";
						} else {
							value = method.invoke(t).toString();
						}
					} catch (NoSuchMethodException e) {
		
						e.printStackTrace();
					} catch (SecurityException e) {
		
						e.printStackTrace();
					} catch (IllegalAccessException e) {
		
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
		
						e.printStackTrace();
					} catch (InvocationTargetException e) {
		
						e.printStackTrace();
					} catch (NullPointerException e) {
		
						return "";
						
					}

					return "`"+fieldName+"`" + " = " + value;
				}).filter(field -> !field.trim().isEmpty()).collect(Collectors.toList());
		return keyFieldsEqualValue;
	}
	/*return list of key-field with value*/ 
	private List<String> getKeyFieldsEqualValue(Object t) {

		List<String> keyFieldsEqualValue = Arrays.asList(t.getClass().getDeclaredFields()).stream()
				.filter(field -> field.isAnnotationPresent(KeyColumn.class)).map(field -> {
					String fieldName = field.getName();
					String methodName = "get" + String.valueOf(fieldName.charAt(0)).toUpperCase()
							+ fieldName.substring(1);
					String value = "";
					Method method;
					try {
						method = t.getClass().getDeclaredMethod(methodName);
						method.setAccessible(true);
						if (field.getType().getSimpleName().equals("String")) {
							value = "'" + method.invoke(t).toString() + "'";
						} else {
							value = method.invoke(t).toString();
						}
					} catch (NoSuchMethodException e) {
		
						e.printStackTrace();
					} catch (SecurityException e) {
		
						e.printStackTrace();
					} catch (IllegalAccessException e) {
		
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
		
						e.printStackTrace();
					} catch (InvocationTargetException e) {
		
						e.printStackTrace();
					}

					return "`"+fieldName+"`" + " = " + value;
				}).collect(Collectors.toList());
		return keyFieldsEqualValue;
	}

	/*return list of non-key-field*/
	private String getListOfFieldWithoutKey(Object t) {
		Class targetClass = t.getClass();
		StringBuilder sql = new StringBuilder();
		List<String> fields = Arrays.asList(targetClass.getDeclaredFields()).stream()
				.filter(field -> !field.isAnnotationPresent(KeyColumn.class)).map(field -> "`"+field.getName()+"`")
				.collect(Collectors.toList());
		return String.join(",", fields);
	}

	/*return list of field include non-key and key*/
	private String getListOfField(Object t) {
		Class targetClass = t.getClass();
		StringBuilder sql = new StringBuilder();
		List<String> fields = Arrays.asList(targetClass.getDeclaredFields()).stream().map(field -> field.getName())
				.collect(Collectors.toList());
		return String.join(",", fields);
	}

	/*return list of value include non-key and key*/
	private static String getListOfValue(Object t) {
		Class targetClass = t.getClass();
		StringBuilder sql = new StringBuilder();
		List<String> values = new ArrayList<>();
		for (Field field : targetClass.getDeclaredFields()) {
			String methodName = "get" + String.valueOf(field.getName().charAt(0)).toUpperCase()
					+ field.getName().substring(1);
			try {
				Method method = targetClass.getDeclaredMethod(methodName);
				method.setAccessible(true);
				if (method.invoke(t) != null) {
					if (field.getType().getSimpleName().equals("String")) {
						values.add("'" + method.invoke(t).toString() + "'");
					} else {
						values.add(method.invoke(t).toString());
					}

				} else {
					if (field.getType().getSimpleName().equals("String")) {
						values.add("''");
					} else {
						values.add("0");
					}
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {

				e.printStackTrace();
			}
		}

		return String.join(",", values);
	}

	/*return list of non-key value*/
	private String getListOfValueWithoutKey(Object t) {
		Class targetClass = t.getClass();
		StringBuilder sql = new StringBuilder();
		List<String> values = new ArrayList<>();
		for (Field field : targetClass.getDeclaredFields()) {
			if (!field.isAnnotationPresent(KeyColumn.class)) {

				String methodName = "get" + String.valueOf(field.getName().charAt(0)).toUpperCase()
						+ field.getName().substring(1);
				try {
					Method method = targetClass.getDeclaredMethod(methodName);
					method.setAccessible(true);
					if (method.invoke(t) != null) {
						if (field.getType().getSimpleName().equals("String") || field.getType().isEnum()) {
							values.add("'" + method.invoke(t).toString() + "'");
						} else {
							values.add(method.invoke(t).toString());
						}

					} else {
						if (field.getType().getSimpleName().equals("String")|| field.getType().isEnum()) {
							values.add("''");
						} else if (field.getType().getSimpleName().equals("Date")) {
							values.add("NULL");
						} else {
							values.add("0");
						}
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
	
					e.printStackTrace();
				}

			}
		}

		return String.join(",", values);
	}

	/*return list of non-key value to preparedstatement*/
	private void getListOfValueWithoutKeyToPreparedstatement(Object t, PreparedStatement ps) throws SQLException {
		Class targetClass = t.getClass();
		StringBuilder sql = new StringBuilder();
		List<String> values = new ArrayList<>();
		int i = 1;
		for (Field field : targetClass.getDeclaredFields()) {
			if (!field.isAnnotationPresent(KeyColumn.class)) {

				String methodName = "get" + String.valueOf(field.getName().charAt(0)).toUpperCase()
						+ field.getName().substring(1);
				try {
					Method method = targetClass.getDeclaredMethod(methodName);
					method.setAccessible(true);
					if (method.invoke(t) != null) {
						if (field.getType().getSimpleName().equals("String") || field.getType().isEnum()) {
							ps.setString(i++, method.invoke(t).toString());
						} else if (field.getType().getSimpleName().equalsIgnoreCase("double")){
							ps.setDouble(i++, Double.parseDouble(method.invoke(t).toString()));
						} else if (field.getType().getSimpleName().equalsIgnoreCase("int")||field.getType().getSimpleName().equalsIgnoreCase("Integer")){
							ps.setInt(i++, Integer.parseInt(method.invoke(t).toString()));
						} else if (field.getType().getSimpleName().equalsIgnoreCase("BigDecimal")){
							ps.setBigDecimal(i++, new BigDecimal(method.invoke(t).toString()));
						}

					} else {
						if (field.getType().getSimpleName().equals("String") || field.getType().isEnum()) {
							ps.setString(i++, "");
						} else if (field.getType().getSimpleName().equalsIgnoreCase("double")){
							ps.setDouble(i++, 0);
						} else if (field.getType().getSimpleName().equalsIgnoreCase("int")||field.getType().getSimpleName().equalsIgnoreCase("Integer")){
							ps.setInt(i++, 0);
						} else if (field.getType().getSimpleName().equalsIgnoreCase("BigDecimal")){
							ps.setBigDecimal(i++, new BigDecimal(0));
						}
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
	
					e.printStackTrace();
				}

			}
		}

	}
	/*return string with field = value where value != null*/
	private static String getSqlQueryForCondition(Object t) {
		Class targetClass = t.getClass();
		StringBuilder sql = new StringBuilder();
		List<String> conditions = new ArrayList<String>();
		for (Field field : targetClass.getDeclaredFields()) {
			String methodName = "get" + String.valueOf(field.getName().charAt(0)).toUpperCase()
					+ field.getName().substring(1);
			try {
				Method method = targetClass.getDeclaredMethod(methodName);
				method.setAccessible(true);
				if (method.invoke(t) != null) {
					if (field.getType().getSimpleName().equals("String") || field.getType().isEnum()) {
						conditions.add(field.getName() + " = " + "'" + method.invoke(t).toString() + "'");
					} else {
						conditions.add(field.getName() + " = " + method.invoke(t).toString());
					}

				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {

//				e.printStackTrace();
			}
		}
		if (!conditions.isEmpty()) {
			sql.append(" WHERE ");
			sql.append(String.join(" AND ", conditions));
		}

		return sql.toString();
	}

	/*return string with field = value where value != null with given key*/
	private static String getSqlQueryForCondition(Object t, List<String> key) {
		Class targetClass = t.getClass();
		StringBuilder sql = new StringBuilder();
		List<String> conditions = new ArrayList<String>();
		for (Field field : targetClass.getDeclaredFields()) {
			if(key.contains(field.getName().toLowerCase())){
				String methodName = "get" + String.valueOf(field.getName().charAt(0)).toUpperCase()
						+ field.getName().substring(1);
				try {
					Method method = targetClass.getDeclaredMethod(methodName);
					method.setAccessible(true);
					if (method.invoke(t) != null) {
						if (field.getType().getSimpleName().equals("String")) {
							conditions.add("`"+field.getName()+"`" + " = " + "'" + method.invoke(t).toString() + "'");
						} else {
							conditions.add("`"+field.getName()+"`" + " = " + method.invoke(t).toString());
						}

					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
	
					e.printStackTrace();
				}
			}
			
		}
		if (!conditions.isEmpty()) {
			sql.append(" WHERE ");
			sql.append(String.join(" AND ", conditions));
		}

		return sql.toString() + ";";
	}
	
	protected T setToInstane(ResultSet rs, Object t) {
		T newT = null;
		try {
			newT = (T) t.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Class targetClass = newT.getClass();

		for (Field field : targetClass.getDeclaredFields()) {
			String methodName = "set" + String.valueOf(field.getName().charAt(0)).toUpperCase()
					+ field.getName().substring(1);
			try {

				if (field.getType().getSimpleName().equals("Integer")) {
					Method method = targetClass.getDeclaredMethod(methodName, Integer.class);
					method.invoke(newT, rs.getInt(field.getName()));
				} else if (field.getType().getSimpleName().equals("Double")) {
					Method method = targetClass.getDeclaredMethod(methodName, Double.class);
					method.invoke(newT, rs.getDouble(field.getName()));
				} else if (field.getType().getSimpleName().equals("Long")) {
					Method method = targetClass.getDeclaredMethod(methodName, Long.class);
					method.invoke(newT, rs.getLong(field.getName()));
				}else if (field.getType().getSimpleName().equals("String")){
					Method method = targetClass.getDeclaredMethod(methodName, String.class);
					method.invoke(newT, rs.getString(field.getName()));
				}else if (field.getType().getSimpleName().equals("Date")){
					Method method = targetClass.getDeclaredMethod(methodName, Date.class);
					method.invoke(newT, rs.getDate(field.getName()));
				} else if (field.getType().isEnum()) {
					Method method = targetClass.getDeclaredMethod(methodName, field.getType());
					method.invoke(newT, Enum.valueOf((Class<? extends Enum>)field.getType(), rs.getString(field.getName())));
				}
				
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (SQLException e) {

				e.printStackTrace();
			}

		}

		return newT;
	}

	@Override
	public Integer updateByInputKey(Object t, List keys) {
		StringBuilder sql = new StringBuilder("UPDATE " + t.getClass().getSimpleName() + " SET "
				+ String.join(" , ", getNonKeyFieldsEqualValue(t))) ;
		List<String> newList = (List<String>) keys.stream().map(key -> String.valueOf(key).toLowerCase()).collect(Collectors.toList());
		sql.append(getSqlQueryForCondition(t, newList)) ;
		Class targetClass = t.getClass();
		int result = 0;
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			result = ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		} finally {

			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			return result;
		}
	}
	@Override
	public Integer updateByInputKeyAndNotNullFields(Object t, List keys) {
		StringBuilder sql = new StringBuilder("UPDATE " + t.getClass().getSimpleName() + " SET "
				+ String.join(" , ", getNonKeyFieldsEqualNotNullValue(t))) ;
		List<String> newList = (List<String>) keys.stream().map(key -> String.valueOf(key).toLowerCase()).collect(Collectors.toList());
		sql.append(getSqlQueryForCondition(t, newList)) ;
		Class targetClass = t.getClass();
		int result = 0;
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			result = ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		} finally {

			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			return result;
		}
	}

	public static void main(String[] args) {}

	@Override
	public List findByKeySortBy(Object t, String sort) {

		StringBuilder sql = new StringBuilder("SELECT * FROM " + t.getClass().getSimpleName() + getSqlQueryForCondition(t) + " ORDER BY " + sort);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		List<T> results = new ArrayList<>();
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			while (rs.next()) {

				results.add(setToInstane(rs, t));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				rs.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		return results;
	
	}

	@Override
	public List findByGivenKeySortBy(Object t, String key, String sort) {
		StringBuilder sql = new StringBuilder("SELECT * FROM " + t.getClass().getSimpleName() + getSqlQueryForCondition(t,Arrays.asList(key.split(","))) + " ORDER BY " + sort);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		List<T> results = new ArrayList<>();
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			while (rs.next()) {

				results.add(setToInstane(rs, t));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				rs.close();
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		return results;
	}
	
	public void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void commit(Connection conn) {
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close(Connection conn, PreparedStatement ps) {
		try {
			conn.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}