package uk.ac.man.cs.eventlite.config.data;

import java.time.LocalDate;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.entities.Event;

@Configuration
@Profile("test")
public class TestDataLoader {

	private final static Logger log = LoggerFactory.getLogger(TestDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
				Venue venue1 =new Venue();
				venue1.setName("Kilburn Building");
				venue1.setCapacity(120);
				log.info("Initialise venue 1 :" + venueService.save(venue1));
				
				Venue venue2 =new Venue();
				venue2.setName("Online");
				venue2.setCapacity(100000);
				log.info("Initialise venue 2 : "+ venueService.save(venue2));


				Event event1=new Event();
				LocalDate localdate1 = LocalDate.of(2022, 03, 01);
				LocalTime localtime1 = LocalTime.of(16, 00);
				event1.setDate(localdate1);
				event1.setName("COMP23412 Lab");
				event1.setTime(localtime1);
				event1.setVenue(venue1);
				log.info("Initialize event1 :" + eventService.save(event1));
				
				Event event2=new Event();
				LocalDate localdate2 = LocalDate.of(2022, 03, 03);
				LocalTime localtime2 = LocalTime.of(12, 00);
				event2.setDate(localdate2);
				event2.setName("PASS");
				event2.setTime(localtime2);
				event2.setVenue(venue1);
				log.info("Initialize event2 : "+ eventService.save(event2));// Build and save initial events here.
	
				Event event3=new Event();
				LocalDate localdate3 = LocalDate.of(2022, 03, 04);
				LocalTime localtime3 = LocalTime.of(10, 00);
				event3.setDate(localdate3);
				event3.setName("COMP26020 Lecture");
				event3.setTime(localtime3);
				event3.setVenue(venue1);
				log.info("Initialize event2 : "+ eventService.save(event3));
				
		};
	}
}
