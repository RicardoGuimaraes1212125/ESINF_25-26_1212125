package dto;

import domain.RailNode;

import java.util.List;
import java.util.Set;

/*
 * Data Transfer Object for the result of the Directed Line Upgrade Plan (US11).
 */
public class DirectedLineResultDTO {

    private final boolean hasCycle;
    private final List<RailNode> upgradeOrder;
    private final Set<RailNode> cycleStations;
    private final String complexity;

    public DirectedLineResultDTO(boolean hasCycle,
                                 List<RailNode> upgradeOrder,
                                 Set<RailNode> cycleStations,
                                 String complexity) {

        this.hasCycle = hasCycle;
        this.upgradeOrder = upgradeOrder;
        this.cycleStations = cycleStations;
        this.complexity = complexity;
    }

    public boolean hasCycle() {
        return hasCycle;
    }

    public List<RailNode> getUpgradeOrder() {
        return upgradeOrder;
    }

    public Set<RailNode> getCycleStations() {
        return cycleStations;
    }

    public String getComplexity() {
        return complexity;
    }
}
