package io.extact.msa.rms.reservation.it.external.stub;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;

import io.extact.msa.rms.platform.test.stub.RentalItemMemoryStub;
import io.extact.msa.rms.reservation.external.restclient.RentalItemCheckApiRestClient;

@ApplicationScoped
@Path("items")
public class RentalItemCheckApiRemoteStub implements RentalItemCheckApiRestClient {

    private RentalItemMemoryStub stub = new RentalItemMemoryStub();

    @PostConstruct
    public void init() {
        stub.init();
    }

    @Override
    public boolean exists(Integer itemId) {
        return stub.exists(itemId);
    }
}
