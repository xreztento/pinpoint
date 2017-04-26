package com.navercorp.pinpoint.plugin.apusic.interceptor;

import com.apusic.deploy.runtime.WebModule;
import com.apusic.web.container.WebContainer;
import com.apusic.web.http.VirtualHost;
import com.navercorp.pinpoint.bootstrap.context.ServerMetaDataHolder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;

import javax.servlet.ServletContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by root on 17-4-25.
 */
public class WebModuleLoadInterceptor implements AroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private final TraceContext traceContext;

    public WebModuleLoadInterceptor(TraceContext traceContext) {
        this.traceContext = traceContext;
    }

    @Override
    public void before(Object target, Object[] args) {
        // Do Nothing
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        // target should be an instance of WebappLoader.
        if (target instanceof WebModule) {
            WebModule webModule = (WebModule)target;
            try {
                String contextKey = extractContextKey(webModule);
                List<String> loadedJarNames = extractLibJars(webModule);
                dispatchLibJars(contextKey, loadedJarNames);
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(e.getMessage(), e);
                }
            }
        } else {
            logger.warn("Webapp loader is not an instance of org.apache.catalina.loader.WebappLoader. Found [{}]", target.getClass().toString());
        }
    }

    private String extractContextKey(WebModule webModule) {
        final String defaultContextName = "";
        try {
            WebContainer container = extractContext(webModule);
            // WebappLoader's associated Container should be a Context.
            if (container instanceof ServletContext) {
                ServletContext context = (ServletContext)container;
                String contextName = context.getServletContextName();
                VirtualHost host = (VirtualHost)container.getVirtualHost();
                StringBuilder sb = new StringBuilder();
                sb.append(host.getServerName());
                if (!contextName.startsWith("/")) {
                    sb.append('/');
                }
                sb.append(contextName);
                return sb.toString();
            }
        } catch (Exception e) {
            // Same action for any and all exceptions.
            logger.warn("Error extracting context name.", e);
        }
        return defaultContextName;
    }

    private WebContainer extractContext(WebModule webModule) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method m;
        try {
            m = webModule.getClass().getDeclaredMethod("getContainer");
        } catch (NoSuchMethodException e1) {
            logger.warn("Webapp loader does not have access to its container.");
            return null;
        }
        Object container = m.invoke(webModule);
        if (container instanceof WebContainer) {
            return (WebContainer)container;
        }
        return null;
    }

    private List<String> extractLibJars(WebModule webModule) {
        ClassLoader classLoader = webModule.getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            URLClassLoader webappClassLoader = (URLClassLoader)classLoader;
            URL[] urls = webappClassLoader.getURLs();
            return extractLibJarNamesFromURLs(urls);
        } else {
            logger.warn("Webapp class loader is not an instance of URLClassLoader. Found [{}]", classLoader.getClass().toString());
            return Collections.emptyList();
        }
    }

    private List<String> extractLibJarNamesFromURLs(URL[] urls) {
        if (urls == null) {
            return Collections.emptyList();
        }
        List<String> libJarNames = new ArrayList<String>(urls.length);
        for (URL url : urls) {
            try {
                URI uri =  url.toURI();
                String libJarName = extractLibJarName(uri);
                if (libJarName.length() > 0) {
                    libJarNames.add(libJarName);
                }
            } catch (URISyntaxException e) {
                // ignore invalid formats
                logger.warn("Invalid library url found : [{}]", url, e);
            } catch (Exception e) {
                logger.warn("Error extracting library name", e);
            }
        }
        return libJarNames;
    }

    private String extractLibJarName(URI uri) {
        String jarName = uri.toString();
        if (jarName == null) {
            return "";
        }
        int lastIndexOfSeparator = jarName.lastIndexOf("/");
        if (lastIndexOfSeparator < 0) {
            return jarName;
        } else {
            return jarName.substring(lastIndexOfSeparator + 1);
        }
    }

    private void dispatchLibJars(String contextKey, List<String> libJars) {
        ServerMetaDataHolder holder = this.traceContext.getServerMetaDataHolder();
        holder.addServiceInfo(contextKey, libJars);
        holder.notifyListeners();
    }

}
