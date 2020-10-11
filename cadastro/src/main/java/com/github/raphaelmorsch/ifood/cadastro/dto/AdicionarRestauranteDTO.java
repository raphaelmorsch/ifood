package com.github.raphaelmorsch.ifood.cadastro.dto;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.br.CNPJ;

import com.github.raphaelmorsch.ifood.cadastro.Restaurante;
import com.github.raphaelmorsch.ifood.cadastro.infra.DTO;
import com.github.raphaelmorsch.ifood.cadastro.infra.ValidDTO;

@ValidDTO
public class AdicionarRestauranteDTO implements DTO {

	@NotNull
	public String proprietario;

	@CNPJ
	@NotNull
	public String cnpj;

	@Size(min = 3, max = 30)
	public String nomeFantasia;

	public LocalizacaoDTO localizacao;

	@Override
	public boolean isValid(ConstraintValidatorContext constraintValidatorContext) {
		constraintValidatorContext.disableDefaultConstraintViolation();
		if (Restaurante.find("cnpj", this.cnpj).count() > 0) {
			constraintValidatorContext.buildConstraintViolationWithTemplate("{cnpj.existente}").addPropertyNode("cnpj")
					.addConstraintViolation();
			return false;
		}
		return true;
	}

}
