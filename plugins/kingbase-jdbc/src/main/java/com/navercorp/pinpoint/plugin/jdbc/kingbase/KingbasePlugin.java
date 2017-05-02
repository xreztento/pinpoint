package com.navercorp.pinpoint.plugin.jdbc.kingbase;

import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.ExecutionPolicy;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.JdbcUrlParserV2;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.PreparedStatementBindingMethodFilter;

import java.security.ProtectionDomain;

import static com.navercorp.pinpoint.common.util.VarArgs.va;


/**
 * Created by root on 17-5-2.
 */
public class KingbasePlugin implements ProfilerPlugin, TransformTemplateAware {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private final JdbcUrlParserV2 jdbcUrlParser = new KingbaseJdbcUrlParser();

    private TransformTemplate transformTemplate;

    @Override
    public void setup(ProfilerPluginSetupContext context) {
        KingbaseConfig config = new KingbaseConfig(context.getConfig());

        if (!config.isPluginEnable()) {
            logger.info("Kingbase plugin is not executed because plugin enable value is false.");
            return;
        }

        context.addJdbcUrlParser(jdbcUrlParser);

        //addConnectionTransformer(config);
        //addDriverTransformer();
        //addPreparedStatementTransformer(config);
    }

    private void addConnectionTransformer(final KingbaseConfig config) {
        transformTemplate.transform("com.kingbase.jdbc4.Jdbc4Connection", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                if (!target.isInterceptable()) {
                    return null;
                }

                target.addField("com.navercorp.pinpoint.bootstrap.plugin.jdbc.DatabaseInfoAccessor");

                target.addInterceptor("com.navercorp.pinpoint.plugin.jdbc.kingbase.interceptor.KingbaseConnectionCreateInterceptor");
                target.addScopedInterceptor("com.navercorp.pinpoint.plugin.jdbc.kingbase.interceptor.KingbasePreparedStatementCreateInterceptor3", KingbaseConstants.KINGBASE_SCOPE);

                return target.toBytecode();
            }
        });

        transformTemplate.transform("com.kingbase.jdbc2.AbstractJdbc2Connection", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                if (!target.isInterceptable()) {
                    return null;
                }

                target.addField("com.navercorp.pinpoint.bootstrap.plugin.jdbc.DatabaseInfoAccessor");

                target.addScopedInterceptor("com.navercorp.pinpoint.bootstrap.plugin.jdbc.interceptor.ConnectionCloseInterceptor", KingbaseConstants.KINGBASE_SCOPE);
                target.addScopedInterceptor("com.navercorp.pinpoint.plugin.jdbc.kingbase.interceptor.KingbasePreparedStatementCreateInterceptor1", KingbaseConstants.KINGBASE_SCOPE);

                if (config.isProfileSetAutoCommit()) {
                    target.addScopedInterceptor("com.navercorp.pinpoint.bootstrap.plugin.jdbc.interceptor.TransactionSetAutoCommitInterceptor", KingbaseConstants.KINGBASE_SCOPE);
                }

                if (config.isProfileCommit()) {
                    target.addScopedInterceptor("com.navercorp.pinpoint.bootstrap.plugin.jdbc.interceptor.TransactionCommitInterceptor", KingbaseConstants.KINGBASE_SCOPE);
                }

                if (config.isProfileRollback()) {
                    target.addScopedInterceptor("com.navercorp.pinpoint.bootstrap.plugin.jdbc.interceptor.TransactionRollbackInterceptor", KingbaseConstants.KINGBASE_SCOPE);
                }

                return target.toBytecode();
            }
        });

        transformTemplate.transform("com.kingbase.jdbc3.AbstractJdbc3Connection", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                if (!target.isInterceptable()) {
                    return null;
                }

                target.addField("com.navercorp.pinpoint.bootstrap.plugin.jdbc.DatabaseInfoAccessor");

                target.addScopedInterceptor("com.navercorp.pinpoint.plugin.jdbc.kingbase.interceptor.KingbasePreparedStatementCreateInterceptor2", KingbaseConstants.KINGBASE_SCOPE);

                return target.toBytecode();
            }
        });
    }

    private void addDriverTransformer() {
        transformTemplate.transform("com.kingbase.Driver", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                target.addScopedInterceptor("com.navercorp.pinpoint.bootstrap.plugin.jdbc.interceptor.DriverConnectInterceptorV2", va(KingbaseConstants.KINGBASE, false), KingbaseConstants.KINGBASE_SCOPE, ExecutionPolicy.ALWAYS);

                return target.toBytecode();
            }
        });
    }

    private void addPreparedStatementTransformer(final KingbaseConfig config) {
        transformTemplate.transform("com.kingbase.jdbc2.AbstractJdbc2Statement", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                target.addField("com.navercorp.pinpoint.bootstrap.plugin.jdbc.DatabaseInfoAccessor");
                target.addField("com.navercorp.pinpoint.bootstrap.plugin.jdbc.ParsingResultAccessor");
                target.addField("com.navercorp.pinpoint.bootstrap.plugin.jdbc.BindValueAccessor");

                int maxBindValueSize = config.getMaxSqlBindValueSize();

                target.addScopedInterceptor("com.navercorp.pinpoint.bootstrap.plugin.jdbc.interceptor.PreparedStatementExecuteQueryInterceptor", va(maxBindValueSize), KingbaseConstants.KINGBASE_SCOPE);

                return target.toBytecode();
            }
        });
        transformTemplate.transform("com.kingbase.jdbc3.AbstractJdbc3Statement", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                target.addField("com.navercorp.pinpoint.bootstrap.plugin.jdbc.DatabaseInfoAccessor");
                target.addField("com.navercorp.pinpoint.bootstrap.plugin.jdbc.ParsingResultAccessor");
                target.addField("com.navercorp.pinpoint.bootstrap.plugin.jdbc.BindValueAccessor");

                final PreparedStatementBindingMethodFilter excludes = PreparedStatementBindingMethodFilter.excludes("setRowId", "setNClob", "setSQLXML");
                target.addScopedInterceptor(excludes, "com.navercorp.pinpoint.bootstrap.plugin.jdbc.interceptor.PreparedStatementBindVariableInterceptor", KingbaseConstants.KINGBASE_SCOPE, ExecutionPolicy.BOUNDARY);

                target.addScopedInterceptor("com.navercorp.pinpoint.bootstrap.plugin.jdbc.interceptor.StatementExecuteQueryInterceptor", KingbaseConstants.KINGBASE_SCOPE);
                target.addScopedInterceptor("com.navercorp.pinpoint.bootstrap.plugin.jdbc.interceptor.StatementExecuteUpdateInterceptor", KingbaseConstants.KINGBASE_SCOPE);

                return target.toBytecode();
            }
        });
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
