# Goal API and initial cooperative goal

This adds a minimal Goal API and an initial cooperative goal (SchoolGrowthGoal) meant to support peaceful multi-user gameplay per posts/MultiuserConcernsPlusGoals.md.

Files added:
- `Java/org/susujava/fishsim/SimulationContext.java` - small simulation context holder
- `Java/org/susujava/fishsim/SchoolManager.java` - lightweight in-memory school manager
- `Java/org/susujava/fishsim/goal/Goal.java` - Goal interface
- `Java/org/susujava/fishsim/goal/SchoolGrowthGoal.java` - initial concrete Goal implementation
- `Java/org/susujava/fishsim/goal/SchoolGrowthGoalTest.java` - simple test harness (run with `java`)

Notes
- This is intentionally small and does not modify rendering code. It is designed to be rebased onto the merged Copilot branches and later integrated with the simulation core and networking layer.
- Next steps I will take (unless you prefer otherwise): integrate Goal registration into the main FishSim class, add ecosystem manager, add more goals (EcosystemStewardship, PatternGoal), and tests.
