package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import twitter4j.Status;
import twitter4j.TwitterException;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.TwitterService;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import java.lang.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.man.cs.eventlite.dao.VenueService;

import uk.ac.man.cs.eventlite.entities.Event;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@RequestMapping(value="/search", method=RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String searchEventResult(@RequestParam(value="name", required=false) String event_name, Model model){
		List<Event> events;

		// Search for future events
		Iterable<Event> upcomingEventsList = eventService.findUpcomingEvents();
		Iterator<Event> upcomingEventsListIterator = upcomingEventsList.iterator();

		while (upcomingEventsListIterator.hasNext()){
			Event event = upcomingEventsListIterator.next();

			if (!(event.getName().toLowerCase().contains(event_name.toLowerCase()))){
				upcomingEventsListIterator.remove();
			}

		}

		// Search for previous events
		Iterable<Event> pastEventsList = eventService.findPastEvents();
		Iterator<Event> pastEventsListIterator = pastEventsList.iterator();

		while (pastEventsListIterator.hasNext()){
			Event event = pastEventsListIterator.next();

			if (!(event.getName().toLowerCase().contains(event_name.toLowerCase()))){
				pastEventsListIterator.remove();
			}

		}

		events = (List<Event>) upcomingEventsList;
		model.addAttribute("futureEvents", (List<Event>) upcomingEventsList);
		model.addAttribute("previousEvents", (List<Event>) pastEventsList);


		return "events/index";
	}

	@Autowired
	private EventService eventService;

    @Autowired
    private VenueService venueService;
    
    private TwitterService twitterService = new TwitterService();

	@ExceptionHandler(EventNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String eventNotFoundHandler(EventNotFoundException ex, Model model) {
		model.addAttribute("not_found_id", ex.getId());

		return "events/not_found";
	}

	@GetMapping("/{id}")
	public String getEvent(@PathVariable("id") long id, Model model) {

		Event event = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		
		model.addAttribute("event", event);
		
		return "events/show";
	}

	@GetMapping
	public String getAllEvents(Model model) {
		
		  ArrayList<Event> futureEvents = new ArrayList<Event>();
		  ArrayList<Event> previousEvents = new ArrayList<Event>();
		  
		  for (Event event : eventService.findAll()) {
			   if (event.getDate().compareTo(LocalDate.now()) > 0 || 
					   ((event.getDate().compareTo(LocalDate.now()) == 0) && 
					    (event.getTime().compareTo(LocalTime.now()) > 0))){
				   
			          futureEvents.add(event);
			    
			   } else {
			          previousEvents.add(event);
			   }
		  }
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
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	
				
		  model.addAttribute("futureEvents", futureEvents);
		  model.addAttribute("previousEvents", previousEvents);

		 return "events/index";
	}
	
    @DeleteMapping(value = "/{id}")
	public String deleteById(@PathVariable("id") long id) {
		eventService.deleteById(id);
		return "redirect:/events" ;
	}
    
    @GetMapping("/new")
    public String newEvent(Model model) {
    	if (!model.containsAttribute("event")) {
    		model.addAttribute("event", new Event());
    	}

    	if (!model.containsAttribute("venues")) {
    		model.addAttribute("venues", venueService.findAll());
    	}

    	return "events/new";
    }
    
    @RequestMapping("/updateEvent")
    public String updateEvent(@RequestParam("id") long id, Model model) {
    	Event event = eventService.findById(id).get();
    	model.addAttribute("events", event);
    	model.addAttribute("venues", venueService.findAll());
    	
    	return "events/updateEvent.html";
    }

    @PostMapping(path = "/createEvent", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createEvent(@RequestBody @Valid @ModelAttribute Event event, BindingResult errors,
    		Model model, RedirectAttributes redirectAttrs) {

    	if (errors.hasErrors()) {
			model.addAttribute("event", event);
			model.addAttribute("venues", venueService.findAll());
			return "events/new";
		}

    	eventService.save(event);
    	redirectAttrs.addFlashAttribute("ok_message", "New event added.");

    	return "redirect:/events";
    }
    
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String update(@RequestBody @Valid @ModelAttribute("events") Event events, BindingResult errors, Model model, RedirectAttributes redirectAttrs) {

    	if (errors.hasErrors()) {
			model.addAttribute("events", events);
			model.addAttribute("venues", venueService.findAll());
			
			return "events/updateEvent";
		}

    	eventService.save(events);
    	redirectAttrs.addFlashAttribute("ok_message", "Event updated.");

    	return "redirect:/events";
    }
  
	@PostMapping(value = "/{id}")
	public String tweetEvent(@PathVariable("id") long id,
							 @RequestParam("tweet") String tweet, 
							 RedirectAttributes redirectAttrs) throws TwitterException {
		Event event = eventService.findById(id).get();
		String tweetwithNameString = "@" +event.getName() + ": " + tweet ;
    
		Status post = twitterService.createTweet(tweetwithNameString);
		String message = String.format("Your tweet: %s was posted.", post.getText());
		redirectAttrs.addFlashAttribute("message", message);
	
		return String.format("redirect:/events/%d", id);
	}
}
