import com.sun.org.apache.bcel.internal.generic.LADD;
import guru.bug.game.*;
import guru.bug.game.ai.GravityAI;
import guru.bug.game.sprite.*;
import guru.bug.game.background.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;


public class MyGame extends Game {

    int lives = 3;

    @Override
    public void setup() {
        setBackground(new SpaceBackground());

        register('#', WallSprite::new)
                .onInit(w -> w.setColor(WallColor.BLUE));

        register( '*', PortalSprite::new);

        register('H', LadderSprite::new);

        register('w', FlameSprite::new);

        register('A', GhostSprite::new)
                .onInit(a -> {
                    a.setSpeed(4);
                    a.setDirection(Direction.E);
                })
                .onLoop(ai::followDirection)
                .onCollision(a -> {
                    if (a.getDirection() == Direction.E) {
                        a.setDirection(Direction.W);
                    } else {
                        a.setDirection(Direction.E);
                    }
                }, '#');

        register('T', ManSprite::new, new GravityAI().wall('#').ladder('H'))
                .onCollision(t -> {
                    lives = lives - 1;
                    if (lives >= 0) {
                        load("/level.txt", this::drawLives);
                    } else {
                        ai.halt(t);
                    }
                }, 'w', 'A')
                .onKeyHold(t -> move(t, Direction.E), KeyCode.RIGHT)
                .onKeyReleased(t -> t.setSpeed(0), KeyCode.RIGHT)
                .onKeyHold(t -> move(t, Direction.W), KeyCode.LEFT)
                .onKeyReleased(t -> t.setSpeed(0), KeyCode.LEFT)
                .onKeyHold(t -> move(t, Direction.N), KeyCode.UP)
                .onKeyReleased(t -> t.setSpeed(0), KeyCode.UP)
                .onKeyHold(t -> move(t, Direction.S), KeyCode.DOWN)
                .onKeyReleased(t -> t.setSpeed(0), KeyCode.DOWN)
                .onKeyPressed(t -> t.activate("jump"), KeyCode.SPACE);


        load("/level.txt", this::drawLives);
    }

    private void drawLives() {
        for (int i = 0; i < lives; i++) {
            sprite(HeartSprite::new, i + 0.5, 0.5);
        }
    }

    private void move(Sprite sprite, Direction dir) {
        sprite.setSpeed(3);
        sprite.setDirection(dir);
        sprite.setRotation(dir);
        ai.followDirection(sprite);
    }


    @Override
    public void loop() {

    }
}