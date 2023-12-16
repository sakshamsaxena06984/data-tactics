package example.com.tactics.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import example.com.tactics.dao.UserRespository;
import example.com.tactics.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRespository userRespository;

	
	@GetMapping("/index")
	 public String dashboard(Model model,Principal principal) {
		String name = principal.getName();
		System.out.println("principal [spring-security]  username : "+name);
		
		User us = this.userRespository.getUserByUserName(name);
		System.out.println("+++++++++++++++++++  "+us.toString());
		model.addAttribute("user", us);
		 return "normal/user_dashboard";
	 }
}
