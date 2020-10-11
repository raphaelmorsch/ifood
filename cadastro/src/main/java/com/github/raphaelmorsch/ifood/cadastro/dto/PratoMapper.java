package com.github.raphaelmorsch.ifood.cadastro.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.github.raphaelmorsch.ifood.cadastro.Prato;

@Mapper(componentModel = "cdi")
public interface PratoMapper {
	
	Prato toPrato(AdicionarPratoDTO dto, @MappingTarget Prato prato);
	
	AdicionarPratoDTO toPratoDTO(Prato entidade);

}
