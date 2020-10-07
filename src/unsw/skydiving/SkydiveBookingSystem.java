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
            this.addFlight(json);

            break;

        // TODO Implement other commands
        case "skydiver":
            this.addSkydiver(json);


        case "request" :
            this.processCommand(json);

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
     * Method to add a flight to the system
     * @param json
     */
    private void addFlight(JSONObject json) {
        String id = json.getString("id");
        int maxLoad = json.getInt("maxload");
        LocalDateTime startTime = LocalDateTime.parse(json.getString("starttime"));
        LocalDateTime endTime = LocalDateTime.parse(json.getString("endtime"));
        String dropZone = json.getString("dropzone");
        
        Flight newFlight = new Flight(id, dropZone, startTime, endTime, maxLoad);
        this.flights.add(newFlight);
        this.sortFlights();
    }

    /**
     * Method to add a skydiver to the system
     * @param json
     */
    private void addSkydiver(JSONObject json) {
        String skydiver = json.getString("skydiver");
        String license = json.getString("license");
        Skydiver newSkydiver = new Skydiver(skydiver, license);
        this.skydivers.add(newSkydiver);
    }


    /**
     * Helper function to process request
     * @param json
     * @return True or false whether or not a request has been able to be fufilled
     */
    private boolean addRequest (JSONObject json) {
        // When processing a request it should do the following in order
        // 1. Create the jump
        // 2. Find an instructor or tandem master if necessary
        // 3. Find an available flight
        // 4. Add the jump to the system, the flight and to all skydivers involved
        String type = json.getString("type");
        String id = json.getString("id");
        LocalDateTime startTime = LocalDateTime.parse(json.getString("starttime"));
        switch (type) {
            case "fun":
                // Take the list of supplied jumpers, and check if anyone is not valid to jump
                List<Skydiver> funJumpers = getJumpersFromJson(json);
                FunJump newFunJump = new FunJump(id, startTime, type, funJumpers);
                for (Skydiver skydiver: funJumpers) {
                    if (!skydiver.isValidToJump(newFunJump)) {
                        return false;
                    }
                }
                Flight availableFlight = this.getAvailableFlight(startTime, funJumpers.size(), "fun");
                if (availableFlight == null) {
                    return false;
                }
                this.jumps.add(newFunJump);
                availableFlight.addJump(newFunJump);
                for (Skydiver skydiver: funJumpers) {
                    skydiver.addJump(newFunJump);
                }
                break;
            case "training":
                Skydiver trainee = getSkydiverFromString(json.getString("trainee"));
                Training newTrainingJump = new Training(id, startTime, type, trainee);
                if (trainee.isValidToJump(newTrainingJump)) {
                    return false;
                }
                Skydiver instructor = getSkydiver("instructor", newTrainingJump);
                if (instructor == null) {
                    return false;
                } else {
                    newTrainingJump.setInstructor(instructor);
                }




                break; 
            case "tandem":

                break;
        }
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    /////                           HELPER FUNCTIONS                                     ///////
    ////////////////////////////////////////////////////////////////////////////////////////////


    
    /**
     * Helper function that returns the skydiver object from a given name
     * @param name name of the skydiver 
     * @return the skydiver object of "name" otherwise returns null
     */
    private Skydiver getSkydiverFromString(String name) {
        for (Skydiver skydiver: skydivers) {
            if (skydiver.getName().equals(name)) {
                return skydiver;
            }
        }
        return null;
    }

    /**
     * Helper function to get a list of skydivers from a json object
     * @param json The json object we are reading
     * @return Returns a list of "Skydiver" objects
     */
    private List<Skydiver> getJumpersFromJson(JSONObject json) {
        List<Skydiver> jumpers = new ArrayList<>();
        JSONArray arrayOfSkydivers = json.getJSONArray("skydivers");
        for (int i = 0; i < arrayOfSkydivers.length(); i++) {
            Skydiver jumper = getSkydiverFromString(arrayOfSkydivers.getString(i));
            jumpers.add(jumper);
        }
        return jumpers;
    }

    /**
     * Helper function to sort flights in order of earliest to latest using bublesort
     */
    private void sortFlights() {
        int nFLights = flights.size();

        for (int i = 0; i <  nFLights-1; i++) {
            for (int j = 0; j < nFLights-i-1; j++) {
                if (flights.get(j).getStartTime().isAfter(flights.get(j+1).getStartTime())) {
                    Flight temp = flights.get(j);
                    flights.set(j, flights.get(j+1));
                    flights.set(j+1, temp);
                }
            }
        }

    }

    /**
     * Helper function to find an available flight given parameters
     * 
     * @param reqStartTime The start time listed on the request
     * @param numParticipants The total number of jumpers
     * @param type The type of jump
     * @return Returns a Flight if one is found, otherwise returns null
     */
    private Flight getAvailableFlight(LocalDateTime reqStartTime, int numParticipants, String type){
        LocalDateTime earliestFlight = reqStartTime;
        if (type.equals("tandem")) {
            earliestFlight.plusMinutes(5);
        }
        
        
        // To find the most available flight loop through all flights added to the system, knowing that the
        // flights are already ordered by time, then by when the flight was registered
        // 1. declare a List<FLight> of possible flights
        // 2. once we have found first available flight add it to the list
        // 3. if the next flight is at the same time, add it to the list, continue for all flights with the same time
        //      3.1 From there we pick either the flight with the most vacancies
        //      3.2 If the flights have the same number of vacancies, choose the first flight in the list as it would have been
        //          the first flight registered
        
        List<Flight> possibleFLights = new ArrayList<>();
        for (Flight flight: flights) {
            if (flight.getStartTime().isEqual(earliestFlight) || flight.getStartTime().isAfter(earliestFlight)) {
                if (possibleFLights.size() == 0 || flight.getStartTime().equals(possibleFLights.get(0))) {
                    possibleFLights.add(flight);
                } else {
                    break;
                }
                
            }
        }
        if (possibleFLights.size() == 0) {
            return null;
        } else if (possibleFLights.size() == 1) {
            return possibleFLights.get(0);
        } 

        Flight tmp = possibleFLights.get(0);

        for (Flight flight: possibleFLights) {
            if (flight.getVacancies() < tmp.getVacancies()) {
                tmp = flight;
            }
        }
        return tmp;

    }

    /**
     * Function to find a skydiver of "license" with the lowest number of jumps
     * @param license license of skydiver we are looking for
     * @return Returns the skydiver object
     */
    public Skydiver getSkydiver(String license, Jump jump) {
        Skydiver tmp = null;
        for (Skydiver skydiver: this.skydivers) {
            if ((skydiver.getLicense().equals(license)) && (tmp == null || skydiver.getNumJumps() < tmp.getNumJumps()) 
                && skydiver.isValidToJump(jump)) {
                tmp = skydiver;
            }
        }
        return tmp;
    }
}
