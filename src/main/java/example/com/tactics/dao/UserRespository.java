package example.com.tactics.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import example.com.tactics.entities.User;

public interface UserRespository extends CrudRepository<User, Integer> {
	
	@Query("select u from User u where u.email = :email")
	public User getUserByUserName(@Param("email") String email);

}
