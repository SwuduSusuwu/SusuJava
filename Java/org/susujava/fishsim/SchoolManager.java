package org.susujava.fishsim;

import java.util.ArrayList;
import java.util.List;

public class SchoolManager {
    public static class Fish {
        public double x, y;
        public double health;
        public Fish(double x, double y) { this.x = x; this.y = y; this.health = 1.0; }
    }

    private final List<Fish> fishes = new ArrayList<>();

    public synchronized void addFish(Fish f) { fishes.add(f); }

    public synchronized List<Fish> getFishes() { return new ArrayList<>(fishes); }

    public synchronized double averageCohesion() {
        if (fishes.isEmpty()) return 1.0;
        double sum = 0;
        for (Fish f : fishes) {
            double distSum = 0;
            int count = 0;
            for (Fish o : fishes) {
                if (o == f) continue;
                distSum += Math.hypot(f.x - o.x, f.y - o.y);
                count++;
            }
            if (count > 0) sum += (distSum / count);
        }
        double avg = sum / fishes.size();
        // map to cohesion metric [0..1] where lower average distance -> higher cohesion
        return 1.0 / (1.0 + avg);
    }

    public synchronized int size() { return fishes.size(); }

    public synchronized void moveFish(int idx, double nx, double ny) {
        if (idx >= 0 && idx < fishes.size()) { fishes.get(idx).x = nx; fishes.get(idx).y = ny; }
    }
}
