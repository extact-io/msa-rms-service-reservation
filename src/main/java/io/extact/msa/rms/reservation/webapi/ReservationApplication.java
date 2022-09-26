package io.extact.msa.rms.reservation.webapi;

import java.util.Set;

import io.extact.msa.rms.platform.core.jwt.login.PropagatedLoginHeaderRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.server.RmsApplication;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReservationApplication extends RmsApplication {

    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                    PropagatedLoginHeaderRequestFilter.class,
                    ReservationResourceImpl.class
                );
    }
}
