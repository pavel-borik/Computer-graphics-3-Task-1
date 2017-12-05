package application;

import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import oglutils.*;
import transforms.*;

import javax.swing.*;
import java.awt.event.*;

public class Renderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	private OGLBuffers buffer, buffer2;
	private OGLTextRenderer textRenderer;

	private int width, height, ox, oy;
	private int shaderProgram, shaderProgram2;
	private int locProjMat, locModelMat, locViewMat, locEye, locTexMode, locLightPos;
	private int locProjMat2, locModelMat2, locViewMat2, locEye2, locObj2, locLightMode2, locDiffCol2, locLightPos2;
	private int objSwitch = 0, shaderMode = 1, texturingMode = 0, lightMode = 0;
	private int polygonMode = GL2GL3.GL_FILL;
	private OGLTexture2D texture0, texture1, texture2, texture3;
	private OGLTexture2D.Viewer textureViewer;

	private Vec3D lightPos = new Vec3D(10.0, 8.0, 13.0);
	private Vec3D matDiffuseColor = new Vec3D(1,1,1);

	private Camera cam = new Camera();
	private Mat4 model, proj;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		OGLUtils.shaderCheck(gl);
		
		OGLUtils.printOGLparameters(gl);
		
		textRenderer = new OGLTextRenderer(gl, glDrawable.getSurfaceWidth(), glDrawable.getSurfaceHeight());

		shaderProgram = ShaderUtils.loadProgram(gl, "/application/textureMapping");
		shaderProgram2 = ShaderUtils.loadProgram(gl, "/application/phong");

		buffer = MeshGenerator.generateGridAsTriangleStrip(gl, 40, 40,"inPosition");
		buffer2 = MeshGenerator.generateGrid(gl, 30, 30,"inPosition");

		locModelMat = gl.glGetUniformLocation(shaderProgram, "modelMat");
		locViewMat = gl.glGetUniformLocation(shaderProgram, "viewMat");
		locProjMat = gl.glGetUniformLocation(shaderProgram, "projMat");
		locEye = gl.glGetUniformLocation(shaderProgram, "eyePos");
		locTexMode = gl.glGetUniformLocation(shaderProgram, "texMode");
		locLightPos = gl.glGetUniformLocation(shaderProgram, "lightPos");

		locObj2 = gl.glGetUniformLocation(shaderProgram2, "object");
		locLightMode2 = gl.glGetUniformLocation(shaderProgram2, "lightMode");
		locModelMat2 = gl.glGetUniformLocation(shaderProgram2, "modelMat");
		locViewMat2 = gl.glGetUniformLocation(shaderProgram2, "viewMat");
		locProjMat2 = gl.glGetUniformLocation(shaderProgram2, "projMat");
		locEye2 = gl.glGetUniformLocation(shaderProgram2, "eyePos");
		locDiffCol2 = gl.glGetUniformLocation(shaderProgram2, "diffCol");
		locLightPos2 = gl.glGetUniformLocation(shaderProgram2, "lightPos");

		texture0 = new OGLTexture2D(gl, "/textures/bricks.jpg");
		texture1 = new OGLTexture2D(gl, "/textures/bricksn.png");
		texture2 = new OGLTexture2D(gl, "/textures/bricksh.png");
		texture3 = new OGLTexture2D(gl, "/textures/jupiter.jpg");

		cam = cam.withPosition(new Vec3D(25, 25, 5))
				.withAzimuth(Math.PI * 1.25)
				.withZenith(Math.PI * -0.05);

		model = new Mat4Identity();

		gl.glEnable(GL2GL3.GL_DEPTH_TEST);
		textureViewer = new OGLTexture2D.Viewer(gl);
	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();

		gl.glUseProgram(shaderProgram);
		gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);

		gl.glUniformMatrix4fv(locModelMat, 1, false,
				ToFloatArray.convert(model), 0);

		gl.glUniformMatrix4fv(locViewMat, 1, false,
				ToFloatArray.convert(cam.getViewMatrix()), 0);

		gl.glUniformMatrix4fv(locProjMat, 1, false,
				ToFloatArray.convert(proj), 0);
		gl.glUniform3fv(locEye, 1, ToFloatArray.convert(cam.getEye()), 0);
		gl.glUniform1i(locTexMode, texturingMode);
		gl.glUniform3fv(locLightPos, 1, ToFloatArray.convert(lightPos), 0);

		texture0.bind(shaderProgram, "texture0", 0);
		texture1.bind(shaderProgram, "texture1", 1);
		texture2.bind(shaderProgram, "texture2", 2);

		gl.glPolygonMode(GL2GL3.GL_FRONT_AND_BACK, polygonMode);

		if(shaderMode == 1)buffer.draw(GL2GL3.GL_TRIANGLE_STRIP, shaderProgram);

		gl.glUseProgram(shaderProgram2);
		gl.glUniformMatrix4fv(locModelMat2, 1, false,
				ToFloatArray.convert(model), 0);
		gl.glUniformMatrix4fv(locViewMat2, 1, false,
				ToFloatArray.convert(cam.getViewMatrix()), 0);
		gl.glUniformMatrix4fv(locProjMat2, 1, false,
				ToFloatArray.convert(proj), 0);
		gl.glUniform1i(locObj2, objSwitch);
		gl.glUniform1i(locLightMode2, lightMode);
		gl.glUniform3fv(locEye2, 1, ToFloatArray.convert(cam.getEye()), 0);
		gl.glUniform3fv(locDiffCol2, 1, ToFloatArray.convert(matDiffuseColor), 0);
		gl.glUniform3fv(locLightPos2, 1, ToFloatArray.convert(lightPos), 0);
		texture3.bind(shaderProgram2, "texture0", 3);
		gl.glPolygonMode(GL2GL3.GL_FRONT_AND_BACK, polygonMode);

		if(shaderMode == 0) buffer2.draw(GL2GL3.GL_TRIANGLES, shaderProgram2);

		drawStrings();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		this.width = width;
		this.height = height;

		proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);

		textRenderer.updateSize(width, height);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		ox = e.getX();
		oy = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if(SwingUtilities.isLeftMouseButton(e)) {
			cam = cam.addAzimuth((double) Math.PI * (ox - e.getX()) / width)
					.addZenith((double) Math.PI * (e.getY() - oy) / width);


		}
		if(SwingUtilities.isRightMouseButton(e)) {
			model = model.mul(new Mat4RotZ((ox - e.getX())*0.02));
		}

		ox = e.getX();
		oy = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				cam = cam.forward(0.5);
				break;
			case KeyEvent.VK_D:
				cam = cam.right(0.5);
				break;
			case KeyEvent.VK_S:
				cam = cam.backward(0.5);
				break;
			case KeyEvent.VK_A:
				cam = cam.left(0.5);
				break;
			case KeyEvent.VK_E:
				objSwitch = (objSwitch + 1) % 9;
				switch(objSwitch) {
					case 0: matDiffuseColor = new Vec3D(1.0, 1.0, 1.0); break;
					case 1: matDiffuseColor = new Vec3D(1.0, 0.0, 0.0); break;
					case 2: matDiffuseColor = new Vec3D(1.0, 1.0, 0.0); break;
					case 3: matDiffuseColor = new Vec3D(0.2, 0.2, 0.9); break;
					case 4: matDiffuseColor = new Vec3D(0.5, 0.8, 0.2); break;
					case 5: matDiffuseColor = new Vec3D(1.0, 0.5, 0.0); break;
					case 6: matDiffuseColor = new Vec3D(1.0, 0.5, 1.0); break;
					case 7: matDiffuseColor = new Vec3D(1.0, 0.5, 0.0); break;
				}
				break;
			case KeyEvent.VK_J:
				model = model.mul(new Mat4Transl(-.5,0,0));
				break;
			case KeyEvent.VK_K:
				model = model.mul(new Mat4Transl(.5,0,0));
				break;
			case KeyEvent.VK_O:
				model = model.mul(new Mat4RotX(0.08));
				break;
			case KeyEvent.VK_P:
				model = model.mul(new Mat4RotX(-0.08));
				break;
			case KeyEvent.VK_V:
				shaderMode = (shaderMode + 1) % 2;
				break;
			case KeyEvent.VK_B:
				polygonMode = polygonMode == GL2GL3.GL_FILL ? GL2GL3.GL_LINE : GL2GL3.GL_FILL;
				break;
			case KeyEvent.VK_T:
				texturingMode = (texturingMode + 1) % 2;
				break;
			case KeyEvent.VK_L:
				lightMode = (lightMode + 1) % 2;
				break;
			case KeyEvent.VK_N:
				lightPos = lightPos.mul(new Mat3RotZ(0.1));
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void dispose(GLAutoDrawable glDrawable) {
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		gl.glDeleteProgram(shaderProgram);
	}

	private void drawStrings() {
		switch (shaderMode) {
			case 0:
				textRenderer.drawStr2D(3, height - 15, "PGRF3 - task 1 | Controls: [LMB] camera, " +
						"[WASD] camera movement, [V] change shader, [B] fill mode, [L] light mode, [E] change object, [OP] rotateX, [JK] translateX");
				textRenderer.drawStr2D(148, height - 30, "[RMB] rotateZ, [N] rotateLightZ");

				if(lightMode == 0) textRenderer.drawStr2D(width - 895, 3, "Light computed per pixel");
				if(lightMode == 1) textRenderer.drawStr2D(width - 895, 3, "Light computed per vertex");
				break;
			case 1:
				textRenderer.drawStr2D(3, height - 15, "PGRF3 - task 1 | Controls: [LMB] camera, " +
						"[WASD] camera movement, [V] change shader, [B] fill mode, [T] texturing mode, [OP] rotateX, [JK] translateX");
				textRenderer.drawStr2D(148, height - 30, "[RMB] rotateZ, [N] rotateLightZ");

				if(texturingMode == 0) textRenderer.drawStr2D(width - 895, 3, "Parallax mapping");
				if(texturingMode == 1) textRenderer.drawStr2D(width - 895, 3, "Normal mapping");
				break;
		}

		textRenderer.drawStr2D(width - 90, 3, " (c) Pavel Borik");
	}
}