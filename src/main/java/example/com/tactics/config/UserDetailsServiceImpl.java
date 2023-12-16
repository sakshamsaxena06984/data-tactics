package example.com.tactics.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import example.com.tactics.dao.UserRespository;
import example.com.tactics.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRespository userRespository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userByUserName = userRespository.getUserByUserName(username);
		if(userByUserName==null) {
			throw new UsernameNotFoundException("username not available!");
		}
		CustomUserDetails customUserDetails=new CustomUserDetails(userByUserName);
		return customUserDetails;
	}

}
