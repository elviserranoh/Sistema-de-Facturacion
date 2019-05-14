package com.bolsadeideas.springboot.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bolsadeideas.springboot.app.entity.Cliente;

public interface IClienteService {
	public List<Cliente> findAll();
	public Page<Cliente> findAll(Pageable pageable);
	void save(Cliente cliente);
	public Cliente findOne(Long id);
	public void delete(Long id);
}
