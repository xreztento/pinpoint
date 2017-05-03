package com.navercorp.pinpoint.plugin.jdbc.oscar;

import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.trace.AnnotationKeyMatchers;
import com.navercorp.pinpoint.common.trace.TraceMetadataProvider;
import com.navercorp.pinpoint.common.trace.TraceMetadataSetupContext;

/**
 * Created by root on 17-4-27.
 */
public class OscarTypeProvider implements TraceMetadataProvider {

    @Override
    public void setup(TraceMetadataSetupContext context) {

        context.addServiceType(OscarConstants.OSCAR, AnnotationKeyMatchers.exact(AnnotationKey.ARGS0));
        context.addServiceType(OscarConstants.OSCAR_EXECUTE_QUERY, AnnotationKeyMatchers.exact(AnnotationKey.ARGS0));
    }

}
