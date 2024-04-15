package com.richieoscar.artwoodcba.config;

import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;

@Data
public class DbInit {

    private final JdbcTemplate mysqlJdbcTemplate;


    private final JdbcTemplate postgresJdbcTemplate;

    public void migrateData() {
        // Export data from MySQL
        String exportQuery = "SELECT * FROM customer";
        // You can customize this query to export specific data or tables

        mysqlJdbcTemplate.query(exportQuery, (rs, rowNum) -> {
            // Import each row into PostgreSQL
            String importQuery = "INSERT INTO artwoodcbadb.customer\n" +
                    "(email, first_name, last_login_date, last_name, password, phone, registration_date, `role`, status, customer_id)\n" +
                    "VALUES('', '', '', '', '', '', '', '', '', '');\n";
            // Adjust this query to match your PostgreSQL table structure

            // Set parameters for the import query
            Object[] params = new Object[]{
                    rs.getObject("firstName"),
                    rs.getObject("lastName"),
                    rs.getObject("email"),
                    rs.getObject("phone"),
                    rs.getObject("password"),
                    rs.getObject("role"),
                    rs.getObject("status"),
                    rs.getObject("registration_date"),
                    rs.getObject("customer_id"),
                    rs.getObject("last_login_Date"),
                    // Add more columns as needed
            };

            // Execute import query
            postgresJdbcTemplate.update(importQuery, params);
            return null; // Not using the result of the query
        });
    }

}
