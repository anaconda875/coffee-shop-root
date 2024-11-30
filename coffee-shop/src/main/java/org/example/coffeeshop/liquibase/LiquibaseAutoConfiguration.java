package org.example.coffeeshop.liquibase;

import javax.sql.DataSource;

import com.htech.jpa.reactive.ReactiveHibernateJpaAutoConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.change.DatabaseChange;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseConnectionDetails;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ReactiveHibernateJpaAutoConfiguration.class)
@ConditionalOnClass({ SpringLiquibase.class, DatabaseChange.class })
@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", matchIfMissing = true)
// @Conditional(LiquibaseAutoConfiguration.LiquibaseDataSourceCondition.class)
@EnableConfigurationProperties(DataSourceProperties.class)
@Import(DatabaseInitializationDependencyConfigurer.class)
public class LiquibaseAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(ConnectionCallback.class)
	@ConditionalOnMissingBean(SpringLiquibase.class)
	@EnableConfigurationProperties(LiquibaseProperties.class)
	public static class LiquibaseConfiguration {

		final DataSourceProperties datasourceProperties;

		LiquibaseConfiguration(DataSourceProperties datasourceProperties) {
			this.datasourceProperties = datasourceProperties;
		}

		@Bean
		@ConditionalOnMissingBean(LiquibaseConnectionDetails.class)
		PropertiesLiquibaseConnectionDetails liquibaseConnectionDetails(
				LiquibaseProperties properties, ObjectProvider<JdbcConnectionDetails> jdbcConnectionDetails) {
			return new PropertiesLiquibaseConnectionDetails( properties );
		}

		@Bean
		public SpringLiquibase liquibase(
				@LiquibaseDataSource ObjectProvider<DataSource> liquibaseDataSource,
				LiquibaseProperties properties,
				LiquibaseConnectionDetails connectionDetails) {
			SpringLiquibase liquibase = createSpringLiquibase(
					liquibaseDataSource.getIfAvailable(), getDataSource( HikariDataSource.class ), connectionDetails );
			liquibase.setChangeLog( properties.getChangeLog() );
			liquibase.setClearCheckSums( properties.isClearChecksums() );
			liquibase.setContexts( properties.getContexts() );
			liquibase.setDefaultSchema( properties.getDefaultSchema() );
			liquibase.setLiquibaseSchema( properties.getLiquibaseSchema() );
			liquibase.setLiquibaseTablespace( properties.getLiquibaseTablespace() );
			liquibase.setDatabaseChangeLogTable( properties.getDatabaseChangeLogTable() );
			liquibase.setDatabaseChangeLogLockTable( properties.getDatabaseChangeLogLockTable() );
			liquibase.setDropFirst( properties.isDropFirst() );
			liquibase.setShouldRun( properties.isEnabled() );
			liquibase.setLabelFilter( properties.getLabelFilter() );
			liquibase.setChangeLogParameters( properties.getParameters() );
			liquibase.setRollbackFile( properties.getRollbackFile() );
			liquibase.setTestRollbackOnUpdate( properties.isTestRollbackOnUpdate() );
			liquibase.setTag( properties.getTag() );
			liquibase.setShowSummary( properties.getShowSummary() );
			liquibase.setShowSummaryOutput( properties.getShowSummaryOutput() );
			return liquibase;
		}

		private DataSource getDataSource(Class<? extends DataSource> type) {
			return datasourceProperties.initializeDataSourceBuilder().type( type ).build();
		}

		private SpringLiquibase createSpringLiquibase(
				DataSource liquibaseDataSource, DataSource dataSource, LiquibaseConnectionDetails connectionDetails) {
			DataSource migrationDataSource = getMigrationDataSource( liquibaseDataSource, dataSource, connectionDetails );
			SpringLiquibase liquibase =
					( migrationDataSource == liquibaseDataSource || migrationDataSource == dataSource )
							? new SpringLiquibase()
							: new DataSourceClosingSpringLiquibase();
			liquibase.setDataSource( migrationDataSource );
			return liquibase;
		}

		private DataSource getMigrationDataSource(
				DataSource liquibaseDataSource, DataSource dataSource, LiquibaseConnectionDetails connectionDetails) {
			if ( liquibaseDataSource != null ) {
				return liquibaseDataSource;
			}
			String url = connectionDetails.getJdbcUrl();
			if ( url != null ) {
				DataSourceBuilder<?> builder = DataSourceBuilder.create().type( SimpleDriverDataSource.class );
				builder.url( url );
				applyConnectionDetails( connectionDetails, builder );
				return builder.build();
			}
			String user = connectionDetails.getUsername();
			if ( user != null && dataSource != null ) {
				DataSourceBuilder<?> builder =
						DataSourceBuilder.derivedFrom( dataSource ).type( SimpleDriverDataSource.class );
				applyConnectionDetails( connectionDetails, builder );
				return builder.build();
			}
			Assert.state( dataSource != null, "Liquibase migration DataSource missing" );
			return dataSource;
		}

		private void applyConnectionDetails(
				LiquibaseConnectionDetails connectionDetails, DataSourceBuilder<?> builder) {
			builder.username( connectionDetails.getUsername() );
			builder.password( connectionDetails.getPassword() );
			String driverClassName = connectionDetails.getDriverClassName();
			if ( StringUtils.hasText( driverClassName ) ) {
				builder.driverClassName( driverClassName );
			}
		}
	}

	static final class PropertiesLiquibaseConnectionDetails implements LiquibaseConnectionDetails {

		private final LiquibaseProperties properties;

		PropertiesLiquibaseConnectionDetails(LiquibaseProperties properties) {
			this.properties = properties;
		}

		@Override
		public String getUsername() {
			return this.properties.getUser();
		}

		@Override
		public String getPassword() {
			return this.properties.getPassword();
		}

		@Override
		public String getJdbcUrl() {
			return this.properties.getUrl();
		}

		@Override
		public String getDriverClassName() {
			String driverClassName = this.properties.getDriverClassName();
			return ( driverClassName != null ) ? driverClassName : LiquibaseConnectionDetails.super.getDriverClassName();
		}
	}
}
