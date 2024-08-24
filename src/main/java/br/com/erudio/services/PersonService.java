package br.com.erudio.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.model.Person;
import br.com.erudio.repositories.PersonRepository;

@Service
public class PersonService {

	private Logger logger = Logger.getLogger(PersonService.class.getName());
	
	@Autowired
	private PersonRepository repository;

	public List<Person> findAll() {
		return repository.findAll();
	}

	public Person findById(Long id) {
		logger.info("Finding one person");
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found this id!"));
	}

	public Person create(Person person) {
		logger.info("Creating one person!");
		return repository.save(person);
	}

	public Person update(Person person) {

		logger.info("Updating one person!");
		Person entity = repository.findById(person.getId()).orElseThrow(() -> new ResourceNotFoundException("No records found this id!"));
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		return repository.save(entity);
	}

	public void delete(Long id) {

		logger.info("Deleting one person!");
		Person person = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found this id!"));
		repository.delete(person);
	}
}
