package com.bolsadeideas.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	@GetMapping({ "/login" })
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model, Principal principal,
			RedirectAttributes flash) {

		// Si principal es distinto de null ya alguien ha iniciado sesion
		if (principal != null) {
			flash.addFlashAttribute("info", "Ya ha iniciado sesión anteriormente");
			return "redirect:/listar";
		}

		if (error != null) {
			model.addAttribute("warning",
					"Error en el login: Nombre de usuario o contraseña incorrecta, por favor vuelva a intentarlo");
		}
		
		if(logout != null) {
			model.addAttribute("success", "Ha cerrado sesión con éxito");
		}

		model.addAttribute("titulo", "Iniciar Sesión");
		return "login";
	}
}
