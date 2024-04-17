package vcp.np.usermanagement.config.datasource.usermanagement;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import vcp.np.usermanagement.profile.Profile;
import vcp.np.usermanagement.utils.Constants.DataSource.Usermanagement;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = Usermanagement.ENTITY_MANAGER_FACTORY_BEAN_NAME,
    transactionManagerRef = Usermanagement.TRANSACTION_MANAGER_BEAN_NAME,
    basePackages = {Usermanagement.REPOSITORIES_PACKAGE}
)
public class UsermanagementDataSourceSetup {

    private final String UM_DS_BASE_KEY = "dataSource.usermanagement.";
    private final Profile profile;


    public UsermanagementDataSourceSetup(Profile profile) {
        System.out.println("\n:::::::::: Initializing usermanagement datasource ::::::::::");

        this.profile = profile;
    }

    @Bean
    @Primary
    public DataSource getDataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();

        driverManagerDataSource.setUrl(profile.getProperty(UM_DS_BASE_KEY + "url"));
        driverManagerDataSource.setDriverClassName(profile.getProperty(UM_DS_BASE_KEY + "driver", "com.mysql.cj.jdbc.Driver"));
        driverManagerDataSource.setUsername(profile.getProperty(UM_DS_BASE_KEY + "username"));
        driverManagerDataSource.setPassword(profile.getProperty(UM_DS_BASE_KEY + "password"));

        return driverManagerDataSource;
    }


    @Bean(name = Usermanagement.ENTITY_MANAGER_FACTORY_BEAN_NAME)
    @Primary
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        localContainerEntityManagerFactoryBean.setDataSource(getDataSource());
        localContainerEntityManagerFactoryBean.setPackagesToScan(Usermanagement.DOMAINS_PACKAGE);

        JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);

        Map<String, String> jpaPropertyMap = new HashMap<>();
        jpaPropertyMap.put("hibernate.dialect", profile.getProperty(UM_DS_BASE_KEY + "hibernate.dialect", "org.hibernate.dialect.MySQLDialect"));
        jpaPropertyMap.put("hibernate.show_sql", profile.getProperty(UM_DS_BASE_KEY + "hibernate.showSql", "true"));
        jpaPropertyMap.put("hibernate.hbm2ddl.auto", profile.getProperty(UM_DS_BASE_KEY + "hibernate.hbm2ddl.auto", "update"));
        localContainerEntityManagerFactoryBean.setJpaPropertyMap(jpaPropertyMap);

        return localContainerEntityManagerFactoryBean;
    }


    @Bean(name = Usermanagement.TRANSACTION_MANAGER_BEAN_NAME)
    @Primary
    public PlatformTransactionManager platformTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean().getObject());

        return jpaTransactionManager;
    }

}
