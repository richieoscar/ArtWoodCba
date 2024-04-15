package com.richieoscar.artwoodcba.dto.enums;

public enum DatabaseType {
    MYSQL("MySql", "jdbc:mysql://", "com.mysql.cj.jdbc.Driver", "org.hibernate.dialect.MySQLDialect", "SELECT 1"),

    MARIADB("MariaDb", "jdbc:mysql://", "com.mysql.cj.jdbc.Driver", "org.hibernate.dialect.MySQLDialect", "SELECT 1"),

    ORACLE(
        "Oracle",
        "jdbc:oracle:thin:@",
        "oracle.jdbc.driver.OracleDriver",
        "org.hibernate.dialect.Oracle10gDialect",
        "SELECT 1 FROM DUAL"
    ),
    POSTGRES("Postgres", "jdbc:postgresql://", "org.postgresql.Driver", "org.hibernate.dialect.ProgressDialect", "SELECT 1"),
    MSSQL(
        "Microsoft SQL Server",
        "jdbc:sqlserver://",
        "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        "org.hibernate.dialect.SQLServerDialect",
        "SELECT 1"
    ),
    HSQL("Hypersonic QL", "jdbc:hsqldb:hsql://", "org.hsqldb.jdbcDriver", "org.hibernate.dialect.HSQLDialect", "SELECT 1"),
    H2("h2", "jdbc:h2:", "org.h2.Driver", "org.hibernate.dialect.H2Dialect", "SELECT 1"),
    SYBASE("Sybase", "jdbc:jtds:sybase://", "net.sourceforge.jtds.jdbc.Driver", "org.hibernate.dialect.SybaseDialect", "SELECT 1");

    private String description;

    private String urlFormat;

    private String className;

    private String jpaDialect;

    private String dbTestsSql;

    DatabaseType(String description, String urlPrefix, String className, String jpaDialect, String dbTestsSql) {
        this.description = description;
        this.urlFormat = urlPrefix;
        this.className = className;
        this.jpaDialect = jpaDialect;
        this.dbTestsSql = dbTestsSql;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlFormat() {
        return urlFormat;
    }

    public void setUrlFormat(String urlFormat) {
        this.urlFormat = urlFormat;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getJpaDialect() {
        return jpaDialect;
    }

    public void setJpaDialect(String jpaDialect) {
        this.jpaDialect = jpaDialect;
    }

    public String getDbTestsSql() {
        return dbTestsSql;
    }

    public void setDbTestsSql(String dbTestsSql) {
        this.dbTestsSql = dbTestsSql;
    }
}
