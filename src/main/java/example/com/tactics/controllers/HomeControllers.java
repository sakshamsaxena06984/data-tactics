package example.com.tactics.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import example.com.tactics.dao.UserRespository;
import example.com.tactics.entities.User;
import example.com.tactics.helper.Message;

@Controller
public class HomeControllers {
	
	@Autowired
	private UserRespository userRespository;
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		User us=new User();
		us.setName("hello");
		us.setEmail("saks123ham@gmail.com");
		us.setPassword("466");
		us.setrole("admin");
		User save = userRespository.save(us);
		System.out.println("+++++++++++++ saved records "+save);
		return "working";
	}
	
	
	// Home page mapping
	@GetMapping("/home")
	public String home(Model model) {
		model.addAttribute("title", "Home");
		return "home";
	}
	
	
	//About page mapping
	@GetMapping("/about")
	public String abome(Model model) {
		model.addAttribute("title", "About");
		return "about";
	}
	
	//Sigup page mapping
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "SignUp");
		model.addAttribute("user", new User());
		
		return "signup";
	}
	
	//register handler
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,
			BindingResult result,
			@RequestParam(value = "agreement",defaultValue = "false") boolean agreement,
			Model model,
			HttpSession session) {
		
		try {
			System.out.println("++ input records : "+user.toString());

			if(!agreement) {
				System.out.println("---++ value of agreement : "+agreement);
				System.out.println("please, agree with terms and conditions!");
				throw new Exception("you have not agreed the terms and conditions!");
			}
			if(result.hasErrors()) {
				System.out.println("++++ validation related error ! "+result.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			System.out.println("+++++++ agreement "+agreement);
			System.out.println("+++++++ users "+user.toString());
			model.addAttribute("user", user);
			
			user.setrole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			
			User result1 = this.userRespository.save(user);
			System.out.println("+++ saved result "+result1.toString());
			model.addAttribute("user", new User());
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Successfully Registered ! ","alert-success"));
		}catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");
			model.addAttribute("user", user);
			session.setAttribute("message",
					new Message("Something went wrong ! ","alert-danger"));
			// TODO: handle exception
		}
		
		
		return "signup";
	}
	


}
