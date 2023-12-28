package example.com.tactics.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import example.com.tactics.dao.ContactRepository;
import example.com.tactics.dao.UserRespository;
import example.com.tactics.entities.Contact;
import example.com.tactics.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRespository userRespository;
	@Autowired
	private ContactRepository contactRepository;
	
	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,
			Principal principal){
		
		System.out.println("++++ result of query ++++ "+query);
		User user=this.userRespository.getUserByUserName(principal.getName());
		
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}

}
