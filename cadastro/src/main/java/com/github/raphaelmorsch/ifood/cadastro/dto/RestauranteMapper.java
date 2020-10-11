package com.github.raphaelmorsch.ifood.cadastro.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.github.raphaelmorsch.ifood.cadastro.Restaurante;

@Mapper(componentModel = "cdi")
public interface RestauranteMapper {

	@Mapping(target = "nome", source = "nomeFantasia")
	void toRestaurante(AdicionarRestauranteDTO dto, @MappingTarget Restaurante restaurante);

	@Mapping(target = "nomeFantasia", source = "nome")
	@Mapping(target = "dataCriacao", dateFormat = "dd/MM/yyyy HH:mm:ss")
	RestauranteDTO toRestauranteDTO(Restaurante entity);

}
