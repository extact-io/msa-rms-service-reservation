package io.extact.msa.rms.reservation.it.external.stub;

import io.extact.msa.rms.platform.test.stub.UserAccountMemoryStub;
import io.extact.msa.rms.reservation.external.UserAccountCheckApi;

public class UserAccountCheckApiLocalStub implements UserAccountCheckApi {

    private UserAccountMemoryStub stub = new UserAccountMemoryStub();

    public void init() {
        stub.init();
    }

    @Override
    public boolean exists(int userId) {
        return stub.exists(userId);
    }
}
