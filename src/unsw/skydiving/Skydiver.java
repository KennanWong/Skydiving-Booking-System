package unsw.skydiving;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class Skydiver {
    private String skydiver;
    private String license;
    private List<Jump> jumps;
    private int numJumps;


    public Skydiver (String skydiver, String license) {
        this.skydiver = skydiver;
        this.license = license;
        this.jumps = new ArrayList<>();
        this.numJumps = 0;
    }

    public String getName() {
        return this.skydiver;
    }

    /**
     * Method to check if a skydiver is valid to jump
     * @return True or False depending on if jump is valid
     */
    public boolean isValidToJump(Jump jump) {
        return true;
    }

    public void addJump(Jump newJump) {
        this.jumps.add(newJump);
        this.numJumps = this.numJumps + 1;
    }

    public int getNumJumps() {
        return numJumps;
    }

    public String getLicense() {
        return license;
    }

}
