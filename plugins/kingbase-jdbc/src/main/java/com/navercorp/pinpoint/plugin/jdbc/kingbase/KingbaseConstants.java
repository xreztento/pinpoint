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

import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;

import static com.navercorp.pinpoint.common.trace.ServiceTypeProperty.*;

/**
 * @author Brad Hong
 *
 */
public final class KingbaseConstants {
    private KingbaseConstants() {
    }

    public static final String KINGBASE_SCOPE = "KINGBASE_JDBC";
    
    public static final ServiceType KINGBASE = ServiceTypeFactory.of(2190, "KINGBASE", TERMINAL, INCLUDE_DESTINATION_ID);
    public static final ServiceType KINGBASE_EXECUTE_QUERY = ServiceTypeFactory.of(2191, "KINGBASE_EXECUTE_QUERY", "KINGBASE", TERMINAL, RECORD_STATISTICS, INCLUDE_DESTINATION_ID);
}
