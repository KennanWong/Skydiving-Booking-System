package unsw.skydiving;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class Skydiver {
    private String skydiver;
    private String license;
    private List<Jump> jumps;


    public Skydiver (String skydiver, String license) {
        this.skydiver = skydiver;
        this.license = license;
        this.jumps = new ArrayList<>();
    }

    public String getName() {
        return this.skydiver;
    }

    /**
     * Method to check if a skydiver is valid to jump
     * @return True or False depending on if jump is valid
     */
    public boolean isValidToJump() {
        return true;
    }

    public void addJump(Jump newJump) {
        this.jumps.add(newJump);
    }

}
