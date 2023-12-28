package example.com.tactics.dao;

import example.com.tactics.entities.Contact;
import example.com.tactics.entities.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    //getting the pegination features

//	/*
//	 * @Query("from Contact as d where d.user.id =:userId") public List<Contact>
//	 * findContactsByUser(@Param("userId") int userId);
//	 */    
    
	@Query("from Contact as d where d.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pageable);
	
	
	// search query result
	public List<Contact> findByNameContainingAndUser(String name,User user);


}
