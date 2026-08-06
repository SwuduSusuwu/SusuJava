package org.susujava.fishsim.goal;

import org.susujava.fishsim.SimulationContext;

public interface Goal {
    void init(SimulationContext ctx);
    void update(double deltaSeconds);
    boolean isComplete();
    String getName();
}
