package dto;

import java.util.List;
import java.util.Set;

/*
* Data Transfer Object for the result of the Directed Line Upgrade Plan.
* It encapsulates whether the graph has cycles, the upgrade order of stations,
* the stations involved in cycles, and the computational complexity of the operation.
*/

public class DirectedLineResultDTO {

    private final boolean hasCycle;
    private final List<String> upgradeOrder;
    private final Set<String> cycleStations;
    private final String complexity;

    public DirectedLineResultDTO(boolean hasCycle, List<String> upgradeOrder, Set<String> cycleStations, String complexity) {
        this.hasCycle = hasCycle;
        this.upgradeOrder = upgradeOrder;
        this.cycleStations = cycleStations;
        this.complexity = complexity;
    }

    public boolean hasCycle() {
        return hasCycle;
    }

    public List<String> getUpgradeOrder() {
        return upgradeOrder;
    }

    public Set<String> getCycleStations() {
        return cycleStations;
    }

    public String getComplexity() {
        return complexity;
    }
}
