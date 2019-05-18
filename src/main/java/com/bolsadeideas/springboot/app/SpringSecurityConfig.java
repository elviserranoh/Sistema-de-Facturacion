package com.bolsadeideas.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bolsadeideas.springboot.app.auth.handler.LoginSuccessHandler;
import com.bolsadeideas.springboot.app.service.JpaUserDetailsService;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
	@Qualifier("jpaUserDetailsService")
	private JpaUserDetailsService userDetailsService;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	protected void configurerGlobal(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder encoder = this.passwordEncoder();
		auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar").permitAll()
//		.antMatchers("/ver/**").hasAnyRole("USER")
//		.antMatchers("/uploads/**").hasAnyRole("USER")
//		.antMatchers("/form/**").hasAnyRole("ADMIN")
//		.antMatchers("/eliminar/**").hasAnyRole("ADMIN")
//		.antMatchers("/factura/**").hasAnyRole("ADMIN")
		.anyRequest().authenticated()
		.and()
			.formLogin()
				.successHandler(successHandler)
				.loginPage("/login")
			.permitAll()
		.and()
			.logout().permitAll()
		.and()
			.exceptionHandling().accessDeniedPage("/error_403");
	}
	
	

}
