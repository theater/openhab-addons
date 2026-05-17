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
package org.openhab.binding.paradoxalarm.internal.parsers;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.paradoxalarm.internal.communication.messages.LiveEvent;
import org.openhab.binding.paradoxalarm.internal.model.ParadoxPanel;
import org.openhab.binding.paradoxalarm.internal.model.Partition;
import org.openhab.binding.paradoxalarm.internal.model.PartitionState;
import org.openhab.binding.paradoxalarm.internal.model.Zone;
import org.openhab.binding.paradoxalarm.internal.model.ZoneState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link EvoLiveEventParser} maps incoming EVO live events to zone and partition model state changes.
 *
 * Only the core event codes that drive the most visible real-time state transitions are handled:
 * <ul>
 * <li>Major 0/1 — zone closed/opened</li>
 * <li>Major 2 — zone tampered</li>
 * <li>Major 9-12 — partition armed</li>
 * <li>Major 13-18, 22 — partition disarmed</li>
 * <li>Major 24 — zone in alarm (also marks partition as triggered)</li>
 * <li>Major 26 — zone alarm restored</li>
 * </ul>
 *
 * @author Konstantin Polihronov - Initial contribution
 */
@NonNullByDefault
public class EvoLiveEventParser {

    private final Logger logger = LoggerFactory.getLogger(EvoLiveEventParser.class);

    /**
     * Applies the state change described by {@code event} to the relevant zone or partition in {@code panel}.
     *
     * @return true if any model state was changed, false if the event was unknown or the target entity was not found
     */
    public boolean applyToModel(LiveEvent event, ParadoxPanel panel) {
        int major = event.getMajor();
        int minor = event.getMinor();
        int partitionId = event.getPartition();

        logger.debug("Live event: {}", event);

        switch (major) {
            case 0:
                return updateZone(panel, minor, z -> z.setOpened(false));
            case 1:
                return updateZone(panel, minor, z -> z.setOpened(true));
            case 2:
                return updateZone(panel, minor, z -> z.setTampered(true));
            case 9:
            case 10:
            case 11:
            case 12:
                return updatePartition(panel, partitionId, p -> {
                    p.setArmed(true);
                    p.setInAlarm(false);
                });
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 22:
                return updatePartition(panel, partitionId, p -> {
                    p.setArmed(false);
                    p.setInAlarm(false);
                    p.setInAudibleAlarm(false);
                    p.setInSilentAlarm(false);
                });
            case 24: {
                boolean zoneChanged = updateZone(panel, minor, z -> {
                    z.setPresentlyInAlarm(true);
                    z.setGeneratedAlarm(true);
                });
                boolean partitionChanged = updatePartition(panel, partitionId, p -> {
                    p.setInAlarm(true);
                    p.setInAudibleAlarm(true);
                    p.setHasAarmInMemory(true);
                });
                return zoneChanged || partitionChanged;
            }
            case 26:
                return updateZone(panel, minor, z -> {
                    z.setPresentlyInAlarm(false);
                    z.setGeneratedAlarm(false);
                });
            default:
                logger.debug("Unhandled live event major={}", major);
                return false;
        }
    }

    private boolean updateZone(ParadoxPanel panel, int zoneId, java.util.function.Consumer<ZoneState> updater) {
        List<Zone> zones = panel.getZones();
        int index = zoneId - 1;
        if (index < 0 || index >= zones.size()) {
            logger.debug("Zone id={} out of range (size={})", zoneId, zones.size());
            return false;
        }
        Zone zone = zones.get(index);
        ZoneState state = zone.getZoneState();
        if (state == null) {
            state = new ZoneState(false, false, false);
            zone.setZoneState(state);
        }
        updater.accept(state);
        return true;
    }

    private boolean updatePartition(ParadoxPanel panel, int partitionId,
            java.util.function.Consumer<PartitionState> updater) {
        List<Partition> partitions = panel.getPartitions();
        int index = partitionId - 1;
        if (index < 0 || index >= partitions.size()) {
            logger.debug("Partition id={} out of range (size={})", partitionId, partitions.size());
            return false;
        }
        Partition partition = partitions.get(index);
        PartitionState state = partition.getState();
        if (state == null) {
            state = new PartitionState();
            partition.setState(state);
        }
        updater.accept(state);
        return true;
    }
}
