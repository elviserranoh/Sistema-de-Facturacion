package com.bolsadeideas.springboot.app.dao;

import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.app.entity.Factura;

public interface IFacturaDao extends CrudRepository<Factura, Long> {

}
