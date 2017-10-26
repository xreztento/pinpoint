/*
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.navercorp.pinpoint.plugin.tongweb;

import com.navercorp.pinpoint.bootstrap.config.*;

import java.util.List;

/**
 * @author Woonduk Kang(emeroad)
 */
public class TongwebConfig {

    private final boolean tongwebEnable;
    private final List<String> tongwebBootstrapMains;
    private final boolean tongwebConditionalTransformEnable;
    private final boolean tongwebHidePinpointHeader;

    private final boolean tongwebTraceRequestParam;
    private final Filter<String> tongwebExcludeUrlFilter;
    private final String tongwebRealIpHeader;
    private final String tongwebRealIpEmptyValue;
    private final Filter<String> tongwebExcludeProfileMethodFilter;

    // for transform conditional check
    private final List<String> springBootBootstrapMains;

    public TongwebConfig(ProfilerConfig config) {
        if (config == null) {
            throw new NullPointerException("config must not be null");
        }

        // plugin
        this.tongwebEnable = config.readBoolean("profiler.tongweb.enable", true);
        this.tongwebBootstrapMains = config.readList("profiler.tongweb.bootstrap.main");
        this.tongwebConditionalTransformEnable = config.readBoolean("profiler.tongweb.conditional.transform", true);
        this.tongwebHidePinpointHeader = config.readBoolean("profiler.tongweb.hidepinpointheader", true);

        // runtime
        this.tongwebTraceRequestParam = config.readBoolean("profiler.tongweb.tracerequestparam", true);
        final String tongwebExcludeURL = config.readString("profiler.tongweb.excludeurl", "");
        if (!tongwebExcludeURL.isEmpty()) {
            this.tongwebExcludeUrlFilter = new ExcludePathFilter(tongwebExcludeURL);
        } else {
            this.tongwebExcludeUrlFilter = new SkipFilter<String>();
        }
        this.tongwebRealIpHeader = config.readString("profiler.tongweb.realipheader", null);
        this.tongwebRealIpEmptyValue = config.readString("profiler.tongweb.realipemptyvalue", null);

        final String tongwebExcludeProfileMethod = config.readString("profiler.tongweb.excludemethod", "");
        if (!tongwebExcludeProfileMethod.isEmpty()) {
            this.tongwebExcludeProfileMethodFilter = new ExcludeMethodFilter(tongwebExcludeProfileMethod);
        } else {
            this.tongwebExcludeProfileMethodFilter = new SkipFilter<String>();
        }

        this.springBootBootstrapMains = config.readList("profiler.springboot.bootstrap.main");
    }

    public boolean isTongwebEnable() {
        return tongwebEnable;
    }

    public List<String> getTongwebBootstrapMains() {
        return tongwebBootstrapMains;
    }

    public boolean isTongwebConditionalTransformEnable() {
        return tongwebConditionalTransformEnable;
    }

    public boolean isTongwebHidePinpointHeader() {
        return tongwebHidePinpointHeader;
    }

    public boolean isTongwebTraceRequestParam() {
        return tongwebTraceRequestParam;
    }

    public Filter<String> getTongwebExcludeUrlFilter() {
        return tongwebExcludeUrlFilter;
    }

    public String getTongwebRealIpHeader() {
        return tongwebRealIpHeader;
    }

    public String getTongwebRealIpEmptyValue() {
        return tongwebRealIpEmptyValue;
    }

    public Filter<String> getTongwebExcludeProfileMethodFilter() {
        return tongwebExcludeProfileMethodFilter;
    }

    public List<String> getSpringBootBootstrapMains() {
        return springBootBootstrapMains;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("tongwebConfig{");
        sb.append("tongwebEnable=").append(tongwebEnable);
        sb.append(", tongwebBootstrapMains=").append(tongwebBootstrapMains);
        sb.append(", tongwebConditionalTransformEnable=").append(tongwebConditionalTransformEnable);
        sb.append(", tongwebHidePinpointHeader=").append(tongwebHidePinpointHeader);
        sb.append(", tongwebTraceRequestParam=").append(tongwebTraceRequestParam);
        sb.append(", tongwebExcludeUrlFilter=").append(tongwebExcludeUrlFilter);
        sb.append(", tongwebRealIpHeader='").append(tongwebRealIpHeader).append('\'');
        sb.append(", tongwebRealIpEmptyValue='").append(tongwebRealIpEmptyValue).append('\'');
        sb.append(", tongwebExcludeProfileMethodFilter=").append(tongwebExcludeProfileMethodFilter);
        sb.append('}');
        return sb.toString();
    }
}
