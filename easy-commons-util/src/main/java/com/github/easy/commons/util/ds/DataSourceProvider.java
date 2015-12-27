package com.github.easy.commons.util.ds;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库连接类
 * @author LIUCHAOHONG
 *
 */
public class DataSourceProvider {
	
	private static Logger logger = Logger.getLogger(DataSourceProvider.class);
	static Map<String,DataSource> dataSourceCache = new HashMap<String,DataSource>();
	
	
	public static synchronized DataSource getDataSource(String jdbcDriver, String jdbcUsername, String jdbcPassword, String jdbcUrl) {
		String key = jdbcDriver+jdbcUsername+jdbcPassword+jdbcUrl;
		DataSource dataSource = dataSourceCache.get(key);
		if(dataSource == null) {
			try {
				ComboPooledDataSource ds = new ComboPooledDataSource();
				ds.setDriverClass(jdbcDriver);
				
				ds.setUser(jdbcUsername);
				ds.setPassword(jdbcPassword);
				ds.setJdbcUrl(jdbcUrl);
				ds.setCheckoutTimeout(10 * 1000);
				
				if(jdbcDriver.contains("mysql")) {
					ds.setPreferredTestQuery("select 1");
					ds.setIdleConnectionTestPeriod(60);
				}
				ds.setUnreturnedConnectionTimeout(60 * 60);
				ds.setLoginTimeout(10);
				
				
				dataSource = ds;
				dataSourceCache.put(key, dataSource);
				logger.info("create DataSource,url:["+jdbcUrl+"], username:"+jdbcUsername);
			} catch (PropertyVetoException e) {
				throw new IllegalArgumentException("invalid driver:"+jdbcDriver,e);
			} catch( SQLException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return dataSource;
	}
	
}

