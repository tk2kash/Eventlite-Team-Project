package uk.ac.man.cs.eventlite.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Response;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class VenueServiceImpl implements VenueService {
	
	private final static Logger log = LoggerFactory.getLogger(VenueServiceImpl.class);

	private final static String DATA = "data/venues.json";
	
	@Autowired
	private VenueRepository venueRepository;

	@Override
	public long count() {
		
		return venueRepository.count();
		
	}

	@Override
	public Iterable<Venue> findAll() {
		
		return venueRepository.findAll(Sort.by("name").ascending());
		
	}

	@Override
	public Iterable<Venue> findAllByNameOrderByNameAsc(String venue_name){
		return venueRepository.findAllByNameOrderByNameAsc(venue_name);
	}

	@Override
	public Iterable<Venue> findByNameIgnoreCaseContainingOrderByNameAsc(String venue_name){
		return venueRepository.findByNameIgnoreCaseContainingOrderByNameAsc(venue_name);
	}

	@Override
	public Venue save(Venue venue) {
		MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
				.accessToken("pk.eyJ1IjoiZXZlbnRsaXRlZjE0IiwiYSI6ImNsMmo1YWZ3YzBjNDUzZnMzeWE4Nmc5Nm4ifQ.142rc5SJ4pN3Q5_OfuOJtA")
				.query(venue.getAddress() + " " + venue.getPostcode()).geocodingTypes(GeocodingCriteria.TYPE_POSTCODE).country("GB").build();
		
		Response<GeocodingResponse> response;
		
		try {
			response = mapboxGeocoding.executeCall();
			
			List<CarmenFeature> results = response.body().features();
			
			if (results.size() > 0) {
				Point result = results.get(0).center();
				venue.setLat(result.latitude());
				venue.setLng(result.longitude());
			} else {
				// @TODO: error handling if the given location couldn't be found 
			}
			
		} catch (IOException e) {
			// @TODO Auto-generated catch block
			e.printStackTrace();
		}

		return venueRepository.save(venue);
		
	}
	
	@Override
	public Optional<Venue> findById(long id){
		return venueRepository.findById(id);
	}
	
	
	@Override
	public void deleteById(long id) {
		venueRepository.deleteById(id);
	}


}
