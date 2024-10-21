package br.com.erudio.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.model.Person;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonRepositoryTest extends AbstractIntegrationTest {

	@Autowired
	private PersonRepository repository;

	private Person person0;

	@BeforeEach
	public void setup() {
		// Given / Arrange
		person0 = new Person("Leandro", "Costa", "Uberlândia", "Male", "leandro@erudio.com.br");
	}

	@DisplayName("Given Person Object When Saved then Return Saved Person")
	@Test
	void testGivenPersonObject_WhenSaved_thenReturnSavedPerson() {

		// When / Act
		Person savedPerson = repository.save(person0);

		// Then / Assert
		assertNotNull(savedPerson);
		assertTrue(savedPerson.getId() > 0);
	}

	@DisplayName("Given Person List When findAll then Return Person List")
	@Test
	void testGivenPersonList_WhenFindAll_thenReturnPersonList() {
		// Given / Arrange

		Person person1 = new Person("Leonardo",
                "Costa",
                "leonardo@erudio.com.br",
                "Male", "Uberlândia - Minas Gerais - Brasil");
		repository.save(person0);
		repository.save(person1);

		// When / Act
		List<Person> personList = repository.findAll();

		// Then / Assert
		assertNotNull(personList);
		// Had to change because if the integration runs first then there are already objects in the database so testing
		// for a specific number can fail some times depending on order of execution of test classes
		assertTrue(personList.size() >= 2);
	}

	@DisplayName("Given Person Object When Find By Id then Return Object")
	@Test
	void testGivenPersonObject_WhenFindById_thenReturnPersonObject() {
		// Given / Arrange
		repository.save(person0);

		// When / Act
		Person savedPerson = repository.findById(person0.getId()).get();

		// Then / Assert
		assertNotNull(savedPerson);
		assertEquals(person0.getId(), savedPerson.getId());
	}

	@DisplayName("Given Person Object When Find By Email then Return Object")
	@Test
	void testGivenPersonObject_WhenFindByEmail_thenReturnPersonObject() {
		// Given / Arrange
		repository.save(person0);

		// When / Act
		Person savedPerson = repository.findByEmail(person0.getEmail()).get();

		// Then / Assert
		assertNotNull(savedPerson);
		assertEquals("leandro@erudio.com.br", savedPerson.getEmail());
	}

	@DisplayName("Given Person Object When Update Person then Return Updated Person Object")
	@Test
	void testGivenPersonObject_WhenUpdatePerson_thenReturnUpdatedPersonObject() {
		// Given / Arrange
		repository.save(person0);

		// When / Act
		Person savedPerson = repository.findById(person0.getId()).get();
		savedPerson.setFirstName("Leonardo");
		savedPerson.setEmail("leonardo@erudio.com.br");

		Person updatedPerson = repository.save(savedPerson);

		// Then / Assert
		assertNotNull(updatedPerson);
		assertEquals(updatedPerson.getId(), savedPerson.getId());
		assertEquals("Leonardo", savedPerson.getFirstName());
		assertEquals("leonardo@erudio.com.br", savedPerson.getEmail());
	}

	@DisplayName("Given Person Object When Delete then Remove Object")
	@Test
	void testGivenPersonObject_WhenDelete_thenRemovePerson() {
		// Given / Arrange
		repository.save(person0);

		// When / Act
		repository.deleteById(person0.getId());

		Optional<Person> personOptional = repository.findById(person0.getId());

		// Then / Assert
		assertTrue(personOptional.isEmpty());
	}

	@DisplayName("Given First Name and Last Name When Find By JPQL  then Return Object")
	@Test
	void testGivenFirstNameAndLastName_WhenFindByJPQL_thenReturnPersonObject() {
		// Given / Arrange
		repository.save(person0);

		// When / Act
		Person savedPerson = repository.findByJPQL("Leandro", "Costa");

		// Then / Assert
		assertNotNull(savedPerson);
		assertEquals("Leandro", savedPerson.getFirstName());
		assertEquals("Costa", savedPerson.getLastName());
	}

	@DisplayName("Given First Name and Last Name When Find By JPQL Named Parameters then Return Object")
	@Test
	void testGivenFirstNameAndLastName_WhenFindByJPQLNamedParameters_thenReturnPersonObject() {
		// Given / Arrange
		repository.save(person0);

		// When / Act
		Person savedPerson = repository.findByJPQLNamedParameters("Leandro", "Costa");

		// Then / Assert
		assertNotNull(savedPerson);
		assertEquals("Leandro", savedPerson.getFirstName());
		assertEquals("Costa", savedPerson.getLastName());
	}

	@DisplayName("Given First Name and Last Name When Find By Native SQL then Return Object")
	@Test
	void testGivenFirstNameAndLastName_WhenFindByNativeSQL_thenReturnPersonObject() {
		// Given / Arrange
		repository.save(person0);

		// When / Act
		Person savedPerson = repository.findByNativeSQL("Leandro", "Costa");

		// Then / Assert
		assertNotNull(savedPerson);
		assertEquals("Leandro", savedPerson.getFirstName());
		assertEquals("Costa", savedPerson.getLastName());
	}

	@DisplayName("Given First Name and Last Name When Find By Native SQL Named Parameters then Return Object")
	@Test
	void testGivenFirstNameAndLastName_WhenFindByNativeSQLNamedParameters_thenReturnPersonObject() {
		// Given / Arrange
		repository.save(person0);

		// When / Act
		Person savedPerson = repository.findByNativeSQLNamedParameters("Leandro", "Costa");

		// Then / Assert
		assertNotNull(savedPerson);
		assertEquals("Leandro", savedPerson.getFirstName());
		assertEquals("Costa", savedPerson.getLastName());
	}
}
