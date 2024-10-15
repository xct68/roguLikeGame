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
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }

    private void processCommand(String command) {
        // Implement your command processing logic here
        System.out.println("Command entered: " + command);
    }
}
