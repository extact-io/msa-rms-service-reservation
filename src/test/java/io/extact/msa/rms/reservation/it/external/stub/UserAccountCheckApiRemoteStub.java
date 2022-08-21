package io.extact.msa.rms.reservation.it.external.stub;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;

import io.extact.msa.rms.platform.test.stub.UserAccountMemoryStub;
import io.extact.msa.rms.reservation.external.restclient.UserAccountCheckApiRestClient;

@ApplicationScoped
@Path("/users")
public class UserAccountCheckApiRemoteStub implements UserAccountCheckApiRestClient {

    private UserAccountMemoryStub stub = new UserAccountMemoryStub();

    @PostConstruct
    public void init() {
        stub.init();
    }

    @Override
    public boolean exists(Integer userId) {
        return stub.exists(userId);
    }
}
