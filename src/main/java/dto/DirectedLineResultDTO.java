package dto;

import domain.RailNode;
import domain.RailLine;

import java.util.List;
import java.util.Set;

/*
 * Data Transfer Object for the result of the Directed Line Upgrade Plan (US11).
 */
public class DirectedLineResultDTO {

    private final boolean hasCycle;
    private final List<RailNode> upgradeOrder;
    private final Set<RailNode> cycleStations;
    private final Set<RailLine> cycleLinks;
    private final int cycleCount;

    public DirectedLineResultDTO(boolean hasCycle,
                                 List<RailNode> upgradeOrder,
                                 Set<RailNode> cycleStations,
                                 Set<RailLine> cycleLinks,
                                 int cycleCount) {

        this.hasCycle = hasCycle;
        this.upgradeOrder = upgradeOrder;
        this.cycleStations = cycleStations;
        this.cycleLinks = cycleLinks;
        this.cycleCount = cycleCount;
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

    public Set<RailLine> getCycleLinks() {
        return cycleLinks;
    }

    public int getCycleCount() {
        return cycleCount;
    }
}
