package com.richieoscar.artwoodcba.config;

import com.richieoscar.artwoodcba.dto.enums.DatabaseType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "artwood.props")
@Data
public class ArtWoodProperties {
    private String dbUserName;
    private String dbPassword;
    private String  dbConnectionUrl;
    private String driverClass;
    private DatabaseType databaseType;
    private String schema;
    private String poolName;
    private int stmtCacheSize;
    private int maxPoolSize;
    private int minIdle;
    private long leakDetectionThreshold;
    private int prepStmtCacheSqlLimit;
    private boolean userServerPrepStmts;

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public long getLeakDetectionThreshold() {

        return leakDetectionThreshold;
    }

    public void setLeakDetectionThreshold(long leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
    }

    public int getPrepStmtCacheSqlLimit() {
        return prepStmtCacheSqlLimit;
    }

    public void setPrepStmtCacheSqlLimit(int prepStmtCacheSqlLimit) {
        this.prepStmtCacheSqlLimit = prepStmtCacheSqlLimit;
    }

    public boolean isUserServerPrepStmts() {
        return userServerPrepStmts;
    }

    public void setUserServerPrepStmts(boolean userServerPrepStmts) {
        this.userServerPrepStmts = userServerPrepStmts;
    }
}
