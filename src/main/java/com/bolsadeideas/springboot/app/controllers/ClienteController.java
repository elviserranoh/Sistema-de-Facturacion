package com.bolsadeideas.springboot.app.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.bolsadeideas.springboot.app.entity.Cliente;
import com.bolsadeideas.springboot.app.service.IClienteService;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping({ "/listar" })
	public String listar(Model model) {
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clienteService.findAll());
		return "cliente/listar";
	}

	@GetMapping({ "/form" })
	public String crear(Map<String, Object> map) {
		Cliente cliente = new Cliente();
		map.put("titulo", "Formulario de Cliente");
		map.put("cliente", cliente);
		return "cliente/form";
	}

	@PostMapping({ "/form" })
	public String crear(@Valid Cliente cliente, BindingResult result, Model model, SessionStatus status) {
		
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "cliente/form";
		}
		
		clienteService.save(cliente);
		
		status.setComplete();
		
		return "redirect:/listar";
	}
	
	@GetMapping({"/form/{id}"})
	public String modificar(@PathVariable(value="id") Long id, Map<String, Object> map) {
		
		Cliente cliente = null;
		
		if(id > 0) {
			cliente = clienteService.findOne(id);
		} else {
			return "redirect:/listar";
		}
		
		map.put("cliente", cliente);
		map.put("titulo", "Editar cliente");
		
		return "cliente/form";
	}
	
	@GetMapping({"/eliminar/{id}"})
	public String eliminar(@PathVariable(value="id") Long id) {
		
		if(id>0) {
			clienteService.delete(id);
		}
		
		return "redirect:/listar";
	}
}
