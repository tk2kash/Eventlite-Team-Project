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

import uk.ac.man.cs.eventlite.assemblers.EventModelAssembler;
import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventsControllerApi.class)
@Import({ Security.class, EventModelAssembler.class })
public class EventsControllerApiTest {
	
	private final static String BAD_ROLE = "USER";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private EventService eventService;

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());

		mvc.perform(get("/api/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllEvents")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/events")));

		verify(eventService).findAll();
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		Event e = new Event();
		Venue venue = new Venue();
		venue.setId(0);
		e.setId(0);
		e.setName("Event");
		e.setDate(LocalDate.now());
		e.setTime(LocalTime.now());
		e.setVenue(venue);
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(e));

		mvc.perform(get("/api/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllEvents")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/events")))
				.andExpect(jsonPath("$._embedded.events.length()", equalTo(1)));

		verify(eventService).findAll();
	}

	@Test
	public void getEventNotFound() throws Exception {
		mvc.perform(get("/api/events/99").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error", containsString("event 99"))).andExpect(jsonPath("$.id", equalTo(99)))
				.andExpect(handler().methodName("getEvent"));
	}
	
	@Test
	public void getEvent() throws Exception {
		Event e = new Event();
		Venue v = new Venue();
		e.setId(0);
		e.setName("Event");
		e.setDate(LocalDate.now());
		e.setVenue(v);
		when(eventService.findById(0L)).thenReturn(Optional.of(e));

		mvc.perform(get("/api/events/0")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("getEvent"))
				.andExpect(jsonPath("$.id", equalTo(0)))
				.andExpect(jsonPath("$.name", equalTo("Event")))
				.andExpect(jsonPath("$.date", equalTo(LocalDate.now().toString())))
				.andExpect(jsonPath("$.venue.id", equalTo(0)));

		verify(eventService).findById(0L);
	}
	
	@Test
	public void getEmptyEventsList() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());

		mvc.perform(get("/api/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllEvents")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/events")));
	}
	
	
	@Test
	public void getEventsList() throws Exception {
		Event e = new Event();
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(e));

		mvc.perform(get("/api/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllEvents")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/events")))
				.andExpect(jsonPath("$._embedded.events.length()", equalTo(1)));
	}
	
	@Test
	public void getNewEvent() throws Exception {
		mvc.perform(get("/api/events/new").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotAcceptable())
				.andExpect(handler().methodName("newEvent"));
	}

	@Test
	public void postEventNoAuth() throws Exception {
		mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"id\": \"1\",\"date\":\"2022-02-01\",\"time\":\"12:00\",\"description\":\"1111\", \"venue\":{\"id\":\"1\"}}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

		verify(eventService, never()).save(any(Event.class));
	}

	@Test
	public void postEventBadAuth() throws Exception {
		mvc.perform(post("/api/events").with(anonymous()).contentType(MediaType.APPLICATION_JSON)
				.content("{ \"id\": \"1\",\"date\":\"2022-02-01\",\"time\":\"12:00\",\"description\":\"1111\", \"venue\":{\"id\":\"1\"}}")
	            .accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

		verify(eventService, never()).save(any(Event.class));
	}

	@Test
	public void postEventBadRole() throws Exception {
		mvc.perform(post("/api/events").with(user("Rob").roles(BAD_ROLE)).contentType(MediaType.APPLICATION_JSON)
				.content("{ \"id\": \"1\",\"date\":\"2022-02-01\",\"time\":\"12:00\",\"description\":\"1111\", \"venue\":{\"id\":\"1\"}}")
	            .accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(eventService, never()).save(any(Event.class));
	}
	


}
