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
package org.openhab.binding.paradoxalarm.internal.communication.messages;

import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link LiveEvent} represents an unsolicited 38-byte EVO live event packet emitted by the panel.
 *
 * Layout (serial payload, after IP header is stripped):
 * 
 * <pre>
 *  0      command nibble (0xE) | status flags
 *  1      0xFF — unsolicited live event marker
 *  2-3    event number (big-endian)
 *  4-9    timestamp (6 bytes, bit-packed — not parsed)
 * 10      major event code
 * 11      [partition 4 bits | minor high 2 bits | minor2 high 2 bits]
 * 12      minor low byte
 * 13      minor2 low byte
 * 14-17   module serial (not parsed)
 * 18      label type
 * 19      spare
 * 20-35   element label (16 bytes ASCII)
 * 36      checksum
 * </pre>
 *
 * @author Konstantin Polihronov - Initial contribution
 */
@NonNullByDefault
public class LiveEvent {

    private static final int MIN_LENGTH = 37;
    private static final int LABEL_OFFSET = 20;
    private static final int LABEL_LENGTH = 16;

    private final int major;
    private final int minor;
    private final int partition;
    private final String label;

    private LiveEvent(int major, int minor, int partition, String label) {
        this.major = major;
        this.minor = minor;
        this.partition = partition;
        this.label = label;
    }

    /**
     * Parses a raw 38-byte EVO live event payload into a {@link LiveEvent}.
     *
     * @param raw the raw bytes (at least 37 bytes)
     * @return parsed event, or null if the packet is too short or not a live event
     */
    public static @org.eclipse.jdt.annotation.Nullable LiveEvent parse(byte[] raw) {
        if (raw == null || raw.length < MIN_LENGTH) {
            return null;
        }
        if ((raw[0] & 0xF0) != 0xE0) {
            return null;
        }

        int major = raw[10] & 0xFF;
        int byte11 = raw[11] & 0xFF;
        int minor = ((byte11 >> 6) << 8) | (raw[12] & 0xFF);
        int partition = byte11 & 0x0F;

        String label = new String(raw, LABEL_OFFSET, LABEL_LENGTH, StandardCharsets.US_ASCII).trim();

        return new LiveEvent(major, minor, partition, label);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPartition() {
        return partition;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "LiveEvent[major=" + major + ", minor=" + minor + ", partition=" + partition + ", label=" + label + "]";
    }
}
