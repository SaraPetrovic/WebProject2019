package model;

import java.util.ArrayList;
import java.util.Date;

public class Flight {

	private int id;
	private String flightNumber;
	private Destination startDestination;
	private Destination endDestination;
	private ArrayList<Reservation> reservations;
	private double priceFirst;
	private double priceBusiness;
	private double priceEconomic;
	private int firstClass;
	private int businessClass;
	private int economicClass;
	private Date date;
	private FlightType flightType;
	private boolean active;
	
	public Flight() {
		super();
	}
	public Flight(int id, String flightNumber, double priceFirst, double priceBusiness, double priceEconomic, int firstClass,
			int businessClass, int economicClass,  boolean active) {
		super();
		this.id = id;
		this.flightNumber = flightNumber;
		this.priceFirst = priceFirst;
		this.priceBusiness = priceBusiness;
		this.priceEconomic = priceEconomic;
		this.firstClass = firstClass;
		this.businessClass = businessClass;
		this.economicClass = economicClass;
		this.active = active;
	}
	public Flight(int id, String flightNumber, Destination startDestination, Destination endDestination,
			ArrayList<Reservation> reservations, double priceFirst, double priceBusiness, double priceEconomic, int firstClass, int businessClass, int economicClass,
			Date date, FlightType flightType, boolean active) {
		super();
		this.id = id;
		this.flightNumber = flightNumber;
		this.startDestination = startDestination;
		this.endDestination = endDestination;
		this.reservations = reservations;
		this.priceFirst = priceFirst;
		this.priceBusiness = priceBusiness;
		this.priceEconomic = priceEconomic;
		this.firstClass = firstClass;
		this.businessClass = businessClass;
		this.economicClass = economicClass;
		this.date = date;
		this.flightType = flightType;
		this.active = active;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	public Destination getStartDestination() {
		return startDestination;
	}
	public void setStartDestination(Destination startDestination) {
		this.startDestination = startDestination;
	}
	public Destination getEndDestination() {
		return endDestination;
	}
	public void setEndDestination(Destination endDestination) {
		this.endDestination = endDestination;
	}
	public ArrayList<Reservation> getReservations() {
		return reservations;
	}
	public void setReservations(ArrayList<Reservation> reservations) {
		this.reservations = reservations;
	}
	public int getFirstClass() {
		return firstClass;
	}
	public void setFirstClass(int firstClass) {
		this.firstClass = firstClass;
	}
	public int getBusinessClass() {
		return businessClass;
	}
	public void setBusinessClass(int businessClass) {
		this.businessClass = businessClass;
	}
	public int getEconomicClass() {
		return economicClass;
	}
	public void setEconomicClass(int economicClass) {
		this.economicClass = economicClass;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public FlightType getFlightType() {
		return flightType;
	}
	public void setFlightType(FlightType flightType) {
		this.flightType = flightType;
	}
	public double getPriceFirst() {
		return priceFirst;
	}
	public void setPriceFirst(double priceFirst) {
		this.priceFirst = priceFirst;
	}
	public double getPriceBusiness() {
		return priceBusiness;
	}
	public void setPriceBusiness(double priceBusiness) {
		this.priceBusiness = priceBusiness;
	}
	public double getPriceEconomic() {
		return priceEconomic;
	}
	public void setPriceEconomic(double priceEconomic) {
		this.priceEconomic = priceEconomic;
	}
	
}
