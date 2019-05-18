package com.bolsadeideas.springboot.app.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.dao.IUsuarioDao;
import com.bolsadeideas.springboot.app.entity.Role;
import com.bolsadeideas.springboot.app.entity.Usuario;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IUsuarioDao usuarioDao;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Usuario usuario = usuarioDao.findByUsername(username);
		
		if(usuario == null) {
			logger.error(String.format("Error login: %s no existe en la base de datos", username));
			throw new UsernameNotFoundException(String.format("Usuario: %s no existe en la base de datos", username));
		}

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (Role role : usuario.getAuthorities()) {
			logger.info(String.format("Usuario: %s, tiene ROL: %s", username, role));
			authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
		}
		
		if(authorities.isEmpty()) {
			logger.error(String.format("Usuario: %s no tiene roles asignados", username));
			throw new UsernameNotFoundException(String.format("Usuario: %s no tiene roles asignados", username));
		}

		return new User(usuario.getUsername(), usuario.getPassword(), usuario.isEnabled(), true, true, true,
				authorities);
	}

}
