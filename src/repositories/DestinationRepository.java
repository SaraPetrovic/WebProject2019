package repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import model.Destination;
import model.User;

public class DestinationRepository {

	private HashMap<String, Destination> destinations = new HashMap<String, Destination>();
	
	public DestinationRepository() {}

	public DestinationRepository(String path) {
		BufferedReader in = null;
		try {
			File file = new File(path + "/destinations.txt");
			in = new BufferedReader(new FileReader(file));
			readDestinations(in);
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
	
	public void readDestinations(BufferedReader in) {
		StringTokenizer st;
		String line, id = "", country = "", name = "", airportName = "", airportCode = "", photo = "", active = "";
		try {
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals(" ") || line.startsWith("#"))
					continue;
				st = new StringTokenizer(line, ";");
				while(st.hasMoreTokens()) {
					id = st.nextToken().trim();
					name = st.nextToken().trim();
					country = st.nextToken().trim();
					airportName = st.nextToken().trim();
					airportCode = st.nextToken().trim();
					photo = st.nextToken().trim();
					active = st.nextToken().trim();
				}
				Destination destination = new Destination(Integer.parseInt(id), name, country, airportName, airportCode, photo, Boolean.parseBoolean(active));
				destinations.put(name, destination);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String, Destination> getDestinations(){
		return destinations;
	}
	public Destination getDestination(String name){
		return destinations.get(name);
	}
	public Collection<Destination> getValues(){
		return destinations.values();
	}
}
