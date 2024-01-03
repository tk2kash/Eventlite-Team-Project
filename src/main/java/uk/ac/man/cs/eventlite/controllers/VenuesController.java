package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import java.lang.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenuesController {

    @RequestMapping(value="/search", method=RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public String searchVenueResult(@RequestParam(value="name", required=false) String venue_name, Model model){
        List<Venue> venues;

        // Search for venue
        Iterable<Venue> venuesList = venueService.findByNameIgnoreCaseContainingOrderByNameAsc(venue_name);
        Iterator<Venue> venuesListIterator = venuesList.iterator();

        while (venuesListIterator.hasNext()){
            Venue venue = venuesListIterator.next();

            if (!(venue.getName().toLowerCase().contains(venue_name.toLowerCase()))){
                venuesListIterator.remove();
            }

        }

        model.addAttribute("venues", venuesList);

        return "venues/index";
    }

    @Autowired
    private VenueService venueService;

    @Autowired
    private EventService eventService;

    @ExceptionHandler(VenueNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String venueNotFoundHandler(VenueNotFoundException ex, Model model) {
        model.addAttribute("not_found_id", ex.getId());

        return "venues/not_found";
    }

    @GetMapping("/{id}")
    public String getVenue(@PathVariable("id") long id, Model model) {
        Venue venue = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));
        ArrayList<Event> futureEvents = new ArrayList<Event>();
        
        for (Event event : eventService.findByVenue(venue)) {
			   if (event.getDate().compareTo(LocalDate.now()) > 0 || 
					   ((event.getDate().compareTo(LocalDate.now()) == 0) && 
					    (event.getTime().compareTo(LocalTime.now()) > 0))){
				   
			          futureEvents.add(event);
			    
		  }
        }
        
        model.addAttribute("venue", venue);
        model.addAttribute("events", futureEvents);
        
        return "venues/show";
    }

    @GetMapping
    public String getAllVenues(Model model) {

        model.addAttribute("venues", venueService.findAll());

        return "venues/index";
    }
    
    @GetMapping("/new")
	public String newVenue(Model model) {
		if (!model.containsAttribute("venue")) {
			model.addAttribute("venue", new Venue());
		}

		return "venues/new";

    }
    
    @PostMapping(path = "/new", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createVenue(@RequestBody @Valid @ModelAttribute Venue venue, BindingResult errors,
    		Model model, RedirectAttributes redirectAttrs) {

    	if (errors.hasErrors()) {
			model.addAttribute("venue", venue);
			return "venues/new";
		}
    	
    	venueService.save(venue);
    	redirectAttrs.addFlashAttribute("ok_message", "New venue added.");

    	return "redirect:/venues";
    }
    
    @DeleteMapping(value = "/{id}")
   	public String deleteById(@PathVariable("id") long id, RedirectAttributes redirectAttrs) {
    	
    	Iterable<Event> events = eventService.findAll();
    	
    	for(Event event: events) {
    		
    		if(event.getVenue().getId() == id) {
    			redirectAttrs.addFlashAttribute("error_message","The venue has some events, you can't delete it");
    			return "redirect:/venues";
    		}
    	}
    	
   		venueService.deleteById(id);
   		return "redirect:/venues" ;
   	}
    
    @RequestMapping("updateVenue")
    public String updateVenue(@RequestParam("id") long id, Model model) {
    	Venue venue = venueService.findById(id).get();
    	model.addAttribute("venue", venue);
    	return "venues/updateVenue.html";
    }
    
    @PostMapping(path = "/update", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String update(@RequestBody @Valid @ModelAttribute Venue venue, BindingResult errors,
    		Model model, RedirectAttributes redirectAttrs) {

    	if (errors.hasErrors()) {
			model.addAttribute("venue", venue);
			return "venues/updateVenue";
		}
    	
    	venueService.save(venue);
    	redirectAttrs.addFlashAttribute("ok_message", "Venue updated.");

    	return "redirect:/venues";
    }
}