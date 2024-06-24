package org.openhab.binding.solax.internal.handlers;

import org.openhab.binding.solax.internal.connectivity.rawdata.local.LocalConnectRawDataBean;
import org.openhab.binding.solax.internal.exceptions.SolaxUpdateException;
import org.openhab.binding.solax.internal.model.local.LocalInverterData;
import org.openhab.binding.solax.internal.model.local.parsers.EvChargerDataParser;
import org.openhab.binding.solax.internal.model.local.parsers.RawDataParser;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.openhab.core.thing.Thing;

public class SolaxLocalAccessChargerHandler extends SolaxLocalAccessAbstractHandler {

    private static final RawDataParser parser = new EvChargerDataParser();

    public SolaxLocalAccessChargerHandler(Thing thing, TranslationProvider i18nProvider,
            TimeZoneProvider timeZoneProvider) {
        super(thing, i18nProvider, timeZoneProvider);
    }

    @Override
    public void initialize() {
        removeUnsupportedChannels(parser.getSupportedChannels());
        super.initialize();
    }

    @Override
    protected void updateFromData(String rawJsonData) throws SolaxUpdateException {
        LocalConnectRawDataBean rawDataBean = parseJson(rawJsonData);
        LocalInverterData data = parser.getData(rawDataBean);
        updateChannels(parser, data);
        updateProperties(data);
    }

    private void updateProperties(LocalInverterData data) {
    }

    private void updateChannels(RawDataParser parser, LocalInverterData data) {
    }
}
