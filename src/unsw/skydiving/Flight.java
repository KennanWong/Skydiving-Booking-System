package unsw.skydiving;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Flight {
    private String id;
    private String dropzone;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxLoad;
    private int vacancies;
    private List<Jump> jumps;

    public Flight(String id, String dropzone, LocalDateTime startTime, LocalDateTime endTime, int maxLoad) {
        this.id = id;
        this.dropzone = dropzone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxLoad = maxLoad;
        this.vacancies = maxLoad;
        this.jumps = new ArrayList<>();
    }

    public String getId() {
        return this.id;
    }

    public int getMaxLoad() {
        return this.maxLoad;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public int getVacancies() {
        return vacancies;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getDropzone() {
        return dropzone;
    }

    public int getIndexOfJump(Jump jump) {
        return jumps.indexOf(jump);
    }

    public List<Jump> getJumps() {
        return jumps;
    }

    public List<Jump> getOrderedJumps() {
        List<Jump> orderedList = new ArrayList<>();
        for (Jump toAdd: jumps) {
            if (orderedList.size() == 0) {
                orderedList.add(toAdd);
            }
            else {
                boolean added = false;
                for (Jump inList: orderedList) {
                    if (getTypeIntValue(toAdd.getType()) < getTypeIntValue(inList.getType())) {
                        orderedList.add(orderedList.indexOf(inList), toAdd);
                        added = true;
                        break;
                    } else if (getTypeIntValue(toAdd.getType()) == getTypeIntValue(inList.getType())) {
                        if (toAdd.getNumSkydivers() < inList.getNumSkydivers()) {
                            orderedList.add(orderedList.indexOf(inList), toAdd);
                            added = true;
                            break;
                        }
                    }
                }
                if (!added) {
                    orderedList.add(toAdd);
                }
            }
            
        }
        return orderedList;
    }

    public void addJump(Jump jump) {
        this.jumps.add(jump);
        this.vacancies = this.vacancies - jump.getNumSkydivers();
        jump.setFlight(this);
        jump.setDropzone(this.dropzone);

    }

    public void removeJump(Jump jump) {
        vacancies = vacancies + jump.getNumSkydivers();
        jumps.remove(jump);
    }

    public void addJumpAtIndex(int index, Jump jump) {
        jumps.add(index, jump);
        vacancies = vacancies - jump.getNumSkydivers();
    }


    /**
     * Helper function to return int value of a type
     * fun = 1;
     * training = 2
     * tandem = 3
     * @param type type we want to get in value of
     * @return
     */
    private int getTypeIntValue(String type) {
        int val = 0;
        switch (type) {
            case "fun":
                val = 1;
                break;       
            case "training":
                val = 2;
                break;
            case "tandem":
                val = 3;
                break;
        }
        return val;
    }
}
