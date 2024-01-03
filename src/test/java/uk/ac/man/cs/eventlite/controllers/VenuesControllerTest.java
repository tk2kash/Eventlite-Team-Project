package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VenuesController.class)
@Import(Security.class)
public class VenuesControllerTest {

	@Autowired
	private MockMvc mvc;

	@Mock
	private Event event;

	@Mock
	private Venue venue;

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;

	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verify(venueService).findAll();
		//verify(venueService).findAll();
		verifyNoInteractions(event);
		verifyNoInteractions(venue);
	}

	@Test
	public void getIndexWithVenues() throws Exception {
		when(venue.getName()).thenReturn("Kilburn Building");
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(venue));

		when(event.getVenue()).thenReturn(venue);
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(event));

		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

//		verify(eventService).findAll();
		verify(venueService).findAll();
	}

	@Test
	public void getVenueNotFound() throws Exception {
		mvc.perform(get("/venues/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("venues/not_found")).andExpect(handler().methodName("getVenue"));
	}
	
	
	
	@Test
	public void deleteVenue() throws Exception {
		mvc.perform(delete("/venues/1").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML).with(csrf()))
		   .andExpect(status().isFound()).andExpect(view().name("redirect:/venues")).andExpect(handler().methodName("deleteById"));
		
		verify(venueService).deleteById(1);
		
	}
	
	@Test
	public void updateVenue() throws Exception {

		when(venue.getName()).thenReturn("VenueName");
		when(venueService.findById(1)).thenReturn(Optional.of(venue));

		mvc.perform(get("/venues/updateVenue?id=1").accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(view().name("venues/updateVenue.html"))
				.andExpect(handler().methodName("updateVenue"));

		verify(venueService).findById(1);
	}

	
	@Test 
	public void deleteVenuewithEvent() throws Exception {
	  when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(event));
	  when(event.getVenue()).thenReturn(venue);
	  when(venue.getId()).thenReturn(Long.valueOf(2));

	  mvc.perform(delete("/venues/2").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML).with(csrf()))
		 .andExpect(status().is(302)).andExpect(view().name("redirect:/venues")).andExpect(handler().methodName("deleteById"));
	  
	}

	 @Test
	public void getVenue() throws Exception {
		when(venueService.findById(1)).thenReturn(Optional.of(venue));

		mvc.perform(get("/venues/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/show")).andExpect(handler().methodName("getVenue"));

	}
	 
	 @Test
		public void getVenuewithFutureEvent() throws Exception {
		 when(venueService.findById(1)).thenReturn(Optional.of(venue));
		 when(eventService.findByVenue(venue)).thenReturn(Collections.<Event>singletonList(event));
		 when(event.getDate()).thenReturn(LocalDate.now().plusYears(1));
		 
			mvc.perform(get("/venues/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
					.andExpect(view().name("venues/show")).andExpect(handler().methodName("getVenue"));
		}
	 
	 @Test
		public void getVenuewithFutureTimeEvent() throws Exception {
		 when(venueService.findById(1)).thenReturn(Optional.of(venue));
		 when(eventService.findByVenue(venue)).thenReturn(Collections.<Event>singletonList(event));
		 when(event.getDate()).thenReturn(LocalDate.now());
		 when(event.getTime()).thenReturn(LocalTime.now().plusHours(1));
		 
			mvc.perform(get("/venues/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
					.andExpect(view().name("venues/show")).andExpect(handler().methodName("getVenue"));
		}
	 
	 @Test
	public void getNewVenue() throws Exception {
		mvc.perform(get("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk()).andExpect(view().name("venues/new"))
				.andExpect(handler().methodName("newVenue"));
	}
	 
	
	 
	@Test
	 public void createVenue() throws Exception {
	  ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
	  when(venueService.save(any(Venue.class))).then(returnsFirstArg());	  
	  
	  mvc.perform(post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
	    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    .param("id", "1")
	    .param("name", "Test1")
	    .param("capacity","200")
	    .param("address", "Oxford Road")
	    .param("postcode", "M15 5GR")
	    .accept(MediaType.TEXT_HTML).with(csrf()))
	    .andExpect(view().name("redirect:/venues"))
	    .andExpect(handler().methodName("createVenue"))
	    .andExpect(flash().attributeExists("ok_message"));
	  
	  verify(venueService).save(arg.capture());
	  assertThat("Test1", equalTo(arg.getValue().getName()));
	  assertThat(1L, equalTo(arg.getValue().getId()));
	 }
	
	@Test
	public void createVenueNoAuth() throws Exception {
		mvc.perform(post("/venues/new").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				 .param("id", "1")
			    .param("name", "Test1")
			    .param("capacity","200")
			    .param("address", "Oxford Road")
			    .param("postcode", "M15 5GR")
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
				.andExpect(header().string("Location", endsWith("/sign-in")));

		verify(venueService, never()).save(any(Venue.class));
	}
	
	@Test
	public void createVenueNoCsrf() throws Exception {
		mvc.perform(post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "1")
			    .param("name", "Test1")
			    .param("capacity","200")
			    .param("address", "Oxford Road")
			    .param("postcode", "M15 5GR")
				.accept(MediaType.TEXT_HTML)).andExpect(status().isForbidden());

		verify(venueService, never()).save(any(Venue.class));
	}
	
	@Test
	 public void createVenuewithError() throws Exception {
		
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
		  when(venueService.save(any(Venue.class))).then(returnsFirstArg());	  
		  
		  mvc.perform(post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
		    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .param("id", "1")
		    .param("capacity","-200")
		    .param("address", "Oxford Road")
		    .param("postcode", "M15 5GR")
		    .accept(MediaType.TEXT_HTML).with(csrf()))
		    .andExpect(view().name("venues/new"))
		    .andExpect(handler().methodName("createVenue"));
		  
		  verify(venueService, never()).save(arg.capture());
	 }
	
	@Test
	public void createVenuewithNoName() throws Exception {
		
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
		  when(venueService.save(any(Venue.class))).then(returnsFirstArg());	  
		  
		  mvc.perform(post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
		    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .param("id", "1")
		    .param("capacity","200")
		    .param("address", "Oxford Road")
		    .param("postcode", "M15 5GR")
		    .accept(MediaType.TEXT_HTML).with(csrf()))
		    .andExpect(view().name("venues/new"))
		    .andExpect(handler().methodName("createVenue"));
		  
		  verify(venueService, never()).save(arg.capture());
	 }
	
	@Test
	public void createVenuewithNoAddress() throws Exception {
		
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
		  when(venueService.save(any(Venue.class))).then(returnsFirstArg());	  
		  
		  mvc.perform(post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
		    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .param("id", "1")
		    .param("name", "Test1")
		    .param("capacity","200")
		    .param("postcode", "M15 5GR")
		    .accept(MediaType.TEXT_HTML).with(csrf()))
		    .andExpect(view().name("venues/new"))
		    .andExpect(handler().methodName("createVenue"));
		  
		  verify(venueService, never()).save(arg.capture());
	 }
	
	@Test
	public void createVenuewithNoPostcode() throws Exception {
		
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
		  when(venueService.save(any(Venue.class))).then(returnsFirstArg());	  
		  
		  mvc.perform(post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
		    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .param("id", "1")
		    .param("name", "Test1")
		    .param("capacity","200")
		    .param("address", "Oxford Road")
		    .accept(MediaType.TEXT_HTML).with(csrf()))
		    .andExpect(view().name("venues/new"))
		    .andExpect(handler().methodName("createVenue"));
		  
		  verify(venueService, never()).save(arg.capture());
	 }
	 
	@Test
	public void searchTestWithNoVenues() throws Exception {		
		when(event.getName()).thenReturn("EventName");
		when(venueService.findByNameIgnoreCaseContainingOrderByNameAsc(any(String.class))).thenReturn(Collections.<Venue>emptyList());
		when(eventService.findPastEvents()).thenReturn(Collections.<Event>emptyList());

		mvc.perform(get("/venues/search?name=Engi").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(view().name("venues/index"))
				.andExpect(handler().methodName("searchVenueResult"));

		verify(venueService).findByNameIgnoreCaseContainingOrderByNameAsc(any(String.class));
	}
	
	@Test
	public void searchTestWithVenueNoMatch() throws Exception {
		Venue venue1 =new Venue();
		venue1.setName("Kilburn Building");
		venue1.setCapacity(120);
		
		List<Venue> venue_match_list = new ArrayList<Venue>();
		venue_match_list.add(venue1);

		when(venueService.findByNameIgnoreCaseContainingOrderByNameAsc(any(String.class))).thenReturn(venue_match_list);

		mvc.perform(get("/venues/search?name=Engineer").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(view().name("venues/index"))
				.andExpect(handler().methodName("searchVenueResult"));

		verify(venueService).findByNameIgnoreCaseContainingOrderByNameAsc(any(String.class));
	}
	
	@Test
	public void searchTestWithVenueWithMatch() throws Exception {
		Venue venue1 =new Venue();
		venue1.setName("Engineering Building");
		venue1.setCapacity(120);
		
		List<Venue> venue_match_list = new ArrayList<Venue>();
		venue_match_list.add(venue1);

		when(venueService.findByNameIgnoreCaseContainingOrderByNameAsc(any(String.class))).thenReturn(venue_match_list);

		mvc.perform(get("/venues/search?name=Engineer").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(view().name("venues/index"))
				.andExpect(handler().methodName("searchVenueResult"));
		verify(venueService).findByNameIgnoreCaseContainingOrderByNameAsc(any(String.class));
	}
}
