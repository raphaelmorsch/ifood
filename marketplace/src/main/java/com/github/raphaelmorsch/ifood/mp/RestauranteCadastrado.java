package com.github.raphaelmorsch.ifood.mp;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.vertx.mutiny.pgclient.PgPool;

@ApplicationScoped
public class RestauranteCadastrado {
	
	@Inject
	PgPool pgPool;

	@Incoming("restaurantes")
	public void receberRestaurante(String json) {
		
		Jsonb jsonb = JsonbBuilder.create();
		Restaurante restaurante = jsonb.fromJson(json, Restaurante.class);
		
		System.out.println("--------------------------------");
		System.out.println(json);
		System.out.println(restaurante);
		System.out.println("--------------------------------");
		
		restaurante.persist(pgPool);
	}
	
}
