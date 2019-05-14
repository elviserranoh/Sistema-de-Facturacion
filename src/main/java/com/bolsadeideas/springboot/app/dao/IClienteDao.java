package com.bolsadeideas.springboot.app.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.app.entity.Cliente;

public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long> {
}
