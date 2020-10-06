package unsw.skydiving;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Skydive Booking System for COMP2511.
 *
 * A basic prototype to serve as the "back-end" of a skydive booking system. Input
 * and output is in JSON format.
 *
 * @author Matthew Perry
 *
 */


public class SkydiveBookingSystem {
    private List<Skydiver> skydivers;
    private List<Flight> flights;
    private List<Jump> jumps;
    /**
     * Constructs a skydive booking system. Initially, the system contains no flights, skydivers, jumps or dropzones
     */
    public SkydiveBookingSystem() {
        // TODO Auto-generated constructor stub
        this.skydivers = new ArrayList<>();
        this.flights = new ArrayList<>();
        this.jumps = new ArrayList<>();
    }

    private void processCommand(JSONObject json) {

        switch (json.getString("command")) {

        case "flight":
            String id = json.getString("id");
            int maxload = json.getInt("maxload");
            LocalDateTime starttime = LocalDateTime.parse(json.getString("starttime"));
            LocalDateTime endtime = LocalDateTime.parse(json.getString("endtime"));
            String dropzone = json.getString("dropzone");
            
            // TODO - add flight
            if (isValidId(id, "flights")) {
                Flight newFlight = new Flight();
            }

            break;

        // TODO Implement other commands
        
        case "skydiver":


        case "request" :

        case "change" :
        
        case "cancel" :

        case "jump-run":

        }
    }

 

    public static void main(String[] args) {
        SkydiveBookingSystem system = new SkydiveBookingSystem();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.trim().equals("")) {
                JSONObject command = new JSONObject(line);
                system.processCommand(command);
            }
        }
        sc.close();
    }

    /**
     * Helper function to check if an id is valid
     * @param id new id
     * @param list list that we want to check
     * @return true or false whether or not the id is valid
     */
    private boolean isValidId (String id, String list) {
        switch (list) {
            case "flights":
                for (Flight flight: flights) {
                    if (flight.getId() == id) {
                        return false;
                    }
                }
                break;
        
            case "jumps" :
                for (Jump jump: jumps) {
                    if (jump.getId() == id) {
                        return false;
                    }
                }
                break;
        }
        return true;
    }
    
}
