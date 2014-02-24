package com.orange.ccmd.paramz;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParamzEndpoint {

	@Autowired
	private Paramz config;

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllParamz() {
		return Response.ok(config.getAll()).build();
	}

	@POST
	@Path("/{key}")
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	public Response setClusterLevel(@PathParam("key") String key,
			String value) {
		config.setParamClusterLevel(key, value);
		return Response.ok().build();
	}

	@POST
	@Path("/persist")
	public Response persistClusterLevel() {
		try {
			config.persistClusterLevel();
			return Response.ok().build();
		} catch (ConfigurationException e) {
			return Response.serverError().build();
		}
	}
}
