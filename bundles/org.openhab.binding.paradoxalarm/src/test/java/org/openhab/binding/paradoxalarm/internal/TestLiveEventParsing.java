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
package org.openhab.binding.paradoxalarm.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openhab.binding.paradoxalarm.internal.communication.messages.LiveEvent;
import org.openhab.binding.paradoxalarm.internal.model.ParadoxPanel;
import org.openhab.binding.paradoxalarm.internal.model.Partition;
import org.openhab.binding.paradoxalarm.internal.model.PartitionState;
import org.openhab.binding.paradoxalarm.internal.model.Zone;
import org.openhab.binding.paradoxalarm.internal.model.ZoneState;
import org.openhab.binding.paradoxalarm.internal.parsers.EvoLiveEventParser;

/**
 * The {@link TestLiveEventParsing} tests live event packet parsing and model state updates.
 *
 * @author Konstantin Polihronov - Initial contribution
 */
@NonNullByDefault
public class TestLiveEventParsing {

    // Captured packets from PAI test suite — 37 bytes each (serial payload, IP header stripped)
    // major=0: zone 5 closed, partition=1
    private static final byte[] ZONE_CLOSED = { (byte) 0xe2, (byte) 0xff, (byte) 0xad, 0x06, 0x14, 0x13, 0x01, 0x04,
            0x0e, 0x10, 0x00, 0x01, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x4c, 0x69, 0x76, 0x69, 0x6e, 0x67,
            0x20, 0x72, 0x6f, 0x6f, 0x6d, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0xcc };

    // major=1: zone 5 opened, partition=1
    private static final byte[] ZONE_OPENED = { (byte) 0xe2, (byte) 0xff, (byte) 0xad, 0x06, 0x14, 0x13, 0x01, 0x04,
            0x0e, 0x10, 0x01, 0x01, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x4c, 0x69, 0x76, 0x69, 0x6e, 0x67,
            0x20, 0x72, 0x6f, 0x6f, 0x6d, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0xcd };

    // major=2: zone 5 tampered, partition=1
    private static final byte[] ZONE_TAMPERED = { (byte) 0xe2, (byte) 0xff, (byte) 0xad, 0x06, 0x14, 0x13, 0x01, 0x04,
            0x0e, 0x10, 0x02, 0x01, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x4c, 0x69, 0x76, 0x69, 0x6e, 0x67,
            0x20, 0x72, 0x6f, 0x6f, 0x6d, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0xce };

    // major=24: zone 3 alarm, partition=1
    private static final byte[] ZONE_ALARM = { (byte) 0xe2, (byte) 0xff, 0x1c, (byte) 0xc4, 0x14, 0x13, 0x0b, 0x01,
            0x0f, 0x2c, 0x18, 0x01, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x4f, 0x66, 0x66, 0x69, 0x63, 0x65,
            0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0xd9 };

    // major=26: zone 2 alarm restored, partition=1
    private static final byte[] ZONE_ALARM_RESTORED = { (byte) 0xe2, (byte) 0xff, 0x1c, (byte) 0xd9, 0x14, 0x13, 0x0b,
            0x01, 0x0f, 0x2f, 0x1a, 0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x45, 0x6e, 0x74, 0x72, 0x61,
            0x6e, 0x63, 0x65, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0x96 };

    // major=10: partition 8 armed (user arm)
    private static final byte[] PARTITION_ARMED = { (byte) 0xe2, (byte) 0xff, (byte) 0xe8, 0x60, 0x14, 0x14, 0x03, 0x04,
            0x15, 0x2d, 0x0a, 0x08, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x54, 0x65, 0x73, 0x74, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x58 };

    // major=14: partition 8 disarmed
    private static final byte[] PARTITION_DISARMED = { (byte) 0xe2, (byte) 0xff, (byte) 0xe8, 0x5e, 0x14, 0x14, 0x03,
            0x04, 0x15, 0x2c, 0x0e, 0x08, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x54, 0x65, 0x73, 0x74, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x59 };

