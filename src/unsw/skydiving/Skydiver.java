package unsw.skydiving;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class Skydiver {
    private String skydiver;
    private String license;
    private List<Jump> bookings;


    public Skydiver (String skydiver, String license) {
        this.skydiver = skydiver;
        this.license = license;
        this.bookings = new ArrayList<>();
    }


}
