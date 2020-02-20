package controllers;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import model.Destination;
import model.Flight;
import repositories.DestinationRepository;
import repositories.FlightRepository;
import repositories.ReservationRepository;
import repositories.UserRepository;

@Path("/destination")
public class DestinationController {

	
	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	
	public static int counterDestination = 0;
	public static int idDestination;
	
	
	@GET
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		Collection<Destination> list = getDestinations().getValues();
		
		return Response.status(Status.OK).entity(list).build();
	}
	
	@POST
	@Path("/get/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		Destination des = null;
		for(Destination d: getDestinations().getValues()) {
			if(d.getId() == id) {
				des = d;
				break;
			}
		}
		return Response.status(Status.OK).entity(des).build();
	}
	
	@GET
	@Path("/getAllActive")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActive() {
		ArrayList<Destination> list = new ArrayList<Destination>();
		for(Destination d : getDestinations().getValues()) {
			if(d.isActive())
				list.add(d);
		}
		return Response.status(Status.OK).entity(list).build();
	}
	
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(Destination d) {
		if(getDestinations().getDestination(d.getName()) != null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		if(counterDestination == 0) {
			idDestination = getDestinations().getValues().size();
			counterDestination = 1;
		}
		idDestination++;

		Destination destination = new Destination(idDestination, d.getName(), d.getCountry(), d.getAirportName(), 
				d.getAirportCode(), "", true);
		getDestinations().getDestinations().put(destination.getName(), destination);
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(destination).build();
	}
	
	@POST
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response change(Destination d) {
		Destination destination = null;
		for(Destination des: getDestinations().getValues()) {
			if(des.getId() == d.getId()) {
				destination = des;
			}
		}
		String name = destination.getName();
		if(!name.equals(d.getName())) {
			if(getDestinations().getDestination(d.getName()) != null)
				return Response.status(Status.BAD_REQUEST).build();
		}
		destination.setAirportName(d.getAirportName());
		destination.setAirportCode(d.getAirportCode());
		getDestinations().getDestinations().remove(name);
		getDestinations().getDestinations().put(destination.getName(), destination);
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(destination).build();
	}
	
	@POST
	@Path("/activate/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeToActive(@PathParam("id") int destinationId) {
		Destination destination = null;
		for(Destination des : getDestinations().getValues()) {
			if(des.getId() == destinationId) {
				destination = des;
			}
		}
		if(destination == null) {
			return Response.status(Status.BAD_REQUEST).entity(destination).build();
		}
		destination.setActive(true);
		getDestinations().getDestinations().remove(destination.getName());
		getDestinations().getDestinations().put(destination.getName(), destination);
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Flight f: getFlights().getValues()) {
			if(f.getStartDestination().getId() == destinationId || f.getEndDestination().getId() == destinationId) {
				f.setActive(true);
			}
		}
		
		return Response.status(Status.OK).entity(destination).build();
	}
	
	@POST
	@Path("/archive/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeToBlocked(@PathParam("id") int destinationId) {
		Destination destination = null;
		for(Destination des : getDestinations().getValues()) {
			if(des.getId() == destinationId) {
				destination = des;
			}
		}
		if(destination == null) {
			return Response.status(Status.BAD_REQUEST).entity(destination).build();
		}
		destination.setActive(false);
		getDestinations().getDestinations().remove(destination.getName());
		getDestinations().getDestinations().put(destination.getName(), destination);
		try {
			writeToFile();
		} catch (IOException e) {
			e.getMessage();
		}
		
		if(getFlights().getValues().size() != 0) {
			for(Flight f: getFlights().getValues()) {
				if(f.getStartDestination().getId() == destinationId || f.getEndDestination().getId() == destinationId) {
					f.setActive(false);
				}
			}
		}
		return Response.status(Status.OK).entity(destination).build();
	}
	
	private void writeToFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ctx.getRealPath("") + "destinations.txt"), StandardCharsets.UTF_16));
		System.out.println("ISPRED FOR PETLJE");
		for(Destination d : getDestinations().getValues()) {
			String id = String.valueOf(d.getId());
			writer.write(id);
			writer.write(";");
			writer.write(d.getName());
			writer.write(";");
			writer.write(d.getCountry());
			writer.write(";");
			writer.write(d.getAirportName());
			writer.write(";");
			writer.write(d.getAirportCode());
			writer.write(";");
			writer.write(d.getPhoto());
			writer.write(";");
			if(d.isActive()) {
				writer.write("true");
			}else {
				writer.write("false");
			}
			writer.newLine();
		}
	    writer.close();
	    System.out.println("ZAVRSIO UPIS U FAJL");
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
}
