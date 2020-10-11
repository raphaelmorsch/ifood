package com.github.raphaelmorsch.ifood.cadastro.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidatorContext;

import com.github.raphaelmorsch.ifood.cadastro.Prato;
import com.github.raphaelmorsch.ifood.cadastro.infra.DTO;
import com.github.raphaelmorsch.ifood.cadastro.infra.ValidDTO;

@ValidDTO
public class AdicionarPratoDTO implements DTO{
	
	public String nome;
	
	public String descricao;
	
	public BigDecimal preco;
	

	@Override
	public boolean isValid(ConstraintValidatorContext constraintValidatorContext) {
		constraintValidatorContext.disableDefaultConstraintViolation();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nome", this.nome);
		if (Prato.count("nome = :nome ", params) > 0) {
			constraintValidatorContext.buildConstraintViolationWithTemplate("{prato.existente}").addPropertyNode("name");
			return false;
		}
		return true;
	}

}
