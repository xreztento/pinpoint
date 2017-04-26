package com.navercorp.pinpoint.plugin.apusic;

import com.navercorp.pinpoint.bootstrap.plugin.ApplicationTypeDetector;
import com.navercorp.pinpoint.bootstrap.resolver.ConditionProvider;
import com.navercorp.pinpoint.common.trace.ServiceType;

import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 17-4-24.
 */
public class ApusicDetector implements ApplicationTypeDetector {

    private static final String DEFAULT_BOOTSTRAP_MAIN = "com.apusic.server.Main";

    //private static final String REQUIRED_SYSTEM_PROPERTY = "com.apusic";

    private static final String REQUIRED_CLASS = "com.apusic.server.Main";

    private final List<String> bootstrapMains;

    public ApusicDetector(List<String> bootstrapMains) {
        if (bootstrapMains == null || bootstrapMains.isEmpty()) {
            this.bootstrapMains = Arrays.asList(DEFAULT_BOOTSTRAP_MAIN);
        } else {
            this.bootstrapMains = bootstrapMains;
        }
    }

    @Override
    public ServiceType getApplicationType() {
        return ApusicConstants.APUSIC;
    }

    @Override
    public boolean detect(ConditionProvider provider) {
//        return provider.checkMainClass(bootstrapMains) &&
//                provider.checkForClass(REQUIRED_CLASS);
        return true;

    }

}
