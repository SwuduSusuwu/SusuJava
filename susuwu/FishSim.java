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
* This `java` source code is split from [`../SusuPosts/posts/Human_ancestors_are_fish.md#request-java-fish`](https://github.com/SwuduSusuwu/SusuPosts/blob/69b7b1545ab51a1c1a562c0ac838a950bb086442/posts/Human_ancestors_are_fish.md#request-java-fish).
* The [original version of this source code](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java) was [produced through *Solar-Pro-2*](https://poe.com/s/ehlOJYRJNsrGJttfJ4HK), but the goal is just to use thus as a template (for future versions to replace all with own source code).

******************************************

Prefixes (used for variables / functions / classes): `` +`Class` `` introduces `Class`, `` -`Class` `` removes `Class`, `` @`Class` `` changes (neutral or improves) `Class` (as [used for `git commit` messages](../README.md#git)). `:%s/from/to/` shows `vim` regular expressions.
**Notice**: this `git branch` improves [*Solar-Pro-2*'s original source code](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java) as this list (plus [*GitHub*'s `/compare/` tool shows](https://github.com/SwuduSusuwu/SusuJava/compare/solarPro2FishSim..susuFishSim#diff-8c440bb92bc6939e1450542897e0bbb1a8737b93808ea63ed32784edfacef4b4)) shows:
* `:%s/, 0, 0, 0/, 0, 0/`: fixes "error: method rotate in class Transform cannot be applied to given types; ... actual and formal argument lists differ in length"
* [+`frameCount`, +`lastTime`, +`fps`, +`fpsText`](https://github.com/SwuduSusuwu/SusuJava/commit/50319ff075fc3a31f761c8fdc1fcce46a2471218) to show **FPS** ([produced through _Solar-Pro-2_](https://poe.com/s/OSENRaU2uCb4TznzRPas)).
  * @`AnimationTimer::handle()`: show true **FPS**, plus do not to redraw `fpsText` unless `fps` changes.
* @`Fish::applySeparation()`: reuse values.
* @`Fish::applyWallAvoidance()`: reuse values, plus replace [magic constants](https://stackoverflow.com/questions/43950998/what-are-symbolic-constants-and-magic-constants) with `BOUNDS_DISTANCE`.
* @`Fish::update()`: if rotation is miniscule, this reuses transforms (to improve `fps`, but `fps` is too unstable to notice differences.)
* +`*_FACTOR`: (`= 1` for original results), scales `Fish::apply*()` forces.
  * @`SEPARATION_FACTOR`: (from `1`) to `2`, so schools are loose enough to view individual fish.
  * @`SEPARATION_DISTANCE`: (from `50`) to `22`, so fish still school.
* @`WIDTH`: (from `800`) to `1280`, @`HEIGHT`: (from `600`) to `720`, since most computers (plus smartphones) can show *720p* resolution.
  * @`FISH_COUNT`: (from `50`) to `102`, since the window now has more room.
* @`Fish::createFishShape()`: produce 2 colors of fish.
  * @`class FishSim`: is now close to a fluid particle sim which has 2 types of molecules which group to similar molecules (such as [oleophilic compounds](https://thepetrosolutions.com/forums/topic/difference-between-oleophobic-and-oleophilic-impurities/#post-3508)) plus separate from nonsimilar molecules ([such as oleophobic compounds](https://poe.com/s/dYx54tOaDTaDnaBT9TRm)), except the numerous steps of *Boids* formula cause some emergent phenomenon which simple molecules do not possess.

******************************************

Notice: [Used *Solar-Pro-2* to improve codeflow](https://github.com/SwuduSusuwu/SusuJava/commit/6242d2045d619dd664c9a8eea9141c5d178a2ce8) (of the ancestor `git commit` --- which was half (`1 / 2`) human-produced source code --- to thus) [so `fps` improves](https://poe.com/s/ifeHY8AcpVmVC7R5aPB7):
* @`class FishSim`: move `class Fish`-specific values into @`class Fish`.
* @`class FishSim`: use `java.util.concurrent.Executor{s,Service}` to offload `updateFish()` physics (now uses 2 **CPU**s).
* +`GRID_SIZE`, @`updateFish()`: use `GRID_SIZE` to split `List<Fish> fishList` into `List<Fish>[][] grid` (which reduces *O(n^2)* to *O(n^2 / (WIDTH / GRID_SIZE) / (HEIGHT / GRID_SIZE))* **CPU** use).
* -`javafx.scene.shape.Polygon`, +`javafx.scene.canvas.Canvas`, +`javafx.scene.canvas.GraphicsContext`: improves renderer **CPU** use?
* @`class FishSim`: replaces `1.0 / 2 < random.nextDouble()` with `random.nextBoolean()`.
* {-`Fish::createFishShape()`, -`Fish::getShape()`}, {+`Fish::render()`, +`FishSim::renderFish()`}: switch to `GraphicsContext`.
* @`Fish::update`: `Fish` now wrap around (to opposite edges) if out-of-bounds.
* Notice: the list which follows is all own improvements (versus version above). Own version:
  * @`FishSim::*`, @`Fish::*`: now mutable (since future versions will allow to resize windows plus configure distances). TODO: introduce `get*()` methods (so that typos do not reconfigure constants, such as view distances).
  * @`GRID_SIZE`: documents minimum value which enforces `*_DISTANCE`s.
  * @`applyFlockingRules()`: replaces magic constants (`100`) with `GRID_SIZE` (fixes undefined behaviour if `GRID_SIZE` changes).
  * @`Fish::applyFlockingRules()`, @`Fish::update()`: Replaces magic constants ({`2600`, `1600`}) with {`WIDTH`, `HEIGHT`}.
  * @`class FishSim`: reduces `UPDATE_INTERVAL` (from `5`) to `2` (since `ExecutorService` is used, this does not lower `fps`) so physics is smooth.
  * +`Fish::isSimilarTo()`: limits schools to similar `Fish`. @`apply*()`: uses this.
  * +`FishSim::refreshLoop()`: houses `FishSim::ApplicationTimer::handle()`'s codeflow. Reason: so is simple for future versions to switch `new AnimationTimer() {@Override public void handle(long now) { refreshLoop(); }}.start();` to alternatives (such as to `Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000.0 / monitorRefreshHertz), event -> { refreshLoop(); })); timeline.setCycleCount(Animation.INDEFINITE); timeline.play();`).
    * @`FishSim::fpsText`: `String.format("%4.2f", fps)` (`4.` so `fpsText.size()` does not change if `fps` magnitude does, `.2` to show miniscule differences).
  * @`Fish::update()`: documents future `Fish::setPos()`, which will have alternatives (versus wraparound) to ensure `Fish` are in bounds.

``` end of *Markdown*
*/

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FishSim extends Application {

	private static int WIDTH = 1280;
	private static int HEIGHT = 720;
	private static int FISH_COUNT = 102;
	private static int GRID_SIZE = 100; // Notice: set this to `Colllections.max({*_DISTANCE})` (which should equal what most sims call "view distance"), so that all relevent `Fish` are processed.
	private static int UPDATE_INTERVAL = 2; // The `frameCounter` per `Fish::applyFlockingRulesUpdate()`

	private List<Fish> fishList = new ArrayList<>();
	private Random random = new Random();
	private Pane root = new Pane();
	private Canvas canvas = new Canvas(WIDTH, HEIGHT);
	private GraphicsContext gc = canvas.getGraphicsContext2D();

	private Text fpsText = new Text("0 FPS");
	private int frameCount = 0;
	private long lastTime = System.nanoTime();
	private double fps = 0;
	private int frameCounter = 0;

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// Initialize fish
		for (int i = 0; i < FISH_COUNT; i++) {
			double x = random.nextDouble() * WIDTH;
			double y = random.nextDouble() * HEIGHT;
			double dx = (random.nextDouble() * 2 - 1) * Fish.MAX_SPEED;
			double dy = (random.nextDouble() * 2 - 1) * Fish.MAX_SPEED;
			fishList.add(new Fish(x, y, dx, dy, random.nextBoolean() ? Color.BLUE : Color.GREEN));
		}

		root.getChildren().add(canvas);
		root.getChildren().add(fpsText);

		Scene scene = new Scene(root, WIDTH, HEIGHT, Color.LIGHTBLUE);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Fish Simulation (Boids)");
		primaryStage.setResizable(false);
		primaryStage.show();

		fpsText.setX(10);
		fpsText.setY(30);
		fpsText.setFill(Color.WHITE);

		// Start animation loop
		new AnimationTimer() {
			@Override
			public void handle(long now) { refreshLoop(now); }
		}.start();
	}

	private void refreshLoop(long now) {
		frameCounter++;
		if (frameCounter % UPDATE_INTERVAL == 0) {
			executor.submit(() -> updateFish());
		}

		renderFish();

		double elapsed = (now - lastTime) / 1_000_000_000.0;
		if (elapsed >= 1.0) {
			lastTime = now;
			fps = frameCount / elapsed;
			Platform.runLater(() -> {
				fpsText.setText(String.format("%4.2f FPS", fps));
			});
			frameCount = 0;
		} else {
			frameCount++;
		}
	}

	private void updateFish() {
		// Use spatial partitioning (simple grid system)
		List<Fish>[][] grid = new ArrayList[WIDTH / GRID_SIZE][HEIGHT / GRID_SIZE];
		for (int i = 0; i < WIDTH / GRID_SIZE; i++) {
			for (int j = 0; j < HEIGHT / GRID_SIZE; j++) {
				grid[i][j] = new ArrayList<>();
			}
		}

		// Assign fish to grid cells
		for (Fish fish : fishList) {
			int gridX = (int) (fish.x / GRID_SIZE);
			int gridY = (int) (fish.y / GRID_SIZE);
			if (gridX >= 0 && gridX < WIDTH / GRID_SIZE && gridY >= 0 && gridY < HEIGHT / GRID_SIZE) {
				grid[gridX][gridY].add(fish);
			}
		}

		// Update each fish
		for (Fish fish : fishList) {
			fish.applyFlockingRules(fishList, grid);
			fish.update();
		}
	}

	private void renderFish() {
		gc.clearRect(0, 0, WIDTH, HEIGHT);
		for (Fish fish : fishList) {
			fish.render(gc);
		}
	}

	@Override
	public void stop() {
		executor.shutdown();
	}

	public static class Fish {
		private static double SEPARATION_DISTANCE = 22;
		private static double SEPARATION_FACTOR = 2;
		private static double SEPARATION_NONSIMILAR_DISTANCE = 42;
		private static double SEPARATION_NONSIMILAR_FACTOR = 2.2;
		private static double ALIGNMENT_DISTANCE = 100;
		private static double ALIGNMENT_FACTOR = 1;
		private static double COHESION_DISTANCE = 100;
		private static double COHESION_FACTOR = 1;
		private static double BOUNDS_DISTANCE = 20;
		private static double BOUNDS_FACTOR = 1;
		private static double MAX_SPEED = 3.0;
		private static double ACCELERATION = 0.1;

		private double x, y;        // Position
		private double dx, dy;      // Velocity
		private Color color;

		public Fish(double x, double y, double dx, double dy, Color color) {
			this.x = x;
			this.y = y;
			this.dx = dx;
			this.dy = dy;
			this.color = color;
		}

		public boolean isSimilarTo(Fish o) {
			return color.equals(o.color); /* TODO: use `Math.hypot()` (Euclidean distance) of color component differences, to allow close matches. Use a function (such as `javafx.scene.shape.Polygon.getPoints()`), for comparison of vertices. */
		}

		public void applyFlockingRules(List<Fish> allFish, List<Fish>[][] grid) {
			int gridX = (int) (x / GRID_SIZE);
			int gridY = (int) (y / GRID_SIZE);
			List<Fish> nearbyFish = new ArrayList<>();

			// Check neighboring grid cells
			for (int i = Math.max(0, gridX - 1); i <= Math.min(grid.length - 1, gridX + 1); i++) {
				for (int j = Math.max(0, gridY - 1); j <= Math.min(grid[0].length - 1, gridY + 1); j++) {
					nearbyFish.addAll(grid[i][j]);
				}
			}

			applySeparation(nearbyFish);
			applyAlignment(nearbyFish);
			applyCohesion(nearbyFish);
			applyWallAvoidance(WIDTH, HEIGHT);
		}

		private void applySeparation(List<Fish> nearbyFish) {
			double sepX = 0, sepY = 0;
			double sepNonsimilarX = 0, sepNonsimilarY = 0;
			int count = 0, countNonsimilar = 0;

			for (Fish other : nearbyFish) {
				if (other != this) {
					double diffX = x - other.x;
					double diffY = y - other.y;
					double dist = Math.hypot(diffX, diffY);
					if (isSimilarTo(other)) {
						if (dist < SEPARATION_DISTANCE) {
							sepX += diffX / dist;
							sepY += diffY / dist;
							count++;
						}
					} else {
						if (dist < SEPARATION_NONSIMILAR_DISTANCE) {
							sepNonsimilarX += diffX / dist;
							sepNonsimilarY += diffY / dist;
							countNonsimilar++;
						}
					}
				}
			}

			if (count > 0) {
				sepX /= count;
				sepY /= count;
				double sepLength = Math.sqrt(sepX * sepX + sepY * sepY);
				if (sepLength > 0) {
					dx += (sepX / sepLength) * ACCELERATION * SEPARATION_FACTOR;
					dy += (sepY / sepLength) * ACCELERATION * SEPARATION_FACTOR;
				}
			}
			if (countNonsimilar > 0) {
				sepNonsimilarX /= countNonsimilar;
				sepNonsimilarY /= countNonsimilar;
				double sepLength = Math.sqrt(sepNonsimilarX * sepNonsimilarX + sepNonsimilarY * sepNonsimilarY);
				if (sepLength > 0) {
					dx += (sepNonsimilarX / sepLength) * ACCELERATION * SEPARATION_NONSIMILAR_FACTOR;
					dy += (sepNonsimilarY / sepLength) * ACCELERATION * SEPARATION_NONSIMILAR_FACTOR;
				}
			}
		}

		private void applyAlignment(List<Fish> nearbyFish) {
			double avgDX = 0, avgDY = 0;
			int count = 0;

			for (Fish other : nearbyFish) {
				if (other != this && isSimilarTo(other)) {
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
				double length = Math.sqrt(avgDX * avgDX + avgDY * avgDY);
				if (length > 0) {
					avgDX = (avgDX / length) * ACCELERATION * ALIGNMENT_FACTOR;
					avgDY = (avgDY / length) * ACCELERATION * ALIGNMENT_FACTOR;
				}
				dx += avgDX;
				dy += avgDY;
			}
		}

		private void applyCohesion(List<Fish> nearbyFish) {
			double avgX = 0, avgY = 0;
			int count = 0;

			for (Fish other : nearbyFish) {
				if (other != this && isSimilarTo(other)) {
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
				double length = Math.sqrt(avgX * avgX + avgY * avgY);
				if (length > 0) {
					avgX = (avgX / length) * ACCELERATION * COHESION_FACTOR;
					avgY = (avgY / length) * ACCELERATION * COHESION_FACTOR;
				}
				dx += avgX;
				dy += avgY;
			}
		}

		private void applyWallAvoidance(double width, double height) {
			double avoidanceX = 0, avoidanceY = 0;

			if (x < BOUNDS_DISTANCE) {
				avoidanceX += (BOUNDS_DISTANCE - x);
			}
			if (x > width - BOUNDS_DISTANCE) {
				avoidanceX -= (x - (width - BOUNDS_DISTANCE));
			}
			if (y < BOUNDS_DISTANCE) {
				avoidanceY += (BOUNDS_DISTANCE - y);
			}
			if (y > height - BOUNDS_DISTANCE) {
				avoidanceY -= (y - (height - BOUNDS_DISTANCE));
			}

			dx += avoidanceX / BOUNDS_DISTANCE * ACCELERATION * BOUNDS_FACTOR;
			dy += avoidanceY / BOUNDS_DISTANCE * ACCELERATION * BOUNDS_FACTOR;
		}

		public void update() {
			// Limit speed
			double speed = Math.sqrt(dx * dx + dy * dy);
			if (speed > MAX_SPEED) {
				dx = (dx / speed) * MAX_SPEED;
				dy = (dy / speed) * MAX_SPEED;
			}

			// Update position // TODO: move into future `Fish::setPos()`, which shall have the invariant `Fish.pos <= FishSim.resolution` established.
			x += dx;
			y += dy;

			// Wrap around screen // TODO: move into future `Fish::setPos()`, as one numerous (optional) solutions which ensure `Fish.pos <= FishSim.resolution` is established.
			x = (x + WIDTH) % WIDTH;
			y = (y + HEIGHT) % HEIGHT;
		}

		public void render(GraphicsContext gc) {
			gc.save();
			gc.translate(x, y);
			gc.rotate(Math.toDegrees(Math.atan2(dy, dx)) + 90);
			gc.setFill(color);
			gc.beginPath();
			gc.moveTo(0, -10);
			gc.lineTo(-5, 10);
			gc.lineTo(-2, 0);
			gc.lineTo(2, 0);
			gc.lineTo(5, 10);
			gc.closePath();
			gc.fill();
			gc.restore();
		}
	};
};

