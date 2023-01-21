package io.extact.msa.rms.reservation.webapi;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;

import io.extact.msa.rms.platform.fw.login.LoginUserFromHttpHeadersRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.RmsApplication;

@ApplicationPath("api")
@ApplicationScoped
public class ReservationApplication extends RmsApplication {

    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                    LoginUserFromHttpHeadersRequestFilter.class,
                    ReservationResourceImpl.class
                );
    }
}
