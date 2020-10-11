package com.github.raphaelmorsch.ifood.cadastro.infra;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;

public class ConstraintViolationImpl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String atributo;

	private final String mensagem;

	private ConstraintViolationImpl(ConstraintViolation<?> violation) {
		this.atributo = Stream.of(violation.getPropertyPath().toString().split("\\.")).skip(2)
				.collect(Collectors.joining("."));
		this.mensagem = violation.getMessage();
	}

	public ConstraintViolationImpl(String atributo, String mensagem) {
		this.mensagem = mensagem;
		this.atributo = atributo;
	}

	public static ConstraintViolationImpl of(ConstraintViolation<?> violation) {
		return new ConstraintViolationImpl(violation);
	}

	public static ConstraintViolationImpl of(String violation) {
		return new ConstraintViolationImpl(null, violation);
	}

	public String getMensagem() {
		return mensagem;
	}

	public String getAtributo() {
		return atributo;
	}

}
