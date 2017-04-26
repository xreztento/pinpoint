package com.navercorp.pinpoint.plugin.apusic.interceptor;

import com.navercorp.pinpoint.bootstrap.context.ServerMetaDataHolder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.apusic.net.Muxer;

/**
 * Created by root on 17-4-25.
 */
public class MuxerStartInterceptor implements AroundInterceptor {

    private PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private TraceContext traceContext;

    public MuxerStartInterceptor(TraceContext traceContext) {
        this.traceContext = traceContext;
    }

    @Override
    public void before(Object target, Object[] args) {

    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args, result, throwable);
        }
        if (target instanceof Muxer) {
            final Muxer muxer = (Muxer) target;
            System.out.println(muxer);
            ServerMetaDataHolder holder = this.traceContext.getServerMetaDataHolder();
            holder.addConnector("HTTP", muxer.getPort());
            holder.notifyListeners();
        }
    }
}
