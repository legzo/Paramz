package com.orange.ccmd.paramz;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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

	@PUT
	@Path("/{key}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response setClusterLevel(@PathParam("key") String key, String value) {
		try {
			config.setParamClusterLevel(key, value);
			return Response.ok().build();
		} catch (ConfigurationException e) {
			return Response.serverError().build();
		}
	}

	@PUT
	@Path("/{key}/node")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response setNodeLevel(@PathParam("key") String key, String value) {
		try {
			config.setParamNodeLevel(key, value);
			return Response.ok().build();
		} catch (ConfigurationException e) {
			return Response.serverError().build();
		}
	}

}