package org.utils.util;

import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

// http://www.baeldung.com/properties-with-spring
// annotations currently do not work, commented
// @Configuration
// @PropertySource("classpath:application.properties")
public class JDBCUtils {
	/*	
			@Bean
			public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
				return new PropertySourcesPlaceholderConfigurer();
			}
	*/
	@Autowired
	private static Environment env;

	private static final Logger logger = Logger
			.getLogger(JDBCUtils.class.getName());
	public static Connection connection = null;
	public static PreparedStatement preparedStatement = null;
	public static ResultSet resultSet = null;

	private JDBCUtils() {
	}

	// does not work
	@Value("${spring.datasource.url}")
	private static String datasourceUrl;

	public static Connection getConnection() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = JDBCUtils.class.getClassLoader()
					.getResourceAsStream("application.properties");
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String url = resolveEnvVars(prop.getProperty("spring.datasource.url"));
		String username = "";
		String password = "";
		if (connection != null) {
			return connection;
		}
		try {
			Class.forName(prop.getProperty("spring.datasource.driver-class-name"))
					.newInstance();
			connection = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | SQLException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		return connection;
	}

	public static void close() {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
			if (preparedStatement != null) {
				preparedStatement.close();
				preparedStatement = null;
			}
			if (resultSet != null) {
				resultSet.close();
				resultSet = null;
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, null, e);
		}
	}

	public static List<?> TranverseToList(ResultSet rs, Class<?> clazz)
			throws SQLException, InstantiationException, IllegalAccessException {
		ResultSetMetaData rsm = rs.getMetaData();
		int colNumber = rsm.getColumnCount();
		List<Object> list = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();
		while (rs.next()) {
			Object obj = clazz.newInstance();
			for (int i = 1; i <= colNumber; i++) {
				Object value = rs.getObject(i);
				for (Field f : fields) {
					if (f.getName().equals(rsm.getColumnName(i))) {
						boolean flag = f.isAccessible();
						f.setAccessible(true);
						f.set(obj, value);
						f.setAccessible(flag);
						break;
					}
				}
			}
			list.add(obj);
		}
		return list;
	}

	private static String resolveEnvVars(String input) {
		if (null == input) {
			return null;
		}
		Pattern p = Pattern.compile("\\$(?:\\{(\\w+)\\}|(\\w+))");
		Matcher m = p.matcher(input);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
			String envVarValue = System.getenv(envVarName);
			m.appendReplacement(sb,
					null == envVarValue ? "" : envVarValue.replace("\\", "\\\\"));
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
