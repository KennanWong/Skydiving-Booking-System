package unsw.skydiving;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Training extends Jump {
    private Skydiver trainee;
    private Skydiver instructor;

    public Training (String id, String dropZone, LocalDateTime startTime, String type, Skydiver trainee, Skydiver instructor) {
        super(id, dropZone, startTime, type);
        this.trainee = trainee;
        this.instructor = instructor;
    }
}
