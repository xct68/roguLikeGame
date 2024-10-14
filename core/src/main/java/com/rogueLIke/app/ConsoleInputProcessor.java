package com.rogueLIke.app; // Ensure this matches your project structure

import com.badlogic.gdx.InputProcessor;

public class ConsoleInputProcessor implements InputProcessor {
    private StringBuilder input = new StringBuilder();

    @Override
    public boolean keyDown(int keycode) {
        return false; // Handle key down events here if needed
    }

    @Override
    public boolean keyUp(int keycode) {
        return false; // Handle key up events here if needed
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == '\n') {
            // Process command
            processCommand(input.toString());
            input.setLength(0); // Clear input after processing
        } else if (character == '\b') {
            // Handle backspace
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
            }
        } else {
            input.append(character); // Append character to input
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false; // Handle touch down events if needed
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false; // Handle touch up events if needed
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false; // Handle touch dragged events if needed
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false; // Handle mouse movement if needed
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Handle scroll events here if needed
        return false; // Returning false to indicate not handled
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false; // Handle touch cancel events if needed
    }

    private void processCommand(String command) {
        // Implement your command processing logic here
        System.out.println("Command entered: " + command);
    }

    public String getInput() {
        return input.toString();
    }
}
