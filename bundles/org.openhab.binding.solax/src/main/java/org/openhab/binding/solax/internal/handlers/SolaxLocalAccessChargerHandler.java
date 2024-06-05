package org.openhab.binding.solax.internal.handlers;

import org.openhab.binding.solax.internal.SolaxConfiguration;
import org.openhab.binding.solax.internal.connectivity.LocalHttpConnector;
import org.openhab.binding.solax.internal.connectivity.SolaxConnector;
import org.openhab.binding.solax.internal.connectivity.rawdata.local.LocalConnectRawDataBean;
import org.openhab.binding.solax.internal.exceptions.SolaxUpdateException;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.binding.ThingHandler;

public class SolaxLocalAccessChargerHandler extends SolaxLocalAccessAbstractHandler {
    public SolaxLocalAccessChargerHandler(Thing thing, TranslationProvider i18nProvider, TimeZoneProvider timeZoneProvider) {
        super(thing, i18nProvider, timeZoneProvider);
    }

    @Override
    protected void updateFromData(String rawJsonData) throws SolaxUpdateException {
        LocalConnectRawDataBean rawDataBean = parseJson(rawJsonData);
    }
}
