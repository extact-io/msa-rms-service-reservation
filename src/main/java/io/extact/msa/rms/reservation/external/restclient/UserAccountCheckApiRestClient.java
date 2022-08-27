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
import io.extact.msa.rms.platform.core.jwt.client.PropagateLoginClientHeadersFactory;
import io.extact.msa.rms.platform.fw.webapi.client.ExceptionPropagateClientMapper;

@RegisterRestClient(configKey = "web-api-user")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(ExceptionPropagateClientMapper.class)
@RegisterClientHeaders(PropagateLoginClientHeadersFactory.class)
@Path("/users")
public interface UserAccountCheckApiRestClient {
    @GET
    @Path("/exists/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    boolean exists(@PathParam("userId") Integer userId);
}
