/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
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
package org.openhab.binding.solax.internal.local.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.solax.internal.local.AbstractParserTest;
import org.openhab.binding.solax.internal.model.InverterType;
import org.openhab.binding.solax.internal.model.local.LocalData;

/**
 * The {@link TestX3UltraParser} simple test that tests for proper parsing against a real data from the inverter
 *
 * @author Konstantin Polihronov - Initial contribution
 */
@NonNullByDefault
public class TestX3UltraParser extends AbstractParserTest {

    private static final String RAW_DATA = """
            {
                 "sn":"SNXXXXXXX",
                 "ver":"1.003.11",
                 "type":25,
                 "Data":[
                     2366,2330,2365,124,107,121,2906,2472,2839,161,
                     7260,7170,15,16,1114,1193,5009,5005,5004,2,
                     0,0,0,0,0,0,0,0,0,0,
                     0,0,0,0,0,0,0,0,0,4697,
                     64526,60747,4698,65433,0,1,34,0,256,14594,
                     268,6402,100,0,39,8003,0,0,7586,1,
                     0,0,0,0,0,0,0,0,0,0,
                     59,180,0,0,24691,0,23304,0,2,60,
                     2710,1,121,2,0,0,2703,0,20700,8,
                     1,0,1032,0,0,0,0,0,0,0,
                     0,0,1,59,1,21,135,256,5256,3600,
                     310,350,226,207,33,33,101,3268,3260,57271,
                     73,0,0,0,0,0,0,0,0,7875,
                     14,1122,0,0,0,0,0,0,0,1,
                     0,0,0,0,0,0,0,0,0,0,
                     0,0,0,0,0,0,60747,65535,59,8217,
                     0,0,0,60698,65535,0,0,0,0,21302,
                     14389,19269,12595,20531,12611,14640,0,0,0,0,
                     0,0,0,0,0,512,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"Information":[30.000,25,"H3BC30K3065001",13,16.15,0.00,17.12,0.09,0.00,1]}
             """;

    @Override
    protected InverterType getInverterType() {
        return InverterType.X3_ULTRA;
    }

    @Override
    protected void assertParserSpecific(LocalData data) {
        assertEquals("SNXXXXXXX", data.getWifiSerial());
        assertEquals("1.003.11", data.getWifiVersion());

        assertEquals(236.6, data.getVoltagePhase1()); // [0]
        assertEquals(233.0, data.getVoltagePhase2()); // [1]
        assertEquals(236.5, data.getVoltagePhase3()); // [2]

        assertEquals(12.4, data.getCurrentPhase1()); // [3]
        assertEquals(10.7, data.getCurrentPhase2()); // [4]
        assertEquals(12.1, data.getCurrentPhase3()); // [5]

        assertEquals(2906, data.getOutputPowerPhase1()); // [6]
        assertEquals(2472, data.getOutputPowerPhase2()); // [7]
        assertEquals(2839, data.getOutputPowerPhase3()); // [8]

        assertEquals(726.0, data.getPV1Voltage()); // [10]
        assertEquals(717.0, data.getPV2Voltage()); // [11]
        assertEquals(787.5, data.getPV3Voltage()); // [129]
        assertEquals(1.5, data.getPV1Current()); // [12]
        assertEquals(1.6, data.getPV2Current()); // [13]
        assertEquals(1.4, data.getPV3Current()); // [130]
        assertEquals(1114, data.getPV1Power()); // [14]
        assertEquals(1193, data.getPV2Power()); // [15]
        assertEquals(1122, data.getPV3Power()); // [131]

        assertEquals(50.09, data.getFrequencyPhase1()); // [16]
        assertEquals(50.05, data.getFrequencyPhase2()); // [17]
        assertEquals(50.04, data.getFrequencyPhase3()); // [18]

        assertEquals(2, data.getInverterWorkModeCode()); // [19]
        assertEquals("2", data.getInverterWorkMode()); // [19]

        // TODO check with the guy
        assertEquals(0, data.getFeedInPower()); // [34][35]

        assertEquals(469.7, data.getBatteryVoltage()); // [39]
        assertEquals(-10.1, data.getBatteryCurrent()); // [40]
        assertEquals(-4789, data.getBatteryPower()); // [41]
        assertEquals(59, data.getBatteryLevel()); // [103]
        assertEquals(21, data.getBatteryTemperature()); // [105]

        // No real data for the battery 2 unfortunately
        assertEquals(0, data.getBattery2Voltage()); // [132]
        assertEquals(0, data.getBattery2Current()); // [133]
        assertEquals(0, data.getBattery2Power()); // [134]
        assertEquals(0, data.getBattery2Level()); // [140]
        assertEquals(0, data.getBattery2Temperature()); // [142]

        // Totals
        assertEquals(0, data.getPowerUsage()); // [47]
        assertEquals(7312.2, data.getTotalEnergy()); // [68][69]
        assertEquals(2469.1, data.getTotalBatteryDischargeEnergy()); // [74][75]
        assertEquals(2330.4, data.getTotalBatteryChargeEnergy()); // [76][77]
        assertEquals(6824.6, data.getTotalPVEnergy()); // [80][81]
        assertEquals(27.03, data.getTotalFeedInEnergy()); // [86][87]
        assertEquals(5449.88, data.getTotalConsumption()); // [88][89]
        assertEquals(5.9, data.getTodayEnergy()); // [70] / 10
        assertEquals(0.01, data.getTodayFeedInEnergy()); // [90][91] / 100
        assertEquals(10.32, data.getTodayConsumption()); // [92][93] / 100
        assertEquals(0.2, data.getTodayBatteryDischargeEnergy()); // [78] / 10
        assertEquals(6, data.getTodayBatteryChargeEnergy()); // [79] / 10
    }

    @Override
    protected String getRawData() {
        return RAW_DATA;
    }
}
