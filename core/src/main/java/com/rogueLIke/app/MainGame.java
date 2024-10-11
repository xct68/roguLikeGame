package com.rogueLIke.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainGame implements ApplicationListener {//class
    Texture characterSheet; // The sprite sheet
    Texture map;//class attributes
    SpriteBatch spriteBatch;
    FitViewport viewport;

    // Animation-related variables
    private static final int FRAME_COLS = 8, FRAME_ROWS = 1; // Number of columns and rows in the sprite sheet
    Animation<TextureRegion> characterAnimation; // Animation object
    float stateTime; // Keeps track of time for animation

    // Character position variables
    float characterX;
    float characterY;

    // Gives character a hitbox
    Rectangle hitbox;

    boolean facingLeft;

    @Override
    public void create() {
        // Load the sprite sheet with walking animation frames
        characterSheet = new Texture(Gdx.files.internal("character_walk.png"));

        // Split the sprite sheet into individual frames
        TextureRegion[][] tmp = TextureRegion.split(characterSheet,
            characterSheet.getWidth() / FRAME_COLS,
            characterSheet.getHeight() / FRAME_ROWS);//assessor gets the Height from the characterSheet texture, not prior defined because fkn libGdx make life ez

        // Convert 2D array to 1D array of frames
        TextureRegion[] characterFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                characterFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the animation with frame duration (e.g., 0.1f per frame)
        characterAnimation = new Animation<>(0.1f, characterFrames);

        map = new Texture(Gdx.files.internal("map.png"));
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(2000, 1000); // Adjusting the window size for a larger view

        characterX = viewport.getWorldWidth() / 2;
        characterY = viewport.getWorldHeight() / 2;

        float scaleFactor = 5f;
        hitbox = new Rectangle(characterX, characterY,
            characterSheet.getWidth() / FRAME_COLS * scaleFactor,
            characterSheet.getHeight() * scaleFactor);

        stateTime = 0f; // Start animation time tracking
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }

    public void render(float delta) {
        // 1. Clear the screen with black color
        ScreenUtils.clear(Color.BLACK);

        // 2. Apply the viewport and set the projection matrix
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);//mutators

        // 3. Update the animation state time
        stateTime += delta;

        // 4. Calculate movement speed
        float speed = 300 * delta;

        // 5. Define movement boundaries
        float minX = 0;
        float maxX = viewport.getWorldWidth();
        float minY = 0;
        float maxY = viewport.getWorldHeight();

        // 6. Check for user input and move the character
        boolean isMoving = false; // Track if the player is moving

        if (Gdx.input.isKeyPressed(Input.Keys.W) && hitbox.y + hitbox.height + speed < maxY) {
            characterY += speed;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) && hitbox.y - speed > minY) {
            characterY -= speed;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && hitbox.x - speed > minX) {
            characterX -= speed;
            isMoving = true;
            facingLeft = true; // Character is moving left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) && hitbox.x + hitbox.width + speed < maxX) {
            characterX += speed;
            isMoving = true;
            facingLeft = false; // Character is moving right
        }

        // Update hitbox position
        hitbox.setPosition(characterX, characterY);//mutator for hitbox pos again fkn magic libGdx

        // 7. Start drawing
        spriteBatch.begin();

        // Draw the map (background)
        spriteBatch.draw(map, 0, 0);

        // 8. Get the current frame of animation
        TextureRegion currentFrame;
        if (isMoving) {
            currentFrame = characterAnimation.getKeyFrame(stateTime, true); // Loop animation
        } else {
            currentFrame = characterAnimation.getKeyFrame(0); // Use the first frame for idle state
        }

        // 9. Check if the frame needs to be flipped based on direction
        if (facingLeft && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false); // Flip horizontally if moving left
        } else if (!facingLeft && currentFrame.isFlipX()) {
            currentFrame.flip(true, false); // Unflip horizontally if moving right
        }


        // 9. Draw the current frame at the updated position
        float scaleFactor = 5f;
        spriteBatch.draw(currentFrame, characterX, characterY,
            currentFrame.getRegionWidth() * scaleFactor,
            currentFrame.getRegionHeight() * scaleFactor);

        // 10. End drawing
        spriteBatch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        characterSheet.dispose();
        map.dispose();
        spriteBatch.dispose();
    }
}
