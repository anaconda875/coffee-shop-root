package com.example.coffeeshop.master.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;

import com.example.coffeeshop.datasource.lookup.LazyMultiTenantDataSource;
import com.example.coffeeshop.master.repository.TenantRepository;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.util.Lazy;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.util.StringUtils;

import static com.example.coffeeshop.util.DataSourceUtil.createDataSource;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(
		basePackages = { "com.example.coffeeshop.master" },
		entityManagerFactoryRef = "masterEntityManagerFactory",
		transactionManagerRef = "masterTransactionManager"
)
@EnableConfigurationProperties(HibernateProperties.class)
public class MasterPersistenceConfig {

	private UUID defaultTenant;

	@Bean
	SqlDataSourceScriptDatabaseInitializer databaseInitializer() {
		return new SqlDataSourceScriptDatabaseInitializer( null, (DatabaseInitializationSettings) null ) {
			@Override
			public void afterPropertiesSet() {
			}
		};
	}

  @Bean
  DataSource masterDataSource(DataSourceProperties properties) {
    return createDataSource( properties );
  }

//	@Bean
//	DataSource masterDataSource(
//			DataSourceProperties properties,
//      ApplicationContext applicationContext
//			/*TenantRepository tenantRepository*/) {
//
//		try {
//			Lazy<Map<Object, DataSource>> resolvedDataSources = Lazy.of( () -> {
//        TenantRepository repository = applicationContext.getBean( TenantRepository.class );
//
//        return repository.findAll()
//            .stream().collect( Collectors.toMap( TenantPersistenceInfo::getId, info -> {
//              HikariDataSource dataSource = dataSource( properties );
//              dataSource.setJdbcUrl( "%s?currentSchema=%s".formatted(
//                  dataSource.getJdbcUrl(),
//                  info.getId()
//              ) );
//
//              return dataSource;
//            } ) );
//      } );
//
//      LazyMultiTenantDataSource dataSource = new LazyMultiTenantDataSource();
//			dataSource.setDefaultTargetDataSource( dataSource(properties) );
//			dataSource.setTargetDataSources( resolvedDataSources );
//
//			dataSource.afterPropertiesSet();
//
//			return dataSource;
//		}
//		catch (Exception e) {
//			throw new RuntimeException( "Problem in tenant datasource:", e );
//		}
//	}

	@Autowired
	public void setDefaultTenant(@Value("${app.tenant.default}") UUID defaultTenant) {
		this.defaultTenant = defaultTenant;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(
			@Qualifier("masterDataSource") DataSource dataSource,
			JpaProperties jpaProperties,
			HibernateProperties hibernateProperties,
			ConfigurableListableBeanFactory beanFactory) {
		LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();

		emfBean.setPersistenceUnitName( "master-persistence-unit" );
		emfBean.setPackagesToScan( "com.example.coffeeshop.master" );
		emfBean.setDataSource( dataSource );

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		emfBean.setJpaVendorAdapter( vendorAdapter );

		Supplier<String> defaultDdlMode = () -> "none";
		Map<String, Object> properties = new LinkedHashMap<>( hibernateProperties
																															.determineHibernateProperties(
																																	jpaProperties.getProperties(),
																																	new HibernateSettings().ddlAuto( defaultDdlMode )
																															) );
//    properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
//    properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
//    properties.put(AvailableSettings.AUTO, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
		properties.put( AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer( beanFactory ) );
		emfBean.setJpaPropertyMap( properties );

		return emfBean;
	}

	@Bean
	public JpaTransactionManager masterTransactionManager(
			@Qualifier("masterEntityManagerFactory") EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory( emf );

		return transactionManager;
	}
}
