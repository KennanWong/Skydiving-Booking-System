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
            

            break;

        // TODO Implement other commands
        
        case "skydiver":

        case "request" :
            String type = json.getString("type");
            String id_req = json.getString("id");
            LocalDateTime startTime_req = LocalDateTime.parse(json.getString("starttime"));
            if (isValidId(id_req, "jumps")) {
                switch (type) {
                    case "fun":
                        List<Skydiver> funJumpers = new ArrayList<>();
                        JSONArray arrayOfSkydivers = json.getJSONArray("skydivers");
                        for (int i = 0; i < arrayOfSkydivers.length(); i++) {
                            Skydiver jumper = getSkydiverFromName(arrayOfSkydivers.getString(i));
                            if (jumper.isValidToJump()) {
                                funJumpers.add(jumper);
                            }
                        }
                        FunJump newFunJump = new FunJump(id_req, startTime_req, type, funJumpers);
                        break;
                    case "training":

                        break; 
                    case "tandem":
                        break;
                }
            }

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
                    if (jump.getId().equals(id)) {
                        return false;
                    }
                }
                break;
        }
        return true;
    }
    
    /**
     * Helper function that returns the skydiver object from a given name
     * @param name name of the skydiver 
     * @return the skydiver object of "name" otherwise returns null
     */
    private Skydiver getSkydiverFromName(String name) {
        for (Skydiver skydiver: skydivers) {
            if (skydiver.getName().equals(name)) {
                return skydiver;
            }
        }
        return null;
    }

    /**
     * Method to add a flight to the system
     * @param json
     */
    private void addFlight(JSONObject json) {
        String id = json.getString("id");
        int maxLoad = json.getInt("maxload");
        LocalDateTime startTime = LocalDateTime.parse(json.getString("starttime"));
        LocalDateTime endTime = LocalDateTime.parse(json.getString("endtime"));
        String dropZone = json.getString("dropzone");
        
        // TODO - add flight
        if (isValidId(id, "flights")) {
            Flight newFlight = new Flight(id, dropZone, startTime, endTime, maxLoad);
            this.flights.add(newFlight);
        } 
    }

    /**
     * Method to add a skydiver to the system
     * @param json
     */
    private void addSkydiver(JSONObject json) {
        String skydiver = json.getString("skydiver");
        String license = json.getString("license");
        for (Skydiver currSkydivers: skydivers) {
            if (currSkydivers.getName().equals(skydiver)) {
                return;
            }
        }
        Skydiver newSkydiver = new Skydiver(skydiver, license);
        this.skydivers.add(newSkydiver);
    }


    /**
     * Method to process the request
     * @param json
     */
    private void processRequest (JSONObject json) {
        String type = json.getString("type");
        String id = json.getString("id");
        LocalDateTime startTime = LocalDateTime.parse(json.getString("starttime"));
        if (isValidId(id, "jumps")) {
            switch (type) {
                case "fun":
                    List<Skydiver> funJumpers = getJumpersFromJson(json);
                    for (Skydiver skydiver: funJumpers) {
                        if (!skydiver.isValidToJump()) {
                            funJumpers.remove(skydiver);
                        }
                    }
                    FunJump newJump = new FunJump(id, startTime, type, funJumpers);
                    for (Skydiver skydiver: funJumpers) {
                        skydiver.addJump(newJump);
                    }

                    break;
                case "training":

                    break; 
                case "tandem":

                    break;
            }

        } else {
            return;
        }
    }

    private List<Skydiver> getJumpersFromJson(JSONObject json) {
        List<Skydiver> funJumpers = new ArrayList<>();
        JSONArray arrayOfSkydivers = json.getJSONArray("skydivers");
        for (int i = 0; i < arrayOfSkydivers.length(); i++) {
            Skydiver jumper = getSkydiverFromName(arrayOfSkydivers.getString(i));
            funJumpers.add(jumper);
        }
        return funJumpers;
    }
}
