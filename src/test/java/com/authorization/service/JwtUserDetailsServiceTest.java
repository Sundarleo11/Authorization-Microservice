package com.authorization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.authorization.model.User;
import com.authorization.repository.UserDao;
import com.authorization.service.JwtUserDetailsService;

@SpringBootTest
class JwtUserDetailsServiceTest {
	
	@Mock
	private UserDao userDao;

	@Mock
	private PasswordEncoder bcryptEncoder;

	@InjectMocks
	private JwtUserDetailsService service;
	
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void loadUserByUserNameShouldThrowExceptionTest() {
		when(userDao.findByUserName("wrongUserName")).thenReturn(null);
		assertThatThrownBy(() -> service.loadUserByUsername("wrongUserName")) 
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("User not found with username: wrongUserName");
		verify(userDao, Mockito.times(1)).findByUserName("wrongUserName");
	}
	
	@Test
	void loadUserByUserNameShouldUserNameTest() {
		when(userDao.findByUserName("sundar")).thenReturn(new User(1,"sundar","pass"));
		assertThat(service.loadUserByUsername("sundar")).isNotNull();
		verify(userDao, Mockito.times(1)).findByUserName("sundar");
	}

}
