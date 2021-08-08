package lambdademo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sessions {

	@JsonProperty("session_id")
	private String session_id;
	
	@JsonProperty("date")
	private String date;
	
	@JsonProperty("available_capacity")
	private Long available_capacity;
	
	@JsonProperty("min_age_limit")
	private Long min_age_limit;
	
	@JsonProperty("vaccine")
	private String vaccine;
	
	@JsonProperty("slots")
	private List<String> slots;
	
	@JsonProperty("available_capacity_dose1")
	private Long available_capacity_dose1;
	
	@JsonProperty("available_capacity_dose2")
	private Long available_capacity_dose2;

	public Long getAvailable_capacity_dose1() {
		return available_capacity_dose1;
	}

	public void setAvailable_capacity_dose1(Long available_capacity_dose1) {
		this.available_capacity_dose1 = available_capacity_dose1;
	}

	public Long getAvailable_capacity_dose2() {
		return available_capacity_dose2;
	}

	public void setAvailable_capacity_dose2(Long available_capacity_dose2) {
		this.available_capacity_dose2 = available_capacity_dose2;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Long getAvailable_capacity() {
		return available_capacity;
	}

	public void setAvailable_capacity(Long available_capacity) {
		this.available_capacity = available_capacity;
	}

	public Long getMin_age_limit() {
		return min_age_limit;
	}

	public void setMin_age_limit(Long min_age_limit) {
		this.min_age_limit = min_age_limit;
	}

	public String getVaccine() {
		return vaccine;
	}

	public void setVaccine(String vaccine) {
		this.vaccine = vaccine;
	}

	public List<String> getSlots() {
		return slots;
	}

	public void setSlots(List<String> slots) {
		this.slots = slots;
	}
	
	
}
