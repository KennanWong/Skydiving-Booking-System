package unsw.skydiving;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import jdk.nashorn.api.scripting.JSObject;


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

        
        case "skydiver":
            this.addSkydiver(json);
            break;

        case "request" :
            JSONObject result = new JSONObject();
            Jump newJump = addRequest(json);
            if (newJump == null) {
                result.put("status", "rejected");
            } else {
                result.put("flight", newJump.getFlight().getId());
                result.put("dropzone", newJump.getDropzone());
                result.put("status", "success");
            }
            System.out.println(result);
            break;
        case "change" :
            JSONObject changeRequest = new JSONObject();
            Jump changedJump = changeJump(json);
            if (changedJump == null) {
                changeRequest.put("status", "rejected");
            } else {
                changeRequest.put("flight", changedJump.getFlight().getId());
                changeRequest.put("dropzone", changedJump.getDropzone());
                changeRequest.put("status", "success");
            }
            System.out.println(changeRequest);
            break;
        case "cancel" :
            cancelJump(json);
            break;
        case "jump-run":
            JSONArray jumpRun = runJump(json);
            System.out.println(jumpRun);
            break;
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
        String dropzone = json.getString("dropzone");
        
        Flight newFlight = new Flight(id, dropzone, startTime, endTime, maxLoad);
        this.flights.add(newFlight);
        this.sortFlights();
    }

    /**
     * Method to add a skydiver to the system
     * @param json
     */
    private void addSkydiver(JSONObject json) {
        String skydiver = json.getString("skydiver");
        String licence = json.getString("licence");
        Skydiver newSkydiver = new Skydiver(skydiver, licence);
        if (licence.equals("tandem-master") || licence.equals("instructor")) {
            String dropzone = json.getString("dropzone");
            newSkydiver.setDropzone(dropzone);
        }
        this.skydivers.add(newSkydiver);
    }


    /**
     * Helper function to process request
     * @param json
     * @return True or false whether or not a request has been able to be fufilled
     */
    private Jump addRequest (JSONObject json) {
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

                List<Flight> funFlights = getAvailableFlight(startTime, funJumpers.size(), "fun");
                if (funFlights == null) {
                    System.out.println("could not find a flight");
                    return null;
                }
                Flight funFlight = funFlights.get(0);

                for (Skydiver skydiver: funJumpers) {
                    if (!skydiver.isValidToJump(newFunJump, funFlight)) {
                        System.out.println("A jumper is not valid to jump");
                        return null;
                    }
                }

                for (Flight availableFun: funFlights) {
                    if (availableFun.getVacancies() < funFlight.getVacancies()) {
                        funFlight = availableFun;
                        System.out.println("found a better flight");
                    }
                }
                this.jumps.add(newFunJump);             // add the jump the bookjing systems booked jumps
                funFlight.addJump(newFunJump);          // add this jump to the booked flight
                newFunJump.setFlight(funFlight);        // add the flight the jump is booked on
                for (Skydiver skydiver: funJumpers) {   // add this flight to all participating skydivers
                    skydiver.addJump(newFunJump);
                }
                return newFunJump;

            case "training":
                Skydiver trainee = getSkydiverFromString(json.getString("trainee"));
                Training newTrainingJump = new Training(id, startTime, type, trainee);

                List<Flight> trainingFlights = getAvailableFlight(startTime, 2, "training");
                if (trainingFlights == null) {
                    System.out.println("could not find a flight");
                    return null;
                }

                Flight trainingFlight = trainingFlights.get(0);
                Skydiver instructor = null;
                for (Flight availableTraining: trainingFlights) {
                    if (!trainee.isValidToJump(newTrainingJump, availableTraining)) {
                        System.out.println("Jumper is not valid to jump");
                        return null;
                    }
                    // for each available flight, check if we are able to get an instructor
                    instructor = getSkydiver("instructor", newTrainingJump, availableTraining, trainee);
                    if (instructor != null && availableTraining.getVacancies() < trainingFlight.getVacancies()) {
                        trainingFlight = availableTraining;
                        System.out.println("found a better flight");
                    } 
                }

                if (instructor == null) {
                    System.out.println("unable to find an instructor");
                    return null;
                } 
                newTrainingJump.setInstructor(instructor);      // set the instructor
                jumps.add(newTrainingJump);                     // add the jump to the booking system list of flights
                trainingFlight.addJump(newTrainingJump);        // add this jump to the booked flight
                newTrainingJump.setFlight(trainingFlight);      // add the booked flight to the jump
                trainee.addJump(newTrainingJump);               // add the jump to the trainee
                instructor.addJump(newTrainingJump);            // add the jump to the instructor
                return newTrainingJump; 

            case "tandem":
                Skydiver passenger = getSkydiverFromString(json.getString("passenger"));
                Tandem newTandemJump = new Tandem(id, startTime, type , passenger);

                List<Flight> tandemFlights = getAvailableFlight(startTime, 2, "tandem");
                if (tandemFlights == null) {
                    System.out.println("could not find a flight");
                    return null;
                }

                Flight tandemFlight = tandemFlights.get(0);
                Skydiver tandemMaster = null;
                for (Flight availableTandem: tandemFlights) {
                    if (!passenger.isValidToJump(newTandemJump, availableTandem)) { 
                        System.out.println("passenger is not valid to jump");
                        return null;
                    }
                    // for each available flight, check if we are able to get an instructor
                    tandemMaster = getSkydiver("tandem-master", newTandemJump, availableTandem, passenger);
                    if (tandemMaster!= null && availableTandem.getVacancies() < tandemFlight.getVacancies()) {
                        trainingFlight = availableTandem;
                    } 
                }

                if (tandemMaster == null) {
                    System.out.println("unable to find tandem master");
                    return null;
                }
                
                newTandemJump.setTandemMaster(tandemMaster);    // set tandem master 
                this.jumps.add(newTandemJump);                  // add the jump to the booking systems list of jumps
                tandemFlight.addJump(newTandemJump);            // add this jump to the booked flight
                newTandemJump.setFlight(tandemFlight);          // set the jumps flight to the booked flight
                passenger.addJump(newTandemJump);               // add this jump to the passenger
                tandemMaster.addJump(newTandemJump);            // add this jump to the tandem master
                return newTandemJump;
        }
        return null;
    }

    /**
     * Helper function to change a given jump
     * @param json
     * @return
     */
    private Jump changeJump(JSONObject json) {
        String changeToType = json.getString("type");
        String jumpId = json.getString("id");
        Jump jumpToChange = getJumpFromId(jumpId);
        LocalDateTime newStartTime = LocalDateTime.parse(json.getString("starttime"));
        List<Skydiver> listOfSkydivers = jumpToChange.getSkydivers();
        Flight flightToChange = jumpToChange.getFlight();

        // need to make a change t o an existing jump id
        // 3 possible cases --> change to 1. tandem, 2. fun, 3. training
        // we need to make sure we can either make the change on the same flight or reassign them to a new flight
        // BUT we need to keep the state of the booking system, so ideally we want to cancel then make the change, but if we cant make the change, then cancel
        // we could remove the jump from the flight and all skydivers associated, then attempt to find another flight,
        // if we are able to find a flight, then we can modify the jump and add it to the flight and and skydivers again
        // otherwise re add to existing skydivers

        /*
        1. find the jump and get participating jumpers as well as the flight
        2. Keep track of the index at which the jump is in the participating jumpers and flights list of jumps
        3. process it like it is a brand new jump
        4. if we recieve a jump back then change is successful
        5. if now we must re add participating jumpers as well as flights   
        */

        // list of integer values to hold onto index of the jump we are about to change
        List<Integer> skydiverJumpIndex = new ArrayList<>();
        for (Skydiver skydiver: listOfSkydivers) {
            skydiverJumpIndex.add(skydiver.getIndexOfJump(jumpToChange));
        }

        // index value of jumptochange in the flights list of jumps
        int jumpIndexInFlight = flightToChange.getIndexOfJump(jumpToChange);

        // index value of the jump in the system
        int jumpIndexInSystem = jumps.indexOf(jumpToChange);


        // remove the jump from the skydiver and the flight and the system
        for (Skydiver skydiver: listOfSkydivers) {
            skydiver.removeJump(jumpToChange);
        }
        flightToChange.removeJump(jumpToChange);
        jumps.remove(jumpToChange);

        // create json object to run addRequest
        JSONObject newRequest = new JSONObject();
        newRequest.put("type", changeToType);
        newRequest.put("id", jumpId);
        newRequest.put("starttime", newStartTime.toString());

        switch (changeToType) {
            case "fun":
                newRequest.put("skydivers", json.getJSONArray("skydivers"));
                break;
        
            case "tandem":
                // tandem jumps, we need to find the passenger, and create a new jump under their name
                newRequest.put("passenger", json.getString("passenger"));
                break;

            case "training":
                // training jumps, we need to find the trainee and create a new jump under their name
                newRequest.put("trainee", json.getString("trainee"));
                break;
        }

        
        // process the request to make a new jump
        Jump newChangedJump = addRequest(newRequest);

        // unable to fufil change request, must reset everything to original state
        // i.e add back the jump to its original index in the skydivers, the flights and the booking systems
        // list of flights
        if (newChangedJump == null) {
            for (int i = 0; i < listOfSkydivers.size(); i++) {
                listOfSkydivers.get(i).addJumpAtIndex(skydiverJumpIndex.get(i), jumpToChange);
            }
            flightToChange.addJumpAtIndex(jumpIndexInFlight, jumpToChange);
            jumps.add(jumpIndexInSystem, jumpToChange);
        } 
        
        return newChangedJump;


    }

    /**
     * Helper function to cancel a jump
     * @param json
     */
    private void cancelJump(JSONObject json) {
        String jumpId = json.getString("id");
        Jump jumpToCancel = getJumpFromId(jumpId);
        // Must remove jump from each participating skydiver, from the booking system and from the flights list of jumps

        List<Skydiver> listOfSkydivers = jumpToCancel.getSkydivers();
        for (Skydiver skydiver: listOfSkydivers) {
            skydiver.removeJump(jumpToCancel);
        }

        Flight flightOfJump = jumpToCancel.getFlight();
        flightOfJump.removeJump(jumpToCancel);

        jumps.remove(jumpToCancel);


    }

    private JSONArray runJump(JSONObject json) {
        String flightId = json.getString("id");
        Flight flightToRun = getFlightFromId(flightId);

        List<Jump> listOfJump = flightToRun.getOrderedJumps();

        JSONArray flightRun = new JSONArray();
        for (Jump jump: listOfJump) {
            List <Skydiver> listOfSkydivers = jump.getSkydivers();
            JSONObject jumpJSON = new JSONObject();
            switch (jump.getType()) {
                case "fun":
                    for (Skydiver skydiver: listOfSkydivers) {
                        jumpJSON.append("skydivers", skydiver.getName());
                    }
                    break;
                case "tandem":
                    Tandem tandemJump = (Tandem) jump;
                    jumpJSON.put("passenger", tandemJump.getPassenger().getName());
                    jumpJSON.put("jump-master", tandemJump.getTandemMaster().getName());
                    break;
                case "training":
                    Training trainingJump = (Training) jump;
                    jumpJSON.put("instructor", trainingJump.getInstructor().getName());
                    jumpJSON.put("trainee", trainingJump.getTrainee().getName());
                    break;
            }
            flightRun.put(jumpJSON);
        }
        return flightRun;
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
        if (nFLights > 1) {
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
    }

    /**
     * Helper function to find an available flight given parameters
     * Must find a flight where 
     * 
     * @param reqStartTime The start time listed on the request
     * @param numParticipants The total number of jumpers
     * @param type The type of jump
     * @return Returns a Flight if one is found, otherwise returns null
     */
    private List<Flight> getAvailableFlight(LocalDateTime reqStartTime, int numParticipants, String type){
        LocalDateTime earliestFlight = reqStartTime;
        if (type.equals("tandem")) {
            earliestFlight = earliestFlight.plusMinutes(5);
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
            if (!flight.getStartTime().isBefore(earliestFlight) && earliestFlight.getDayOfYear()==flight.getStartTime().getDayOfYear()) {
                System.out.println("Flight: "+ flight.getId() + ", vacancies are: " + flight.getVacancies() + ". Attempting to add: "+ numParticipants);
                if ((possibleFLights.size() == 0 || flight.getStartTime() == possibleFLights.get(0).getStartTime()) && flight.getVacancies() >= numParticipants) {
                    possibleFLights.add(flight);
                } 
                
            }
        }
        if (possibleFLights.size() == 0) {
            return null;
        } 
        return possibleFLights;

    }

    /**
     * Helper function to convert licence string to a int value
     * @param licence the licence we need int value from
     * @return
     */
    private int licenceToInt(String licence) {
        switch (licence) {
            case "student":
                return 1;
            case "licenced-jumper":
                return 2;
            case "instructor":
                return 3;
            case "tandem-master":
                return 4;
        }
        return 0;
    }
    /**
     * Helper function to get a skydiver, either a tandem master or instructor
     * @param licence licensce of skydiver we want
     * @param jump the jump we want the skydiver to make
     * @param flight the flight we want the skydiver to be on
     * @param notThem the caller who is looking for a skydiver
     * @return
     */
    public Skydiver getSkydiver(String licence, Jump jump, Flight flight, Skydiver callee) {
        Skydiver tmp = null;
        System.out.println("Flight: " + flight.getId() + ", Dropzone: " + flight.getDropzone());
        for (Skydiver skydiver: this.skydivers) {
            System.out.println("Skydiver: " + skydiver.getName() + ", DropZone: " + skydiver.getDropzone());
            if (licenceToInt(skydiver.getLicence()) >= licenceToInt(licence) && 
                (tmp == null || skydiver.getNumJumpsOnDay(jump.getStartTime()) < tmp.getNumJumpsOnDay(jump.getStartTime()))     // seeing that we are getting a skydivier with the least
                                                                                // number of jumps
                && skydiver.isValidToJump(jump, flight)                         // ensure skydiver valid to jump
                && skydiver.getDropzone() != null                               // ensure that the skydiver has a dropzone set
                && !skydiver.getName().equals(callee.getName())) {              // if the skydiver is not the callee
                if (skydiver.getDropzone().equals(flight.getDropzone())) {
                    tmp = skydiver;
                    System.out.println("Found " + licence + " " + tmp.getName());
                    break;
                } 
            }
        }

        if (tmp == null) {
            System.out.println("Unable to get a " + licence + " skydiver");
            return null;
        }
        
        return tmp;
    }

    /**
     * Helper function to retrieve a jump from the booking system from a given id
     * @param id Id of the jump we would like to recieve
     * @return
     */
    public Jump getJumpFromId(String id) {
        for (Jump jump: jumps) {
            if (jump.getId().equals(id)) {
                return jump;
            }
        }
        return null;
    }

    public Flight getFlightFromId(String id) {
        for (Flight flight: flights) {
            if(flight.getId().equals(id)) {
                return flight;
            }
        }
        return null;
    }
}