package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.man.cs.eventlite.assemblers.VenueModelAssembler;
import uk.ac.man.cs.eventlite.assemblers.EventModelAssembler;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;

@RestController
@RequestMapping(value = "/api/venues", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class VenuesControllerApi {

    private static final String NOT_FOUND_MSG = "{ \"error\": \"%s\", \"id\": %d }";

    @Autowired
    private VenueService venueService;
    
    @Autowired
    private EventService eventService;

    @Autowired
    private VenueModelAssembler venueAssembler;
    
    @Autowired
	private EventModelAssembler eventAssembler;

    @ExceptionHandler(VenueNotFoundException.class)
    public ResponseEntity<?> venueNotFoundHandler(VenueNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format(NOT_FOUND_MSG, ex.getMessage(), ex.getId()));
    }

    @GetMapping("/{id}")
    public EntityModel<Venue> getVenue(@PathVariable("id") long id) {
        Venue venue = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));

        return venueAssembler.toModel(venue)
        		.add(linkTo(methodOn(VenuesControllerApi.class).getEventsByVenue(venue)).withRel("events"))
        		.add(linkTo(methodOn(VenuesControllerApi.class).getNext3EventsByVenue(venue)).withRel("next3events"));

    }


    @GetMapping
    public CollectionModel<EntityModel<Venue>> getAllVenues() {
        return venueAssembler.toCollectionModel(venueService.findAll())
                .add(linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).withSelfRel());
    }

    @GetMapping("/new")
    public ResponseEntity<?> newVenue() {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createVenue(@RequestBody @Valid Venue venue, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        Venue newVenue = venueService.save(venue);
        EntityModel<Venue> entity = venueAssembler.toModel(newVenue);

        return ResponseEntity.created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri()).build();
    }

    @DeleteMapping(path="{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable long id) {
        try {
            venueService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/events")
    public CollectionModel<EntityModel<Event>> getEventsByVenue(@PathVariable("id") Venue venue) {
		return eventAssembler.toCollectionModel(eventService.findByVenue(venue));
	}
    
    @GetMapping("/{id}/next3events")
	public CollectionModel<EntityModel<Event>> getNext3EventsByVenue(@PathVariable("id") Venue venue) {
		return eventAssembler.toCollectionModel(eventService.findNext3ByVenue(venue));
	}

}
