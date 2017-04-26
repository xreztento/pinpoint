package com.navercorp.pinpoint.plugin.apusic.interceptor;

import com.apusic.web.WebService;
import com.navercorp.pinpoint.bootstrap.context.ServerMetaDataHolder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;

/**
 * Created by root on 17-4-25.
 */
public class WebServiceStartServiceInterceptor implements AroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final TraceContext traceContext;

    public WebServiceStartServiceInterceptor(TraceContext context) {
        this.traceContext = context;
    }

    @Override
    public void before(Object target, Object[] args) {
        // Do nothing
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args, result, throwable);
        }

        WebService service = (WebService)target;
        ServerMetaDataHolder holder = this.traceContext.getServerMetaDataHolder();
        holder.setServerName(service.getServerName());
        holder.notifyListeners();
    }
}
