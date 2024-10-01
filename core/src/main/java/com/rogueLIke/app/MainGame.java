package com.rogueLIke.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame implements ApplicationListener {
    Texture character;
    Texture map;
    SpriteBatch spriteBatch;
    FitViewport viewport;

    // Character position variables
    float characterX = 100;
    float characterY = 100;

    @Override
    public void create() {
        // Prepare your application here.
        character = new Texture(Gdx.files.internal("character.png"));
        map = new Texture(Gdx.files.internal("map.png"));
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8000, 6000); // Adjusting the window size for a larger view
    }

    @Override
    public void resize(int width, int height) {
        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void render() {
        // This render method will be automatically called by LibGDX
        render(Gdx.graphics.getDeltaTime()); // Call the custom render method with delta time
    }

    public void render(float delta) {
        // 1. Clear the screen with a different color (e.g., black)
        ScreenUtils.clear(Color.BLACK);

        // 2. Apply the viewport and set the projection matrix
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        // 3. Define movement speed (e.g., 100 pixels per second)
        float speed = 10000 * delta;  // Adjust speed by delta time for smooth movement

        // 4. Define the movement boundaries
        float minX = 0;                           // Left boundary
        float maxX = viewport.getWorldWidth();     // Right boundary (based on viewport size)
        float minY = 0;                           // Bottom boundary
        float maxY = viewport.getWorldHeight();    // Top boundary (based on viewport size)

        // 5. Check for user input and move the character
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W) && characterY + speed < maxY) {
            characterY += speed;  // Move up
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S) && characterY - speed > minY) {
            characterY -= speed;  // Move down
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A) && characterX - speed > minX) {
            characterX -= speed;  // Move left
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D) && characterX + speed < maxX) {
            characterX += speed;  // Move right
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && characterY + speed < maxY) {
            characterY += speed;  // Move up
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && characterY - speed > minY) {
            characterY -= speed;  // Move down
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && characterX - speed > minX) {
            characterX -= speed;  // Move left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && characterX + speed < maxX) {
            characterX += speed;  // Move right
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SPACE)) {}

        // 6. Begin the spriteBatch to draw textures
        spriteBatch.begin();

        // 7. Draw the map (background) first, so the character is drawn on top
        // spriteBatch.draw(map, 0, 0);

        // 8. Define the scale factor (e.g., 0.5 for half size)
        float scaleFactor = 0.25f;

        // 9. Draw the character texture at the updated position, scaling its size
        spriteBatch.draw(
            character,
            characterX,
            characterY,
            character.getWidth() * scaleFactor,
            character.getHeight() * scaleFactor
        );

        // 10. End the spriteBatch
        spriteBatch.end();
    }



    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Clean up your application's resources when closing.
        character.dispose();
        map.dispose();
        spriteBatch.dispose();
    }
}

