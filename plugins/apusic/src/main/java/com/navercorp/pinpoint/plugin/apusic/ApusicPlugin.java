package com.navercorp.pinpoint.plugin.apusic;

import com.navercorp.pinpoint.bootstrap.async.AsyncTraceIdAccessor;
import com.navercorp.pinpoint.bootstrap.instrument.*;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;
import com.navercorp.pinpoint.bootstrap.resolver.ConditionProvider;


import java.security.ProtectionDomain;

/**
 * Created by root on 17-4-24.
 */
public class ApusicPlugin implements ProfilerPlugin, TransformTemplateAware {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private TransformTemplate transformTemplate;

    /*
     * (non-Javadoc)
     *
     * @see com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin#setUp(com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext)
     */
    @Override
    public void setup(ProfilerPluginSetupContext context) {
        System.out.println("------------------Setup ApusicPlugin-----------------");


//        final ApusicConfig config = new ApusicConfig(context.getConfig());
//        if (logger.isInfoEnabled()) {
//            logger.info("ApusicPlugin config:{}", config);
//        }
//        if (!config.isApusicEnable()) {
//            logger.info("ApusicPlugin disabled");
//            return;
//        }

        //ApusicDetector apusicDetector = new ApusicDetector(config.getApusicBootstrapMains());
        ApusicDetector apusicDetector = new ApusicDetector(null);

        context.addApplicationTypeDetector(apusicDetector);

//        if (shouldAddTransformers(config)) {
//            logger.info("Adding Apusic transformers");
//            System.out.println("Adding Apusic transformers");
//            addTransformers(config);
//        } else {
//            logger.info("Not adding Apusic transfomers");
//            System.out.println("Not adding Apusic transfomers");
//        }
        addTransformers();
    }

    private boolean shouldAddTransformers(ApusicConfig config) {
        // Transform if conditional check is disabled
        if (!config.isApusicConditionalTransformEnable()) {
            return true;
        }
        return false;
    }

    private void addTransformers() {


        addRequestEditor();
        addWebContainerEditor();
        addWebServiceEditor();
        addMuxerEditor();
        addWebModuleEditor();
        addAsyncContextImpl();
        System.out.println("------------------Adding Apusic transformers-----------------");

    }

    private void addRequestEditor() {
        transformTemplate.transform("com.apusic.web.container.Request", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                target.addField(ApusicConstants.TRACE_ACCESSOR);
                target.addField(ApusicConstants.ASYNC_ACCESSOR);

                //if (config.isApusicHidePinpointHeader()) {
                    target.weave("com.navercorp.pinpoint.plugin.apusic.aspect.RequestAspect");
                //}


                // trace asynchronous process.
                InstrumentMethod doStartAsyncMethodEditor = target.getDeclaredMethod("doStartAsync", "javax.servlet.ServletRequest", "javax.servlet.ServletResponse", boolean.class.getName());
                if (doStartAsyncMethodEditor != null) {
                    doStartAsyncMethodEditor.addInterceptor("com.navercorp.pinpoint.plugin.apusic.interceptor.RequestDoStartAsyncInterceptor");
                }

                return target.toBytecode();
            }
        });
    }


    private void addWebContainerEditor() {
        transformTemplate.transform("com.apusic.web.container.WebContainer", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);

                InstrumentMethod method = target.getDeclaredMethod("processRequest", "com.apusic.web.http.HttpProtocol");
                if (method != null) {
                    method.addInterceptor("com.navercorp.pinpoint.plugin.apusic.interceptor.WebContainerDoProcessRequestInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    private void addWebServiceEditor() {
        transformTemplate.transform("com.apusic.web.WebService", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);

                InstrumentMethod startEditor = target.getDeclaredMethod("startService");
                if (startEditor != null) {
                    startEditor.addInterceptor("com.navercorp.pinpoint.plugin.apusic.interceptor.WebServiceStartServiceInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    private void addMuxerEditor() {
        transformTemplate.transform("com.apusic.net.Muxer", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);

                InstrumentMethod initInternalEditor = target.getDeclaredMethod("startService");
                if (initInternalEditor != null) {
                    initInternalEditor.addInterceptor("com.navercorp.pinpoint.plugin.apusic.interceptor.MuxerStartInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    private void addWebModuleEditor() {
        transformTemplate.transform("com.apusic.deploy.runtime.WebModule", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);

                InstrumentMethod startMethod = target.getDeclaredMethod("load");

                if (startMethod != null) {
                    startMethod.addInterceptor("com.navercorp.pinpoint.plugin.apusic.interceptor.WebModuleLoadInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    private void addAsyncContextImpl() {
        transformTemplate.transform("com.apusic.web.container.AsyncContextImpl", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                target.addField(AsyncTraceIdAccessor.class.getName());
                for (InstrumentMethod method : target.getDeclaredMethods(MethodFilters.name("dispatch"))) {
                    method.addInterceptor("com.navercorp.pinpoint.plugin.apusic.interceptor.AsyncContextImplDispatchMethodInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}