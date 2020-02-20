package model;

public class Destination {

	private int id;
	private String name;
	private String country;
	private String airportName;
	private String airportCode;
	private String photo;
	private boolean active;
	//LOCATION
	
	public Destination() {
		super();
	}
	public Destination(int id, String name, String country, String airportName, String airportCode, String photo,
			boolean active) {
		super();
		this.id = id;
		this.name = name;
		this.country = country;
		this.airportName = airportName;
		this.airportCode = airportCode;
		this.photo = photo;
		this.active = active;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAirportName() {
		return airportName;
	}
	public void setAirportName(String airportName) {
		this.airportName = airportName;
	}
	public String getAirportCode() {
		return airportCode;
	}
	public void setAirportCode(String airportCode) {
		this.airportCode = airportCode;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
}
