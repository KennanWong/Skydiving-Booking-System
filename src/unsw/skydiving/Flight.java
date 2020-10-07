package unsw.skydiving;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.*;


public class Flight {
    private String id;
    private String dropZone;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxLoad;
    private int vacancies;
    private List<Skydiver> skydivers;
    private List<Jump> jumps;

    public Flight(String id, String dropZone, LocalDateTime startTime, LocalDateTime endTime, int maxLoad) {
        this.id = id;
        this.dropZone = dropZone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxLoad = maxLoad;
        this.vacancies = maxLoad;
        this.skydivers = new ArrayList<>();
        this.jumps = new ArrayList<>();
    }

    public String getId() {
        return this.id;
    }

    public int getMaxLoad() {
        return this.maxLoad;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public int getVacancies() {
        return vacancies;
    }

    public void addJump(Jump jump) {
        for (Skydiver skydiver: jump.getSkydivers()){
            this.skydivers.add(skydiver);
        }
        this.jumps.add(jump);
        this.vacancies = this.vacancies - jump.getNumSkydivers();
        jump.addFlight(this);
    }


    


}
