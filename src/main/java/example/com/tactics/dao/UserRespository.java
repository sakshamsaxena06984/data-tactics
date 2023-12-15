package example.com.tactics.dao;

import org.springframework.data.repository.CrudRepository;

import example.com.tactics.entities.User;

public interface UserRespository extends CrudRepository<User, Integer> {

}
