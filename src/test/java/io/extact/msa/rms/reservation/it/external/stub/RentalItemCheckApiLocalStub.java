package io.extact.msa.rms.reservation.it.external.stub;

import io.extact.msa.rms.platform.test.stub.RentalItemMemoryStub;
import io.extact.msa.rms.reservation.external.RentalItemCheckApi;

public class RentalItemCheckApiLocalStub implements RentalItemCheckApi {

    private RentalItemMemoryStub stub = new RentalItemMemoryStub();

    public void init() {
        stub.init();
    }

    @Override
    public boolean exists(int itemId) {
        return stub.exists(itemId);
    }
}
