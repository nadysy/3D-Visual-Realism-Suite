
package com.grafkom.viewing3d;

import org.joml.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    private final ShaderProgram scene, depth;

    private final Mesh smoothSphere, flatSphere, plane;

    private final Texture2D texture;

    private final ShadowMapFBO shadow;

    private boolean blinn = true;
    private boolean flat = false;
    private boolean depthOn = true;
    private boolean cullOn = false;
    private boolean textureOn = true;
    private boolean linear = true;

    private final Vector3f lightPos =
            new Vector3f(-2.5f, 4.0f, 2.0f);

    private final Matrix4f lightSpace = new Matrix4f();

    public Renderer() {

        scene = new ShaderProgram(
                "/shaders/scene.vert",
                "/shaders/scene.frag"
        );

        depth = new ShaderProgram(
                "/shaders/depth.vert",
                "/shaders/depth.frag"
        );

        smoothSphere = Mesh.sphere(48, 32, false);
        flatSphere = Mesh.sphere(48, 32, true);

        plane = createPlane();

        texture = new Texture2D(
                "src/main/resources/textures/checker.png"
        );

        shadow = new ShadowMapFBO();
    }

    private Mesh createPlane() {

        float[] v = {
            -5, -1, -5, 0, 1, 0, 0, 0,
             5, -1, -5, 0, 1, 0, 5, 0,
             5, -1,  5, 0, 1, 0, 5, 5,
            -5, -1,  5, 0, 1, 0, 0, 5
        };

        int[] i = {
            0, 2, 1,
            0, 3, 2
        };

        return new Mesh(v, i);
    }

    // Toggle fitur rendering

    public void toggleBlinn() {
        blinn = !blinn;
    }

    public void toggleFlat() {
        flat = !flat;
    }

    public void toggleDepth() {
        depthOn = !depthOn;
    }

    public void toggleCull() {
        cullOn = !cullOn;
    }

    public void toggleTexture() {
        textureOn = !textureOn;
    }

    public void toggleFiltering() {
        linear = !linear;
        texture.setFiltering(linear);
    }

    public void render(int w, int h) {

        if (w <= 0 || h <= 0) {
            return;
        }

        HSRManager.setupDepthTest(true);
        HSRManager.setupBackFaceCulling(false);

        // Light projection

        Matrix4f lightProj = new Matrix4f()
                .ortho(-8, 8, -8, 8, 1, 20);

        Matrix4f lightView = new Matrix4f()
                .lookAt(
                        lightPos,
                        new Vector3f(0, -0.5f, 0),
                        new Vector3f(0, 1, 0)
                );

        lightSpace.set(lightProj).mul(lightView);

        // Pass 1: Shadow mapping

        shadow.bindForWriting();

        glClear(GL_DEPTH_BUFFER_BIT);

        depth.use();

        depth.set("lightSpaceMatrix", lightSpace);

        Matrix4f model = new Matrix4f()
                .translate(0, 0, 0)
                .rotateY(
                        (float) (System.currentTimeMillis() % 100000)
                        / 100000f * 6.283f
                );

        depth.set("model", model);

        (flat ? flatSphere : smoothSphere).draw();

        depth.set("model", new Matrix4f().identity());

        plane.draw();

        shadow.unbind(w, h);

        // Pass 2: Normal camera rendering

        glClearColor(0.055f, 0.065f, 0.09f, 1);

        glClear(
                GL_COLOR_BUFFER_BIT |
                GL_DEPTH_BUFFER_BIT
        );

        HSRManager.setupDepthTest(depthOn);
        HSRManager.setupBackFaceCulling(cullOn);

        scene.use();

        Matrix4f view = new Matrix4f()
                .lookAt(
                        new Vector3f(3.4f, 2.4f, 5.5f),
                        new Vector3f(0, -0.1f, 0),
                        new Vector3f(0, 1, 0)
                );

        // FIX: Gunakan java.lang.Math agar tidak ambigu

        Matrix4f projection = new Matrix4f()
                .perspective(
                        (float) java.lang.Math.toRadians(45),
                        (float) w / h,
                        0.1f,
                        100
                );

        scene.set("view", view);
        scene.set("projection", projection);
        scene.set("lightSpaceMatrix", lightSpace);

        scene.set("lightPos", lightPos);

        scene.set(
                "viewPos",
                new Vector3f(3.4f, 2.4f, 5.5f)
        );

        scene.set("lightColor", new Vector3f(1, 1, 1));

        scene.set(
                "objectColor",
                new Vector3f(0.25f, 0.65f, 0.95f)
        );

        scene.set("useBlinnPhong", blinn ? 1 : 0);
        scene.set("useTexture", textureOn ? 1 : 0);

        scene.set("shadowMap", 1);
        scene.set("textureSampler", 0);
        scene.set("useShadow", 1);

        glActiveTexture(GL_TEXTURE1);

        glBindTexture(
                GL_TEXTURE_2D,
                shadow.getDepthTextureID()
        );

        texture.bind();

        scene.set("model", model);

        (flat ? flatSphere : smoothSphere).draw();

        scene.set(
                "objectColor",
                new Vector3f(0.55f, 0.58f, 0.62f)
        );

        scene.set("useTexture", 0);

        scene.set("model", new Matrix4f().identity());

        plane.draw();

        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    public void cleanup() {

        scene.delete();
        depth.delete();

        smoothSphere.delete();
        flatSphere.delete();
        plane.delete();

        texture.delete();
        shadow.delete();
    }
}
