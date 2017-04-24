package com.navercorp.pinpoint.plugin.apusic;

import com.navercorp.pinpoint.common.trace.TraceMetadataProvider;
import com.navercorp.pinpoint.common.trace.TraceMetadataSetupContext;

/**
 * Created by root on 17-4-24.
 */
public class ApusicTypeProvider implements TraceMetadataProvider {

    @Override
    public void setup(TraceMetadataSetupContext context) {
        context.addServiceType(ApusicConstants.APUSIC);
        context.addServiceType(ApusicConstants.APUSIC_METHOD);
    }
}
