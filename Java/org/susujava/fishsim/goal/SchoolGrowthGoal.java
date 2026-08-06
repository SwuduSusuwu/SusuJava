package org.susujava.fishsim.goal;

import org.susujava.fishsim.SimulationContext;
import org.susujava.fishsim.SchoolManager;

public class SchoolGrowthGoal implements Goal {
    private SimulationContext ctx;
    private double timeAccum = 0;
    private final int targetSize;
    private final double cohesionThreshold;

    public SchoolGrowthGoal(int targetSize, double cohesionThreshold) {
        this.targetSize = targetSize;
        this.cohesionThreshold = cohesionThreshold;
    }

    @Override
    public void init(SimulationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void update(double deltaSeconds) {
        timeAccum += deltaSeconds;
        // Simple growth: every 5 seconds, if cohesion above threshold, increase school size
        if (timeAccum >= 5.0) {
            timeAccum = 0;
            double cohesion = ctx.schoolManager.averageCohesion();
            if (cohesion >= cohesionThreshold) {
                ctx.schoolManager.addFish(new SchoolManager.Fish(Math.random() * 100, Math.random() * 100));
            }
        }
    }

    @Override
    public boolean isComplete() {
        return ctx != null && ctx.schoolManager.size() >= targetSize;
    }

    @Override
    public String getName() { return "SchoolGrowthGoal"; }
}
