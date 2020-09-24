package com.github.raphaelmorsch.ifood.cadastro;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Path("/restaurantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestauranteResource {

	@GET
	public List<PanacheEntityBase> buscar() {
		return Restaurante.listAll();
	}

	@POST
	@Transactional
	/**
	 * 
	 * @param dto
	 * @param uriInfo
	 * @return returns the response status and the URI for the resource in order to
	 * follow the HATEOAS pattern for RESTful applications (Hypertext as Engine of Application State)
	 */
	public Response adicionar(Restaurante dto, @Context UriInfo uriInfo) {
		dto.persist();
		UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
		uriBuilder.path(Long.toString(dto.id));
		return Response.created(uriBuilder.build()).build();
	}

	@PUT
	@Transactional
	@Path("{id}")
	public void atualizar(@PathParam("id") Long id, Restaurante dto) {

		Optional<Restaurante> restaurante = Restaurante.findByIdOptional(id);

		if (restaurante.isEmpty()) {
			throw new NotFoundException();
		}

		restaurante.get().nome = dto.nome;

		restaurante.get().persist();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	public void remover(@PathParam("id") Long id) {

		Optional<Restaurante> restaurante = Restaurante.findByIdOptional(id);

		restaurante.ifPresentOrElse(Restaurante::delete, () -> {
			throw new NotFoundException();
		});
	}

}