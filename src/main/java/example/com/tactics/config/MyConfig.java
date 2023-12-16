package example.com.tactics.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter{
	
	@Bean
	public UserDetailsService getDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	
	@Bean
	public BCryptPasswordEncoder getBCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider doaAuthenticationProvider=new DaoAuthenticationProvider();
		doaAuthenticationProvider.setUserDetailsService(this.getDetailsService());
		doaAuthenticationProvider.setPasswordEncoder(getBCryptPasswordEncoder());
		
		return doaAuthenticationProvider;
	}


	// configure method
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	   auth.authenticationProvider(authenticationProvider());
	}


	// way of informing spring boot security related to pages
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
	     http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
	     .antMatchers("/user/**").hasRole("USER")
	     .antMatchers("/**").permitAll().and().formLogin().loginPage("/signin")
	     .loginProcessingUrl("/dologin")
	     .defaultSuccessUrl("/user/index")
	     .and().csrf().disable();
		
		
//		  http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
//		  .antMatchers("/user/**").hasRole("USER")
//		  .antMatchers("/**").permitAll().and().formLogin().and().csrf().disable();
		 

	}
	
	
	
	
	
	

}
