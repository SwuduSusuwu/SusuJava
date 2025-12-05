/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> has the newest version of `./susuwu/FishSim.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; /* Usage: `import susuwu.FishSim;` */
/*
```markdown
Intro to simple [*JavaFX*](https://github.com/openjdk/jfx) fish sim. Usage: `import susuwu.FishSim;` includes `public class`s (for new sims to use),
* This ([`./susuwu/FishSim.java`](./FishSim.java)) uses pseudo-*Markdown* for comments, but [`./posts/FishSim.md`](../posts/FishSim.md) is the actual [*Markdown*](https://github.github.com/gfm/) document for this.
* [Comments with multiple rows](../README.md#java) tend to start all rows with " *", but this comment omits those, due to use of "* " for *Markdown* lists.
* This *Java* source code is split from [`../SusuPosts/posts/Human_ancestors_are_fish.md#request-java-fish`](https://github.com/SwuduSusuwu/SusuPosts/blob/69b7b1545ab51a1c1a562c0ac838a950bb086442/posts/Human_ancestors_are_fish.md#request-java-fish).
* The original version of this source code was [produced through *Solar-Pro-2*](https://poe.com/s/ehlOJYRJNsrGJttfJ4HK), but the goal is just to use thus as a template (for future versions to replace all with own source code).

******************************************

Notices: what follows is [*Solar-Pro-2*'s original version](https://poe.com/s/ehlOJYRJNsrGJttfJ4HK) (published for historical value), which `javac ./susuwu/FishSim.java` can not build as-is. Future versions will replace all of this.
* Use `:%s/, 0, 0, 0/, 0, 0/` if *Java* says "error: method rotate in class Transform cannot be applied to given types; ... actual and formal argument lists differ in length"
* You must improve `applySeparation` (such as `:%s/ACCELERATION/ACCELERATION * 2/`, to enforce more room) so you can view individual fish.
* Use `sudo apt install openjfx openjdk-25-jdk-headless` for the packages on *Ubuntu*.
  * use `PATH_TO_FX=/usr/share/openjfx/lib/` to build on *Ubuntu*.

``` end of *Markdown*
*/

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FishSim extends Application {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final int FISH_COUNT = 50;
	private static final double SEPARATION_DISTANCE = 50;
	private static final double ALIGNMENT_DISTANCE = 100;
	private static final double COHESION_DISTANCE = 100;
	private static final double MAX_SPEED = 3.0;
	private static final double ACCELERATION = 0.1;

	private List<Fish> fishList = new ArrayList<>();
	private Random random = new Random();
	private Pane root = new Pane();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// Initialize fish
		for (int i = 0; i < FISH_COUNT; i++) {
			double x = random.nextDouble() * WIDTH;
			double y = random.nextDouble() * HEIGHT;
			double dx = (random.nextDouble() * 2 - 1) * MAX_SPEED;
			double dy = (random.nextDouble() * 2 - 1) * MAX_SPEED;
			fishList.add(new Fish(x, y, dx, dy));
			root.getChildren().add(fishList.get(i).getShape());
		}

		Scene scene = new Scene(root, WIDTH, HEIGHT, Color.LIGHTBLUE);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Fish Simulation (Boids)");
		primaryStage.setResizable(false);
		primaryStage.show();

		// Start animation loop
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				updateFish();
			}
		}.start();
	}

	private void updateFish() {
		for (Fish fish : fishList) {
			// Apply flocking rules
			fish.applySeparation();
			fish.applyAlignment();
			fish.applyCohesion();
			fish.applyWallAvoidance(WIDTH, HEIGHT);

			// Update position and velocity
			fish.update();
		}
	}

	// Fish class representing each fish
	private class Fish {
		private double x, y;        // Position
		private double dx, dy;      // Velocity
		private double angle;       // Direction fish is facing
		private Polygon shape;      // Visual representation

		public Fish(double x, double y, double dx, double dy) {
			this.x = x;
			this.y = y;
			this.dx = dx;
			this.dy = dy;
			this.angle = Math.atan2(dy, dx);
			this.shape = createFishShape();
		}

		private Polygon createFishShape() {
			Polygon fish = new Polygon(
				0, -10,    // Top (head)
				-5, 10,    // Left (tail)
				-2, 0,     // Middle-left
				2, 0,      // Middle-right
				5, 10      // Right (tail)
			);
			fish.setFill(Color.ORANGERED);
			fish.setTranslateX((int) x);
			fish.setTranslateY((int) y);
			fish.getTransforms().add(javafx.scene.transform.Rotate.rotate(Math.toDegrees(angle) + 90, 0, 0, 0));
			return fish;
		}

		public void update() {
			// Limit speed
			double speed = Math.sqrt(dx * dx + dy * dy);
			if (speed > MAX_SPEED) {
				dx = (dx / speed) * MAX_SPEED;
				dy = (dy / speed) * MAX_SPEED;
			}

			// Update position
			x += dx;
			y += dy;

			// Update shape position and rotation
			shape.setTranslateX(x);
			shape.setTranslateY(y);
			angle = Math.atan2(dy, dx);
			shape.getTransforms().clear();
			shape.getTransforms().add(javafx.scene.transform.Rotate.rotate(Math.toDegrees(angle) + 90, 0, 0, 0));
		}

		public void applySeparation() {
			double sepX = 0, sepY = 0;
			int count = 0;

			for (Fish other : fishList) {
				if (other != this) {
					double dist = Math.hypot(x - other.x, y - other.y);
					if (dist < SEPARATION_DISTANCE) {
						double diffX = x - other.x;
						double diffY = y - other.y;
						sepX += diffX / dist;
						sepY += diffY / dist;
						count++;
					}
				}
			}

			if (count > 0) {
				sepX /= count;
				sepY /= count;

				// Normalize and scale separation force
				double sepLength = Math.sqrt(sepX * sepX + sepY * sepY);
				if (sepLength > 0) {
					sepX = (sepX / sepLength) * ACCELERATION;
					sepY = (sepY / sepLength) * ACCELERATION;
				}

				dx += sepX;
				dy += sepY;
			}
		}

		public void applyAlignment() {
			double avgDX = 0, avgDY = 0;
			int count = 0;

			for (Fish other : fishList) {
				if (other != this) {
					double dist = Math.hypot(x - other.x, y - other.y);
					if (dist < ALIGNMENT_DISTANCE) {
						avgDX += other.dx;
						avgDY += other.dy;
						count++;
					}
				}
			}

			if (count > 0) {
				avgDX /= count;
				avgDY /= count;

				// Normalize and scale alignment force
				double length = Math.sqrt(avgDX * avgDX + avgDY * avgDY);
				if (length > 0) {
					avgDX = (avgDX / length) * ACCELERATION;
					avgDY = (avgDY / length) * ACCELERATION;
				}

				dx += avgDX;
				dy += avgDY;
			}
		}

		public void applyCohesion() {
			double avgX = 0, avgY = 0;
			int count = 0;

			for (Fish other : fishList) {
				if (other != this) {
					double dist = Math.hypot(x - other.x, y - other.y);
					if (dist < COHESION_DISTANCE) {
						avgX += other.x;
						avgY += other.y;
						count++;
					}
				}
			}

			if (count > 0) {
				avgX = (avgX / count) - x;
				avgY = (avgY / count) - y;

				// Normalize and scale cohesion force
				double length = Math.sqrt(avgX * avgX + avgY * avgY);
				if (length > 0) {
					avgX = (avgX / length) * ACCELERATION;
					avgY = (avgY / length) * ACCELERATION;
				}

				dx += avgX;
				dy += avgY;
			}
		}

		public void applyWallAvoidance(double width, double height) {
			double avoidanceX = 0, avoidanceY = 0;

			// Left wall
			if (x < 20) {
				avoidanceX += (20 - x) / 20 * ACCELERATION;
			}
			// Right wall
			if (x > width - 20) {
				avoidanceX -= (x - (width - 20)) / 20 * ACCELERATION;
			}
			// Top wall
			if (y < 20) {
				avoidanceY += (20 - y) / 20 * ACCELERATION;
			}
			// Bottom wall
			if (y > height - 20) {
				avoidanceY -= (y - (height - 20)) / 20 * ACCELERATION;
			}

			dx += avoidanceX;
			dy += avoidanceY;
		}

		public Polygon getShape() {
			return shape;
		}
	};
};

