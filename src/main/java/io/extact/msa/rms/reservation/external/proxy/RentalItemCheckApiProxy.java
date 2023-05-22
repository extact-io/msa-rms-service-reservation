package io.extact.msa.rms.reservation.external.proxy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.platform.fw.exception.RmsNetworkConnectionException;
import io.extact.msa.rms.platform.fw.exception.interceptor.NetworkConnectionErrorAware;
import io.extact.msa.rms.reservation.external.RentalItemCheckApi;
import io.extact.msa.rms.reservation.external.restclient.RentalItemCheckApiRestClient;

@ApplicationScoped
@NetworkConnectionErrorAware
@CircuitBreaker(
        requestVolumeThreshold = 4, 
        failureRatio=0.5, 
        delay = 10000, 
        successThreshold = 3,
        failOn = RmsNetworkConnectionException.class)
public class RentalItemCheckApiProxy implements RentalItemCheckApi {

    private RentalItemCheckApiRestClient client;

    @Inject
    public RentalItemCheckApiProxy(@RestClient RentalItemCheckApiRestClient client) {
        this.client = client;
    }

    @Override
    public boolean exists(int itemId) {
        return client.exists(itemId);
    }
}
