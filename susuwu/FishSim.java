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
* The [original version of this source code](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java) was [produced through *Solar-Pro-2*](https://poe.com/s/ehlOJYRJNsrGJttfJ4HK), but the goal is just to use thus as a template (for future versions to replace all with own source code).

******************************************

Prefixes (used for variables / functions / classes): `` +`Class` `` introduces `Class`, `` -`Class` `` removes `Class`, `` @`Class` `` changes (neutral or improves) `Class` (as [used for `git commit` messages](../README.md#git)). `:%s/from/to/` shows `vim` regular expressions.
**Notice**: this `git branch` improves [*Solar-Pro-2*'s original source code](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java) as this list (plus [*GitHub*'s `/compare/` tool shows](https://github.com/SwuduSusuwu/SusuJava/compare/solarPro2FishSim..susuFishSim#diff-8c440bb92bc6939e1450542897e0bbb1a8737b93808ea63ed32784edfacef4b4)) shows:
* `:%s/, 0, 0, 0/, 0, 0/`: fixes "error: method rotate in class Transform cannot be applied to given types; ... actual and formal argument lists differ in length"
* [+`frameCount`, +`lastTime`, +`fps`, +`fpsText`](https://github.com/SwuduSusuwu/SusuJava/commit/50319ff075fc3a31f761c8fdc1fcce46a2471218) to show **FPS** ([produced through _Solar-Pro-2_](https://poe.com/s/OSENRaU2uCb4TznzRPas)).
  * @`AnimationTimer::handle()`: show true **FPS**, plus do not to redraw `fpsText` unless `fps` changes.
* @`Fish::applySeparation()`: reuse values.
* @`Fish::applyWallAvoidance()`: reuse values, plus replace [magic constants](https://stackoverflow.com/questions/43950998/what-are-symbolic-constants-and-magic-constants) with `BOUNDS_DISTANCE`.
* +`FishSim::refreshLoop()`: now houses `FishSim::ApplicationTimer::handle()`'s codeflow. Reason: so is simple for future versions to switch `new AnimationTimer() {@Override public void handle(long now) { refreshLoop(); }}.start();` to alternatives (such as to `Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000.0 / monitorRefreshHertz), event -> { refreshLoop(); })); timeline.setCycleCount(Animation.INDEFINITE); timeline.play();`).
* @`Fish::update()`: if rotation is miniscule, this reuses transforms (to improve `fps`, but `fps` is too unstable to notice differences.)
* +`*_FACTOR`: (`= 1` for original results), scales `Fish::apply*()` forces.
  * @`SEPARATION_FACTOR`: (from `1`) to `2`, so schools are loose enough to view individual fish.
  * @`SEPARATION_DISTANCE`: (from `50`) to `32`, so fish still school.

``` end of *Markdown*
*/

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FishSim extends Application {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final int FISH_COUNT = 50;
	private static final double SEPARATION_DISTANCE = 32;
	private static final double SEPARATION_FACTOR = 2;
	private static final double ALIGNMENT_DISTANCE = 100;
	private static final double ALIGNMENT_FACTOR = 1;
	private static final double COHESION_DISTANCE = 100;
	private static final double COHESION_FACTOR = 1;
	private static final double BOUNDS_DISTANCE = 20;
	private static final double BOUNDS_FACTOR = 1;
	private static final double MAX_SPEED = 3.0;
	private static final double ACCELERATION = 0.1;

	private List<Fish> fishList = new ArrayList<>();
	private Random random = new Random();
	private Pane root = new Pane();

	private Text fpsText = new Text("0 FPS");
	private int frameCount = 0;
	private long lastTime = System.nanoTime();
	private double fps = 0;

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

		fpsText.setX(10);
		fpsText.setY(30);
		fpsText.setFill(Color.WHITE);
		root.getChildren().add(fpsText);

		// Start animation loop
		new AnimationTimer() {
			@Override
			public void handle(long now) { refreshLoop(now); }
		}.start();
	}

	private void refreshLoop(long now) {
		updateFish();

		double elapsed = (now - lastTime) / 1_000_000_000.0;
		if (elapsed >= 1.0) {
			lastTime = now;
			fps = frameCount / elapsed;
			Platform.runLater(() -> {
				fpsText.setText(String.format("%.1f FPS", fps));
			});
			frameCount = 0;
		} else {
			frameCount++;
		}
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
	public class Fish {
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
			fish.getTransforms().add(javafx.scene.transform.Rotate.rotate(Math.toDegrees(angle) + 90, 0, 0));
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
			double newAngle = Math.atan2(dy, dx);
			if (0.002 < Math.abs(newAngle - angle)) {
				angle = newAngle;
				shape.getTransforms().clear();
				shape.getTransforms().add(javafx.scene.transform.Rotate.rotate(Math.toDegrees(angle) + 90, 0, 0));
			}
		}

		public void applySeparation() {
			double sepX = 0, sepY = 0;
			int count = 0;

			for (Fish other : fishList) {
				if (other != this) {
					double diffX = x - other.x;
					double diffY = y - other.y;
					double dist = Math.hypot(diffX, diffY);
					if (dist < SEPARATION_DISTANCE) {
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
					sepX = (sepX / sepLength) * ACCELERATION * SEPARATION_FACTOR;
					sepY = (sepY / sepLength) * ACCELERATION * SEPARATION_FACTOR;
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
					avgDX = (avgDX / length) * ACCELERATION * ALIGNMENT_FACTOR;
					avgDY = (avgDY / length) * ACCELERATION * ALIGNMENT_FACTOR;
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
					avgX = (avgX / length) * ACCELERATION * COHESION_FACTOR;
					avgY = (avgY / length) * ACCELERATION * COHESION_FACTOR;
				}

				dx += avgX;
				dy += avgY;
			}
		}

		public void applyWallAvoidance(double width, double height) {
			double avoidanceX = 0, avoidanceY = 0;

			// Left wall
			if (x < BOUNDS_DISTANCE) {
				avoidanceX += (BOUNDS_DISTANCE - x);
			}
			// Right wall
			if (x > width - BOUNDS_DISTANCE) {
				avoidanceX -= (x - (width - BOUNDS_DISTANCE));
			}
			// Top wall
			if (y < BOUNDS_DISTANCE) {
				avoidanceY += (BOUNDS_DISTANCE - y);
			}
			// Bottom wall
			if (y > height - BOUNDS_DISTANCE) {
				avoidanceY -= (y - (height - BOUNDS_DISTANCE));
			}

			dx += avoidanceX / BOUNDS_DISTANCE * ACCELERATION * BOUNDS_FACTOR;
			dy += avoidanceY / BOUNDS_DISTANCE * ACCELERATION * BOUNDS_FACTOR;
		}

		public Polygon getShape() {
			return shape;
		}
	};
};

