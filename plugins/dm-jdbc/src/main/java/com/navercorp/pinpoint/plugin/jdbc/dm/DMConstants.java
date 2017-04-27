package com.navercorp.pinpoint.plugin.jdbc.dm;

import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;

import static com.navercorp.pinpoint.common.trace.ServiceTypeProperty.INCLUDE_DESTINATION_ID;
import static com.navercorp.pinpoint.common.trace.ServiceTypeProperty.RECORD_STATISTICS;
import static com.navercorp.pinpoint.common.trace.ServiceTypeProperty.TERMINAL;

/**
 * Created by root on 17-4-27.
 */
public class DMConstants {
    private DMConstants(){}
    public static final String DM_SCOPE = "DM_JDBC";

    public static final ServiceType DM = ServiceTypeFactory.of(2180, "DM", TERMINAL, INCLUDE_DESTINATION_ID);
    public static final ServiceType DM_EXECUTE_QUERY = ServiceTypeFactory.of(2181, "DM_EXECUTE_QUERY", "DM", TERMINAL, RECORD_STATISTICS, INCLUDE_DESTINATION_ID);
}
