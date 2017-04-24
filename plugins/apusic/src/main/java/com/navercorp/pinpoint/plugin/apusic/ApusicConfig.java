package com.navercorp.pinpoint.plugin.apusic;

import com.navercorp.pinpoint.bootstrap.config.*;

import java.util.List;

/**
 * Created by root on 17-4-24.
 */
public class ApusicConfig {
    private final boolean apusicEnable;
    private final List<String> apusicBootstrapMains;
    private final boolean apusicConditionalTransformEnable;
    private final boolean apusicHidePinpointHeader;

    private final boolean apusicTraceRequestParam;
    private final Filter<String> apusicExcludeUrlFilter;
    private final String apusicRealIpHeader;
    private final String apusicRealIpEmptyValue;
    private final Filter<String> apusicExcludeProfileMethodFilter;


    public ApusicConfig(ProfilerConfig config) {
        if (config == null) {
            throw new NullPointerException("config must not be null");
        }

        // plugin
        this.apusicEnable = config.readBoolean("profiler.apusic.enable", true);
        this.apusicBootstrapMains = config.readList("profiler.apusic.bootstrap.main");
        this.apusicConditionalTransformEnable = config.readBoolean("profiler.apusic.conditional.transform", true);
        this.apusicHidePinpointHeader = config.readBoolean("profiler.apusic.hidepinpointheader", true);

        // runtime
        this.apusicTraceRequestParam = config.readBoolean("profiler.apusic.tracerequestparam", true);
        final String apusicExcludeURL = config.readString("profiler.apusic.excludeurl", "");
        if (!apusicExcludeURL.isEmpty()) {
            this.apusicExcludeUrlFilter = new ExcludePathFilter(apusicExcludeURL);
        } else {
            this.apusicExcludeUrlFilter = new SkipFilter<String>();
        }
        this.apusicRealIpHeader = config.readString("profiler.apusic.realipheader", null);
        this.apusicRealIpEmptyValue = config.readString("profiler.apusic.realipemptyvalue", null);

        final String apusicExcludeProfileMethod = config.readString("profiler.apusic.excludemethod", "");
        if (!apusicExcludeProfileMethod.isEmpty()) {
            this.apusicExcludeProfileMethodFilter = new ExcludeMethodFilter(apusicExcludeProfileMethod);
        } else {
            this.apusicExcludeProfileMethodFilter = new SkipFilter<String>();
        }

    }

    public boolean isapusicEnable() {
        return apusicEnable;
    }

    public List<String> getapusicBootstrapMains() {
        return apusicBootstrapMains;
    }

    public boolean isapusicConditionalTransformEnable() {
        return apusicConditionalTransformEnable;
    }

    public boolean isapusicHidePinpointHeader() {
        return apusicHidePinpointHeader;
    }

    public boolean isapusicTraceRequestParam() {
        return apusicTraceRequestParam;
    }

    public Filter<String> getapusicExcludeUrlFilter() {
        return apusicExcludeUrlFilter;
    }

    public String getapusicRealIpHeader() {
        return apusicRealIpHeader;
    }

    public String getapusicRealIpEmptyValue() {
        return apusicRealIpEmptyValue;
    }

    public Filter<String> getapusicExcludeProfileMethodFilter() {
        return apusicExcludeProfileMethodFilter;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("apusicConfig{");
        sb.append("apusicEnable=").append(apusicEnable);
        sb.append(", apusicBootstrapMains=").append(apusicBootstrapMains);
        sb.append(", apusicConditionalTransformEnable=").append(apusicConditionalTransformEnable);
        sb.append(", apusicHidePinpointHeader=").append(apusicHidePinpointHeader);
        sb.append(", apusicTraceRequestParam=").append(apusicTraceRequestParam);
        sb.append(", apusicExcludeUrlFilter=").append(apusicExcludeUrlFilter);
        sb.append(", apusicRealIpHeader='").append(apusicRealIpHeader).append('\'');
        sb.append(", apusicRealIpEmptyValue='").append(apusicRealIpEmptyValue).append('\'');
        sb.append(", apusicExcludeProfileMethodFilter=").append(apusicExcludeProfileMethodFilter);
        sb.append('}');
        return sb.toString();
    }
}
