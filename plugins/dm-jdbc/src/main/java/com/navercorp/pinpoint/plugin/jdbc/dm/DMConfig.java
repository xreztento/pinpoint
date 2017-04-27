package com.navercorp.pinpoint.plugin.jdbc.dm;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.JdbcConfig;

/**
 * Created by root on 17-4-27.
 */
public class DMConfig extends JdbcConfig {
    private final boolean profileSetAutoCommit;
    private final boolean profileCommit;
    private final boolean profileRollback;

    public DMConfig(ProfilerConfig config) {
        super(config.readBoolean("profiler.jdbc.dm", false),
                config.readBoolean("profiler.jdbc.dm.tracesqlbindvalue", config.isTraceSqlBindValue()),
                config.getMaxSqlBindValueSize());
        this.profileSetAutoCommit = config.readBoolean("profiler.jdbc.dm.setautocommit", false);
        this.profileCommit = config.readBoolean("profiler.jdbc.dm.commit", false);
        this.profileRollback = config.readBoolean("profiler.jdbc.dm.rollback", false);
    }

    public boolean isProfileSetAutoCommit() {
        return profileSetAutoCommit;
    }

    public boolean isProfileCommit() {
        return profileCommit;
    }

    public boolean isProfileRollback() {
        return profileRollback;
    }

    @Override
    public String toString() {
        return "DMConfig [" + super.toString() + ", profileSetAutoCommit=" + profileSetAutoCommit + ", profileCommit=" + profileCommit + ", profileRollback=" + profileRollback + "]";
    }
}
