package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.*;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Date;

public interface EventRepository extends CrudRepository<Event, Long>{

	public Iterable<Event> findAll(Sort sort);

	public Iterable<Event> findAllByNameContaining(String event_name);
	
	public Iterable<Event> findAllByOrderByDateAscTimeAsc();
	
	public Iterable<Event> findByVenue(Venue venue);

	public List<Event> findAllByDateAfterOrderByDate(@NotNull(message = "Date can't be empty") @Future(message = "The date of the event must be in the future.") LocalDate date);

	public List<Event> findAllByDateBeforeOrderByDate(@NotNull(message = "Date can't be empty") @Future(message = "The date of the event must be in the future.") LocalDate date);

	public List<Event> findAllByNameIgnoreCaseContainingOrderByNameAsc(String name);

	public List<Event> findAllByOrderByDateDesc();
	
	public List<Event> findTop3ByVenueIdAndDateAfterOrderByDateAscNameAsc(Long id, LocalDate date);
	
	public List<Event> findByVenueId(Long id);

}