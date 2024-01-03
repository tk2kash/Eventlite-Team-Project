package uk.ac.man.cs.eventlite.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import twitter4j.Status;
import twitter4j.TwitterException;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.TwitterService;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;
import uk.ac.man.cs.eventlite.dao.VenueService;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller
@RequestMapping(value = "/", produces = { MediaType.TEXT_HTML_VALUE })
public class HomePageController {
	@Autowired
	private EventService eventService;
	
    @Autowired
    private VenueService venueService;
    
    private TwitterService twitterService = new TwitterService();
	
	@GetMapping
	public String getAllEvents(Model model) {
		List<Event> events = eventService.findUpcomingEvents();
		model.addAttribute("events", events.subList(0, Math.min(3, events.size())));
		
		List<Venue> venues = (List<Venue>)venueService.findAll();
		
		Map<Venue, Integer> eventCounts = new HashMap<>();
		for (Venue v: venues) {
			List<Event> eventsInVenue = (List<Event>) eventService.findByVenue(v);
			eventCounts.put(v, eventsInVenue.size());
		} 
		
		venues.sort(Comparator.comparing(v -> eventCounts.get(v), Comparator.reverseOrder()));
		List<Status> timeline;
		try {
			timeline = twitterService.getTimeLine();
			if (timeline != null) {
			  if (timeline.size() > 5) {
					timeline = timeline.subList(0, 5);
			  }
			}
			model.addAttribute("timeline", timeline);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		model.addAttribute("venues", venues.subList(0, Math.min(3, venues.size())));
		model.addAttribute("eventCounts", eventCounts);
		
		return "homepage/index";
	}
}
