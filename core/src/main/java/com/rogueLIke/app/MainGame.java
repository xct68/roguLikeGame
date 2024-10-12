package com.rogueLIke.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainGame implements ApplicationListener {
    Texture characterSheet;
    SpriteBatch spriteBatch;
    FitViewport viewport;
    BitmapFont font; // Font for drawing console text
    BitmapFont errorFont; // Font for error messages

    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    // Animation-related variables
    private static final int FRAME_COLS = 8, FRAME_ROWS = 1;
    Animation<TextureRegion> characterAnimation;
    float stateTime;

    // Character position variables
    float characterX;
    float characterY;

    Rectangle hitbox;

    boolean facingLeft;

    // Console-related variables
    boolean consoleVisible = false; // Is the console visible?
    StringBuilder consoleInput = new StringBuilder(); // User input for console
    String errorMessage = ""; // Store any error messages for unrecognized commands

    // Background color variable
    Color backgroundColor = Color.BLACK; // Default background color

    // Character movement speed
    float movementSpeed = 300f; // Default speed

    @Override
    public void create() {
        characterSheet = new Texture(Gdx.files.internal("character_walk.png"));

        // Split the sprite sheet into individual frames
        TextureRegion[][] tmp = TextureRegion.split(characterSheet,
            characterSheet.getWidth() / FRAME_COLS,
            characterSheet.getHeight() / FRAME_ROWS);

        TextureRegion[] characterFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                characterFrames[index++] = tmp[i][j];
            }
        }

        characterAnimation = new Animation<>(0.1f, characterFrames);

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(2000, 1000);

        characterX = viewport.getWorldWidth() / 2;
        characterY = viewport.getWorldHeight() / 2;

        float scaleFactor = 5f;
        hitbox = new Rectangle(characterX, characterY,
            characterSheet.getWidth() / FRAME_COLS * scaleFactor,
            characterSheet.getHeight() * scaleFactor);

        font = new BitmapFont(); // Default font for console text
        errorFont = new BitmapFont(); // Font for error messages
        errorFont.setColor(Color.RED); // Error messages are displayed in red

        stateTime = 0f;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }

    public void render(float delta) {
        // Clear the screen with the current background color
        ScreenUtils.clear(backgroundColor);

        // Update the viewport
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        // Update the animation state time
        stateTime += delta;

        // Handle input if console is not visible
        if (!consoleVisible) {
            handleCharacterMovement(delta);
        }

        // Begin drawing
        spriteBatch.begin();

        // Draw the character animation
        drawCharacter();

        // Draw the console if it's visible
        if (consoleVisible) {
            drawConsole();
        }

        spriteBatch.end();
    }

    private void handleCharacterMovement(float delta) {
        // Character movement code...
        float speed = movementSpeed * delta;

        float minX = 0;
        float maxX = viewport.getWorldWidth();
        float minY = 0;
        float maxY = viewport.getWorldHeight();

        boolean isMoving = false;

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
            facingLeft = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) && hitbox.x + hitbox.width + speed < maxX) {
            characterX += speed;
            isMoving = true;
            facingLeft = false;
        }

        hitbox.setPosition(characterX, characterY);

        // Open the console if the backtick key is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) { // ` key is called GRAVE
            consoleVisible = true;
            Gdx.input.setInputProcessor(new ConsoleInputProcessor());
        }
    }

    private void drawCharacter() {
        TextureRegion currentFrame = isMoving() ?
            characterAnimation.getKeyFrame(stateTime, true) :
            characterAnimation.getKeyFrame(0);

        if (facingLeft && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!facingLeft && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        float scaleFactor = 5f;
        spriteBatch.draw(currentFrame, characterX, characterY,
            currentFrame.getRegionWidth() * scaleFactor,
            currentFrame.getRegionHeight() * scaleFactor);
    }

    private void drawConsole() {
        // Draw a simple console box and the current input
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "Console: " + consoleInput.toString(), 20, viewport.getWorldHeight() - 20);

        // Display error message if any
        if (!errorMessage.isEmpty()) {
            errorFont.draw(spriteBatch, errorMessage, 20, viewport.getWorldHeight() - 60);
        }
    }

    private boolean isMoving() {
        return Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        characterSheet.dispose();
        map.dispose();
        spriteBatch.dispose();
        font.dispose();
        errorFont.dispose();
    }

    // Input processor to handle console input
    class ConsoleInputProcessor extends com.badlogic.gdx.InputAdapter {
        @Override
        public boolean keyTyped(char character) {
            if (character == '\b') { // Handle backspace
                if (consoleInput.length() > 0) {
                    consoleInput.setLength(consoleInput.length() - 1);
                }
            } else if (character == '\r' || character == '\n') { // Handle enter
                handleConsoleCommand(consoleInput.toString());
                consoleInput.setLength(0); // Clear input after command
            } else {
                consoleInput.append(character); // Add typed character to console input
            }
            return true;
        }

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.GRAVE) { // Close console with ESC or `
                consoleVisible = false;
                Gdx.input.setInputProcessor(null); // Remove input processor to return to normal input handling
            }
            return true;
        }
    }

    private void handleConsoleCommand(String command) {
        errorMessage = ""; // Clear previous error message

        if (command.equals("quit")) {
            Gdx.app.exit(); // Exit the game
        } else if (command.equals("bg white")) {
            backgroundColor = Color.WHITE; // Change background to white
        } else if (command.equals("bg black")) {
            backgroundColor = Color.BLACK; // Change background to black
        } else if (command.startsWith("speed ")) {
            try {
                float newSpeed = Float.parseFloat(command.split(" ")[1]);
                movementSpeed = newSpeed; // Set new movement speed
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                errorMessage = "Invalid speed value!"; // Show error if the input is not a valid number
            }
        } else if (command.equals("speed")) {
            // Print current speed
            errorMessage = "Current speed: " + movementSpeed; // Display the current speed
        } else {
            errorMessage = "Command not recognized"; // Show error for unrecognized commands
        }
    }
}
