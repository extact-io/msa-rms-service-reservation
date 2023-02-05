package io.extact.msa.rms.reservation.external.proxy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.reservation.external.RentalItemCheckApi;
import io.extact.msa.rms.reservation.external.restclient.RentalItemCheckApiRestClient;

@ApplicationScoped
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
