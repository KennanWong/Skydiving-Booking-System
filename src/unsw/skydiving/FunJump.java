package unsw.skydiving;

import java.time.LocalDateTime;
import java.util.List;


public class FunJump extends Jump {
    private List<Skydiver> skydivers;

    public FunJump (String id, LocalDateTime startTime, String type, List<Skydiver> skydivers) {
        super(id, startTime, type);
        this.skydivers = skydivers;
        super.setNumSkydivers(skydivers.size());
    }
    
    public List<Skydiver> getSkydivers() {
        int numSkydivers = skydivers.size();
        if (numSkydivers > 1) {
            for (int i = 0; i <  numSkydivers-1; i++) {
                for (int j = 0; j < numSkydivers-i-1; j++) {
                    if (skydivers.get(j).getName().compareTo(skydivers.get(j+1).getName()) > 0) {
                        Skydiver temp = skydivers.get(j);
                        skydivers.set(j, skydivers.get(j+1));
                        skydivers.set(j+1, temp);
                    }
                }
            }
        }
        return skydivers;
    }
}
