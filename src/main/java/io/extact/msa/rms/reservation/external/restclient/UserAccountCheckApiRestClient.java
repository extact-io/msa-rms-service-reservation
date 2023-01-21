package io.extact.msa.rms.reservation.external.restclient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.fw.external.PropagateLoginUserClientHeadersFactory;
import io.extact.msa.rms.platform.fw.external.PropagateResponseExceptionMapper;

@RegisterRestClient(configKey = "web-api-user")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(PropagateResponseExceptionMapper.class)
@RegisterClientHeaders(PropagateLoginUserClientHeadersFactory.class)
@Path("api/users")
public interface UserAccountCheckApiRestClient {
    @GET
    @Path("/exists/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    boolean exists(@PathParam("userId") Integer userId);
}
