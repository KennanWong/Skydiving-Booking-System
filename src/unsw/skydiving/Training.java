package unsw.skydiving;

import java.time.LocalDateTime;

public class Training extends Jump {
    private Skydiver trainee;
    private Skydiver instructor;

    public Training (String id, LocalDateTime startTime, String type, Skydiver trainee) {
        super(id, startTime, type);
        this.trainee = trainee;
        super.setNumSkydivers(1);
    }

    public void setInstructor(Skydiver instructor) {
        this.instructor = instructor;
        super.setNumSkydivers(2);
    }

    public Skydiver getTrainee() {
        return trainee;
    }

    public Skydiver getInstructor() {
        return instructor;
    }

}
