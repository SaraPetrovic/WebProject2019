package repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import model.Destination;
import model.Flight;
import model.FlightType;
import model.Reservation;

public class FlightRepository {

	private HashMap<String, Flight> flights = new HashMap<String, Flight>();
	
	public FlightRepository() {}

	public FlightRepository(String path, ReservationRepository reservations, DestinationRepository destionations) {
		BufferedReader in = null;
		try {
			File file = new File(path + "/flights.txt");
			in = new BufferedReader(new FileReader(file));
			readFlights(in, reservations, destionations);
		}catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if ( in != null ) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
	}

	public void readFlights(BufferedReader in, ReservationRepository reservations, DestinationRepository destionations) {
		StringTokenizer st;
		String line, id = "", flightNumber = "", startDestinationId = "", endDestinationId = "", reservationsList= "", firstClass = "";
		String priceFirst = "", priceBusiness = "", priceEconomic = "";
		String businessClass = "", economicClass = "", date = "", flightType = "", idRes = "", active = "";
		try {
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals(" ") || line.startsWith("#"))
					continue;
				st = new StringTokenizer(line, ";");
				while(st.hasMoreTokens()) {
					id = st.nextToken().trim();
					flightNumber = st.nextToken().trim();
					startDestinationId = st.nextToken().trim();
					endDestinationId = st.nextToken().trim();
					reservationsList = st.nextToken().trim();
					priceFirst = st.nextToken().trim();
					priceBusiness = st.nextToken().trim();
					priceEconomic = st.nextToken().trim();
					firstClass = st.nextToken().trim();
					businessClass = st.nextToken().trim();
					economicClass = st.nextToken().trim();
					date = st.nextToken().trim();
					flightType = st.nextToken().trim();
					active = st.nextToken().trim();
				}
				Flight flight = new Flight(Integer.parseInt(id), flightNumber, Double.parseDouble(priceFirst), Double.parseDouble(priceBusiness),
						Double.parseDouble(priceEconomic), Integer.parseInt(firstClass),
						Integer.parseInt(businessClass), Integer.parseInt(economicClass), Boolean.parseBoolean(active));
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date dateTime = sdf.parse(date);
				flight.setDate(dateTime);
				for(Destination d : destionations.getValues()) {
					if(d.getId() == Integer.parseInt(startDestinationId)){
						flight.setStartDestination(d);
					}
					if(d.getId() == Integer.parseInt(endDestinationId)) {
						flight.setEndDestination(d);
					}
				}
				switch(FlightType.valueOf(flightType)) {
				case CHARTER:
					flight.setFlightType(FlightType.CHARTER);
					break;
				case REGIONAL:
					flight.setFlightType(FlightType.REGIONAL);
					break;
				case OVERSEAS:
					flight.setFlightType(FlightType.OVERSEAS);
					break;
				default:
					flight.setFlightType(null);
					System.out.println("Lose parsiranje tipa leta.");
				}
				st = new StringTokenizer(reservationsList, ",");
				ArrayList<Reservation> list = new ArrayList<Reservation>();
				while(st.hasMoreTokens()) {
					int numOfTokens = st.countTokens();
					for(int i = 0; i < numOfTokens; i++) {
						idRes = st.nextToken().trim();
						for(Reservation r: reservations.getValues()) {
							if(r.getId() == Integer.parseInt(idRes)) {
								list.add(r);
							}
						}
					}
				}
				flight.setReservations(list);
				flights.put(flightNumber, flight);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public HashMap<String, Flight> getFlights(){
		return flights;
	}
	public Collection<Flight> getValues(){
		return flights.values();
	}
	public Flight getFlight(String username) {
		return flights.get(username);
	}
}
