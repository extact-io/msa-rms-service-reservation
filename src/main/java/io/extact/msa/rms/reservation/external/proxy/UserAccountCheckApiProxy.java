package io.extact.msa.rms.reservation.external.proxy;

import static io.extact.msa.rms.reservation.external.ApiType.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;
import io.extact.msa.rms.reservation.external.UserAccountCheckApi;
import io.extact.msa.rms.reservation.external.restclient.UserAccountCheckApiRestClient;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = PROP_NAME, value = REAL)
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
