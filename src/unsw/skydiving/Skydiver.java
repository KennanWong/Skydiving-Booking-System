package unsw.skydiving;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes.Name;



public class Skydiver {
    private String skydiver;
    private String licence;
    private List<Jump> jumps;
    private int numJumps;
    private String dropzone;


    public Skydiver (String skydiver, String licence) {
        this.skydiver = skydiver;
        this.licence = licence;
        this.jumps = new ArrayList<>();
        this.numJumps = 0;
        this.dropzone = null;
    }

    public String getName() {
        return this.skydiver;
    }

    public int getNumJumps() {
        return numJumps;
    }

    public String getLicence() {
        return licence;
    }

    public String getDropzone() {
        return dropzone;
    }

    public List<Jump> getJumps() {
        return jumps;
    }

    public int getNumJumpsOnDay(LocalDateTime day) {
        int counter = 0;
        for (Jump jump: jumps) {
            if (jump.getStartTime().getDayOfYear() == day.getDayOfYear()) {
                counter = counter + 1;
            }
        }
        return counter;
    }

    public int getIndexOfJump(Jump jump) {
        return jumps.indexOf(jump);
    }

    public void addJump(Jump newJump) {
        this.jumps.add(newJump);
        this.numJumps = this.numJumps + 1;
    }

    public void setDropzone(String dropzone) {
        this.dropzone = dropzone;
    }

    public void removeJump(Jump jump) {
        jumps.remove(jump);
    }

    public void addJumpAtIndex(int index, Jump jump) {
        jumps.add(index, jump);
    }

    /**
     * Method to check if a skydiver is valid to jump, by looking at their currently booked jumps
     * @return True or False depending on if jump is valid
     */
    public boolean isValidToJump(Jump jump, Flight flight) {
        // A skydiver is not valid to jump, iof the flight times are mismatched
        // Parachuites take 10 minutes to repack AFTER jump, everyone has to repack it unless they are a passenger of a tandem
        // Tandem jumps require 5 minutes BEFORE jump, this is not necessary when determining jump
        // Training jumps require 15 minutes AFTER jump, occures BEFORE 10 minute repacking
        // 1. check if any of the start times of previous jumps interfere with previous jumps
        // 2. check the interval between one of the booked jumps
        // 3. check if any of the end times of the flight interfere with this requested jump
        //    , i.e seeing debriefing or repacking times would stop them from begining this jump on that flight

        LocalDateTime startTimeOfJump = jump.getStartTime();

        for (Jump bookedJump: jumps) {
            // Check if one of the previous booked jumps is occuring at the same time as the requested jump
            if (bookedJump.getStartTime() == jump.getStartTime()) {
                System.out.println("A jump is occuring at the same time");
                return false;
            } 
            LocalDateTime bookedStartTime = bookedJump.getFlight().getStartTime();
            LocalDateTime bookedEndTime = getEndTimeOfJump(bookedJump, bookedJump.getFlight());

            LocalDateTime requestedStartTime = flight.getStartTime();
            LocalDateTime requestedEndTime = getEndTimeOfJump(jump, flight);
           
            if (!requestedStartTime.isBefore(bookedStartTime) && !requestedStartTime.isAfter(bookedEndTime)
                || !requestedEndTime.isBefore(bookedStartTime) && !requestedEndTime.isAfter(bookedEndTime)){
                System.out.println("Attempting to start or end a jump between an already booked jump");
                return false;
            } 
            

            
        }          
        System.out.println(skydiver + " is valid to jump");
        return true;
    }

    /**
     * Private helper function to get the end time of a jump, from a given jump
     * @param endTime
     * @return
     */
    private LocalDateTime getEndTimeOfJump(Jump jump, Flight flight) {
        LocalDateTime endTime = flight.getEndTime();
        if (jump.getType().equals("training")) {
            endTime = endTime.plusMinutes(15);
        }
        boolean isPassenger = false;
        if (jump.getType().equals("tandem")) {
            Tandem tandemJump = (Tandem) jump;
            if (tandemJump.getPassenger().equals(this)) {
                isPassenger = true;
            }
        }
        if (!isPassenger && !licence.equals("student")) {
            endTime = endTime.plusMinutes(10);
        }
        return endTime;
        
    }
}
