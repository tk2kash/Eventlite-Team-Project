package uk.ac.man.cs.eventlite.dao;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class EventServiceImpl implements EventService {

	private final static Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

	private final static String DATA = "data/events.json";
	
	@Autowired
	private EventRepository eventRepository;

	@Override
	public Iterable<Event> findAllByNameContaining(String event_name){
		return eventRepository.findAllByNameContaining(event_name);
	}

	@Override
	public long count() {
		return eventRepository.count();
	}

	@Override
	public Iterable<Event> findAll() {
		return eventRepository.findAll(Sort.by("date").ascending().and(Sort.by("name").ascending()));
	}

	@Override
	public List<Event> findUpcomingEvents() {
		return eventRepository.findAllByDateAfterOrderByDate(LocalDate.now());

	}

	@Override
	public List<Event> findPastEvents() {
		return eventRepository.findAllByDateBeforeOrderByDate(LocalDate.now());
	}

	@Override
	public List<Event> findAllByNameIgnoreCaseContainingOrderByNameAsc(String event_name) {
		return eventRepository.findAllByNameIgnoreCaseContainingOrderByNameAsc(event_name);
	}

	@Override
	public List<Event> findAllByOrderByDateDesc() {
		return eventRepository.findAllByOrderByDateDesc();
	}

	@Override
	public Event save(Event event) {
		return eventRepository.save(event);
	}
	
	@Override
	public void delete(Event event) {
		eventRepository.delete(event);
	}

	
	@Override
	public void deleteById(long id) {
		eventRepository.deleteById(id);
	}
	
	@Override
    public Optional<Event> findById(long id){
    	return eventRepository.findById(id);
    }
	
	@Override
	public Iterable<Event> findByVenue(Venue venue){
		return eventRepository.findByVenue(venue);
	}
	
	@Override
	public List<Event> findNext3ByVenue(Venue venue){
		return eventRepository.findTop3ByVenueIdAndDateAfterOrderByDateAscNameAsc(venue.getId(), LocalDate.now());
	}

}
