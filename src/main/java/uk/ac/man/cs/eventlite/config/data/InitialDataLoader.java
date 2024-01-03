package uk.ac.man.cs.eventlite.config.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.ac.man.cs.eventlite.entities.*;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
@Profile("default")
public class InitialDataLoader {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			if (venueService.count() > 0) {
				log.info("Database already populated with venues. Skipping venue initialization.");
			} else {
				Venue venue1 =new Venue();
				venue1.setName("Kilburn Building");
				venue1.setCapacity(120);
				venue1.setAddress("Oxford Road");
				venue1.setPostcode("M13 9PL");
				log.info("Initialise venue 1 :" + venueService.save(venue1));
				
				Venue venue2 =new Venue();
				venue2.setName("Online");
				venue2.setCapacity(100000);
				venue2.setAddress("No where");
				venue2.setPostcode("M5");
				log.info("Initialise venue 2 : "+ venueService.save(venue2));

			}

			if (eventService.count() > 0) {
				log.info("Database already populated with events. Skipping event initialization.");
			} else {
				Event event1=new Event();
				Venue venue3 =new Venue();
				venue3.setName("Engineering Building");
				venue3.setCapacity(10000);
				venue3.setAddress("Booth St E");
				venue3.setPostcode("M13");
				venueService.save(venue3);
				LocalDate localdate1 = LocalDate.of(2022, 10, 3);
				LocalTime localtime1 = LocalTime.of(16, 00);;
				event1.setDate(localdate1);
				event1.setName("COMP23412 Lab");
				event1.setTime(localtime1);
				event1.setVenue(venue3);
				log.info("Initialize event1 :" + eventService.save(event1));
				
				Event event2=new Event();
				Venue venue4 =new Venue();
				venue4.setName("Engineering Building");
				venue4.setCapacity(10000);
				venue4.setAddress("Booth St E");
				venue4.setPostcode("M13");
				venueService.save(venue4);
				LocalDate localdate2 = LocalDate.of(2022, 9, 8);
				LocalTime localtime2 = LocalTime.of(12, 00);
				event2.setDate(localdate2);
				event2.setName("PASS");
				event2.setTime(localtime2);
				event2.setVenue(venue4);
				log.info("Initialize event2 : "+ eventService.save(event2));
				
				Event event3=new Event();
				Venue venue5 =new Venue();
				venue5.setName("Kilburn");
				venue5.setCapacity(5000);
				venue5.setAddress("Oxford Road");
				venue5.setPostcode("M13 9PL");
				venueService.save(venue5);
				LocalDate localdate3 = LocalDate.of(2022, 03, 04);
				LocalTime localtime3 = LocalTime.of(10, 00);
				event3.setDate(localdate3);
				event3.setName("COMP26020 Lecture");
				event3.setTime(localtime3);
				event3.setVenue(venue5);
				log.info("Initialize event3 : "+ eventService.save(event3));// Build and save initial events here.
			}
		};
	}
}
