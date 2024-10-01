package br.com.erudio.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.erudio.model.Person;
import br.com.erudio.services.PersonService;

@WebMvcTest
public class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private PersonService service;

	private Person person;

	@BeforeEach
	public void setup() {
		// Given / Arrange
		person = new Person("Leandro", "Costa", "Uberlândia", "Male", "leandro@erudio.com.br");
	}

	@DisplayName("test Given Person Object When Create Person then Return Saved Person")
	@Test
	void testGivenPersonObject_WhenCreatePerson_thenReturnSavedPerson() throws JsonProcessingException, Exception {
		// Given / Arrange
		when(service.create(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When / Act
		ResultActions response = mockMvc.perform(
				post("/person").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(person)));

		// Then / Assert
		response.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(person.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(person.getLastName()))
				.andExpect(jsonPath("$.email").value(person.getEmail()));
	}

	@DisplayName("test Given Person List WhenFind All then Return Person List")
	@Test
	void testGivenPersonList_WhenFindAll_thenReturnPersonList() throws JsonProcessingException, Exception {
		// Given / Arrange
		List<Person> persons = new ArrayList<>();
		persons.add(person);
		persons.add(new Person("Leonardo", "Costa", "Uberlândia", "Male", "leonardo@erudio.com.br"));

		when(service.findAll()).thenReturn(persons);

		// When / Act
		ResultActions response = mockMvc.perform(get("/person"));

		// Then / Assert
		response.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.size()").value(persons.size()));
	}

}
