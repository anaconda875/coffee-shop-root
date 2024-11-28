package com.example.coffeeshop.util;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.util.StringUtils;

@UtilityClass
public class DataSourceUtil {

	public static HikariDataSource createDataSource(DataSourceProperties properties) {
		HikariDataSource dataSource = createDataSource( properties, HikariDataSource.class,
																										properties.getClassLoader()
		);
		if ( StringUtils.hasText( properties.getName() ) ) {
			dataSource.setPoolName( properties.getName() );
		}
		return dataSource;
	}

	private static <T> T createDataSource(
			DataSourceProperties properties, Class<? extends DataSource> type,
			ClassLoader classLoader) {
		return (T) DataSourceBuilder.create( classLoader )
				.type( type )
				.driverClassName( properties.determineDriverClassName() )
				.url( properties.determineUrl() )
				.username( properties.determineUsername() )
				.password( properties.determinePassword() )
				.build();
	}
}
