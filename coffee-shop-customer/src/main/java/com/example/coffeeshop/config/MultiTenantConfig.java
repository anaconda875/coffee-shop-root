package com.example.coffeeshop.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiTenantConfig {

//	private UUID defaultTenant;
//
//	@Bean
//	SqlDataSourceScriptDatabaseInitializer databaseInitializer() {
//		return new SqlDataSourceScriptDatabaseInitializer( null, (DatabaseInitializationSettings) null ) {
//			@Override
//			public void afterPropertiesSet() {
//
//			}
//		};
//	}
//
//	@Bean(name = "dataSource")
////	@ConfigurationProperties(prefix = "spring.datasource.hikari")
//	 DataSource masterDataSource(
//			DataSourceProperties properties,
//			TenantRepository tenantRepository) {
//
//		try {
//			Map<Object, Object> resolvedDataSources = tenantRepository.findAll()
//					.stream().collect( Collectors.toMap( TenantPersistenceInfo::getId, info -> {
//						HikariDataSource dataSource = dataSource( properties );
//						dataSource.setJdbcUrl( "%s?currentSchema=%s".formatted(
//								dataSource.getJdbcUrl(),
//								info.getId()
//						) );
//
//						return dataSource;
//					} ) );
//
//			AbstractRoutingDataSource dataSource = new MultiTenantDataSource();
//			dataSource.setDefaultTargetDataSource( resolvedDataSources.get( defaultTenant ) );
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
//
//	HikariDataSource dataSource(DataSourceProperties properties) {
//		HikariDataSource dataSource = createDataSource( properties, HikariDataSource.class,
//														properties.getClassLoader()
//		);
//		if ( StringUtils.hasText( properties.getName() ) ) {
//			dataSource.setPoolName( properties.getName() );
//		}
//		return dataSource;
//	}
//
//	private static <T> T createDataSource(
//			DataSourceProperties properties, Class<? extends DataSource> type,
//			ClassLoader classLoader) {
//		return (T) DataSourceBuilder.create( classLoader )
//				.type( type )
//				.driverClassName( properties.determineDriverClassName() )
//				.url( properties.determineUrl() )
//				.username( properties.determineUsername() )
//				.password( properties.determinePassword() )
//				.build();
//	}
//
//	@Autowired
//	public void setDefaultTenant(@Value("${app.tenant.default}") UUID defaultTenant) {
//		this.defaultTenant = defaultTenant;
//	}
}
