package com.bolsadeideas.springboot.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bolsadeideas.springboot.app.entity.Cliente;
import com.bolsadeideas.springboot.app.entity.Factura;
import com.bolsadeideas.springboot.app.entity.Producto;

public interface IClienteService {
	public List<Cliente> findAll();

	public Page<Cliente> findAll(Pageable pageable);

	void save(Cliente cliente);

	public Cliente findOne(Long id);

	public void delete(Long id);
	
	public List<Producto> findByNombre(String term);
	
	public void saveFactura(Factura factura);
	
	public Producto findProductoByid(Long id);
	
	public Factura findFacturaById(Long id);
	
	public void deleteFactura(Long id);
	
	public Factura fetchByIdWithItemFacturaWithProducto(Long id);
	
	public Cliente fetchByIdWithFacturas(Long id);
}
