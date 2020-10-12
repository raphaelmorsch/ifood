package com.github.raphaelmorsch.ifood.cadastro;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
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

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import com.github.raphaelmorsch.ifood.cadastro.dto.AdicionarPratoDTO;
import com.github.raphaelmorsch.ifood.cadastro.dto.AdicionarRestauranteDTO;
import com.github.raphaelmorsch.ifood.cadastro.dto.PratoMapper;
import com.github.raphaelmorsch.ifood.cadastro.dto.RestauranteDTO;
import com.github.raphaelmorsch.ifood.cadastro.dto.RestauranteMapper;
import com.github.raphaelmorsch.ifood.cadastro.infra.ConstraintViolationResponse;

@Path("/restaurantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("proprietario")
@SecurityScheme(securitySchemeName = "ifood-oauth", type = SecuritySchemeType.OAUTH2, 
	flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8081/auth/realms/ifood/protocol/openid-connect/token")))
public class RestauranteResource {

	@Inject
	RestauranteMapper restauranteMapper;

	@Inject
	PratoMapper pratoMapper;

	@GET
	@Tag(ref = "restaurante")
	@Counted(name = "Quantidade buscas Restaurante")
	@SimplyTimed(name = "Tempo simples de busca")
	@Timed(name = "Tempo completo de busca")
	public List<RestauranteDTO> buscar() {

		return Restaurante.streamAll().map(r -> restauranteMapper.toRestauranteDTO((Restaurante) r))
				.collect(Collectors.toList());
	}

	@POST
	@Transactional
	@Tag(ref = "restaurante")
	@APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ConstraintViolationResponse.class)))
	@APIResponse(responseCode = "201", description = "Caso o restaurante seja cadastrado com sucesso")
	/**
	 * 
	 * @param dto
	 * @param uriInfo
	 * @return returns the response status and the URI for the resource in order to
	 *         follow the HATEOAS pattern for RESTful applications (Hypertext as
	 *         Engine of Application State)
	 */
	public Response adicionar(@Valid AdicionarRestauranteDTO dto, @Context UriInfo uriInfo) {

		Restaurante restaurante = new Restaurante();
		restauranteMapper.toRestaurante(dto, restaurante);
		restaurante.persist();
		UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
		uriBuilder.path(Long.toString(restaurante.id));
		return Response.created(uriBuilder.build()).build();
	}

	@PUT
	@Transactional
	@Path("{id}")
	@Tag(ref = "restaurante")
	public Response atualizar(@PathParam("id") Long id, @Valid AdicionarRestauranteDTO dto,
			@Context UriInfo contextInfo) {

		Optional<Restaurante> restaurante = Restaurante.findByIdOptional(id);

		if (!restaurante.isPresent()) {
			throw new NotFoundException();
		}

		restauranteMapper.toRestaurante(dto, restaurante.get());

		restaurante.get().persist();

		UriBuilder uriBuilder = contextInfo.getAbsolutePathBuilder();
		return Response.noContent().contentLocation(uriBuilder.build()).build();
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
	public List<AdicionarPratoDTO> buscarPratos(@PathParam("id") Long restauranteId) {
		Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(restauranteId);
		if (!restauranteOp.isPresent()) {
			throw new NotFoundException("Restaurante não localizado", Response.status(Status.NOT_FOUND).build());
		}

		Stream<Prato> pratos = Prato.stream("restaurante", restauranteOp.get());
		return pratos.map(p -> pratoMapper.toPratoDTO(p)).collect(Collectors.toList());
	}

	@POST
	@Transactional
	@Path("{restauranteId}/pratos")
	@Tags(refs = { "restaurante", "prato" })
	@APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ConstraintViolationResponse.class)))
	@APIResponse(responseCode = "201", description = "caso o prato seja cadastrado com sucesso")
	public Response adicionarPrato(@PathParam("restauranteId") Long restauranteId, @Valid AdicionarPratoDTO dto,
			@Context UriInfo uriInfo) {

		Optional<Restaurante> restaurante = Restaurante.findByIdOptional(restauranteId);

		Prato prato = new Prato();
		restaurante.ifPresentOrElse(r -> {
			pratoMapper.toPrato(dto, prato);
			prato.restaurante = r;
			prato.persist();
		}, () -> {
			throw new NotFoundException();
		});

		UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
		uriBuilder.path(Long.toString(prato.id));
		return Response.created(uriBuilder.build()).build();
	}

	@PUT
	@Transactional
	@Path("{restauranteId}/pratos/{id}")
	@Tags(refs = { "restaurante", "prato" })
	public void atualizarPrato(@PathParam("restauranteId") Long restauranteId, @PathParam("id") Long id,
			AdicionarPratoDTO dto) {

		Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(restauranteId);
		if (restauranteOp.isEmpty()) {
			throw new NotFoundException("Restaurante não Localizado");
		}

		Optional<Prato> pratoOp = Prato.findByIdOptional(id);
		if (pratoOp.isEmpty()) {
			throw new NotFoundException();
		} else {
			Prato prato = pratoOp.get();
			pratoMapper.toPrato(dto, prato);
			prato.persist();
		}
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