package com.github.raphaelmorsch.ifood.mp;

import io.vertx.mutiny.sqlclient.Row;

public class RestauranteDTO {

	public Long id;

	public String nome;

	public RestauranteDTO(Long id, String nome) {
		this.id = id;
		this.nome = nome;

	}

	public static RestauranteDTO from(Row restaurante) {
		return new RestauranteDTO(restaurante.getLong("id"), restaurante.getString("nome"));

	}

}
