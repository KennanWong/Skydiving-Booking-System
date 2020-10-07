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

    public Jump(String id, LocalDateTime startTime, String type) {
        this.id = id;
        this.startTime = startTime;
        this.type = type;
    }

    public String getId() {
        return this.id;
    }
}
