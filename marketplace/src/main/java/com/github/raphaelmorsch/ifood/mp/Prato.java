package com.github.raphaelmorsch.ifood.mp;

import java.math.BigDecimal;
import java.util.stream.StreamSupport;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class Prato {

	public Long id;

	public Restaurante restaurante;

	public String nome;

	public String descricao;

	public BigDecimal preco;

	public static Multi<PratoDTO> findAll(PgPool pgPool) {
		Uni<RowSet<Row>> preparedQuery = pgPool.preparedQuery("SELECT * FROM prato").execute();
		return preparedQuery.onItem().transformToMulti(rowSet -> Multi.createFrom().items(() -> {
			return StreamSupport.stream(rowSet.spliterator(), false);
		})).onItem().transform(PratoDTO::from);
	}

	public static Multi<PratoDTO> findAllByRestaurante(PgPool client, Long id) {
		return client
				.preparedQuery("SELECT * FROM prato "
						+ "WHERE restaurante_id = $1")
				.execute(Tuple.of(id)).onItem().transformToMulti(set -> Multi.createFrom().iterable(set)).onItem()
				.transform(PratoDTO::from);
	}

}
