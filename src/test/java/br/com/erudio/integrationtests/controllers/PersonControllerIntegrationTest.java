package br.com.erudio.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.model.Person;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PersonControllerIntegrationTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static Person person;

	@BeforeAll
	public static void setup() {
		// Given / Arrange

		objectMapper = new ObjectMapper();

		// Doesn't fail when it fails an unknown property
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		specification = new RequestSpecBuilder().setBasePath("/person").setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();

		person = new Person("Leandro", "Costa", "Uberlândia", "Male", "leandro@erudio.com.br");
	}

	@Test
	@Order(1)
	@DisplayName("JUnit integration Test When Create One Person Should Return A Person Object")
	void integrationTest_When_CreateOnePerson_ShouldReturnAPersonObject()
			throws JsonMappingException, JsonProcessingException {
		String content = given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON).body(person).when()
				.post().then().statusCode(200).extract().body().asString();

		// Converts JSON to object
		Person createdPerson = objectMapper.readValue(content, Person.class);
		person = createdPerson;

		assertNotNull(createdPerson);
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());
		assertNotNull(createdPerson.getEmail());

		assertTrue(createdPerson.getId() > 0);
		assertEquals("Leandro", createdPerson.getFirstName());
		assertEquals("Costa", createdPerson.getLastName());
		assertEquals("Uberlândia", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertEquals("leandro@erudio.com.br", createdPerson.getEmail());
	}

	@Test
	@Order(2)
	@DisplayName("JUnit integration Test When Update One Person Should Return Updated Person Object")
	void integrationTest_When_UpdateOnePerson_ShouldReturnUpdatedPersonObject()
			throws JsonMappingException, JsonProcessingException {

		person.setFirstName("Leonardo");
		person.setEmail("leonardo@erudio.com.br");

		String content = given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON).body(person).when()
				.put().then().statusCode(200).extract().body().asString();

		// Converts JSON to object
		Person updatedPerson = objectMapper.readValue(content, Person.class);
		person = updatedPerson;

		assertNotNull(updatedPerson);
		assertNotNull(updatedPerson.getId());
		assertNotNull(updatedPerson.getFirstName());
		assertNotNull(updatedPerson.getLastName());
		assertNotNull(updatedPerson.getAddress());
		assertNotNull(updatedPerson.getGender());
		assertNotNull(updatedPerson.getEmail());

		assertTrue(updatedPerson.getId() > 0);
		assertEquals("Leonardo", updatedPerson.getFirstName());
		assertEquals("Costa", updatedPerson.getLastName());
		assertEquals("Uberlândia", updatedPerson.getAddress());
		assertEquals("Male", updatedPerson.getGender());
		assertEquals("leonardo@erudio.com.br", updatedPerson.getEmail());
	}

	@Test
	@Order(3)
	@DisplayName("JUnit integration Test Given Person Object When Find By Id Should Return A Person Object")
	void integrationTestGivenPersonObject_When_findById_ShouldReturnAPersonObject()
			throws JsonMappingException, JsonProcessingException {

		String content = given().spec(specification).pathParam("id", person.getId()).when().get("{id}").then()
				.statusCode(200).extract().body().asString();

		// Converts JSON to object
		Person foundPerson = objectMapper.readValue(content, Person.class);

		assertNotNull(foundPerson);
		assertNotNull(foundPerson.getId());
		assertNotNull(foundPerson.getFirstName());
		assertNotNull(foundPerson.getLastName());
		assertNotNull(foundPerson.getAddress());
		assertNotNull(foundPerson.getGender());
		assertNotNull(foundPerson.getEmail());

		assertTrue(foundPerson.getId() > 0);
		assertEquals("Leonardo", foundPerson.getFirstName());
		assertEquals("Costa", foundPerson.getLastName());
		assertEquals("Uberlândia", foundPerson.getAddress());
		assertEquals("Male", foundPerson.getGender());
		assertEquals("leonardo@erudio.com.br", foundPerson.getEmail());
	}

	@Test
	@Order(4)
	@DisplayName("JUnit integration Test  When Find All Should Return A Person List")
	void integrationTest_When_findAll_ShouldReturnAPersonList() throws JsonMappingException, JsonProcessingException {

		Person anotherPerson = new Person("Gabriela", "Rodrigues", "São Paulo", "Female", "gabi@erudio.com.br");

		given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON).body(anotherPerson).when().post().then()
				.statusCode(200);

		String content = given().spec(specification).when().get() // Doesn't pass parameters because findAll uses base
																	// path
				.then().statusCode(200).extract().body().asString();

		// Converts JSON to object
		Person[] personArray = objectMapper.readValue(content, Person[].class);
		List<Person> people = Arrays.asList(personArray);

		Person foundPerson = people.get(0);
		assertNotNull(foundPerson);
		assertNotNull(foundPerson.getId());
		assertNotNull(foundPerson.getFirstName());
		assertNotNull(foundPerson.getLastName());
		assertNotNull(foundPerson.getAddress());
		assertNotNull(foundPerson.getGender());
		assertNotNull(foundPerson.getEmail());

		assertTrue(foundPerson.getId() > 0);
		assertEquals("Leonardo", foundPerson.getFirstName());
		assertEquals("Costa", foundPerson.getLastName());
		assertEquals("Uberlândia", foundPerson.getAddress());
		assertEquals("Male", foundPerson.getGender());
		assertEquals("leonardo@erudio.com.br", foundPerson.getEmail());

		Person foundPerson1 = people.get(1);
		assertNotNull(foundPerson1);
		assertNotNull(foundPerson1.getId());
		assertNotNull(foundPerson1.getFirstName());
		assertNotNull(foundPerson1.getLastName());
		assertNotNull(foundPerson1.getAddress());
		assertNotNull(foundPerson1.getGender());
		assertNotNull(foundPerson1.getEmail());

		assertTrue(foundPerson1.getId() > 0);
		assertEquals("Gabriela", foundPerson1.getFirstName());
		assertEquals("Rodrigues", foundPerson1.getLastName());
		assertEquals("São Paulo", foundPerson1.getAddress());
		assertEquals("Female", foundPerson1.getGender());
		assertEquals("gabi@erudio.com.br", foundPerson1.getEmail());
	}

	@Test
	@Order(5)
	@DisplayName("JUnit integration Test Given Person Object When Delete Should Return No Content")
	void integrationTestGivenPersonObject_When_delete_ShouldReturnNoContent()
			throws JsonMappingException, JsonProcessingException {

		// Only assets that the status code is 204 No Content
		given().spec(specification).pathParam("id", person.getId()).when().delete("{id}").then().statusCode(204);

	}

}
