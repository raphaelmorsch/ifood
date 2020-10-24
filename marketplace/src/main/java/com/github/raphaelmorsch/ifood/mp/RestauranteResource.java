package com.github.raphaelmorsch.ifood.mp;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;

@Path("/restaurantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestauranteResource {

	@Inject
	PgPool pgPool;

	@GET
	@Path("{id}/pratos")
	public Multi<PratoDTO> buscarPratosPorRestaurante(@PathParam("id") Long restauranteId) {

		return Prato.findAllByRestaurante(pgPool, restauranteId);
	}

}
