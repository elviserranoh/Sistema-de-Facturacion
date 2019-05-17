package com.bolsadeideas.springboot.app.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.app.entity.Cliente;

public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long> {

	@Query("select c from Cliente c left join fetch c.facturas f where c.id = ?1")
	public Cliente fetchByIdWithFacturas(Long id);

}
