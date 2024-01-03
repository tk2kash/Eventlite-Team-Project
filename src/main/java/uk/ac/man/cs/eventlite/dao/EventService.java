package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import java.util.List;
import java.util.Optional;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();
	
	public Event save(Event event);

	public Iterable<Event> findAllByNameContaining(String event_name);
	
	public Iterable<Event> findByVenue(Venue venue);
	
	public List<Event> findNext3ByVenue(Venue venue);
	
	public void delete(Event event);
	
	public void deleteById(long id);
	
	public Optional<Event> findById(long id);

	public List<Event> findUpcomingEvents();

	public List<Event> findPastEvents();

	public List<Event> findAllByNameIgnoreCaseContainingOrderByNameAsc(String name);

	public List<Event> findAllByOrderByDateDesc();


}
