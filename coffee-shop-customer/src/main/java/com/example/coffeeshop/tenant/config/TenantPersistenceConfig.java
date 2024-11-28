package com.example.coffeeshop.tenant.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
//import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

//import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

//import liquibase.integration.spring.MultiTenantSpringLiquibase;
import com.example.coffeeshop.datasource.lookup.LazyMultiTenantDataSource;
import com.example.coffeeshop.master.repository.TenantRepository;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.util.Lazy;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import com.example.coffeeshop.master.domain.model.TenantPersistenceInfo;

import static com.example.coffeeshop.util.DataSourceUtil.createDataSource;

@Component
@RequiredArgsConstructor
@EnableJpaRepositories(
    basePackages = {"com.example.coffeeshop.tenant"},
    entityManagerFactoryRef = "tenantEntityManagerFactory",
    transactionManagerRef = "tenantTransactionManager"
)
@EnableConfigurationProperties(HibernateProperties.class)
public class TenantPersistenceConfig {
  
  private final ConfigurableListableBeanFactory beanFactory;
  private final JpaProperties jpaProperties;

  	@Bean
	  DataSource tenantDataSource(
			DataSourceProperties properties,
      ApplicationContext applicationContext
			/*TenantRepository tenantRepository*/) {

		try {
			Lazy<Map<Object, DataSource>> resolvedDataSources = Lazy.of( () -> {
        TenantRepository repository = applicationContext.getBean( TenantRepository.class );

        return repository.findAll()
            .stream().collect( Collectors.toMap( TenantPersistenceInfo::getId, info -> {
              HikariDataSource dataSource = createDataSource( properties );
              dataSource.setJdbcUrl( "%s?currentSchema=%s".formatted(
                  dataSource.getJdbcUrl(),
                  info.getId()
              ) );

              return dataSource;
            } ) );
      } );

      LazyMultiTenantDataSource dataSource = new LazyMultiTenantDataSource();
			dataSource.setDefaultTargetDataSource( createDataSource( properties) );
			dataSource.setTargetDataSources( resolvedDataSources );

			dataSource.afterPropertiesSet();

			return dataSource;
		}
		catch (Exception e) {
			throw new RuntimeException( "Problem in tenant datasource:", e );
		}
	}
  
  @Primary
  @Bean
  public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(@Qualifier("tenantDataSource") DataSource dataSource,
																																					 HibernateProperties hibernateProperties) {
    LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();

		emfBean.setPersistenceUnitName("tenant-persistence-unit");
    emfBean.setPackagesToScan("com.example.coffeeshop.tenant");
    emfBean.setDataSource( dataSource );

    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    emfBean.setJpaVendorAdapter(vendorAdapter);
    
//    Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
		Supplier<String> defaultDdlMode = () -> "none";
		Map<String, Object> properties = new LinkedHashMap<>( hibernateProperties
																															.determineHibernateProperties(
																																	jpaProperties.getProperties(),
																																	new HibernateSettings().ddlAuto( defaultDdlMode )
																															) );




		//    properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
//    properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
    properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory));
    properties.remove(AvailableSettings.DEFAULT_SCHEMA);
//    properties.put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
//    properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
//    properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
    emfBean.setJpaPropertyMap(properties);
    
    return emfBean;
  }
  
  @Primary
  @Bean
  public JpaTransactionManager tenantTransactionManager(
      @Qualifier("tenantEntityManagerFactory") EntityManagerFactory emf) {
    JpaTransactionManager tenantTransactionManager = new JpaTransactionManager();
    tenantTransactionManager.setEntityManagerFactory(emf);
    return tenantTransactionManager;
  }
  
//  @Bean
//  public MultiTenantSpringLiquibase multiTenantSpringLiquibase(DataSource dataSource, LiquibaseProperties properties) {
//    MultiTenantSpringLiquibase liquibase = new MultiTenantSpringLiquibase();
//    liquibase.setDataSource(dataSource);
//    liquibase.setChangeLog("classpath:/pg/tenant/changelog-tenant.xml");
//    liquibase.setClearCheckSums(properties.isClearChecksums());
//    liquibase.setContexts(properties.getContexts());
//    liquibase.setDefaultSchema(properties.getDefaultSchema());
//    liquibase.setLiquibaseSchema(properties.getLiquibaseSchema());
//    liquibase.setLiquibaseTablespace(properties.getLiquibaseTablespace());
//    liquibase.setDatabaseChangeLogTable(properties.getDatabaseChangeLogTable());
//    liquibase.setDatabaseChangeLogLockTable(properties.getDatabaseChangeLogLockTable());
//    liquibase.setDropFirst(properties.isDropFirst());
//    liquibase.setShouldRun(properties.isEnabled());
//    liquibase.setLabels(properties.getLabels());
//    liquibase.setRollbackFile(properties.getRollbackFile());
//
//    liquibase.setSchemas(List.of("test"));
//
//    return liquibase;
//  }
  
}
