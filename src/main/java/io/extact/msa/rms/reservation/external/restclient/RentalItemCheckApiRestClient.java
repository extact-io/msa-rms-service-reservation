package io.extact.msa.rms.reservation.external.restclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.core.jwt.client.PropagateLoginClientHeadersFactory;
import io.extact.msa.rms.platform.fw.webapi.client.ExceptionPropagateClientMapper;

@RegisterRestClient(configKey = "web-api-item")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(ExceptionPropagateClientMapper.class)
@RegisterClientHeaders(PropagateLoginClientHeadersFactory.class)
@Path("/items")
public interface RentalItemCheckApiRestClient {
    @GET
    @Path("/exists/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    boolean exists(@PathParam("itemId") Integer itemId);
}
