package com.github.raphaelmorsch.ifood.mp;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class Restaurante {

	public Long id;

	public String nome;

	public Localizacao localizacao;

	public static Uni<RestauranteDTO> findById(PgPool client, Long id) {
		Uni<RowSet<Row>> preparedQuery = client.preparedQuery("SELECT * FROM restaurante WHERE id = $1")
				.execute(Tuple.of(id));
		return preparedQuery.onItem().transformToMulti(set -> Multi.createFrom().iterable(set)).onItem()
				.transform(RestauranteDTO::from).toUni();
	}
}
