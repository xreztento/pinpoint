package com.navercorp.pinpoint.plugin.apusic;

import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;

import static com.navercorp.pinpoint.common.trace.ServiceTypeProperty.RECORD_STATISTICS;

/**
 * Created by root on 17-4-24.
 */
public final class ApusicConstants {
    private ApusicConstants() {
    }

    public static final ServiceType APUSIC = ServiceTypeFactory.of(1910, "APUSIC", RECORD_STATISTICS);
    public static final ServiceType APUSIC_METHOD = ServiceTypeFactory.of(1911, "APUSIC_METHOD");

    public static final String APUSIC_SERVLET_ASYNC_SCOPE = "ApusicServletAsyncScope";

    public static final String ASYNC_ACCESSOR = "com.navercorp.pinpoint.plugin.apusic.AsyncAccessor";
    public static final String TRACE_ACCESSOR = "com.navercorp.pinpoint.plugin.apusic.TraceAccessor";

}
