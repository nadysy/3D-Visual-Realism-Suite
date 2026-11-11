
package com.grafkom.viewing3d;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    private long window;
    private Renderer renderer;

    private boolean bPressed, fPressed, zPressed;
    private boolean cPressed, tPressed, nPressed;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("GLFW gagal diinisialisasi.");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        window = glfwCreateWindow(
                1100,
                720,
                "3D Visual Realism Suite | Grafika Komputer",
                0,
                0
        );

        if (window == 0) {
            throw new RuntimeException("Window tidak dapat dibuat.");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        GL.createCapabilities();

        glfwSetFramebufferSizeCallback(
                window,
                (w, width, height) -> glViewport(0, 0, width, height)
        );

        renderer = new Renderer();

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {

            if (action == GLFW_PRESS) {

                if (key == GLFW_KEY_ESCAPE) {
                    glfwSetWindowShouldClose(w, true);
                }

                if (key == GLFW_KEY_B && !bPressed) {
                    renderer.toggleBlinn();
                }

                if (key == GLFW_KEY_F && !fPressed) {
                    renderer.toggleFlat();
                }

                if (key == GLFW_KEY_Z && !zPressed) {
                    renderer.toggleDepth();
                }

                if (key == GLFW_KEY_C && !cPressed) {
                    renderer.toggleCull();
                }

                if (key == GLFW_KEY_T && !tPressed) {
                    renderer.toggleTexture();
                }

                if (key == GLFW_KEY_N && !nPressed) {
                    renderer.toggleFiltering();
                }

                bPressed = key == GLFW_KEY_B;
                fPressed = key == GLFW_KEY_F;
                zPressed = key == GLFW_KEY_Z;
                cPressed = key == GLFW_KEY_C;
                tPressed = key == GLFW_KEY_T;
                nPressed = key == GLFW_KEY_N;
            }

            if (action == GLFW_RELEASE) {

                if (key == GLFW_KEY_B) bPressed = false;
                if (key == GLFW_KEY_F) fPressed = false;
                if (key == GLFW_KEY_Z) zPressed = false;
                if (key == GLFW_KEY_C) cPressed = false;
                if (key == GLFW_KEY_T) tPressed = false;
                if (key == GLFW_KEY_N) nPressed = false;
            }
        });

        while (!glfwWindowShouldClose(window)) {

            int width = 1100;
            int height = 720;

            try (MemoryStack stack = MemoryStack.stackPush()) {

                IntBuffer widthBuffer = stack.mallocInt(1);
                IntBuffer heightBuffer = stack.mallocInt(1);

                glfwGetFramebufferSize(
                        window,
                        widthBuffer,
                        heightBuffer
                );

                width = widthBuffer.get(0);
                height = heightBuffer.get(0);
            }

            if (width > 0 && height > 0) {
                renderer.render(width, height);
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        renderer.cleanup();

        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
