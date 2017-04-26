package com.navercorp.pinpoint.plugin.apusic.aspect;

import com.navercorp.pinpoint.bootstrap.context.Header;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.Aspect;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.JointPoint;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.PointCut;

import java.util.Enumeration;

/**
 * Created by root on 17-4-25.
 */
@Aspect
public abstract class RequestAspect {

    @PointCut
    public String getHeader(String name) {
        if (Header.hasHeader(name)) {
            return null;
        }
        return __getHeader(name);
    }

    @JointPoint
    abstract String __getHeader(String name);


    @PointCut
    public Enumeration getHeaders(String name) {
        final Enumeration headers = Header.getHeaders(name);
        if (headers != null) {
            return headers;
        }
        return __getHeaders(name);
    }

    @JointPoint
    abstract Enumeration __getHeaders(String name);


    @PointCut
    public Enumeration getHeaderNames() {
        final Enumeration enumeration = __getHeaderNames();
        return Header.filteredHeaderNames(enumeration);
    }

    @JointPoint
    abstract Enumeration __getHeaderNames();

}
