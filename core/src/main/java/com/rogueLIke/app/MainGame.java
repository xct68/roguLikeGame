package com.rogueLIke.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame implements ApplicationListener {
    Texture pepe;
    Texture map;
    SpriteBatch spriteBatch;
    FitViewport viewport;

    // Character position variables
    float pepeX;
    float pepeY;

    //Gives pepe a hitbox
    Rectangle hitbox;

    @Override
    public void create() {
        MainGame maingame = new MainGame();
        // Prepare your application here.
        pepe = new Texture(Gdx.files.internal("pepe.png"));
        map = new Texture(Gdx.files.internal("map.png"));
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(3840, 2160);// Adjusting the window size for a larger view
        pepeX = viewport.getWorldWidth() / 2;
        pepeY = viewport.getWorldHeight() / 2;

        float scaleFactor = 0.25f;
        hitbox = new Rectangle(pepeX, pepeY, pepe.getWidth() * scaleFactor, pepe.getHeight() * scaleFactor);
    }

    @Override
    public void resize(int width, int height) {
        Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setWindowedMode(1920, 1100);
        Gdx.graphics.setUndecorated(true);
        Gdx.graphics.setTitle("Rogue Like Game");
        System.out.println("Window size: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());


        // Resize your application here. The parameters represent the new window size.
        viewport.update((int) pepeX, (int) pepeY, true); // true centers the camera
    }

    @Override
    public void render() {
        // This render method will be automatically called by LibGDX
        render(Gdx.graphics.getDeltaTime());
        // Call the custom render method with delta time
    }

    public void render(float delta) {
        // 1. Clear the screen with a different color (e.g., black)
        ScreenUtils.clear(Color.BLACK);

        // 2. Apply the viewport and set the projection matrix
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        // 3. Define movement speed (e.g., 100 pixels per second)
        float speed = 1000 * delta;  // Adjust speed by delta time for smooth movement

        // 4. Define the movement boundaries
        float minX = 0;                              // Left boundary
        float maxX = viewport.getWorldWidth();     // Right boundary (based on viewport size)
        float minY = 0;                           // Bottom boundary
        float maxY = viewport.getWorldHeight();    // Top boundary (based on viewport size)

        // 5. Check for user input and move the pepe
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W) && hitbox.y + hitbox.height + speed < maxY) {
            pepeY += speed;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S) && hitbox.y - speed > minY) {
            pepeY -= speed;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A) && hitbox.x - speed > minX) {
            pepeX -= speed;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D) && hitbox.x + hitbox.width + speed < maxX) {
            pepeX += speed;
        }

        // Update the hitbox position to match the character's new position
        hitbox.setPosition(pepeX, pepeY);


        // 6. Begin the spriteBatch to draw textures
        spriteBatch.begin();

        // 7. Draw the map (background) first, so the pepe is drawn on top
        // spriteBatch.draw(map, 0, 0);

        // 8. Define the scale factor (e.g., 0.5 for half size)
        float scaleFactor = 0.25f;

        // 9. Draw the pepe texture at the updated position, scaling its size
        spriteBatch.draw(
            pepe,
            pepeX,
            pepeY,
            pepe.getWidth() * scaleFactor,
            pepe.getHeight() * scaleFactor
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
        pepe.dispose();
        map.dispose();
        spriteBatch.dispose();
    }
}

