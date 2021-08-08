package lambdademo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {

	@JsonProperty("centers")
	private List<Centers> centers;

	public List<Centers> getCenters() {
		return centers;
	}

	public void setCenters(List<Centers> centers) {
		this.centers = centers;
	}
	
	
}
