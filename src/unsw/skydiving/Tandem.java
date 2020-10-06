package unsw.skydiving;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class Tandem extends Jump {
    private Skydiver passenger;
    private Skydiver tandemMaster;

    public Tandem(String id, String dropZone, LocalDateTime startTime, String type, Skydiver passenger, Skydiver tandemMaster) {
        super(id, dropZone, startTime, type);
        this.passenger = passenger;
        this.tandemMaster = tandemMaster;
    }
}
