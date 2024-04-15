package com.richieoscar.artwoodcba.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DbConfig {

    private final ArtWoodProperties artWoodProperties;
    // MySQL DataSource configuration
    @Bean
    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/artwoodcbadb");
        dataSource.setUsername("root");
        dataSource.setPassword("password2020");
        return dataSource;
    }

    // PostgreSQL DataSource configuration
    @Bean
    public DataSource postgresDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/your_postgres_database");
        dataSource.setUsername("postgres");
        dataSource.setPassword("password2020");
        return dataSource;
    }

    // JdbcTemplate for MySQL
    @Bean
    public JdbcTemplate mysqlJdbcTemplate(DataSource mysqlDataSource) {
        return new JdbcTemplate(mysqlDataSource);
    }

    // JdbcTemplate for PostgreSQL
    @Bean
    public JdbcTemplate postgresJdbcTemplate(DataSource postgresDataSource) {
        return new JdbcTemplate(postgresDataSource);
    }
}
