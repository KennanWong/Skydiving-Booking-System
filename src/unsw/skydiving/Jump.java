package unsw.skydiving;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class Jump {
    private String id;
    private Flight flight;
    private String dropzone;
    private LocalDateTime startTime;
    private String type;
    private int numSkydivers;

    public Jump(String id, LocalDateTime startTime, String type) {
        this.id = id;
        this.startTime = startTime;
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return type;
    }

    public int getNumSkydivers() {
        return numSkydivers;
    }

    public Flight getFlight() {
        return flight;
    }

    public String getDropzone() {
        return dropzone;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Helper function to get skydivers and return them in a lexicographic order
     * @return
     */
    public List<Skydiver> getSkydivers() {
        List<Skydiver> listOfSkydivers = new ArrayList<>();
        switch (type) {
            case "fun":
                FunJump funJump = (FunJump) this;
                listOfSkydivers = funJump.getSkydivers();
                numSkydivers = listOfSkydivers.size();
                break;
            case "tandem":
                Tandem tandemJump = (Tandem) this;
                listOfSkydivers.add((tandemJump.getPassenger()));
                listOfSkydivers.add(tandemJump.getTandemMaster());
                break;
            case "training":
                Training trainingJump = (Training) this;
                listOfSkydivers.add(trainingJump.getTrainee());
                listOfSkydivers.add(trainingJump.getInstructor());
                break;
        }
        // sort skydivers lixicography
        
        return listOfSkydivers;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public void setNumSkydivers(int numSkydivers) {
        this.numSkydivers = numSkydivers;
    }

    public void setDropzone(String dropzone) {
        this.dropzone = dropzone;
    }
}
