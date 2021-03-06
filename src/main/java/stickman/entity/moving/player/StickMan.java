package stickman.entity.moving.player;

import stickman.entity.Entity;
import stickman.entity.moving.MovingObject;
import stickman.level.Level;
import stickman.level.LevelManager;

import java.util.List;

/**
 * The player controlled character.
 */
public class StickMan extends MovingObject implements Controllable {

    /**
     * The maximum speed the player can move horizontally.
     */
    public static final double HORIZONTAL_SPEED = 1;

    /**
     * The height of the player when normal sized.
     */
    public static final double NORMAL_HEIGHT = 40;

    /**
     * The width of the player when normal sized.
     */
    public static final double NORMAL_WIDTH = 24;

    /**
     * The height of the player when large.
     */
    public static final double LARGE_HEIGHT = 60;

    /**
     * The width of the player when large.
     */
    public static final double LARGE_WIDTH = 36;

    /**
     * The possible sizes the player can be.
     */
    private enum Size {
        NORMAL,
        LARGE
    }

    /**
     * The current size of the player.
     */
    private Size size;

    /**
     * The level the player exists within.
     */
    private Level level;

    /**
     * Whether the player is pressing left.
     */
    private boolean left;

    /**
     * Whether the player is pressing right.
     */
    private boolean right;

    /**
     * Whether the hero has upgraded.
     */
    private boolean upgraded;

    /**
     * Whether the sprite is facing left.
     */
    private boolean leftFacing;

    /**
     * The initial x position of hero
     */
    private double initXPos;

    /**
     * The initial y position of hero
     */
    private double initYPos;

    /**
     * Creates a new StickMan object.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param size The size of the player.
     * @param level The level the player exists within.
     */
    public StickMan(double x, double y, String size, Level level) {
        super("ch_stand1.png", x, y, 0, 0, Layer.FOREGROUND);

        this.level = level;
        this.upgraded = false;
        this.leftFacing = false;

        if (size.equals("normal")) {
            this.size = Size.NORMAL;
            this.width = NORMAL_WIDTH;
            this.height = NORMAL_HEIGHT;
        } else if (size.equals("large")) {
            this.size = Size.LARGE;
            this.width = LARGE_WIDTH;
            this.height = LARGE_HEIGHT;
        }

        this.yPos -= this.height;
        this.initXPos = this.xPos;
        this.initYPos = this.yPos;
    }

    /**
     * Create a new StickMan object
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param size The size of the player
     * @param level The current level
     * @param upgraded Whether the hero is upgraded or not
     */
    public StickMan(double x, double y, String size, Level level, boolean upgraded) {
        super("ch_stand1.png", x, y, 0, 0, Layer.FOREGROUND);

        this.level = level;
        this.upgraded = false;
        this.leftFacing = false;

        if (size.equals("normal")) {
            this.size = Size.NORMAL;
            this.width = NORMAL_WIDTH;
            this.height = NORMAL_HEIGHT;
        } else if (size.equals("large")) {
            this.size = Size.LARGE;
            this.width = LARGE_WIDTH;
            this.height = LARGE_HEIGHT;
        }

        this.yPos -= this.height;
        this.initXPos = this.xPos;
        this.initYPos = this.yPos;
        this.upgraded = upgraded;
    }

    @Override
    public Entity deepCopy() {
        return new StickMan(this.getXPos(), this.getYPos(), this.size.toString().toLowerCase(), this.level, this.upgraded);
    }

    @Override
    public void tick(List<Entity> entities, double heroX, double floorHeight) {

        this.gravity(entities, floorHeight);
        this.yPos += this.yVelocity;

        this.horizontalMove(entities);
        this.xPos += this.xVelocity;

    }

    /**
     * Updates x-velocity based on the proximity of other entities.
     * @param entities The other entities in the scene
     */
    private void horizontalMove(List<Entity> entities) {
        if (left) {
            this.xVelocity = Math.max(-HORIZONTAL_SPEED, -horizontalRaycast(true, entities, 0));
        } else if (right) {
            this.xVelocity = Math.min(HORIZONTAL_SPEED, horizontalRaycast(false, entities, level.getWidth()));
        } else {
            this.xVelocity = 0;
        }
    }

    @Override
    public boolean moveLeft() {
        this.left = true;
        this.right = false;
        faceLeft();
        return horizontalRaycast(true, this.level.getEntities(), 0) > 0;
    }

    @Override
    public boolean moveRight() {
        this.right = true;
        this.left = false;
        faceRight();
        return horizontalRaycast(false, this.level.getEntities(), 0) > 0;
    }

    @Override
    public boolean jump() {
        if (canJump()) {
            this.yVelocity = -4;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the player is able to jump.
     * @return Whether the player can jump
     */
    private boolean canJump() {
        return verticalRaycast(false, this.level.getEntities(), this.level.getFloorHeight()) < 0.05;
    }

    @Override
    public boolean stop() {
        this.xVelocity = 0;
        this.left = false;
        this.right = false;
        return true;
    }

    @Override
    public void upgrade() {
        this.upgraded = true;
        ((LevelManager) level).changeCurrentScore(50);
    }

    @Override
    public void die() {
        ((LevelManager) level).decreaseLives();

        this.xPos = this.initXPos;
        this.yPos = this.initYPos;
    }

    @Override
    public boolean isLeftFacing() {
        return this.leftFacing;
    }

    @Override
    public boolean upgraded() {
        return this.upgraded;
    }

    @Override
    public void nextLevel() {
        ((LevelManager) level).nextLevel();
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    /**
     * Turns the player left and updates the sprite.
     */
    private void faceLeft() {
        this.leftFacing = true;
        this.imagePath = "ch_stand4.png";
    }

    /**
     * Turns the player right and updates the sprite.
     */
    private void faceRight() {
        this.leftFacing = false;
        this.imagePath = "ch_stand1.png";
    }
}
