package repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import model.User;

public class UserRepository {

	private HashMap<String, User> users = new HashMap<String, User>();
	
	public UserRepository() {}

	public UserRepository(String path) {
		BufferedReader in = null;
		try {
			File file = new File(path + "/users.txt");
			System.out.println(path);
			in = new BufferedReader(new FileReader(file));
			readUsers(in);
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
	
	public void readUsers(BufferedReader in) {
		StringTokenizer st;
		String line, id = "", username = "", pass = "", name = "", surname = "", number = "", email = "", photo = "";
		String blocked = "", admin = "";
		try {
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals(" ") || line.startsWith("#"))
					continue;
				st = new StringTokenizer(line, ";");
				while(st.hasMoreTokens()) {
					id = st.nextToken().trim();
					username = st.nextToken().trim();
					pass = st.nextToken().trim();
					name = st.nextToken().trim();
					surname = st.nextToken().trim();
					number = st.nextToken().trim();
					email = st.nextToken().trim();
					photo = st.nextToken().trim();
					blocked = st.nextToken().trim();
					admin = st.nextToken().trim();
				}
				
				User user = new User(Integer.parseInt(id), username, pass, name, surname, number, email, photo, Boolean.parseBoolean(blocked), Boolean.parseBoolean(admin));
				users.put(username, user);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public HashMap<String, User> getUsers(){
		return users;
	}
	public Collection<User> getValues(){
		return users.values();
	}
	public User getUser(String username) {
		return users.get(username);
	}
	
}
