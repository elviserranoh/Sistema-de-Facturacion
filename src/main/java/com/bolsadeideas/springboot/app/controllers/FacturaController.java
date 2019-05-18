package com.bolsadeideas.springboot.app.controllers;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.entity.Cliente;
import com.bolsadeideas.springboot.app.entity.Factura;
import com.bolsadeideas.springboot.app.entity.ItemFactura;
import com.bolsadeideas.springboot.app.entity.Producto;
import com.bolsadeideas.springboot.app.service.IClienteService;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
@Secured("ROLE_ADMIN")
public class FacturaController {

	@Autowired
	private IClienteService clienteService;

	private final Logger log = LoggerFactory.getLogger(getClass());

	@GetMapping({ "/ver/{id}" })
	public String ver(@PathVariable Long id, Model model, RedirectAttributes flash) {

		Factura factura = clienteService.fetchByIdWithItemFacturaWithProducto(id); // clienteService.findFacturaById(id);

		if (factura == null) {
			flash.addFlashAttribute("danger", "La factura no existe en la base de datos");
			return "redirect:/listar";
		}

		model.addAttribute("factura", factura);
		model.addAttribute("titulo", "Factura: ".concat(factura.getDescripcion()));

		return "factura/ver";
	}

	@GetMapping({ "/form/{clienteId}" })
	public String crear(@PathVariable Long clienteId, Model model, RedirectAttributes flash) {

		Cliente cliente = clienteService.findOne(clienteId);

		if (cliente == null) {
			flash.addFlashAttribute("danger", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}

		Factura factura = new Factura();
		factura.setCliente(cliente);

		model.addAttribute("factura", factura);
		model.addAttribute("titulo", "Crear Factura");
		return "factura/form";
	}

	// @ResponseBody lo que haces es suprimir, evitar cargar una vista o sea un
	// .html y guardar en este caso
	// el json en el body del metodo

	@GetMapping(value = "/cargar-productos/{term}", produces = { "application/json" })
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term, Model model) {
		return clienteService.findByNombre(term);
	}

	@PostMapping("/form")
	public String guardar(@Valid Factura factura, BindingResult result, Model model,
			@RequestParam(name = "item_id[]", required = false) Long[] itemId,
			@RequestParam(name = "cantidad[]", required = false) Integer[] cantidad, RedirectAttributes flash,
			SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Crear Factura");
			return "factura/form";
		}

		if (itemId == null || itemId.length == 0) {
			model.addAttribute("titulo", "Crear Factura");
			model.addAttribute("danger", "Error: la factura NO puede no tener lineas!");
			return "factura/form";
		}

		for (int i = 0; i < itemId.length; i++) {
			Producto producto = clienteService.findProductoByid(itemId[i]);
			ItemFactura linea = new ItemFactura();
			linea.setCantidad(cantidad[i]);
			linea.setProducto(producto);
			factura.addItemFactura(linea);

			log.info("ID: " + itemId[i].toString() + " cantidad: " + cantidad[i].toString());
		}

		clienteService.saveFactura(factura);
		status.setComplete();
		flash.addFlashAttribute("success", "Factura creada con éxito");

		return "redirect:/ver/" + factura.getCliente().getId();
	}

	@GetMapping("eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes flash) {

		Factura factura = clienteService.findFacturaById(id);

		if (factura != null) {
			clienteService.deleteFactura(id);
			flash.addFlashAttribute("success", "La factura se ha eliminado correctamente");
			return "redirect:/ver/" + factura.getCliente().getId();
		}

		flash.addFlashAttribute("danger", "La factura no existe en la base de datos, no se pudo eliminar");
		return "redirect:/listar";
	}

}
