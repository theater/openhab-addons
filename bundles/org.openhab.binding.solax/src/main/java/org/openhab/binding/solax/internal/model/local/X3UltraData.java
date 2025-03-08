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
package org.openhab.binding.solax.internal.model.local;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.solax.internal.connectivity.rawdata.local.LocalConnectRawDataBean;
import org.openhab.binding.solax.internal.util.ByteUtil;

/**
 * The {@link X3UltraData} is an implementation of the single phased inverter data interface for X3 Ultra
 * inverter.
 *
 * @author Vaclav Cejka - Initial contribution
 */
@NonNullByDefault
public class X3UltraData extends CommonLocalDeviceData {

    public X3UltraData(LocalConnectRawDataBean data) {
        super(data);
    }

    // Inverter data

    @Override
    public double getVoltagePhase1() {
        return ((double) getFromRawData(0)) / 10;
    }

    @Override
    public double getVoltagePhase2() {
        return ((double) getFromRawData(1)) / 10;
    }

    @Override
    public double getVoltagePhase3() {
        return ((double) getFromRawData(2)) / 10;
    }

    @Override
    public double getCurrentPhase1() {
        return ((double) getFromRawData(3)) / 10;
    }

    @Override
    public double getCurrentPhase2() {
        return ((double) getFromRawData(4)) / 10;
    }

    @Override
    public double getCurrentPhase3() {
        return ((double) getFromRawData(5)) / 10;
    }

    @Override
    public short getOutputPowerPhase1() {
        return getFromRawData(6);
    }

    @Override
    public short getOutputPowerPhase2() {
        return getFromRawData(7);
    }

    @Override
    public short getOutputPowerPhase3() {
        return getFromRawData(8);
    }

    @Override
    public double getPV1Voltage() {
        return ((double) getFromRawData(10)) / 10;
    }

    @Override
    public double getPV2Voltage() {
        return ((double) getFromRawData(11)) / 10;
    }

    // new not defined yet
    // @Override
    // public double getPV3Voltage() {
    // return ((double) getFromRawData(129)) / 10;
    // }

    @Override
    public double getPV1Current() {
        return ((double) getFromRawData(12)) / 10;
    }

    @Override
    public double getPV2Current() {
        return ((double) getFromRawData(13)) / 10;
    }

    // new not defined yet
    // @Override
    // public double getPV3Current() {
    // return ((double) getFromRawData(130)) / 10;
    // }

    @Override
    public short getPV1Power() {
        return getFromRawData(14);
    }

    @Override
    public short getPV2Power() {
        return getFromRawData(15);
    }

    // new not defined yet
    // @Override
    // public short getPV3Power() {
    // return getFromRawData(131);
    // }

    @Override
    public double getFrequencyPhase1() {
        return ((double) getFromRawData(16)) / 100;
    }

    @Override
    public double getFrequencyPhase2() {
        return ((double) getFromRawData(17)) / 100;
    }

    @Override
    public double getFrequencyPhase3() {
        return ((double) getFromRawData(18)) / 100;
    }

    // Battery

    @Override
    public double getBatteryVoltage() {
        return ((double) getFromRawData(39)) / 10;
    }

    // new not defined yet
    // @Override
    // public double getBattery2Voltage() {
    // return ((double) getFromRawData(132)) / 10;
    // }

    @Override
    public double getBatteryCurrent() {
        return ((double) getFromRawData(40)) / 100;
    }

    // new not defined yet
    // @Override
    // public double getBattery2Current() {
    // return ((double) getFromRawData(133)) / 100;
    // }

    @Override
    public short getBatteryPower() {
        return getFromRawData(41);
    }

    // new not defined yet
    // @Override
    // public short getBattery2Power() {
    // return getFromRawData(134);
    // }

    @Override
    public short getBatteryTemperature() {
        return getFromRawData(105);
    }

    // new not defined yet
    // @Override
    // public short getBattery2Temperature() {
    // return getFromRawData(142);
    // }

    @Override
    public short getBatteryLevel() {
        return getFromRawData(103);
    }

    // new not defined yet
    // @Override
    // public short getBattery2Level() {
    // return getFromRawData(140);
    // }

    // Feed in power

    @Override
    public int getFeedInPower() {
        return ByteUtil.read32BitSigned(getFromRawData(34), getFromRawData(35));
    }

    // Totals

    @Override
    public short getPowerUsage() {
        return getFromRawData(47);
    }

    @Override
    public double getTotalEnergy() {
        return ((double) ByteUtil.read32BitSigned(getFromRawData(58), getFromRawData(59))) / 10;
    }

    @Override
    public double getTotalBatteryDischargeEnergy() {
        return ((double) ByteUtil.read32BitSigned(getFromRawData(74), getFromRawData(75))) / 10;
    }

    @Override
    public double getTotalBatteryChargeEnergy() {
        return ((double) ByteUtil.read32BitSigned(getFromRawData(76), getFromRawData(77))) / 10;
    }

    @Override
    public double getTotalPVEnergy() {
        return ((double) ByteUtil.read32BitSigned(getFromRawData(80), getFromRawData(81))) / 10;
    }

    @Override
    public double getTotalFeedInEnergy() {
        return ((double) ByteUtil.read32BitSigned(getFromRawData(86), getFromRawData(87))) / 100;
    }

    @Override
    public double getTotalConsumption() {
        return ((double) ByteUtil.read32BitSigned(getFromRawData(88), getFromRawData(89))) / 100;
    }

    @Override
    public double getTodayEnergy() {
        return ((double) getFromRawData(70)) / 10;
    }

    @Override
    public double getTodayFeedInEnergy() {
        return ((double) ByteUtil.read32BitSigned(getFromRawData(90), getFromRawData(91))) / 100;
    }

    @Override
    public double getTodayConsumption() {
        return ((double) ByteUtil.read32BitSigned(getFromRawData(92), getFromRawData(93))) / 100;
    }

    @Override
    public double getTodayBatteryDischargeEnergy() {
        return ((double) getFromRawData(78)) / 10;
    }

    @Override
    public double getTodayBatteryChargeEnergy() {
        return ((double) getFromRawData(79)) / 10;
    }

    @Override
    public short getInverterWorkModeCode() {
        return getFromRawData(19);
    }
}
