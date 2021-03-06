package com.ilibellus.factory;


import android.content.Context;

import io.nlopez.smartlocation.location.LocationProvider;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider;

public class GeocodeProviderFactory {
    public static LocationProvider getProvider(Context context) {
        return new LocationGooglePlayServicesWithFallbackProvider(context);
    }
}
