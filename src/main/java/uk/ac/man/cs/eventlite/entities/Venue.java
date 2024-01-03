package uk.ac.man.cs.eventlite.entities;
import java.io.IOException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Response;
import uk.ac.man.cs.eventlite.config.data.InitialDataLoader;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Entity
@Table(name="venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

	@NotEmpty(message = "Venue must have a name")
	@Size(max = 256, message = "Event name must have < 256 characters")
	private String name;
	
	@Min(value=1, message = "Capacity must be above zero")
	private int capacity;
	
	@NotEmpty(message = "Venue must have an address")
	@Size(max = 300, message = "Address must have < 300 characters")
	private String address;
	
	@NotEmpty(message = "Venue must have a postcode")
	@Size(max = 30, message = "Postcode must have < 10 characters")
	private String postcode;
	
	//@NotNull(message = "Venue must have a Latitude")
	@Min(value=-90, message = "Latitude must be between -90 and 90")
	@Max(value=90, message = "Latitude must be between -90 and 90")
	private Double lat;
	
	//@NotNull(message = "Venue must have a Longitude")
	@Min(value=-180, message = "Longitude must be between -180 and 180")
	@Max(value=180, message = "Longitude must be between -180 and 180")
	private Double lng;

	public Venue() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	
	public String getPostcode() {
		return postcode;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	public Double getLng() {
		return lng;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLat() {
		return lat;
	}
}