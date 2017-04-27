package com.navercorp.pinpoint.plugin.jdbc.dm;

import com.navercorp.pinpoint.bootstrap.context.DatabaseInfo;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.DefaultDatabaseInfo;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.JdbcUrlParserV2;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.StringMaker;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.UnKnownDatabaseInfo;
import com.navercorp.pinpoint.common.trace.ServiceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 17-4-27.
 */
public class DMJdbcUrlParser implements JdbcUrlParserV2 {

    private static final String URL_PREFIX = "jdbc:dm:";
    // jdbc:dm:loadbalance://10.22.33.44:3306,10.22.33.55:3306/MySQL?characterEncoding=UTF-8
    private static final String LOADBALANCE_URL_PREFIX = URL_PREFIX + "loadbalance:";

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    @Override
    public DatabaseInfo parse(String jdbcUrl) {
        if (jdbcUrl == null) {
            logger.info("jdbcUrl may not be null");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        if (!jdbcUrl.startsWith(URL_PREFIX)) {
            logger.info("jdbcUrl has invalid prefix.(url:{}, prefix:{})", jdbcUrl, URL_PREFIX);
            return UnKnownDatabaseInfo.INSTANCE;
        }

        DatabaseInfo result = null;
        try {
            result = parse0(jdbcUrl);
        } catch (Exception e) {
            logger.info("DMJdbcUrl parse error. url: {}, Caused: {}", jdbcUrl, e.getMessage(), e);
            result = UnKnownDatabaseInfo.createUnknownDataBase(DMConstants.DM, DMConstants.DM_EXECUTE_QUERY, jdbcUrl);
        }
        return result;
    }

    private DatabaseInfo parse0(String jdbcUrl) {
        if (isLoadbalanceUrl(jdbcUrl)) {
            return parseLoadbalancedUrl(jdbcUrl);
        }
        return parseNormal(jdbcUrl);
    }

    private boolean isLoadbalanceUrl(String jdbcUrl) {
        return jdbcUrl.regionMatches(true, 0, LOADBALANCE_URL_PREFIX, 0, LOADBALANCE_URL_PREFIX.length());
    }

    private DatabaseInfo parseLoadbalancedUrl(String jdbcUrl) {
        // jdbc:dm://1.2.3.4:5678/test_db
        StringMaker maker = new StringMaker(jdbcUrl);
        maker.after(URL_PREFIX);
        // 1.2.3.4:5678 In case of replication driver could have multiple values
        // We have to consider mm db too.
        String host = maker.after("//").before('/').value();

        // Decided not to cache regex. This is not invoked often so don't waste memory.
        String[] parsedHost = host.split(",");
        List<String> hostList = Arrays.asList(parsedHost);


        String databaseId = maker.next().afterLast('/').before('?').value();
        String normalizedUrl = maker.clear().before('?').value();

        return new DefaultDatabaseInfo(DMConstants.DM, DMConstants.DM_EXECUTE_QUERY, jdbcUrl, normalizedUrl, hostList, databaseId);
    }

    private DatabaseInfo parseNormal(String jdbcUrl) {
        // jdbc:dm://1.2.3.4:5678/test_db
        StringMaker maker = new StringMaker(jdbcUrl);
        maker.after(URL_PREFIX);
        // 1.2.3.4:5678 In case of replication driver could have multiple values
        // We have to consider mm db too.
        String host = maker.after("//").before('/').value();
        List<String> hostList = new ArrayList<String>(1);
        hostList.add(host);
        // String port = maker.next().after(':').before('/').value();

        String databaseId = maker.next().afterLast('/').before('?').value();
        String normalizedUrl = maker.clear().before('?').value();
        return new DefaultDatabaseInfo(DMConstants.DM, DMConstants.DM_EXECUTE_QUERY, jdbcUrl, normalizedUrl, hostList, databaseId);
    }

    @Override
    public ServiceType getServiceType() {
        return DMConstants.DM;
    }

}
