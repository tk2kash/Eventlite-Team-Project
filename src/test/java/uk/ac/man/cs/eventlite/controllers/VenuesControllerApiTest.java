package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.assemblers.VenueModelAssembler;
import uk.ac.man.cs.eventlite.assemblers.EventModelAssembler;
import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VenuesControllerApi.class)
@Import({ Security.class, VenueModelAssembler.class, EventModelAssembler.class })
public class VenuesControllerApiTest {
	
	private final static String BAD_ROLE = "USER";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private VenueService venueService;
	
	@MockBean 
	private EventService eventService;

	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")));

		verify(venueService).findAll();
	}

	@Test
	public void getIndexWitVenues() throws Exception {
		Venue v = new Venue();

		v.setId(0);
		v.setName("Venue");
		v.setCapacity(200);
		v.setAddress("Street");
		v.setPostcode("M13");
		v.setLat(12.00);
		v.setLng(12.00);
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(v));
		
		

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
				.andExpect(jsonPath("$._embedded.venues.length()", equalTo(1)));

		verify(venueService).findAll();
	}

	@Test
	public void getVenueNotFound() throws Exception {
		mvc.perform(get("/api/venues/99").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error", containsString("venue 99"))).andExpect(jsonPath("$.id", equalTo(99)))
				.andExpect(handler().methodName("getVenue"));
	}
	
	@Test
	public void getVenue() throws Exception {
		int id = 0;
		Venue v = new Venue();
		when(venueService.findById(id)).thenReturn(Optional.of(v));
		
		mvc.perform(get("/api/venues/{id}", id).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(handler().methodName("getVenue"))
		.andExpect(jsonPath("$._links.self.href", endsWith("" + id)));
	}
	
	@Test
	public void getEmptyVenuesList() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")));
	}
	
	
	@Test
	public void getVenuesList() throws Exception {
		Venue v = new Venue();
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(v));

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
				.andExpect(jsonPath("$._embedded.venues.length()", equalTo(1)));
	}
	
	@Test
	public void getNewVenue() throws Exception {
		mvc.perform(get("/api/venues/new").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotAcceptable())
				.andExpect(handler().methodName("newVenue"));
	}

	@Test
	public void postVenueNoAuth() throws Exception {
		mvc.perform(post("/api/venues").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"id\": \"1\",\"name\":\"test\",\"capacity\":\"200\",\"address\":\"Road\", \"postcode\":\"M13\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

		verify(venueService, never()).save(any(Venue.class));
	}

	@Test
	public void postVenueBadAuth() throws Exception {
		mvc.perform(post("/api/venues").with(anonymous()).contentType(MediaType.APPLICATION_JSON)
				.content("{ \"id\": \"1\",\"name\":\"test\",\"capacity\":\"200\",\"address\":\"Road\", \"postcode\":\"M13\"}")
	            .accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

		verify(venueService, never()).save(any(Venue.class));
	}

	@Test
	public void postVenueBadRole() throws Exception {
		mvc.perform(post("/api/venues").with(user("Rob").roles(BAD_ROLE)).contentType(MediaType.APPLICATION_JSON)
				.content("{ \"id\": \"1\",\"name\":\"test\",\"capacity\":\"200\",\"address\":\"Road\", \"postcode\":\"M13\"}")
	            .accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(venueService, never()).save(any(Venue.class));
	}
	
	@Test
	public void deleteVenue() throws Exception {

		mvc.perform(delete("/api/venues/0")
				.with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("deleteBookById"));

		verify(venueService).deleteById(0L);
	}
	
	@Test
	 public void getIndexWithVenues() throws Exception {
		Venue v = new Venue();
		v.setId(0);
	  
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(v));

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(2)))
			.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
			.andExpect(jsonPath("$._embedded.venues.length()", equalTo(1)));

		verify(venueService).findAll();
	 }	
	
}