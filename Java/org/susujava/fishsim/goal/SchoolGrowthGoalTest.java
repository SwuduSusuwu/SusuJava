package org.susujava.fishsim.goal;

import org.susujava.fishsim.SchoolManager;
import org.susujava.fishsim.SimulationContext;

public class SchoolGrowthGoalTest {
    public static void main(String[] args) {
        SchoolManager sm = new SchoolManager();
        // start with small cohesive school
        sm.addFish(new SchoolManager.Fish(0, 0));
        sm.addFish(new SchoolManager.Fish(1, 0));
        sm.addFish(new SchoolManager.Fish(0, 1));
        SimulationContext ctx = new SimulationContext(sm);
        SchoolGrowthGoal g = new SchoolGrowthGoal(5, 0.2);
        g.init(ctx);
        for (int i = 0; i < 60; i++) {
            g.update(1.0);
            if (g.isComplete()) {
                System.out.println("Goal completed at second " + i + ", size=" + sm.size());
                return;
            }
        }
        System.out.println("Not completed. size=" + sm.size());
    }
}
