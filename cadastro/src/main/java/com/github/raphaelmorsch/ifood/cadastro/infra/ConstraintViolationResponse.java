package com.github.raphaelmorsch.ifood.cadastro.infra;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

public class ConstraintViolationResponse {
	
	private final List<ConstraintViolationImpl> violacoes = new ArrayList<>();
	
	public List<ConstraintViolationImpl> getViolacoes() {
		return violacoes;
	}

	private ConstraintViolationResponse(ConstraintViolationException exception) {
		exception.getConstraintViolations().forEach(violation -> violacoes.add(ConstraintViolationImpl.of(violation)));
	}
	

}
