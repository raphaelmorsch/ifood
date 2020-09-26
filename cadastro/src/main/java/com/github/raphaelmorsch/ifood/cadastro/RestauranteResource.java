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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Path("/restaurantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestauranteResource {

	@GET
	@Tag(ref = "restaurante")
	public List<PanacheEntityBase> buscar() {
		return Restaurante.listAll();
	}

	@POST
	@Transactional
	@Tag(ref = "restaurante")
	/**
	 * 
	 * @param dto
	 * @param uriInfo
	 * @return returns the response status and the URI for the resource in order to
	 *         follow the HATEOAS pattern for RESTful applications (Hypertext as
	 *         Engine of Application State)
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
	@Tag(ref = "restaurante")
	public void atualizar(@PathParam("id") Long id, Restaurante dto) {

		Optional<Restaurante> restaurante = Restaurante.findByIdOptional(id);

		if (!restaurante.isPresent()) {
			throw new NotFoundException();
		}

		restaurante.get().nome = dto.nome;

		restaurante.get().persist();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	@Tag(ref = "restaurante")
	public void remover(@PathParam("id") Long id) {

		Optional<Restaurante> restaurante = Restaurante.findByIdOptional(id);

		if (restaurante.isPresent())
			restaurante.get().delete();
		else
			throw new NotFoundException();

	}

	@GET
	@Path("{id}/pratos")
	@Tags(refs = { "prato", "restaurante" })
	public List<Prato> buscarPratos(@PathParam("id") Long restauranteId) {
		Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(restauranteId);
		if (!restauranteOp.isPresent()) {
			throw new NotFoundException("Restaurante não localizado", Response.status(Status.NOT_FOUND).build());
		}
		return Prato.list("restaurante", restauranteOp.get());
	}

	@POST
	@Transactional
	@Path("{restauranteId}/pratos")
	@Tags(refs = { "restaurante", "prato" })
	public Response adicionarPrato(@PathParam("restauranteId") Long restauranteId, Prato dto,
			@Context UriInfo uriInfo) {

		Optional<Restaurante> restaurante = Restaurante.findByIdOptional(restauranteId);

		restaurante.ifPresentOrElse(r -> {
			dto.restaurante = r;
			dto.persist();
		}, () -> {
			throw new NotFoundException();
		});

		UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
		uriBuilder.path(Long.toString(dto.id));
		return Response.created(uriBuilder.build()).build();
	}

	@PUT
	@Transactional
	@Path("{restauranteId}/pratos/{id}")
	@Tags(refs = { "restaurante", "prato" })
	public void atualizarPrato(@PathParam("restauranteId") Long restauranteId, @PathParam("id") Long id, Prato dto) {

		Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(restauranteId);

		if (restauranteOp.isEmpty()) {
			throw new NotFoundException("Restaurante não Localizado");
		}

		Optional<Prato> prato = Prato.findByIdOptional(id);

		if (prato.isEmpty()) {
			throw new NotFoundException();
		}

		prato.get().descricao = dto.descricao;
		prato.get().nome = dto.nome;
		prato.get().preco = dto.preco;
		prato.get().restaurante = restauranteOp.get();

		prato.get().persist();
	}

	@DELETE
	@Transactional
	@Path("{restauranteId}/pratos/{id}")
	@Tags(refs = { "restaurante", "prato" })
	public void removerPrato(@PathParam("restauranteId") Long restauranteId, @PathParam("id") Long id) {

		Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(restauranteId);

		if (restauranteOp.isEmpty()) {
			throw new NotFoundException();
		}

		Optional<Prato> pratoOp = Prato.findByIdOptional(id);

		pratoOp.ifPresentOrElse(Prato::delete, () -> {
			throw new NotFoundException();
		});
	}

}