    private ParadoxPanel panel = new ParadoxPanel();
    private EvoLiveEventParser parser = new EvoLiveEventParser();

    @BeforeEach
    public void setUp() {
        panel = new ParadoxPanel();
        parser = new EvoLiveEventParser();

        // Populate 10 zones and 8 partitions so index lookups succeed
        List<Zone> zones = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            zones.add(new Zone(panel, i, "Zone " + i));
        }
        panel.setZones(zones);

        List<Partition> partitions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            partitions.add(new Partition(panel, i, "Partition " + i));
        }
        panel.setPartitions(partitions);
    }

    // --- LiveEvent.parse() field extraction ---

    @Test
    public void testParseZoneClosed() {
        LiveEvent event = LiveEvent.parse(ZONE_CLOSED);
        assertNotNull(event);
        assertEquals(0, event.getMajor());
        assertEquals(5, event.getMinor());
        assertEquals(1, event.getPartition());
        assertEquals("Living room", event.getLabel());
    }

    @Test
    public void testParseZoneOpened() {
        LiveEvent event = LiveEvent.parse(ZONE_OPENED);
        assertNotNull(event);
        assertEquals(1, event.getMajor());
        assertEquals(5, event.getMinor());
        assertEquals(1, event.getPartition());
    }

    @Test
    public void testParseZoneTampered() {
        LiveEvent event = LiveEvent.parse(ZONE_TAMPERED);
        assertNotNull(event);
        assertEquals(2, event.getMajor());
        assertEquals(5, event.getMinor());
        assertEquals(1, event.getPartition());
    }

    @Test
    public void testParseZoneAlarm() {
        LiveEvent event = LiveEvent.parse(ZONE_ALARM);
        assertNotNull(event);
        assertEquals(24, event.getMajor());
        assertEquals(3, event.getMinor());
        assertEquals(1, event.getPartition());
        assertEquals("Office", event.getLabel());
    }

    @Test
    public void testParseZoneAlarmRestored() {
        LiveEvent event = LiveEvent.parse(ZONE_ALARM_RESTORED);
        assertNotNull(event);
        assertEquals(26, event.getMajor());
        assertEquals(2, event.getMinor());
        assertEquals(1, event.getPartition());
        assertEquals("Entrance", event.getLabel());
    }

    @Test
    public void testParsePartitionArmed() {
        LiveEvent event = LiveEvent.parse(PARTITION_ARMED);
        assertNotNull(event);
        assertEquals(10, event.getMajor());
        assertEquals(8, event.getPartition());
        assertEquals("Test", event.getLabel());
    }

    @Test
    public void testParsePartitionDisarmed() {
        LiveEvent event = LiveEvent.parse(PARTITION_DISARMED);
        assertNotNull(event);
        assertEquals(14, event.getMajor());
        assertEquals(8, event.getPartition());
    }

    // --- Detection logic ---

    @Test
    public void testDetectionAcceptsLiveEventPacket() {
        assertNotNull(LiveEvent.parse(ZONE_OPENED));
    }

    @Test
    public void testDetectionRejectsShortPacket() {
        assertNull(LiveEvent.parse(new byte[10]));
    }

    @Test
    public void testDetectionRejectsNonEHighNibblePacket() {
        byte[] notLive = ZONE_OPENED.clone();
        notLive[0] = 0x00;
        assertNull(LiveEvent.parse(notLive));
    }

    // --- EvoLiveEventParser.applyToModel() ---

    @Test
    public void testZoneClosedUpdatesState() {
        // Pre-set zone 5 as opened
        Zone zone5 = panel.getZones().get(4);
        ZoneState state = new ZoneState(true, false, false);
        zone5.setZoneState(state);

        LiveEvent event = LiveEvent.parse(ZONE_CLOSED);
        assertNotNull(event);
        boolean changed = parser.applyToModel(event, panel);
        assertTrue(changed);
        ZoneState updated = zone5.getZoneState();
        assertNotNull(updated);
        assertFalse(updated.isOpened());
    }

    @Test
    public void testZoneOpenedUpdatesState() {
        LiveEvent event = LiveEvent.parse(ZONE_OPENED);
        assertNotNull(event);
        boolean changed = parser.applyToModel(event, panel);
        assertTrue(changed);
        ZoneState state = panel.getZones().get(4).getZoneState();
        assertNotNull(state);
        assertTrue(state.isOpened());
    }

    @Test
    public void testZoneTamperedUpdatesState() {
        LiveEvent event = LiveEvent.parse(ZONE_TAMPERED);
        assertNotNull(event);
        boolean changed = parser.applyToModel(event, panel);
        assertTrue(changed);
        ZoneState state = panel.getZones().get(4).getZoneState();
        assertNotNull(state);
        assertTrue(state.isTampered());
    }

    @Test
    public void testZoneAlarmUpdatesZoneAndPartition() {
        LiveEvent event = LiveEvent.parse(ZONE_ALARM);
        assertNotNull(event);
        boolean changed = parser.applyToModel(event, panel);
        assertTrue(changed);

        ZoneState zoneState = panel.getZones().get(2).getZoneState();
        assertNotNull(zoneState);
        assertTrue(zoneState.isPresentlyInAlarm());
        assertTrue(zoneState.isGeneratedAlarm());

        PartitionState partState = panel.getPartitions().get(0).getState();
        assertNotNull(partState);
        assertTrue(partState.isInAlarm());
        assertTrue(partState.isInAudibleAlarm());
        assertTrue(partState.isHasAarmInMemory());
    }

    @Test
    public void testZoneAlarmRestoredClearsFlags() {
        // Pre-set zone 2 as in alarm
        Zone zone2 = panel.getZones().get(1);
        ZoneState state = new ZoneState(false, false, false);
        state.setPresentlyInAlarm(true);
        state.setGeneratedAlarm(true);
        zone2.setZoneState(state);

        LiveEvent event = LiveEvent.parse(ZONE_ALARM_RESTORED);
        assertNotNull(event);
        boolean changed = parser.applyToModel(event, panel);
        assertTrue(changed);
        ZoneState updated = zone2.getZoneState();
        assertNotNull(updated);
        assertFalse(updated.isPresentlyInAlarm());
        assertFalse(updated.isGeneratedAlarm());
    }

    @Test
    public void testPartitionArmedUpdatesState() {
        LiveEvent event = LiveEvent.parse(PARTITION_ARMED);
        assertNotNull(event);
        boolean changed = parser.applyToModel(event, panel);
        assertTrue(changed);
        PartitionState state = panel.getPartitions().get(7).getState();
        assertNotNull(state);
        assertTrue(state.isArmed());
        assertFalse(state.isInAlarm());
    }

    @Test
    public void testPartitionDisarmedUpdatesState() {
        // Pre-set partition 8 as armed and in alarm
        Partition partition8 = panel.getPartitions().get(7);
        PartitionState state = new PartitionState();
        state.setArmed(true);
        state.setInAlarm(true);
        state.setInAudibleAlarm(true);
        partition8.setState(state);

        LiveEvent event = LiveEvent.parse(PARTITION_DISARMED);
        assertNotNull(event);
        boolean changed = parser.applyToModel(event, panel);
        assertTrue(changed);
        PartitionState updated = partition8.getState();
        assertNotNull(updated);
        assertFalse(updated.isArmed());
        assertFalse(updated.isInAlarm());
        assertFalse(updated.isInAudibleAlarm());
        assertFalse(updated.isInSilentAlarm());
    }

    @Test
    public void testOutOfRangeZoneReturnsFalse() {
        // minor=99, no zone at index 98 in our 10-zone panel
        byte[] raw = ZONE_OPENED.clone();
        raw[12] = 99;
        LiveEvent event = LiveEvent.parse(raw);
        assertNotNull(event);
        assertFalse(parser.applyToModel(event, panel));
    }
}
