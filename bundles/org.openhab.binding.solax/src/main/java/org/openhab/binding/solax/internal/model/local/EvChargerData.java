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
package org.openhab.binding.solax.internal.model.local;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.solax.internal.connectivity.rawdata.local.LocalConnectRawDataBean;
import org.openhab.binding.solax.internal.model.InverterType;

/**
 * The {@link EvChargerData} is an abstract class that contains the common information, applicable for all
 * inverters.
 *
 * @author Konstantin Polihronov - Initial contribution
 */
@NonNullByDefault
public class EvChargerData extends CommonLocalInverterData {

    public EvChargerData(LocalConnectRawDataBean bean) {
        super(bean);
    }

    @Override
    public InverterType getInverterType() {
        return InverterType.UNKNOWN;
    }

    public short getDeviceState() {
        return getFromRawData(0);
    }

    public short getDeviceStatMode() {
        return getFromRawData(1);
    }

    public double getEqSingle() {
        return (double) getFromRawData(12) / 10;
    }

    public double getEqTotal() {
        // TODO implement this
        throw new UnsupportedOperationException("Not implemented");
    }

    public short getTotalChargePower() {
        return getFromRawData(11);
    }

    @Override
    public double getVoltagePhase1() {
        return ((double) getFromRawData(2)) / 100;
    }

    @Override
    public double getVoltagePhase2() {
        return ((double) getFromRawData(3)) / 100;
    }

    @Override
    public double getVoltagePhase3() {
        return ((double) getFromRawData(4)) / 100;
    }

    @Override
    public double getCurrentPhase1() {
        return ((double) getFromRawData(5)) / 100;
    }

    @Override
    public double getCurrentPhase2() {
        return ((double) getFromRawData(6)) / 100;
    }

    @Override
    public double getCurrentPhase3() {
        return ((double) getFromRawData(7)) / 100;
    }

    @Override
    public short getOutputPowerPhase1() {
        return getFromRawData(8);
    }

    @Override
    public short getOutputPowerPhase2() {
        return getFromRawData(9);
    }

    @Override
    public short getOutputPowerPhase3() {
        return getFromRawData(10);
    }

    public double getExternalCurrentPhase1() {
        return read16BitSigned(16) / 100;
    }

    public double getExternalCurrentPhase2() {
        return read16BitSigned(17) / (double) 100;
    }

    public double getExternalCurrentPhase3() {
        return read16BitSigned(18) / 100;
    }

    public double getExternalPowerPhase1() {
        return read16BitSigned(19);
    }

    public double getExternalPowerPhase2() {
        return read16BitSigned(20);
    }

    public double getExternalPowerPhase3() {
        return read16BitSigned(21);
    }

    public short getPlugTemperature() {
        return getFromRawData(23);
    }

    public short getInternalTemperature() {
        return getFromRawData(24);
    }

    public short getCPState() {
        return getFromRawData(26);
    }

    // public double getChargingDuration() {
    // }

    private double read32BitSigned(short a, short b) {
        if (a < Short.MAX_VALUE) {
            return b + 65536 * a;
        } else {
            return (double) b + 65536 * a - 0xFFFFFFFF;
        }
    }

    private double read16BitSigned(int index) {
        short a = getFromRawData(index);
        if (a <= Short.MAX_VALUE) {
            return a;
        } else {
            return a - 65536;
        }
    }
}
