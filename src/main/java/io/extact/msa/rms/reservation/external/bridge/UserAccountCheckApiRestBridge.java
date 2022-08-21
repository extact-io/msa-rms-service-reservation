package io.extact.msa.rms.reservation.external.bridge;

import static io.extact.msa.rms.reservation.external.ApiType.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;
import io.extact.msa.rms.reservation.external.UserAccountCheckApi;
import io.extact.msa.rms.reservation.external.restclient.UserAccountCheckApiRestClient;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = PROP_NAME, value = REAL)
public class UserAccountCheckApiRestBridge implements UserAccountCheckApi {

    private UserAccountCheckApiRestClient client;

    @Inject
    public UserAccountCheckApiRestBridge(@RestClient UserAccountCheckApiRestClient client) {
        this.client = client;
    }

    @Override
    public boolean exists(int userId) {
        return client.exists(userId);
    }
}
