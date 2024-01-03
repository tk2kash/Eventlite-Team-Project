package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public interface VenueRepository extends CrudRepository<Venue, Long>{
	
	public Iterable<Venue> findAllByName(String name);

	public Iterable<Venue> findAllByNameOrderByNameAsc(String name);

	public Iterable<Venue> findByNameIgnoreCaseContainingOrderByNameAsc(String name);

	public Iterable<Venue> findAll(Sort sort);
	
	public Venue findFirstByNameOrderByNameAsc(String name);

	public Venue findByNameContainingAndCapacity(String nameSearch, int capacity);

	public Iterable<Venue> findAllByCapacityBetween(int min, int max);

}
