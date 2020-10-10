package unsw.skydiving;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class Tandem extends Jump {
    private Skydiver passenger;
    private Skydiver tandemMaster;

    public Tandem(String id, LocalDateTime startTime, String type, Skydiver passenger) {
        super(id, startTime, type);
        this.passenger = passenger;
        super.setNumSkydivers(1);
    }

    public Skydiver getPassenger() {
        return passenger;
    }

    public Skydiver getTandemMaster() {
        return tandemMaster;
    }


    public void setTandemMaster(Skydiver tandemMaster) {
        this.tandemMaster = tandemMaster;
        super.setNumSkydivers(2);
    }
}
