package unsw.skydiving;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class Jump {
    private String id;
    private String dropZone;
    private LocalDateTime startTime;
    private String type;

    public Jump(String id, String dropZone, LocalDateTime startTime, String type) {
        this.id = id;
        this.dropZone = dropZone;
        this.startTime = startTime;
        this.type = type;
    }

    public getId() {
        return this.id;
    }
}
