/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.solax.internal.local;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.solax.internal.connectivity.rawdata.local.LocalConnectRawDataBean;
import org.openhab.binding.solax.internal.model.InverterType;
import org.openhab.binding.solax.internal.model.local.LocalInverterData;
import org.openhab.binding.solax.internal.model.local.parsers.RawDataParser;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The {@link TestEVChargerParser} Simple test that tests for proper parsing against a real data from the inverter
 *
 * @author Konstantin Polihronov - Initial contribution
 */
@NonNullByDefault
public class TestEVChargerParser {

    private static final String RAW_DATA = """
            {
                "SN":"SQXXXXXXXX",
                "ver":"3.004.11",
                "type":1,
                "Data":[
                    0,0,24354,24316,23945,0,0,0,0,0,
                    0,0,0,0,302,0,101,35463,65455,0,
                    3,0,64730,0,31,0,0,15,0,0,
                    0,0,0,4992,4992,4988,4864,789,6150,1,
                    0,0,0,0,0,0,0,0,1,100,
                    0,0,0,0,0,0,0,0,0,0,
                    0,0,0,0,0,0,0,0,0,0,
                    0,0,0,0,0,0,0,0,0,0,
                    0,0,1836,775,6150,0,1,1,1,0,0,120,1205,1000,0,50,0,0,1,1,0],
                "Information":[11.000,1,"C3XXXXXXXXX",1,1.13,1.01,0.00,0.00,0.00,1],
                "OCPPServer":"testserver",
                "OCPPChargerId":"ocpchargerid"
            }
            """;
    private static final String RAW_DATA_SECOND = """
            {
                "SN":"SQXXXXXXXXXX",
                "ver":"3.004.11",
                "type":1,
                "Data":[
                    2,2,23914,23991,23895,1517,1513,1519,3654,3657,
                    3656,10968,44,0,346,0,65434,35463,65459,65508,
                    65513,27,402,0,43,0,2,15,0,0,
                    0,0,0,5004,5000,4996,10518,1547,6150,4,
                    0,0,0,0,0,0,0,0,1,100,
                    0,0,0,0,0,0,0,0,0,0,
                    0,0,0,0,0,0,0,0,0,0,
                    0,0,0,0,0,0,0,0,0,0,
                    1717,0,3114,1547,6150,0,1,1,1,0,0,121,584,266,0,50,0,0,1,1,0],
                "Information":[11.000,1,"CXXXXXXXXXX",1,1.13,1.01,0.00,0.00,0.00,1],
                "OCPPServer":"",
                "OCPPChargerId":""
            }
            """;

    @Test
    public void testParser() {
        LocalConnectRawDataBean bean = LocalConnectRawDataBean.fromJson(RAW_DATA);
        assertNotNull(bean);
    }
}
