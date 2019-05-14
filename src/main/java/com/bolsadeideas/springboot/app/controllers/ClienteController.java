package com.bolsadeideas.springboot.app.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.entity.Cliente;
import com.bolsadeideas.springboot.app.service.IClienteService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping({ "/listar" })
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		Pageable pageRequest = PageRequest.of(page, 5);

		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);

		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		
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
	public String crear(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flash,
			SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "cliente/form";
		}

		String messageFlash = (cliente.getId() != null) ? "Cliente editado con exito" : "Cliente creado con exito";

		clienteService.save(cliente);

		status.setComplete();
		flash.addFlashAttribute("success", messageFlash);
		return "redirect:/listar";
	}

	@GetMapping({ "/form/{id}" })
	public String modificar(@PathVariable(value = "id") Long id, Map<String, Object> map, RedirectAttributes flash) {

		Cliente cliente = null;
		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("warning", "El ID del cliente no existe en la BBDD");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("danger", "El ID del cliente no existe!");
			return "redirect:/listar";
		}

		map.put("cliente", cliente);
		map.put("titulo", "Editar cliente");

		return "cliente/form";
	}

	@GetMapping({ "/eliminar/{id}" })
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con exito");
		}

		return "redirect:/listar";
	}
}
