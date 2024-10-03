package br.com.erudio.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import br.com.erudio.exceptions.ResourceNotFoundException;
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

	@DisplayName("test Given Person Id When Find By Id then Return Person Object")
	@Test
	void testGivenPersonId_WhenFindById_thenReturnPersonObject() throws JsonProcessingException, Exception {
		// Given / Arrange
		Long personId = 1L;
		when(service.findById(personId)).thenReturn(person);

		// When / Act
		ResultActions response = mockMvc.perform(get("/person/{id}", personId));

		// Then / Assert
		response.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.firstName").value(person.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(person.getLastName()))
				.andExpect(jsonPath("$.email").value(person.getEmail()));
	}

	@DisplayName("test Given Invalid Person Id When Find By Id then Return Not Found")
	@Test
	void testGivenInvalidPersonId_WhenFindById_thenReturnNotFound() throws JsonProcessingException, Exception {
		// Given / Arrange
		Long personId = 1L;
		when(service.findById(personId)).thenThrow(ResourceNotFoundException.class);

		// When / Act
		ResultActions response = mockMvc.perform(get("/person/{id}", personId));

		// Then / Assert
		response.andExpect(status().isNotFound()).andDo(print());
	}

	@DisplayName("test Given Updated Person When Update then Return Updated Person Object")
	@Test
	void testGivenUpdatedPerson_WhenUpdate_thenReturnUpdatedPersonObject() throws JsonProcessingException, Exception {
		// Given / Arrange
		Long personId = 1L;
		when(service.findById(personId)).thenReturn(person);
		when(service.update(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When / Act
		Person updatedPerson = new Person("Leonardo", "Costa", "Uberlândia", "Male", "leonardo@erudio.com.br");
		ResultActions response = mockMvc.perform(put("/person").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(updatedPerson)));

		// Then / Assert
		response.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.firstName").value(updatedPerson.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(updatedPerson.getLastName()))
				.andExpect(jsonPath("$.email").value(updatedPerson.getEmail()));
	}

	@DisplayName("test Given Unexistent Updated Person When Update then Return Not Found")
	@Test
	void testUnexistentUpdatedPerson_WhenUpdate_thenReturnNotFound() throws JsonProcessingException, Exception {
		// Given / Arrange
		Long personId = 1L;
		when(service.findById(personId)).thenThrow(ResourceNotFoundException.class);
		when(service.update(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(1));

		// When / Act
		Person updatedPerson = new Person("Leonardo", "Costa", "Uberlândia", "Male", "leonardo@erudio.com.br");
		ResultActions response = mockMvc.perform(put("/person").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(updatedPerson)));

		// Then / Assert
		response.andExpect(status().isNotFound()).andDo(print());
	}

	@DisplayName("test Given Unexistent Updated Person When Update then Return Not Found")
	@Test
	void testPersonId_WhenDelete_thenNoContent() throws JsonProcessingException, Exception {
		// Given / Arrange
		Long personId = 1L;
		doNothing().when(service).delete(personId);

		// When / Act
		ResultActions response = mockMvc.perform(delete("/person/{id}", personId));
		// Then / Assert
		response.andExpect(status().isNoContent()).andDo(print());
	}

}
