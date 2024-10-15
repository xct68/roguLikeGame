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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class MainGame implements ApplicationListener {
    Texture characterSheet;
    SpriteBatch spriteBatch;
    BitmapFont font;
    BitmapFont errorFont;

    private OrthographicCamera camera;
    public static TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private ScreenViewport viewport2;

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
    boolean consoleVisible = false;
    StringBuilder consoleInput = new StringBuilder();
    String errorMessage = "";

    // bgColor color variable
    Color bgColorColor = Color.BLACK;

    // Character movement speed
    float movementSpeed = 300f;

    // Character size scale factor
    float scaleFactor = 1f;

    // Walls for Walls
    private TiledMapTileLayer Walls;

    @Override
    public void create() {
        map = new TmxMapLoader().load("map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1.0f);

        camera = new OrthographicCamera(16, 10);
        mapRenderer.setView(camera);
        camera.update();

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
        viewport2 = new ScreenViewport(camera);

        characterX = 120;
        characterY = 120;

        hitbox = new Rectangle(characterX, characterY,
            characterSheet.getWidth() / FRAME_COLS * scaleFactor,
            characterSheet.getHeight() * scaleFactor);

        font = new BitmapFont();
        errorFont = new BitmapFont();
        errorFont.setColor(Color.RED);

        stateTime = 0f;

        // Fetch collision layer and add a null check
        Walls = (TiledMapTileLayer) map.getLayers().get("Walls");
        if (Walls == null) {
            Gdx.app.error("Collision Layer", "The collision layer is missing or misnamed in the Tiled map.");
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport2.update(width, height, true);
    }

    @Override
    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }

    public void render(float delta) {
        // Clear the screen with the current bgColor color
        ScreenUtils.clear(bgColorColor);

        // Handle input if console is not visible
        if (!consoleVisible) {
            handleCharacterMovement(delta); // Call to handle movement
        }

        // Update the camera
        camera.update();
        camera.position.set(characterX, characterY, 0);
        mapRenderer.setView(camera);

        // Update the viewport
        viewport2.apply();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Update the animation state time
        stateTime += delta;

        // Begin drawing
        spriteBatch.begin();

        // Render the map layers
        mapRenderer.render();

        // Draw the character animation
        drawCharacter();

        // Draw the console if it's visible
        if (consoleVisible) {
            drawConsole();
        }

        spriteBatch.end();
    }

    private void handleCharacterMovement(float delta) {
        float speed = movementSpeed * delta; // Calculate speed based on delta time
        float newCharacterX = characterX;
        float newCharacterY = characterY;
        boolean isMoving = false;

        // Check for input and update position accordingly
        if (Gdx.input.isKeyPressed(Input.Keys.W)) { // Move up
            newCharacterY += speed;
            if (!isCollidingWithWall(newCharacterX, newCharacterY)) {
                characterY = newCharacterY;
                isMoving = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { // Move down
            newCharacterY -= speed;
            if (!isCollidingWithWall(newCharacterX, newCharacterY)) {
                characterY = newCharacterY;
                isMoving = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { // Move left
            newCharacterX -= speed;
            if (!isCollidingWithWall(newCharacterX, newCharacterY)) {
                characterX = newCharacterX;
                isMoving = true;
                facingLeft = true; // Update direction facing
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { // Move right
            newCharacterX += speed;
            if (!isCollidingWithWall(newCharacterX, newCharacterY)) {
                characterX = newCharacterX;
                isMoving = true;
                facingLeft = false; // Update direction facing
            }
        }

        // Update the hitbox position
        hitbox.setPosition(characterX, characterY);

        // Open the console if the backtick key is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) {
            consoleVisible = true;
            Gdx.input.setInputProcessor(new ConsoleInputProcessor());
        }
    }

    private boolean isCollidingWithWall(float x, float y) {
        // Convert the character's coordinates to tile coordinates
        float tileWidth = Walls.getTileWidth();
        float tileHeight = Walls.getTileHeight();

        int tileX = Math.round(x/tileWidth);
        int tileY = Math.round(y/tileHeight);

        // Check if the tile has the "isWall" property
        TiledMapTileLayer.Cell cell = Walls.getCell(tileX, tileY);
        if (cell != null && cell.getTile() != null) {
            Object isWall = cell.getTile().getProperties().get("isWall");
            if (isWall != null && isWall.equals(true)) {
                return true; // There is a wall, so prevent movement
            }
        }
        return false; // No wall, movement allowed
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

        spriteBatch.draw(currentFrame, characterX, characterY,
            currentFrame.getRegionWidth() * scaleFactor,
            currentFrame.getRegionHeight() * scaleFactor);
    }

    private void drawConsole() {
        // Draw a simple console box and the current input
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "Console: " + consoleInput.toString(), 20, viewport2.getWorldHeight() - 20);

        // Display error message if any
        if (!errorMessage.isEmpty()) {
            errorFont.draw(spriteBatch, errorMessage, 20, viewport2.getWorldHeight() - 60);
        }
    }

    private boolean isMoving() {
        return Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D);
    }

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
            bgColorColor = Color.WHITE; // Change bgColor to white
        } else if (command.equals("bg black")) {
            bgColorColor = Color.BLACK; // Change bgColor to black
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
        } else if (command.startsWith("size ")) {
            try {
                float newSize = Float.parseFloat(command.split(" ")[1]);
                scaleFactor = newSize; // Update the scale factor
                hitbox.setSize((float) characterSheet.getWidth() / FRAME_COLS * scaleFactor,
                    characterSheet.getHeight() * scaleFactor); // Update hitbox size
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                errorMessage = "Invalid size value!"; // Show error if the input is not a valid number
            }
        } else if (command.equals("size")) {
            // Print current size
            errorMessage = "Current size: " + scaleFactor; // Display the current size
        } else {
            errorMessage = "Command not recognized"; // Show error for unrecognized commands
        }
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
}
