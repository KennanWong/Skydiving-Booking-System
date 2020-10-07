package unsw.skydiving;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class Jump {
    private String id;
    private Flight flight;
    private String dropZone;
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


    public int getNumSkydivers() {
        return numSkydivers;
    }

    public Flight getFlight() {
        return flight;
    }

    public void addFlight(Flight flight) {
        this.flight = flight;
    }

    public void setNumSkydivers(int numSkydivers) {
        this.numSkydivers = numSkydivers;
    }

    public void setDropZone(String dropZone) {
        this.dropZone = dropZone;
    }
}
