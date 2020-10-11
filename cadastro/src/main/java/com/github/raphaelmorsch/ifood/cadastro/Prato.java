package com.github.raphaelmorsch.ifood.cadastro;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;


@Entity
@Table(name = "prato")
public class Prato extends PanacheEntityBase {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	public Restaurante restaurante;
	
	public String nome;
	
	public String descricao;
	
	public BigDecimal preco;
 

}
