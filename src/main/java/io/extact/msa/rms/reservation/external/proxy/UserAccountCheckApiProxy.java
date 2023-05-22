package io.extact.msa.rms.reservation.external.proxy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.platform.fw.exception.RmsNetworkConnectionException;
import io.extact.msa.rms.platform.fw.exception.interceptor.NetworkConnectionErrorAware;
import io.extact.msa.rms.reservation.external.UserAccountCheckApi;
import io.extact.msa.rms.reservation.external.restclient.UserAccountCheckApiRestClient;

@ApplicationScoped
@NetworkConnectionErrorAware
@CircuitBreaker(
        requestVolumeThreshold = 4, 
        failureRatio=0.5, 
        delay = 10000, 
        successThreshold = 3,
        failOn = RmsNetworkConnectionException.class)
public class UserAccountCheckApiProxy implements UserAccountCheckApi {

    private UserAccountCheckApiRestClient client;

    @Inject
    public UserAccountCheckApiProxy(@RestClient UserAccountCheckApiRestClient client) {
        this.client = client;
    }

    @Override
    public boolean exists(int userId) {
        return client.exists(userId);
    }
}
