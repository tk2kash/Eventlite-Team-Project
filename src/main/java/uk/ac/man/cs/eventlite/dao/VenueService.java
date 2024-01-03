package uk.ac.man.cs.eventlite.dao;

import java.util.Optional;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {

	public long count();

	public Iterable<Venue> findAll();
	
	public Venue save(Venue venue);

	public Iterable<Venue> findAllByNameOrderByNameAsc(String name);

	public Iterable<Venue> findByNameIgnoreCaseContainingOrderByNameAsc(String name);
	
	public Optional<Venue> findById(long id);
	
	public void deleteById(long id);
}
