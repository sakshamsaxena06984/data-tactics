package example.com.tactics.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import example.com.tactics.dao.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import example.com.tactics.dao.UserRespository;
import example.com.tactics.entities.Contact;
import example.com.tactics.entities.User;
import example.com.tactics.helper.Message;
import net.bytebuddy.matcher.ModifierMatcher.Mode;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRespository userRespository;
	@Autowired
	private ContactRepository contactRepository;


	// adding common data for every handler
	@ModelAttribute
	public void addContactData(Model model, Principal principal) {
		String name = principal.getName();
		// getting information from database
		User user = userRespository.getUserByUserName(name);
		model.addAttribute("user", user);

	}


	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
//		String name = principal.getName();
//		System.out.println("principal [spring-security]  username : "+name);
//		
//		User us = this.userRespository.getUserByUserName(name);
//		System.out.println("+++++++++++++++++++  "+us.toString());
//		model.addAttribute("user", us);
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}


	//add contact handler
	@GetMapping("add-contact")
	public String openAddContact(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}


	// processing add contact
	@PostMapping("/process-contact")
	public String procesContact(@ModelAttribute("contact") Contact contact,
								@RequestParam("profileImage") MultipartFile file,
								Principal principal,
								HttpSession session) {
		System.out.println("++++ contact form info ++++ " + contact.toString());

		try {
			String name = principal.getName();
			User user = this.userRespository.getUserByUserName(name);

			//processing and uploading file...
			if (file.isEmpty()) {
				//inserted file empty
				System.out.println("+++++++++++++++++++++++++++      :    inserted file in empty ");
				contact.setImage("contacts.jpg");
			} else {
				//uploading file
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("  file path : +++++++++++++++ " + path);
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRespository.save(user);
			System.out.println("Contacted added into user-contact list!");
			session.setAttribute("message", new Message("your connect has been added", "success"));
		} catch (Exception e) {
			System.out.println("Error in process-contact: " + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("something went wrong! ", "danger"));
			// TODO: handle exception
		}
		return "normal/add_contact_form";
	}

	//handler of showing contacts
	
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page,Model model,Principal principal) {
		model.addAttribute("title", "Show Contacts");
		//sending contacts information

		String name = principal.getName();
		System.out.println("user name ++++++ "+name);
		User user = this.userRespository.getUserByUserName(name);

		// it is using for paginationnnn..........
		Pageable pageable = PageRequest.of(page, 5);
				
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages", contacts.getTotalPages());

//		System.out.println("contacts ++++++ information "+contacts.toString());

		return "normal/show_contact";
	}
	
	//for particular contact information
	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,
			Model m,
			Principal principal) {
		System.out.println("in contact detail handler id :: "+cId);
		System.out.println("lets check the id value : "+cId);
		Optional<Contact> con = this.contactRepository.findById(cId);
		Contact contact = con.get();
		//getting the name of currently active user
		 String name = principal.getName();
		 User user=this.userRespository.getUserByUserName(name);
		 
		 if(user.getId()==contact.getUser().getId()) {
				m.addAttribute("contact", contact); 
		 }
		return "normal/contact_detail";
	}
	
	//deleting contact
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId,Model model,Principal principal,HttpSession session) {

		String name = principal.getName();
		User user = this.userRespository.getUserByUserName(name);
		
		Optional<Contact> findById = this.contactRepository.findById(cId);
		Contact contact = findById.get();
		
		//checking the deletion user and contact
		if(user.getId()==contact.getUser().getId()) {
//			contact.setUser(null);
//			this.contactRepository.delete(contact);
			user.getContacts().remove(contact);  
			this.userRespository.save(user);
			session.setAttribute("message", new Message("Contact deleted successfully..", "success"));
		}else {
			session.setAttribute("message", new Message("Wrong Call!", "danger"));
		}
		
		
		return "redirect:/user/show-contacts/0";
	}
	
	// update contact handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cId,Model m) {
		m.addAttribute("title", "Update Contact");
		System.out.println("calling update handle");
		Optional<Contact> findById = this.contactRepository.findById(cId);
		Contact contact=findById.get();
		
		m.addAttribute("contact", contact);
		
		return "normal/update_form";
	}
	
	// update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Model m,
			HttpSession session,
			Principal principal) {
		
		try {
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			Contact oldContact = this.contactRepository.findById(contact.getcId()).get();
			if(!file.isEmpty()) {
				//will delete old image and update new image
				File deleteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,oldContact.getImage());
				file1.delete();
				
				//update the file
				
				
				File saveFile=new ClassPathResource("static/img").getFile();
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				
			}else {
				//set old image
				contact.setImage(oldContact.getImage());
			}
			User user = this.userRespository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your Contact is updated..","success"));
//			System.out.println("incoming contact information +++++++++++++  "+contact.toString());
			System.out.println("rendering path +++++++++++   "+"/user/"+contact.getcId()+"/contact");
			
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return "redirect:/user/"+contact.getcId()+"/contact";
		
		
		
	}
	
	// your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

}
