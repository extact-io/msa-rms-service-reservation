package io.extact.msa.rms.reservation.it.external.stub;

import java.util.Set;

import io.extact.msa.rms.platform.core.jwt.login.PropagatedLoginHeaderRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.server.RmsApplication;


public class CheckApiRemoteStubApplication extends RmsApplication {
    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                PropagatedLoginHeaderRequestFilter.class,
                RentalItemCheckApiRemoteStub.class,
                UserAccountCheckApiRemoteStub.class
                );
    }
}
