package org.openhab.binding.solax.internal.handlers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.openhab.binding.solax.internal.SolaxConfiguration;
import org.openhab.binding.solax.internal.connectivity.LocalHttpConnector;
import org.openhab.binding.solax.internal.connectivity.SolaxConnector;
import org.openhab.binding.solax.internal.connectivity.rawdata.local.LocalConnectRawDataBean;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SolaxLocalAccessAbstractHandler extends AbstractSolaxHandler {
    private final Logger logger = LoggerFactory.getLogger(SolaxLocalAccessAbstractHandler.class);

    protected final Set<String> unsupportedExistingChannels = new HashSet<>();
    protected boolean alreadyRemovedUnsupportedChannels;

    public SolaxLocalAccessAbstractHandler(Thing thing, TranslationProvider i18nProvider,
            TimeZoneProvider timeZoneProvider) {
        super(thing, i18nProvider, timeZoneProvider);
    }

    @Override
    protected SolaxConnector createConnector(SolaxConfiguration config) {
        return new LocalHttpConnector(config.password, config.hostname);
    }

    protected LocalConnectRawDataBean parseJson(String rawJsonData) {
        LocalConnectRawDataBean fromJson = LocalConnectRawDataBean.fromJson(rawJsonData);
        logger.debug("Received a new inverter JSON object. Data = {}", fromJson.toString());
        return fromJson;
    }

    protected void removeUnsupportedChannels(Set<String> supportedChannels) {
        if (supportedChannels.isEmpty()) {
            return;
        }
        List<Channel> channels = getThing().getChannels();
        List<Channel> channelsToRemove = channels.stream()
                .filter(channel -> !supportedChannels.contains(channel.getUID().getId())).toList();

        if (!channelsToRemove.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logRemovedChannels(channelsToRemove);
            }
            updateThing(editThing().withoutChannels(channelsToRemove).build());
        }
    }

    private void logRemovedChannels(List<Channel> channelsToRemove) {
        List<String> channelsToRemoveForLog = channelsToRemove.stream().map(channel -> channel.getUID().getId())
                .toList();
        logger.debug("Detected unsupported channels for the current inverter. Channels to be removed: {}",
                channelsToRemoveForLog);
    }

    protected <T extends Quantity<T>> void updateChannel(String channelID, double value, Unit<T> unit,
            Set<String> supportedChannels) {
        if (supportedChannels.contains(channelID)) {
            if (value > Short.MIN_VALUE) {
                updateState(channelID, new QuantityType<>(value, unit));
            } else if (!unsupportedExistingChannels.contains(channelID)) {
                updateState(channelID, UnDefType.UNDEF);
                unsupportedExistingChannels.add(channelID);
                logger.warn(
                        "Channel {} is marked as supported, but its value is out of the defined range. Value = {}. This is unexpected behaviour. Please file a bug.",
                        channelID, value);
            }
        }
    }
}
