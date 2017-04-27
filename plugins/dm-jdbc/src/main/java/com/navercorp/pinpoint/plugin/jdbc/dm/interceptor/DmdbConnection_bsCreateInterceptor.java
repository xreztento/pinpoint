package com.navercorp.pinpoint.plugin.jdbc.dm.interceptor;

import com.navercorp.pinpoint.bootstrap.context.DatabaseInfo;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.annotation.TargetConstructor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.DatabaseInfoAccessor;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.DefaultDatabaseInfo;
import com.navercorp.pinpoint.bootstrap.util.InterceptorUtils;
import com.navercorp.pinpoint.plugin.jdbc.dm.DMConstants;
import com.navercorp.pinpoint.plugin.jdbc.dm.DMJdbcUrlParser;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by root on 17-4-27.
 */
@TargetConstructor({ "java.lang.String", "java.util.Properties" })
public class DmdbConnection_bsCreateInterceptor implements AroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final TraceContext traceContext;

    public DmdbConnection_bsCreateInterceptor(TraceContext traceContext) {
        this.traceContext = traceContext;
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args, result, throwable);
        }
        if (args == null || args.length != 2) {
            return;
        }
        Properties properties = (Properties)args[1];
        DMJdbcUrlParser parser = new DMJdbcUrlParser();
        DatabaseInfo info = parser.parse(String.valueOf(args[0]));

        final String hostToConnectTo = properties.getProperty("host");
        final Integer portToConnectTo = Integer.valueOf(properties.getProperty("port"));

        final String databaseId = getString(info.getDatabaseId());



        // In case of loadbalance, connectUrl is modified.
        // final String url = getString(args[4]);
        DatabaseInfo databaseInfo = null;
        if (hostToConnectTo != null && portToConnectTo != null && databaseId != null) {
            // It's dangerous to use this url directly
            databaseInfo = createDatabaseInfo(hostToConnectTo, portToConnectTo, databaseId);
            if (InterceptorUtils.isSuccess(throwable)) {
                // Set only if connection is success.
                if (target instanceof DatabaseInfoAccessor) {
                    ((DatabaseInfoAccessor) target)._$PINPOINT$_setDatabaseInfo(databaseInfo);
                }
            }
        }

        final Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }

        SpanEventRecorder recorder = trace.currentSpanEventRecorder();
        // We must do this if current transaction is being recorded.
        if (databaseInfo != null) {
            recorder.recordServiceType(databaseInfo.getType());
            recorder.recordEndPoint(databaseInfo.getMultipleHost());
            recorder.recordDestinationId(databaseInfo.getDatabaseId());
        }

    }

    private DatabaseInfo createDatabaseInfo(String url, Integer port, String databaseId) {
        if (url.indexOf(':') == -1) {
            url += ":" + port;
        }

        DatabaseInfo databaseInfo = new DefaultDatabaseInfo(DMConstants.DM, DMConstants.DM_EXECUTE_QUERY, url, url, Arrays.asList(url), databaseId);
        System.out.println(databaseInfo.toString());
        return databaseInfo;

    }

    private String getString(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    private Integer getInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return null;
    }

    @Override
    public void before(Object target, Object[] args) {

    }
}

