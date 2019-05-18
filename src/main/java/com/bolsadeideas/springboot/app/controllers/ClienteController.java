package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.entity.Cliente;
import com.bolsadeideas.springboot.app.service.IClienteService;
import com.bolsadeideas.springboot.app.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileService;
	
	@Secured("ROLE_USER")
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping({ "/ver/{id}" })
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		Cliente cliente = clienteService.fetchByIdWithFacturas(id);

		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}

		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", "Detalle cliente: " + cliente.getNombre());

		return "cliente/ver";

	}

	@GetMapping({ "/listar", "/", "" })
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication, HttpServletRequest request) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (this.hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso"));
		} else {
			logger.info("Hola ".concat(auth.getName()).concat(" no tienes acceso"));
		}

		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request,
				"ROLE_");

		if (securityContext.isUserInRole("ADMIN")) {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName())
					.concat(" tienes acceso"));
		} else {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName())
					.concat(" no tienes acceso"));
		}
		

		if (request.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName())
					.concat(" tienes acceso"));
		} else {
			logger.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName())
					.concat(" no tienes acceso"));
		}

		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);

		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);

		return "cliente/listar";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping({ "/form" })
	public String crear(Map<String, Object> map) {
		Cliente cliente = new Cliente();
		map.put("titulo", "Formulario de Cliente");
		map.put("cliente", cliente);
		return "cliente/form";
	}

	@Secured("ROLE_ADMIN")
	@PostMapping({ "/form" })
	public String crear(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		String uniqueFilename = null;

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "cliente/form";
		}

		if (!foto.isEmpty()) {

			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {

				uploadFileService.delete(cliente.getFoto());

			}

			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", "Ha subido correctamente '" + uniqueFilename + "'");
			cliente.setFoto(uniqueFilename);
		}

		String messageFlash = (cliente.getId() != null) ? "Cliente editado con exito" : "Cliente creado con exito";
		clienteService.save(cliente);

		status.setComplete();
		flash.addFlashAttribute("success", messageFlash);
		return "redirect:/listar";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
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

	@Secured({"ROLE_ADMIN"})
	@GetMapping({ "/eliminar/{id}" })
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);
			if (uploadFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("success", "Cliente eliminado con exito");
			}

		}

		return "redirect:/listar";
	}

	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return false;
		}

		Authentication auth = context.getAuthentication();

		if (auth == null) {
			return false;
		}

		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		return authorities.contains(new SimpleGrantedAuthority(role));

//		for (GrantedAuthority authority : authorities) {
//			if (role.equals(authority.getAuthority())) {
//
//				logger.info("Hola ".concat(auth.getName().concat(" tu rol es: ").concat(authority.getAuthority())));
//				return true;
//			}
//		}
//
//		return false;
	}
}
