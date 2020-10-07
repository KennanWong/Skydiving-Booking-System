package unsw.skydiving;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FunJump extends Jump {
    private List<Skydiver> skydivers;

    public FunJump (String id, LocalDateTime startTime, String type, List<Skydiver> skydivers) {
        super(id, startTime, type);
        this.skydivers = skydivers;
    }
    
}
