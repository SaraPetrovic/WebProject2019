package controllers;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import model.Flight;
import model.Reservation;
import model.TicketType;
import model.User;
import repositories.DestinationRepository;
import repositories.FlightRepository;
import repositories.ReservationRepository;
import repositories.UserRepository;

@Path("/reservation")
public class ReservationController {

	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	
	public static int counterReservation = 0;
	public static int idReservation;
	
	
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserReservations() {
		
		User user =  (User) request.getSession().getAttribute("user");
		ArrayList<Reservation> list = new ArrayList<Reservation>();
		
		for(Reservation r : getReservations().getValues()) {
			if(r.getUser().getId() == user.getId()) {
				list.add(r);
			}
		}
		return Response.status(Status.OK).entity(list).build();
	}
	
	@POST
	@Path("/book")
	@Produces(MediaType.APPLICATION_JSON)
	public Response bookTicket(Reservation r) {
		User user =  (User) request.getSession().getAttribute("user");
		if(user.isBlocked() == true) {
			return Response.status(Status.CONFLICT).build();
		}
		Flight flight = getFlights().getFlight(r.getFlightNumber());
		
		switch(r.getTicketType()) {
		case ECONOMIC:
			if(flight.getEconomicClass() < r.getNumOfPassengers())
				return Response.status(Status.BAD_REQUEST).build();
			else
				flight.setEconomicClass(flight.getEconomicClass() - r.getNumOfPassengers());
			break;
		case FIRST:
			if(flight.getFirstClass() < r.getNumOfPassengers())
				return Response.status(Status.BAD_REQUEST).build();
			else
				flight.setFirstClass(flight.getFirstClass() - r.getNumOfPassengers());
			break;
		case BUSINESS:
			if(flight.getBusinessClass() < r.getNumOfPassengers())
				return Response.status(Status.BAD_REQUEST).build();
			else
				flight.setBusinessClass(flight.getBusinessClass() - r.getNumOfPassengers());
			break;
		}
		if(counterReservation == 0) {
			idReservation = getReservations().getValues().size();
			counterReservation = 1;
		}
		idReservation++;
		Reservation reservation = new Reservation();
		reservation.setId(idReservation);
		reservation.setUser(user);
		reservation.setDate(flight.getDate());
		reservation.setTicketType(r.getTicketType());
		reservation.setNumOfPassengers(r.getNumOfPassengers());
		reservation.setFlightNumber(r.getFlightNumber());
		
		getReservations().getReservations().put(idReservation, reservation);
		flight.getReservations().add(reservation);
		
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		double totalPrice = 0;
		switch(r.getTicketType()) {
		case FIRST:
			totalPrice = r.getNumOfPassengers() * flight.getPriceFirst();
			break;
		case BUSINESS:
			totalPrice = r.getNumOfPassengers() * flight.getPriceBusiness();
			break;
		case ECONOMIC:
			totalPrice = r.getNumOfPassengers() * flight.getPriceEconomic();
			break;
		}
		return Response.status(Status.OK).entity(totalPrice).build();
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addReservation(Reservation r) {
		User user =  (User) request.getSession().getAttribute("user");
		if(user.isBlocked() == true) {
			return Response.status(Status.CONFLICT).build();
		}
		Flight flight = getFlights().getFlight(r.getFlightNumber());
		
		switch(r.getTicketType()) {
		case ECONOMIC:
			if(flight.getEconomicClass() < r.getNumOfPassengers())
				return Response.status(Status.BAD_REQUEST).build();
			else
				flight.setEconomicClass(flight.getEconomicClass() - r.getNumOfPassengers());
			break;
		case FIRST:
			if(flight.getFirstClass() < r.getNumOfPassengers())
				return Response.status(Status.BAD_REQUEST).build();
			else
				flight.setFirstClass(flight.getFirstClass() - r.getNumOfPassengers());
			break;
		case BUSINESS:
			if(flight.getBusinessClass() < r.getNumOfPassengers())
				return Response.status(Status.BAD_REQUEST).build();
			else
				flight.setBusinessClass(flight.getBusinessClass() - r.getNumOfPassengers());
			break;
		}
		if(counterReservation == 0) {
			idReservation = getReservations().getValues().size();
			counterReservation = 1;
		}
		idReservation++;
		Reservation reservation = new Reservation();
		reservation.setId(idReservation);
		reservation.setUser(user);
		Date d = Calendar.getInstance(Locale.getDefault()).getTime();
		System.out.println("DATUM: " +  d);
		reservation.setDate(d);
		reservation.setTicketType(r.getTicketType());
		reservation.setNumOfPassengers(r.getNumOfPassengers());
		reservation.setFlightNumber(r.getFlightNumber());
		
		getReservations().getReservations().put(idReservation, reservation);
		flight.getReservations().add(reservation);
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(reservation).build();
	}
	
	@POST
	@Path("/cancel/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelReservation(@PathParam("id") int id) {
		Reservation reservation = null;
		for(Reservation r : getReservations().getValues()) {
			if(r.getId() == id) {
				reservation = r;
			}
		}
		if(reservation.getUser().isBlocked()) {
			return Response.status(Status.CONFLICT).build();
		}
		Flight flight = getFlights().getFlight(reservation.getFlightNumber());
		if(flight != null) {
			Date date = new Date();
			if(date.before(flight.getDate())) {
				flight.getReservations().remove(reservation);
			}else {
				return Response.status(Status.BAD_REQUEST).build();
			}
			if(reservation.getTicketType() == TicketType.BUSINESS) {
				flight.setBusinessClass(flight.getBusinessClass() + reservation.getNumOfPassengers());
			}else if(reservation.getTicketType() == TicketType.FIRST) {
				flight.setFirstClass(flight.getFirstClass() + reservation.getNumOfPassengers());
			}else {
				flight.setEconomicClass(flight.getEconomicClass() + reservation.getNumOfPassengers());
			}
		
			getReservations().getReservations().remove(id);
			try {
				writeToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Response.status(Status.OK).entity(flight).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(flight).build();
	}
	
	@GET
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(){
		Collection<Reservation> list = getReservations().getValues();
		return Response.status(Status.OK).entity(list).build();
	}
	
	private void writeToFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ctx.getRealPath("") + "reservations.txt"), StandardCharsets.UTF_16));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		for(Reservation r : getReservations().getValues()) {
			String id = String.valueOf(r.getId());
			writer.write(id);
			writer.write(";");
			writer.write(String.valueOf(r.getUser().getId()));
			writer.write(";");
			writer.write(sdf.format(r.getDate()));
			writer.write(";");
			switch(r.getTicketType()) {
			case BUSINESS:
				writer.write("BUSINESS");
				break;
			case ECONOMIC:
				writer.write("ECONOMIC");
				break;
			case FIRST:
				writer.write("FIRST");
				break;
			default:
				writer.write("null");
				System.out.println("Greska prilikom upisa datuma rezervacije u fajl!");
			}
			writer.write(";");
			writer.write(String.valueOf(r.getNumOfPassengers()));
			writer.write(";");
			writer.write(r.getFlightNumber());
			writer.newLine();
		}
	    writer.close();
	}
	
	public ReservationRepository getReservations(){
		ReservationRepository rep = (ReservationRepository) ctx.getAttribute("reservations");
		if(rep == null) {
			rep = new ReservationRepository(ctx.getRealPath(""), getUsers());
			ctx.setAttribute("reservations", rep);
		}
		return rep;
	}
	
	public FlightRepository getFlights(){
		FlightRepository rep = (FlightRepository) ctx.getAttribute("flights");
		if(rep == null) {
			rep = new FlightRepository(ctx.getRealPath(""), getReservations(), getDestinations());
			ctx.setAttribute("flights", rep);
		}
		return rep;
	}
	
	public DestinationRepository getDestinations() {
		DestinationRepository rep = (DestinationRepository) ctx.getAttribute("destinations");
		if(rep == null) {
			rep = new DestinationRepository(ctx.getRealPath(""));
			ctx.setAttribute("destinations", rep);
		}
		return rep;
	}
	
	public UserRepository getUsers() {
		UserRepository users = (UserRepository) ctx.getAttribute("users");
		if(users == null) {
			users = new UserRepository(ctx.getRealPath(""));
			ctx.setAttribute("users", users);
		}
		return users;
	}
}
