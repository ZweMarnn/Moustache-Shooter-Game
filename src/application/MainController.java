package application;

import java.io.IOException;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class MainController implements Initializable {

	@FXML
	private Pane root;

	@FXML
	private Label timerRecord;

	@FXML
	private Label lblLevel;

	@FXML
	private Label scoreRecord;

	@FXML
	private ProgressBar hpBar;

	double progress = 1;

	Player player;
	Player mainBoss;

	ImageView gunImageView;

	private double timeInterval = 0.0;

	private boolean gameRunning = true;

	private ImageView explosion;

	private MediaPlayer backgroundMusicPlayer;
	private AudioClip explosionSound;
	private AudioClip laserSound;

	public LocalDateTime start;
	public LocalDateTime end;
	public static String formattedTime;
	public static String level;

	public static int score;

	public void playingLevel() {
	    //System.out.println("in the playing" + level);
	    if (level.equalsIgnoreCase("Level1")) {
	      // First row (higher)
	      for (int i = 0; i < 5; i++) {
	        Player enemy1 = createEnemy(90.0 + i * 100.0, -30, "/images/ok.png");
	        root.getChildren().add(enemy1);
	        flyInFromTop(enemy1);
	      }
	      
	      Player enemy2 = createEnemy(20.0, 60, "/images/side.gif");
			root.getChildren().add(enemy2);
			flyInFromTop(enemy2);

			Player enemy3 = createEnemy(520, 60, "/images/side.gif");
			root.getChildren().add(enemy3);
			flyInFromTop(enemy3);

	    } else if (level.equalsIgnoreCase("Level2")) {
	      int numEnemies = 10;

	      double centerX = root.getPrefWidth() / 2.2;
	      double centerY = 10.0;
	      // double radius = 150.0; // Radius of the circular pattern

	      for (int i = 0; i < numEnemies; i++) {

	        double t = i * 2 * Math.PI / numEnemies;
	        double x = 16 * Math.pow(Math.sin(t), 1);
	        double y = 13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t);

	        Player enemy = createEnemy(centerX + x * 10, centerY - y * 10, "/images/devil-girl-unscreen.gif");
	        root.getChildren().add(enemy);
	        flyInFromTop(enemy); 
	      }

	      Player enemy2 = createEnemy(16.0, 60, "/images/veibae-bleb.gif");
	      flyInFromTop(enemy2);

	      Player enemy3 = createEnemy(523, 60, "/images/veibae-bleb.gif");
	      flyInFromTop(enemy3);

	      Player enemy4 = createEnemy(275, 60, "/images/loligod.gif");
	      root.getChildren().addAll(enemy2, enemy3, enemy4);
	      flyInFromTop(enemy4);

	    } else {

	      mainBoss = createBoss(380.0, 350.0, "/images/lastBoss.gif");
	      root.getChildren().add(mainBoss);
	      flyInFromTop(mainBoss);

	      Player enemy1 = createEnemy(80, 100, "/images/flyingHot.gif");
	      Player enemy2 = createEnemy(280, 155, "/images/flyingHot.gif");
	      Player enemy3 = createEnemy(450, 100, "/images/flyingHot.gif");

	      flyInFromTop(enemy1);
	      flyInFromTop(enemy2);
	      flyInFromTop(enemy3);
	      root.getChildren().addAll(enemy1, enemy2, enemy3);

	      Timeline bulletTimeline = new Timeline(
	          new KeyFrame(javafx.util.Duration.seconds(5), event -> fireBullet(mainBoss)));
	      bulletTimeline.setCycleCount(Timeline.INDEFINITE);
	      bulletTimeline.play();

	    }
	  }

	private void flyInFromTop(Player enemy) {
		Timeline timeline = new Timeline(
				new KeyFrame(javafx.util.Duration.ZERO, new KeyValue(enemy.translateYProperty(), -30.0)),
				new KeyFrame(javafx.util.Duration.millis(1000),
						new KeyValue(enemy.translateYProperty(), 150.0 + enemy.getTranslateY())));
		timeline.play();
	}

	private Player createEnemy(double x, double y, String imagePath) {
		return new Player(x, y, 65.0, 70.0, 1, "enemy", imagePath, 2);

	}

	private Player createBoss(double x, double y, String imagePath) {
		return new Player(200, -100, 250.0, 250.0, 10, "enemy", imagePath, 5);

	}

	public List<Player> getAllActors() {
//		return root.getChildren().stream().map(a -> (Player) a).toList();
		return root.getChildren().stream().filter(Player.class::isInstance).map(Player.class::cast).toList();
	}

	public void update() {
		if (!gameRunning) {
			return;
		}
		if (level.equalsIgnoreCase("Level1") || level.equalsIgnoreCase("Leve2") ) {
			timeInterval += 0.016;
		} else {
			timeInterval += 0.026;
		}

		// Create a list to collect actors to be removed
		List<Player> actorsToRemove = new ArrayList<>();

		getAllActors().forEach(a -> {
			switch (a.type) {

			case "enemybullet":

				a.moveDown();

				// checkPlayerBulletCollision();
				if (a.getBoundsInParent().intersects(player.getBoundsInParent())) {
					player.dead = true;
					a.dead = true;
				}
				break;

			case "playerbullet":
				a.moveUpNoLimit();
				getAllActors().stream().filter(e -> e.type.equals("enemy") || e.type.equals("boss")).forEach(enemy -> {
					if (a.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
						//System.out.println("Player bullet hit enemy!");
						enemy.decreaseHp(1);
						decreaseHpBar();
						a.dead = true;

						if (enemy.dead) {
							showExplosion(enemy.getTranslateX(), enemy.getTranslateY(), enemy.getWidth(),
									enemy.getHeight());
							if (level.equalsIgnoreCase("Level1")|| level.equalsIgnoreCase("Level2")) {
								score += 10;
								calculateScore(score);
							}else {
								score += 100;
								calculateScore(score);
							}
							
						}
						if ((progress <= 1) & (progress > 0.6)) {
							hpBar.setStyle("-fx-accent :  #00FF00");
						} else if ((progress <= 0.6) & (progress > 0.3)) {
							hpBar.setStyle("-fx-accent :  #FFFF00");
						} else if ((progress <= 0.3) & (progress > 0.0)) {
							hpBar.setStyle("-fx-accent :  #FF0000");
						} else {
							hpBar.setVisible(false);
							root.getChildren().remove(hpBar);
						}

					}

				});

				break;
			case "bossbullet":
				a.moveDown();
				if (a.getBoundsInParent().intersects(player.getBoundsInParent())) {
					player.dead = true;
				}
				break;

			case "enemy":

				if (timeInterval > 2) {
					if (Math.random() < 0.3) {
						//System.out.println(level);
						if (level.equalsIgnoreCase("Level1") || level.equalsIgnoreCase("Level2")) {
							enemyshot(a);
							// a.moveDown();
						} else {
							fireBullet(a);
						}
						a.moveDown();

					}
				}

			}

			// Collect actors to be removed
			if (a.dead) {
				actorsToRemove.add(a);
			}

		});

		// Remove dead actors from the root pane
		root.getChildren().removeAll(actorsToRemove);

		if (player.dead) {
			stopGame();
			// Write Score/Level
			String scoreLabel = String.valueOf(score);
			String levelLabel = level.substring(5);
			FileTest.writeRecord(levelLabel, scoreLabel);
			showUI("GameEndUI.fxml", "GAME OVER");

		}

		if (getAllActors().stream().filter(a -> a.type.equals("enemy")).allMatch(e -> e.dead)) {
			stopGame();
			String scoreLabel = String.valueOf(score);
			String levelLabel = level.substring(5);
			FileTest.writeRecord(levelLabel, scoreLabel);
			showUI("LevelComplete.fxml", "LEVEL FINISH");

		}

		if (timeInterval > 2) {
			timeInterval = 0;
		}

		end = LocalDateTime.now();
		timeDuration(start, end);

	}

	private void showUI(String UIPath, String Title) {
		Stage stage = (Stage) (root.getScene().getWindow());
		stage.hide();
		backgroundMusicPlayer.stop();
		Pane gameEnd = null;
		try {
			gameEnd = (Pane) FXMLLoader.load(getClass().getResource(UIPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene scene = new Scene(gameEnd);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle(Title);
		stage.show();

	}

	private void decreaseHpBar() {
		// TODO Auto-generated method stub
		progress -= 0.1;
		hpBar.setProgress(progress);

	}

	private void calculateScore(int score2) {
		// TODO Auto-generated method stub
		scoreRecord.setText("Score: " + score);

	}

	private void timeDuration(LocalDateTime start2, LocalDateTime end2) {
		// TODO Auto-generated method stub

		long countSec = 0;
		Duration d = Duration.between(start, end);
		countSec = d.getSeconds();
		long countMinute = countSec / 60;
		countSec = countSec % 60;
		long countHr = countMinute / 60;
		countMinute = countMinute % 60;
		formattedTime = String.format("%02d:%02d:%02d", countHr, countMinute, countSec);
		timerRecord.setText("Time: " + formattedTime);

	}

	private void stopGame() {
		gameRunning = false;
	}

	public void shot(Player who) {
		String bulletImage;

		if (who.type.equals("player")) {
			bulletImage = "/images/playerBullet.png";
		} else {
			return;
		}
		Player bullet = new Player(who.getTranslateX() + 50, who.getTranslateY(), 20, 15, 0, who.type + "bullet",
				bulletImage, 5);
		root.getChildren().add(bullet);

		bullet.setOnMouseClicked(event -> {
			// Handle collision with enemy
			getAllActors().stream().filter(e -> e.type.equals("enemy"))
					.filter(enemy -> bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())).findFirst()
					.ifPresent(enemy -> {
						root.getChildren().removeAll(bullet, enemy);
						showExplosion(enemy.getTranslateX(), enemy.getTranslateY(), enemy.getWidth(),
								enemy.getHeight());
					});
		});
	}

	public void enemyshot(Player who) {
		String bulletImage;

		if (who.type.equals("enemy")) {
			bulletImage = "/images/lip.png";
		} else {
			return;
		}
		Player bullet = new Player(who.getTranslateX() + 20, who.getTranslateY(), 18, 20, 0, who.type + "bullet",
				bulletImage, 3);
		root.getChildren().add(bullet);

		bullet.setOnMouseClicked(event -> {
			// Handle collision with player
			getAllActors().stream().filter(e -> e.type.equals("player"))
					.filter(enemy -> bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())).findFirst()
					.ifPresent(enemy -> {
						root.getChildren().removeAll(bullet, enemy);
						showExplosion(enemy.getTranslateX(), enemy.getTranslateY(), enemy.getWidth(),
								enemy.getHeight());
					});
		});
	}

	public void fireBullet(Player boss) {
		String bulletImage = "/images/lips.png";
		int numBullets = 3;
		double bossBottomCenterX = boss.getTranslateX() + boss.getWidth() / 2;
		double bossBottomCenterY = boss.getTranslateY() + boss.getHeight();
		double bulletSpeed = 0.8;

		for (int i = 0; i < numBullets; i++) {
			double randomAngle = Math.toRadians(Math.random() * 360);
			double bulletX = bossBottomCenterX;
			double bulletY = bossBottomCenterY + 10 * Math.sin(randomAngle); // Ensure it stays below the boss's bottom

			double directionFactor = Math.random() < 0.5 ? -1 : 1;
			double finalBulletX = bulletX + directionFactor * 60 * Math.cos(randomAngle);

			Player bullet = new Player(finalBulletX, bulletY, 45, 45, 0, "bossbullet", bulletImage, 3);
			root.getChildren().add(bullet);

			moveBullet(bullet, randomAngle, bulletSpeed);

		}
	}

	private void moveBullet(Player bullet, double angle, double speed) {
		Timeline bulletTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(0.016), event -> {
			bullet.setTranslateX(bullet.getTranslateX() + speed * Math.cos(angle));
			bullet.setTranslateY(bullet.getTranslateY() + speed * Math.sin(angle));

			if (bullet.getTranslateY() > root.getHeight()) {
				// Remove the bullet when it goes beyond the screen
				root.getChildren().remove(bullet);
			}
		}));
		bulletTimeline.setCycleCount(Animation.INDEFINITE); // Make the bullet continuously move
		bulletTimeline.play();
	}

	private void showExplosion(double x, double y, double enemyWidth, double enemyHeight) {
		//System.out.println("Explosion at: " + x + ", " + y);

		explosion.setTranslateX(x);
		explosion.setTranslateY(y);
		explosion.setFitWidth(enemyWidth); // Set explosion size same as enemy
		explosion.setFitHeight(enemyHeight);
		explosion.setVisible(true);
		explosionSound.play();

		// Create a TimeLine for the explosion animation
		Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.millis(500), e -> {
			explosion.setVisible(false);
			//System.out.println("Explosion hidden");
		}));
		timeline.play();
	}

	private long lastShotTime = 0;
	private static final long SHOT_COOLDOWN = 300;

	Set<KeyCode> pressedKeys = new HashSet<>();

	// To move simultaneously and also shoot bullets at the same time
	private void updatePlayerMovement() {

		long currentTime = System.currentTimeMillis();
		if (pressedKeys.contains(KeyCode.W)) {
			player.moveUp();
		}

		if (pressedKeys.contains(KeyCode.A)) {
			player.moveLeft();
		}

		if (pressedKeys.contains(KeyCode.D)) {
			player.moveRight(root.getPrefWidth());
		}

		if (pressedKeys.contains(KeyCode.S)) {
			player.moveDown(root.getPrefHeight());
		}

		if (pressedKeys.contains(KeyCode.SPACE)) {
			if (currentTime - lastShotTime >= SHOT_COOLDOWN) {
				shot(player);
				laserSound.play();
				lastShotTime = currentTime;
			}
		}

		if (pressedKeys.isEmpty()) {
			player.moveStop();
		}
	}

	public void movePlayer(Pane root) {

		root.getScene().setOnKeyPressed(e -> {
			pressedKeys.add(e.getCode());
			updatePlayerMovement();
		});

		root.getScene().setOnKeyReleased(e -> {
			pressedKeys.remove(e.getCode());
			updatePlayerMovement();
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		level = lblLevel.getText();
		//System.out.println(level);

		player = new Player((root.getPrefWidth() / 2.0) - 20.0, root.getPrefHeight() - 80, 75.0, 85.0, 5, "player",
				"/images/mustache.png", 10);

		root.getChildren().add(player);

		Platform.runLater(() -> movePlayer(root));

		AnimationTimer timer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				// TODO Auto-generated method stub
				update();
			}

		};

		// explosion
		explosion = new ImageView(new Image(getClass().getResourceAsStream("/images/explotion-explode.gif")));
		explosion.setFitWidth(45);
		explosion.setFitHeight(65);
		explosion.setVisible(false);
		root.getChildren().add(explosion);

		// background sound
		String backgroundMusicFile = "/audio/bgMusic.mp3";
		Media backgroundMusic = new Media(getClass().getResource(backgroundMusicFile).toExternalForm());
		backgroundMusicPlayer = new MediaPlayer(backgroundMusic);
		backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

		// explosion sound
		String explosionSoundFile = "/audio/yamete_kudasai.mp3";
		explosionSound = new AudioClip(getClass().getResource(explosionSoundFile).toExternalForm());

		// laser shot
		String laserShotFile = "/audio/pewpew.mp3";
		laserSound = new AudioClip(getClass().getResource(laserShotFile).toExternalForm());

		timer.start();

		playingLevel();

		backgroundMusicPlayer.play();

		start = LocalDateTime.now();

	}

}
