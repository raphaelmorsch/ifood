package com.github.raphaelmorsch.ifood.cadastro;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.approvaltests.Approvals;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.database.rider.cdi.api.DBRider;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.raphaelmorsch.ifood.cadastro.dto.AdicionarRestauranteDTO;
import com.github.raphaelmorsch.ifood.cadastro.util.TokenUtils;
import com.google.gson.JsonObject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@QuarkusTest
@QuarkusTestResource(CadastroTestResourceLifecycleManager.class)
public class RestauranteResourceTest {

	private String token;
	
	@BeforeEach
	public void generateToken() throws InvalidJwtException, Exception {
		this.token = TokenUtils.generateTokenString("/JWTProprietarioClaims.json", null);
	}

	private RequestSpecification given() {

		return RestAssured.given()
				.contentType(ContentType.JSON).accept(ContentType.ANY)
				.header(new Header("Authorization", "Bearer " + token));
	}

	@Test
	@DataSet("restaurantes-cenario-1.yml")
	public void testBuscarRestaurantes() {
		String resultado = given().when().get("/restaurantes").then().statusCode(200).extract().asString();
		Approvals.verifyJson(resultado);
	}

	@Test
	public void testAdicionarRestaurante() {
		Map<String, Matcher<?>> responseHeaders = new HashMap<String, Matcher<?>>();
		responseHeaders.put("content-length", Matchers.equalTo("0"));
		responseHeaders.put("location", Matchers.equalTo("http://localhost:8081/restaurantes/1"));

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("accept", "*/*");
		requestHeaders.put("Content-Type", "application/json");

		JsonObject jsonRequest = new JsonObject();
		jsonRequest.addProperty("cnpj", "58.888.082/0001-32");
		jsonRequest.addProperty("nome", "Meu Novo Restaurante");
		jsonRequest.addProperty("proprietario", "Novo ID do keycloak");

		given().when().headers(requestHeaders).body(jsonRequest.toString())
				.post("/restaurantes", Collections.emptyMap()).then().assertThat().headers(responseHeaders)
				.statusCode(Status.CREATED.getStatusCode());
	}

	@Test
	@DataSet("restaurantes-cenario-1.yml")
	public void atualizarRestaurante() {

		AdicionarRestauranteDTO dto = new AdicionarRestauranteDTO();
		dto.nomeFantasia = "novo nome";
		dto.cnpj = "57.218.236/0001-16";
		dto.proprietario = "novo proprietario";
		Long parameterValue = 123L;
		
		Response resposta = given().when().body(dto).put("/restaurantes/{id}", parameterValue);

		resposta.then()
				.statusCode(Status.NO_CONTENT.getStatusCode()).and()
				.header("content-location", Matchers.endsWith("/restaurantes/" + parameterValue.toString()));

		Restaurante findById = Restaurante.findById(parameterValue);
		Assert.assertEquals(dto.nomeFantasia, findById.nome);

	}

}