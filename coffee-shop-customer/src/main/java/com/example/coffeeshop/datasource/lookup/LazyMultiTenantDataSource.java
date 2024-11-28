package com.example.coffeeshop.datasource.lookup;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import com.example.coffeeshop.util.TenantContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.util.Lazy;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class LazyMultiTenantDataSource extends AbstractDataSource implements InitializingBean {

	@Nullable
	private Lazy<Map<Object, DataSource>> targetDataSources;

	@Nullable
	private DataSource defaultTargetDataSource;

	private boolean lenientFallback = true;

	private final Map<Object, DataSource> resolvedDataSources = new HashMap<>();

	@Nullable
	private DataSource resolvedDefaultDataSource;

	public void setTargetDataSources(Lazy<Map<Object, DataSource>> targetDataSources) {
		this.targetDataSources = targetDataSources;
	}

	public void setDefaultTargetDataSource(DataSource defaultTargetDataSource) {
		this.defaultTargetDataSource = defaultTargetDataSource;
	}

	public void setLenientFallback(boolean lenientFallback) {
		this.lenientFallback = lenientFallback;
	}

	@Override
	public void afterPropertiesSet() {
//		initialize();
	}

//	public void initialize() {
//		if (this.targetDataSources == null) {
//			throw new IllegalArgumentException("Property 'targetDataSources' is required");
//		}
//		this.resolvedDataSources = CollectionUtils.newHashMap( this.targetDataSources.size());
//		this.targetDataSources.forEach((key, value) -> {
//			Object lookupKey = resolveSpecifiedLookupKey(key);
//			Lazy<DataSource> dataSource = resolveSpecifiedDataSource(value);
//			this.resolvedDataSources.put(lookupKey, dataSource);
//		});
//		if (this.defaultTargetDataSource != null) {
//			this.resolvedDefaultDataSource = resolveSpecifiedDataSource(this.defaultTargetDataSource);
//		}
//	}

	protected Object resolveSpecifiedLookupKey(Object lookupKey) {
		return lookupKey;
	}

	protected Lazy<DataSource> resolveSpecifiedDataSource(Object dataSourceObject) throws IllegalArgumentException {
		if (dataSourceObject instanceof Lazy dataSource) {
			return dataSource;
		} else {
			throw new IllegalArgumentException(
					"Illegal data source value - only [javax.sql.DataSource] and String supported: " + dataSourceObject);
		}
	}

//	@Nullable
//	public DataSource getResolvedDefaultDataSource() {
//		return this.resolvedDefaultDataSource.get();
//	}


	@Override
	public Connection getConnection() throws SQLException {
		return determineTargetDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return determineTargetDataSource().getConnection(username, password);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (iface.isInstance(this)) {
			return (T) this;
		}
		return determineTargetDataSource().unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return (iface.isInstance(this) || determineTargetDataSource().isWrapperFor(iface));
	}

	protected DataSource determineTargetDataSource() {
//		Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
		Object lookupKey = determineCurrentLookupKey();
		if(lookupKey == null) {
			return defaultTargetDataSource;
		}
//		Lazy<DataSource> dataSource = this.resolvedDataSources.get(lookupKey);

		return this.resolvedDataSources.computeIfAbsent( lookupKey, key -> {
			DataSource dataSource = this.resolvedDefaultDataSource;
			if (dataSource == null && this.lenientFallback) {
				dataSource = this.targetDataSources.get().get( lookupKey );;
			}

			if (dataSource == null) {
				throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
			}

			this.resolvedDefaultDataSource = dataSource;

			return dataSource;
		} );
	}

	protected String determineCurrentLookupKey() {
		return TenantContext.getCurrentTenant();
	}
}