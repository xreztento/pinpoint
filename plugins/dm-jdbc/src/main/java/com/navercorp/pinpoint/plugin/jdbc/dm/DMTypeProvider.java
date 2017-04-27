package com.navercorp.pinpoint.plugin.jdbc.dm;

import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.trace.AnnotationKeyMatchers;
import com.navercorp.pinpoint.common.trace.TraceMetadataProvider;
import com.navercorp.pinpoint.common.trace.TraceMetadataSetupContext;

/**
 * Created by root on 17-4-27.
 */
public class DMTypeProvider implements TraceMetadataProvider {

    @Override
    public void setup(TraceMetadataSetupContext context) {

        context.addServiceType(DMConstants.DM, AnnotationKeyMatchers.exact(AnnotationKey.ARGS0));
        context.addServiceType(DMConstants.DM_EXECUTE_QUERY, AnnotationKeyMatchers.exact(AnnotationKey.ARGS0));
    }

}
