package repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import model.Reservation;
import model.TicketType;
import model.User;

public class ReservationRepository {

	private HashMap<Integer, Reservation> reservations = new HashMap<Integer, Reservation>();
	
	public ReservationRepository() {
		
	}
	public ReservationRepository(String path, UserRepository users) {
		BufferedReader in = null;
		try {
			File file = new File(path + "/reservations.txt");
			in = new BufferedReader(new FileReader(file));
			readReservations(in, users);
		}
		catch(Exception e){
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
	
	public void readReservations(BufferedReader in, UserRepository users) {
		StringTokenizer st;
		String line, id = "", userId = "", date = "", ticketType = "", numOfPassengers = "", flightNumber = "";
		try {
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.startsWith("#"))
					continue;
				st = new StringTokenizer(line, ";");
				while(st.hasMoreTokens()) {
					id = st.nextToken().trim();
					userId = st.nextToken().trim();
					date = st.nextToken().trim();
					ticketType = st.nextToken().trim();
					numOfPassengers = st.nextToken().trim();
					flightNumber = st.nextToken().trim();
				}
				User user = null;
				for(User u : users.getValues()) {
					if(u.getId() == Integer.parseInt(userId)) {
						user = u;
					}
				}
				Reservation reservation = new Reservation();
				switch(TicketType.valueOf(ticketType)) {
				case FIRST:
					reservation.setTicketType(TicketType.FIRST);
					break;
				case BUSINESS:
					reservation.setTicketType(TicketType.BUSINESS);
					break;
				case ECONOMIC:
					reservation.setTicketType(TicketType.ECONOMIC);
					break;
				default:
					reservation.setTicketType(null);
					System.out.println("Lose prosledjen tip karte.");
				}
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date dateTime = sdf.parse(date);
				System.out.println(dateTime);
				reservation.setDate(dateTime);
				reservation.setId(Integer.parseInt(id));
				reservation.setNumOfPassengers(Integer.parseInt(numOfPassengers));
				reservation.setFlightNumber(flightNumber);
				reservation.setUser(user);
				reservations.put(Integer.parseInt(id), reservation);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<Integer, Reservation> getReservations() {
		return this.reservations;
	}
	public Reservation getReservation(int id) {
		return reservations.get(id);
	}
	public Collection<Reservation> getValues(){
		return reservations.values();
	}
}
