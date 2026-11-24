package com.taskflow.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void registerLoginAndAccessProfile() throws Exception {
		String registration = """
				{"fullName":"Test Member","email":"member@taskflow.test","password":"Password123!"}
				""";

		String token = mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(registration))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.user.email").value("member@taskflow.test"))
				.andExpect(jsonPath("$.user.role").value("TEAM_MEMBER"))
				.andReturn().getResponse().getContentAsString()
				.replaceAll(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");

		mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.fullName").value("Test Member"));

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"member@taskflow.test\",\"password\":\"Password123!\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty());
	}

	@Test
	void rejectsInvalidRegistration() throws Exception {
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"fullName\":\"\",\"email\":\"invalid\",\"password\":\"short\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Validation failed"));
	}
}
