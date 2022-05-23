package com.authorization.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.authorization.config.JwtTokenUtil;
import com.authorization.model.JwtRequest;
import com.authorization.model.User;
import com.authorization.service.JwtUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationControllerEndPointsTest {

	@SuppressWarnings("deprecation")
	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JwtUserDetailsService userDetailsService;

	@MockBean
	private JwtTokenUtil jwtTokenUtil;

	@MockBean
	private AuthenticationManager authenticationManager;

	@Test
	void testBadRequestGenerateToken() throws Exception {
		this.mockMvc.perform(post("/api/authenticate")).andExpect(status().isBadRequest());
	}

	@Test
	void testAuthorizedGenerateToken() throws Exception {

		User user = new User(1, "sundar", "sundar");
		UserDetails details = new org.springframework.security.core.userdetails.User(user.getUserName(),
				user.getPassword(), new ArrayList<>());
		when(jwtTokenUtil.generateToken(details)).thenReturn("Bearer @token@token");
		when(userDetailsService.loadUserByUsername("sundar")).thenReturn(details);
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(new JwtRequest("sundar", "sundar"));
		this.mockMvc.perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(jsonString))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void testBadRequest() throws Exception {
		this.mockMvc.perform(post("/api/authenticate")).andExpect(status().isBadRequest());
	}

	@Test
	void testRandomUrl() throws Exception {
		this.mockMvc.perform(get("/api/other/url")).andExpect(status().isNotFound());
	}

	@Test
	void textExistingUserAuthenticate() throws Exception {
		User user = new User(1, "sundar", "pass");
		UserDetails details = new org.springframework.security.core.userdetails.User(user.getUserName(),
				user.getPassword(), new ArrayList<>());
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				"sundar", "sundar");
		when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("sundar", "sundar")))
				.thenReturn(usernamePasswordAuthenticationToken);
		when(userDetailsService.loadUserByUsername("sundar")).thenReturn(details);
		when(jwtTokenUtil.getUsernameFromToken("token")).thenReturn("sundar");
		when(jwtTokenUtil.generateToken(details)).thenReturn("token");
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticate").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new JwtRequest("sundar", "pass")))).andExpect(status().isUnauthorized());

	}

	@Test
	void textExistingUserAuthorize() throws Exception {
		User user = new User(1, "sundar", "pass");
		UserDetails details = new org.springframework.security.core.userdetails.User(user.getUserName(),
				user.getPassword(), new ArrayList<>());
		when(userDetailsService.loadUserByUsername("sundar")).thenReturn(details);
		when(jwtTokenUtil.getUsernameFromToken("token")).thenReturn("sundar");
		mockMvc.perform(MockMvcRequestBuilders.post("/api/authorize").header("Authorization", "Bearer token"))
				.andExpect(status().isOk());

	}

	@Test
	void textNullTokenAuthorize() throws Exception {
		User user = new User(1, "sundar", "pass");
		UserDetails details = new org.springframework.security.core.userdetails.User(user.getUserName(),
				user.getPassword(), new ArrayList<>());
		when(userDetailsService.loadUserByUsername("sundar")).thenReturn(details);
		when(jwtTokenUtil.getUsernameFromToken("token")).thenReturn("sundar");
		mockMvc.perform(MockMvcRequestBuilders.post("/api/authorize").header("Authorization", "")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}
	@Test
    void heatlthCheck() throws Exception {
        this.mockMvc.perform(get("/api/health-check")).andExpect(status().isOk());
    }

}
