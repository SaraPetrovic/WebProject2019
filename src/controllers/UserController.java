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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import model.User;
import repositories.UserRepository;

@Path("/user")
public class UserController {

	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	
	public static int counterUser = 0;
	public static int idUser;
	
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User u) {

		User user = getUsers().getUser(u.getUsername());
		
		if(user == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		if(!user.getPassword().equals(u.getPassword())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		request.getSession().setAttribute("user", user);
		
		return Response.status(Status.ACCEPTED).entity(user).build();
	}
	
	@POST
	@Path("/signup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response signUp(User user){
		
		for(User u : getUsers().getValues()) {
			if(u.getUsername().equals(user.getUsername())) {
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
		
		if(counterUser == 0) {
			idUser = getUsers().getValues().size();
			counterUser = 1;
		}
		idUser++;
		
		User newUser = new User(idUser, user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(),
				user.getPhoneNumber(), user.getEmail(), "", false, false);
		getUsers().getUsers().put(user.getUsername(), newUser);
		try {
			System.out.println("TRY");
			writeToFile();
			System.out.println("UPISANO U FILE");
		} catch (IOException e) {
			System.out.println("EXCEPTION");
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(newUser).build();
	}
	
	@POST
	@Path("/activate/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeToActive(@PathParam("userId") int userId) {
		User user = null;
		for(User us : getUsers().getValues()) {
			if(us.getId() == userId) {
				user = us;
			}
		}
		if(user == null) {
			return Response.status(Status.BAD_REQUEST).entity(user).build();
		}
		user.setBlocked(false);
		getUsers().getUsers().remove(user.getUsername());
		getUsers().getUsers().put(user.getUsername(), user);
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(user).build();
	}
	
	@POST
	@Path("/block/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeToBlocked(@PathParam("userId") int userId) {
		User user = null;
		for(User us : getUsers().getValues()) {
			if(us.getId() == userId) {
				user = us;
			}
		}
		if(user == null) {
			return Response.status(Status.BAD_REQUEST).entity(user).build();
		}
		user.setBlocked(true);
		getUsers().getUsers().remove(user.getUsername());
		getUsers().getUsers().put(user.getUsername(), user);
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(user).build();
	}
	
	@GET
	@Path("/loggedUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sessionUser() {
		
		User user = (User) request.getSession().getAttribute("user");
		if(user == null) {
			return Response.status(Status.NOT_FOUND).build();	
		}
		return Response.status(Status.OK).entity(user).build();
	}

	@POST
	@Path("/changeProfile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeProfile(User u) {
		User user = null;
		for(User us : getUsers().getValues()) {
			if(us.getId() == u.getId()) {
				user = us;
			}
		}
		if(user.isBlocked()) {
			return Response.status(Status.CONFLICT).build();
		}
		String oldUsername = user.getUsername();
		if(!u.getUsername().equals(oldUsername)){
			if(getUsers().getUser(u.getUsername()) != null) {
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
		user.setUsername(u.getUsername());
		user.setPassword(u.getPassword());
		user.setFirstName(u.getFirstName());
		user.setLastName(u.getLastName());
		user.setPhoneNumber(u.getPhoneNumber());
		user.setEmail(u.getEmail());
		user.setPhoto("");
		getUsers().getUsers().remove(oldUsername);
		getUsers().getUsers().put(user.getUsername(), user);
		try {
			writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(user).build();
	}
	
	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout() {
		
		request.getSession().invalidate();
		System.out.println("User: " + request.getSession().getAttribute("user").toString());
		return Response.status(Status.OK).build();
	}
	
	@GET
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers(){
		ArrayList<User> list = new ArrayList<User>();
		for(User u: getUsers().getValues()) {
			if(!u.isAdmin())
				list.add(u);
		}
		return Response.status(Status.OK).entity(list).build();
	}
	
	private void writeToFile() throws IOException {
		System.out.println(ctx.getRealPath("") + "users.txt");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ctx.getRealPath("") + "users.txt"), StandardCharsets.UTF_16));
		for(User u : getUsers().getValues()) {
			String id = String.valueOf(u.getId());
			writer.write(id);
			writer.write(";");
			writer.write(u.getUsername());
			writer.write(";");
			writer.write(u.getPassword());
			writer.write(";");
			writer.write(u.getFirstName());
			writer.write(";");
			writer.write(u.getLastName());
			writer.write(";");
			writer.write(u.getPhoneNumber());
			writer.write(";");
			writer.write(u.getEmail());
			writer.write(";");
			writer.write(u.getPhoto());
			writer.write(";");
			if(u.isBlocked()) {
				writer.write("true");
			}else {
				writer.write("false");
			}
			writer.write(";");
			if(u.isAdmin()) {
				writer.write("true");
			}else {
				writer.write("false");
			}
			writer.newLine();
		}
	    writer.close();
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
