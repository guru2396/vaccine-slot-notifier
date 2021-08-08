package lambdademo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Centers {

	@JsonProperty("center_id")
	private Long center_id;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("address")
	private String address;
	
	@JsonProperty("district_name")
	private String district_name;
	
	@JsonProperty("block_name")
	private String block_name;
	
	@JsonProperty("pincode")
	private Long pincode;
	
	@JsonProperty("lat")
	private Long lat;
	
	@JsonProperty("long")
	private Long llong;
	
	@JsonProperty("from")
	private String from;
	
	@JsonProperty("to")
	private String to;
	
	@JsonProperty("fee_type")
	private String fee_type;
	
	@JsonProperty("sessions")
	private List<Sessions> sessions;

	public Long getCenter_id() {
		return center_id;
	}

	public void setCenter_id(Long center_id) {
		this.center_id = center_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDistrict_name() {
		return district_name;
	}

	public void setDistrict_name(String district_name) {
		this.district_name = district_name;
	}

	public String getBlock_name() {
		return block_name;
	}

	public void setBlock_name(String block_name) {
		this.block_name = block_name;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}

	public Long getLat() {
		return lat;
	}

	public void setLat(Long lat) {
		this.lat = lat;
	}

	public Long getLlong() {
		return llong;
	}

	public void setLlong(Long llong) {
		this.llong = llong;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public List<Sessions> getSessions() {
		return sessions;
	}

	public void setSessions(List<Sessions> sessions) {
		this.sessions = sessions;
	}
	
	
}
