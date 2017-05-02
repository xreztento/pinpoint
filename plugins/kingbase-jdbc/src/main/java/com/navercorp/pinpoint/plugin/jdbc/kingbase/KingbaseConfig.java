/*
 * Copyright 2014 NAVER Corp.
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
 */
package com.navercorp.pinpoint.plugin.jdbc.kingbase;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

/**
 * @author Brad Hong
 *
 */
public class KingbaseConfig {
    private final boolean pluginEnable;
    private final boolean profileSetAutoCommit;
    private final boolean profileCommit;
    private final boolean profileRollback;
    private final int maxSqlBindValueSize;

    public KingbaseConfig(ProfilerConfig config) {
        this.pluginEnable = config.readBoolean("profiler.jdbc.kingbase", false);
        this.profileSetAutoCommit = config.readBoolean("profiler.jdbc.kingbase.setautocommit", false);
        this.profileCommit = config.readBoolean("profiler.jdbc.kingbase.commit", false);
        this.profileRollback = config.readBoolean("profiler.jdbc.kingbase.rollback", false);
        this.maxSqlBindValueSize = config.readInt("profiler.jdbc.maxsqlbindvaluesize", 1024);
    }

    public boolean isPluginEnable() {
        return pluginEnable;
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
    
    public int getMaxSqlBindValueSize() {
        return maxSqlBindValueSize;
    }
    
    @Override
    public String toString() {
        return "KingbaseConfig [pluginEnable="+ pluginEnable + ",profileSetAutoCommit=" + profileSetAutoCommit + ", profileCommit=" + profileCommit + ", profileRollback=" + profileRollback + ", maxSqlBindValueSize=" + maxSqlBindValueSize + "]";
    }
}
