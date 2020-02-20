package controllers;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import model.Destination;
import model.Flight;
import model.FlightType;
import model.Reservation;
import model.User;
import repositories.DestinationRepository;
import repositories.FlightRepository;
import repositories.ReservationRepository;
import repositories.UserRepository;

@Path("/flight")
public class FlightController {

	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	
	public static int counterFlight = 0;
	public static int idFlight;
	
	
	@GET
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		ArrayList<Flight> flights = new ArrayList<Flight>();
		for(Flight f: getFlights().getValues()) {
			//SVI LETOVI KOJI PREDSTOJE, SA AKTIVNIM DESTINACIJAMA
			if(f.getDate().after(new Date()) && f.isActive() && f.getStartDestination().isActive() && f.getEndDestination().isActive()){
				flights.add(f);
			}
		}
		return Response.status(Status.OK).entity(flights).build();
	}
	
	@GET
	@Path("/get/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		Flight flight = null;
		for(Flight f: getFlights().getValues()) {
			if(f.getId() == id) {
				flight = f;
			}
		}
		return Response.status(Status.OK).entity(flight).build();
	}
	
	@POST
	@Path("/find")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response findFlights(Flight f) {
		ArrayList<Flight> flights = new ArrayList<Flight>();
		
		String dateClient = f.getDate().toString();
		String mesec2 = dateClient.split(" ")[1];
		String dan2 = dateClient.split(" ")[2];
		
		for(Flight flight: getAllFlights()) {
			
			String dateFile = flight.getDate().toString();
			String mesec1 = dateFile.split(" ")[1];
			String dan1 = dateFile.split(" ")[2];
			
			if(flight.getStartDestination().getName().equalsIgnoreCase(f.getStartDestination().getName()) &&
					flight.getEndDestination().getName().equalsIgnoreCase(f.getEndDestination().getName()) &&
					mesec1.equalsIgnoreCase(mesec2) && dan1.equalsIgnoreCase(dan2)) {
				
				flights.add(flight);
			}
		}
		return Response.status(Status.OK).entity(flights).build();
	}
	
	@GET
	@Path("/getFlightTypes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFlightTypes() {
		List<FlightType> types = Arrays.asList(FlightType.values());
		
		return Response.status(Status.OK).entity(types).build();
	}
	
	@GET
	@Path("/filterAndSearch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response filter(@QueryParam("filter") String filter, 
						@QueryParam("input") String input,
						@QueryParam("date") String date) {
		
		ArrayList<Flight> flights = new ArrayList<Flight>(getAllFlights());
		ArrayList<Flight> rez = filterMethod(filter, flights);
		
		rez = searchMethod(input, date, rez);
		
		return Response.status(Status.OK).entity(rez).build();
	}
	
	@GET
	@Path("/filter/{param}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response filter(@PathParam("param") String f) {
		ArrayList<Flight> flights = new ArrayList<Flight>(getAllFlights());
		ArrayList<Flight> rez = filterMethod(f, getAllFlights());
		return Response.status(Status.OK).entity(rez).build();
	}
	
	@GET
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@QueryParam("first") String input, 
							@QueryParam("second") String date) {
		
		if(input.equals("") && date.equals("")) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		ArrayList<Flight> flights = new ArrayList<Flight>(getAllFlights());
		ArrayList<Flight> rez = searchMethod(input, date, flights);
		return Response.status(Status.OK).entity(rez).build();
	}
	
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(Flight f) {
		
		if(getFlights().getFlight(f.getFlightNumber()) != null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		if(counterFlight == 0) {
			idFlight = getDestinations().getValues().size();
			counterFlight = 1;
		}
		idFlight++;

		Flight flight = new Flight(idFlight, f.getFlightNumber(), f.getPriceFirst(), f.getPriceBusiness(), f.getPriceEconomic(),
				f.getFirstClass(), f.getBusinessClass(), f.getEconomicClass(), true);
		flight.setReservations(new ArrayList<Reservation>());
		flight.setFlightType(f.getFlightType());
		//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
		flight.setDate(f.getDate());
		Destination enddestination = null;
		Destination startdestination = null;
		//AKO SE OZNACI ISTA DESTINACIJA ZA POCETNU I KRAJNJU
		if(f.getEndDestination().getId() == f.getStartDestination().getId()) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		for(Destination des: getDestinations().getValues()) {
			if(des.getId() == f.getEndDestination().getId()) {
				enddestination = des;
			}
			if(des.getId() == f.getStartDestination().getId()) {
				startdestination = des;
			}
		}
		flight.setStartDestination(startdestination);
		flight.setEndDestination(enddestination);
		getFlights().getFlights().put(flight.getFlightNumber(), flight);
		
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(flight).build();
	}
	
	@POST
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response change(Flight f) {
		Flight flight = null;
		for(Flight x: getFlights().getValues()) {
			if(x.getId() == f.getId()) {
				flight = x;
			}
		}
		String number = flight.getFlightNumber();
//		if(!number.equals(f.getFlightNumber())) {
//			if(getFlights().getFlight(f.getFlightNumber()) != null)
//				return Response.status(Status.BAD_REQUEST).build();
//		}
		//NE MOGU DA SE MENJAJU DESTINACIJE I REZERVACIJE, TIP LETA, DATUM, NI BROJ LETA, ostalo mogu
		flight.setBusinessClass(f.getBusinessClass());
		flight.setFirstClass(f.getFirstClass());
		flight.setEconomicClass(f.getEconomicClass());
		flight.setPriceFirst(f.getPriceFirst());
		flight.setPriceBusiness(f.getPriceBusiness());
		flight.setPriceEconomic(f.getPriceEconomic());
//		flight.setFlightNumber(f.getFlightNumber());
		
		getFlights().getFlights().remove(number);
		getFlights().getFlights().put(number, flight);
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(flight).build();
	}
	
	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFlight(@PathParam("id") int id) {
		Flight flight = null;
		for(Flight f : getFlights().getValues()) {
			if(f.getId() == id) {
				flight = f;
			}
		}
		if(flight == null)
			return Response.status(Status.NOT_FOUND).entity(flight).build();

		if(flight.getReservations().size() != 0)
			return Response.status(Status.BAD_REQUEST).entity(flight).build();
		
		getFlights().getFlights().remove(flight.getFlightNumber());
		
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Response.status(Status.OK).entity(flight).build();
	}
	
	public ArrayList<Flight> getAllFlights(){
		ArrayList<Flight> flights = new ArrayList<Flight>();
		for(Flight f: getFlights().getValues()) {
			//SVI LETOVI KOJI PREDSTOJE, SA AKTIVNIM DESTINACIJAMA
			if(f.getDate().after(new Date()) && f.isActive() && f.getStartDestination().isActive() && f.getEndDestination().isActive()){
				flights.add(f);
			}
		}
		return flights;
	}
	public ArrayList<Flight> searchMethod(String input, String date, ArrayList<Flight> list){
		
		ArrayList<Flight> flights = new ArrayList<Flight>();
		
		boolean first = false;
		if(!input.equals("")) {
			first = true;
			for(Flight flight: list) {
				
				if(flight.getStartDestination().getName().toLowerCase().contains(input.toLowerCase()) || 
						flight.getEndDestination().getName().toLowerCase().contains(input.toLowerCase()) ||
						flight.getStartDestination().getCountry().toLowerCase().contains(input.toLowerCase()) || 
						flight.getEndDestination().getCountry().toLowerCase().contains(input.toLowerCase())) {
	
					flights.add(flight);
				}
			}
		}
		if(!date.equals("")) {
			String mesec2 = date.split("-")[1];
			String dan2 = date.split("-")[2];
			if(mesec2.equals("01"))
				mesec2 = "JAN";
			if(mesec2.equals("02"))
				mesec2 = "FEB";
			if(mesec2.equals("03"))
				mesec2 = "MAR";
			if(mesec2.equals("04"))
				mesec2 = "APR";
			if(mesec2.equals("05"))
				mesec2 = "MAY";
			if(mesec2.equals("06"))
				mesec2 = "JUN";
			if(mesec2.equals("07"))
				mesec2 = "JUL";
			if(mesec2.equals("08"))
				mesec2 = "AUG";
			if(mesec2.equals("09"))
				mesec2 = "SEP";
			if(mesec2.equals("10"))
				mesec2 = "OCT";
			if(mesec2.equals("11"))
				mesec2 = "NOV";
			if(mesec2.equals("12"))
				mesec2 = "DEC";
			if(first) {
				ArrayList<Flight> rez = new ArrayList<Flight>();
				for(Flight flight: flights) {
					
					String dateFile = flight.getDate().toString();
					String mesec1 = dateFile.split(" ")[1];
					String dan1 = dateFile.split(" ")[2];
					
					if(mesec1.equalsIgnoreCase(mesec2) && dan1.equalsIgnoreCase(dan2)) {
						rez.add(flight);
					}
				}
				return rez;
				
			}else {
				for(Flight flight: getAllFlights()) {
					
					String dateFile = flight.getDate().toString();
					String mesec1 = dateFile.split(" ")[1];
					String dan1 = dateFile.split(" ")[2];
					
					if(mesec1.equalsIgnoreCase(mesec2) && dan1.equalsIgnoreCase(dan2)) {
						flights.add(flight);
					}
				}
			}
			
		}
		return flights;
	}
	public ArrayList<Flight> filterMethod(String param, ArrayList<Flight> flights){
		ArrayList<Flight> rez = new ArrayList<Flight>();
		
		if(param.equals("date")) {
			Collections.sort(flights, new Comparator<Flight>() {
			  public int compare(Flight o1, Flight o2) {
				  return o1.getDate().compareTo(o2.getDate());
			  }
			});
			Collections.reverse(flights);
		}else if(param.equals("code")) {
			System.out.println("CODE");
			Collections.sort(flights, new Comparator<Flight>() {
			  public int compare(Flight o1, Flight o2) {
				  return o1.getFlightNumber().compareTo(o2.getFlightNumber());
			  }
			});
		}else {
			for(Flight flight: flights) {
				if(flight.getFlightType().equals(FlightType.valueOf(param))) {
					rez.add(flight);
				}
			}
			return rez;
		}
		
		return flights;
	}
	private void writeToFile() throws IOException {
		System.out.println(ctx.getRealPath("") + "flights.txt");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ctx.getRealPath("") + "flights.txt"), StandardCharsets.UTF_16));
		for(Flight f : getFlights().getValues()) {
			writer.write(String.valueOf(f.getId()));
			writer.write(";");
			writer.write(f.getFlightNumber());
			writer.write(";");
			writer.write(f.getStartDestination().getId());
			writer.write(";");
			writer.write(f.getEndDestination().getId());
			writer.write(";");
			int i = 0;
			for(Reservation r: getReservations().getValues()) {
				i++;
				writer.write(r.getId());
				if(i == getReservations().getValues().size()) {
					writer.write(";");
				}else {
					writer.write(",");
				}
			}
			writer.write(String.valueOf(f.getPriceFirst()));
			writer.write(";");
			writer.write(String.valueOf(f.getPriceBusiness()));
			writer.write(";");
			writer.write(String.valueOf(f.getPriceEconomic()));
			writer.write(";");
			writer.write(String.valueOf(f.getFirstClass()));
			writer.write(";");
			writer.write(String.valueOf(f.getBusinessClass()));
			writer.write(";");
			writer.write(String.valueOf(f.getEconomicClass()));
			writer.write(";");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			writer.write(sdf.format(f.getDate()));
			writer.write(";");
			switch(f.getFlightType()) {
			case CHARTER:
				writer.write("CHARTER");
				writer.write(";");
				break;
			case REGIONAL:
				writer.write("REGIONAL");
				writer.write(";");
				break;
			case OVERSEAS:
				writer.write("OVERSEAS");
				writer.write(";");
				break;
			}
			writer.write(";");
			if(f.isActive())
				writer.write("true");
			else
				writer.write("false");
			writer.newLine();
		}
	    writer.close();
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
	public ReservationRepository getReservations(){
		ReservationRepository rep = (ReservationRepository) ctx.getAttribute("reservations");
		if(rep == null) {
			rep = new ReservationRepository(ctx.getRealPath(""), getUsers());
			ctx.setAttribute("reservations", rep);
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
