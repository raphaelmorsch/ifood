package com.github.raphaelmorsch.ifood.cadastro;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "restaurante")
public class Restaurante extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	public String proprietario;

	public String cnpj;

	public String nome;

	@CreationTimestamp
	public LocalDateTime dataCriacao;

	@UpdateTimestamp
	public LocalDateTime dataAtualizacao;

	@ManyToOne
	public Localizacao localizacao;


}
