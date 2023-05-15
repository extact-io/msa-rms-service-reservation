package io.extact.msa.rms.reservation.external.proxy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.platform.fw.exception.interceptor.NetworkConnectionErrorAware;
import io.extact.msa.rms.reservation.external.UserAccountCheckApi;
import io.extact.msa.rms.reservation.external.restclient.UserAccountCheckApiRestClient;

@ApplicationScoped
@NetworkConnectionErrorAware
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
