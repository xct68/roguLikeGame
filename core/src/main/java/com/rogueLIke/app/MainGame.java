package com.rogueLIke.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
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
        viewport = new FitViewport(800, 600); // Adjusting the window size for a larger view
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
        // 1. Clear the screen with a different color (e.g., blue) to ensure it's working
        ScreenUtils.clear(Color.RED);
        int width = 1;
        int height = 1;

        // 2. Apply the viewport and set the projection matrix
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        // 3. Begin the spriteBatch to draw textures
        spriteBatch.begin();

        // 4. Draw the map (background) first, so the character is drawn on top
        spriteBatch.draw(map, 0, 0);

        // 5. Draw the character texture at the specified position
        spriteBatch.draw(character, characterX, characterY, character.getWidth(), character.getHeight());

        // 6. End the spriteBatch
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
