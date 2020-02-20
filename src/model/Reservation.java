package model;

import java.util.Date;

public class Reservation {

	private int id;
	private User user;
	private Date date;
	private TicketType ticketType;
	private int numOfPassengers;
	private String flightNumber;
	
	public Reservation() {
		super();
	}
	public Reservation(int id, User user, Date date, TicketType ticketType, int numOfPassengers, String num) {
		super();
		this.id = id;
		this.user = user;
		this.date = date;
		this.ticketType = ticketType;
		this.numOfPassengers = numOfPassengers;
		this.flightNumber = num;
	}
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public TicketType getTicketType() {
		return ticketType;
	}
	public void setTicketType(TicketType ticketType) {
		this.ticketType = ticketType;
	}
	public int getNumOfPassengers() {
		return numOfPassengers;
	}
	public void setNumOfPassengers(int numOfPassengers) {
		this.numOfPassengers = numOfPassengers;
	}
	
	
}
