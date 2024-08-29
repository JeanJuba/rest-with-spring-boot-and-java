package br.com.erudio.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.model.Person;
import br.com.erudio.repositories.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

	@Mock
	private PersonRepository repository;

	@InjectMocks
	private PersonService service;

	private Person person0;

	@BeforeEach
	public void setup() {
		// Given / Arrange
		person0 = new Person("Leandro", "Costa", "Uberlândia", "Male", "leandro@erudio.com.br");
	}

	@DisplayName("JUnit Test for Given Person Object When Save Person Then Return object")
	@Test
	void testGivenPersonObject_WhenSavePerson_thenReturnPersonObject() {
		// Given / Arrange
		when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(repository.save(person0)).thenReturn(person0);

		// When / Act
		Person savedPerson = service.create(person0);

		// Then / Assert
		assertNotNull(savedPerson);
		assertEquals("Leandro", savedPerson.getFirstName());
	}

	@DisplayName("JUnit Test for Given Existing Person Object When Save Person Then Return Exception")
	@Test
	void testGivenExistingPersonObject_WhenSavePerson_thenThrowsException() {
		// Given / Arrange
		when(repository.findByEmail(anyString())).thenReturn(Optional.of(person0));

		// When / Act
		assertThrows(ResourceNotFoundException.class, () -> {
			service.create(person0);
		});

		// Then / Assert
		verify(repository, never()).save(any(Person.class));
	}

	@DisplayName("JUnit Test for Given Person List When Find All Person Then Return Person List")
	@Test
	void testGivenPersonList_WhenFindAllPerson_thenReturnPersonList() {
		// Given / Arrange

		Person person1 = new Person("Jean", "Juba", "Santa Cruz Do Sul", "Male", "jean.juba@sulprint.com.br");

		when(repository.findAll()).thenReturn(List.of(person0, person1));

		// When / Act
		List<Person> personList = service.findAll();

		// Then / Assert
		assertNotNull(personList);
		assertEquals(2, personList.size());
	}

	@DisplayName("JUnit Test for Given Empty Person List When Find All Person Then Return Empty Person List")
	@Test
	void testGivenEmptyPersonList_WhenFindAllPerson_thenReturnEmptyPersonList() {
		// Given / Arrange

		when(repository.findAll()).thenReturn(Collections.emptyList());

		// When / Act
		List<Person> personList = service.findAll();

		// Then / Assert
		assertTrue(personList.isEmpty());
		assertEquals(0, personList.size());
	}
}
