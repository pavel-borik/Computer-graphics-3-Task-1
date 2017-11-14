package application;

import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import oglutils.*;
import transforms.*;

import java.awt.event.*;

public class Renderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	OGLBuffers buffer;
	OGLTextRenderer textRenderer;

	int width, height, ox, oy;
	int shaderProgram, shaderProgram2;
	int locProjMat, locModelMat, locViewMat, locEye, locTexMode;
	int locProjMat2, locModelMat2, locViewMat2, locEye2, locObj2;
	int objSwitch = 0, shaderMode = 0, texturingMode = 0;
	int polygonMode = GL2GL3.GL_FILL;
	OGLTexture2D texture0, texture1, texture2, texture3;
	OGLTexture2D.Viewer textureViewer;

	Camera cam = new Camera();
	Mat4 model, proj;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		OGLUtils.shaderCheck(gl);
		
		OGLUtils.printOGLparameters(gl);
		
		textRenderer = new OGLTextRenderer(gl, glDrawable.getSurfaceWidth(), glDrawable.getSurfaceHeight());

		shaderProgram = ShaderUtils.loadProgram(gl, "/application/textureMapping");
		shaderProgram2 = ShaderUtils.loadProgram(gl, "/application/phong");

		//buffer = GridFactory.generateGrid(gl, 50, 50, TopologyType.TRIANGLES);
		buffer = MeshGenerator.generateGrid(gl, 50, 50,"inPosition");

		locModelMat = gl.glGetUniformLocation(shaderProgram, "modelMat");
		locViewMat = gl.glGetUniformLocation(shaderProgram, "viewMat");
		locProjMat = gl.glGetUniformLocation(shaderProgram, "projMat");
		locEye = gl.glGetUniformLocation(shaderProgram, "eyePos");
		locTexMode = gl.glGetUniformLocation(shaderProgram, "texMode");

		locObj2 = gl.glGetUniformLocation(shaderProgram2, "object");
		locModelMat2 = gl.glGetUniformLocation(shaderProgram2, "modelMat");
		locViewMat2 = gl.glGetUniformLocation(shaderProgram2, "viewMat");
		locProjMat2 = gl.glGetUniformLocation(shaderProgram2, "projMat");
		locEye2 = gl.glGetUniformLocation(shaderProgram2, "eyePos");

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

		switch (shaderMode) {
			case 0:
				gl.glUseProgram(shaderProgram2); break;
			case 1:
				gl.glUseProgram(shaderProgram); break;
		}

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

		gl.glUniformMatrix4fv(locModelMat2, 1, false,
				ToFloatArray.convert(model), 0);

		gl.glUniformMatrix4fv(locViewMat2, 1, false,
				ToFloatArray.convert(cam.getViewMatrix()), 0);

		gl.glUniformMatrix4fv(locProjMat2, 1, false,
				ToFloatArray.convert(proj), 0);
		gl.glUniform1i(locObj2, objSwitch);
		gl.glUniform3fv(locEye2, 1, ToFloatArray.convert(cam.getEye()), 0);

		switch (shaderMode) {
			case 0:
				texture3.bind(shaderProgram2, "texture0", 3);

				break;
			case 1:
				texture0.bind(shaderProgram, "texture0", 0);
				texture1.bind(shaderProgram, "texture1", 1);
				texture2.bind(shaderProgram, "texture2", 2);
				break;
		}

		gl.glPolygonMode(GL2GL3.GL_FRONT_AND_BACK, polygonMode);

		buffer.draw(GL2GL3.GL_TRIANGLES, shaderProgram);
		//buffer.draw(GL2GL3.GL_TRIANGLE_STRIP, shaderProgram);

		//textureViewer.view(texture, -1, -1, 0.5);

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
		cam = cam.addAzimuth((double) Math.PI * (ox - e.getX()) / width)
			.addZenith((double) Math.PI * (e.getY() - oy) / width);
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
			case KeyEvent.VK_CONTROL:
				cam = cam.down(0.5);
				break;
			case KeyEvent.VK_SHIFT:
				cam = cam.up(0.5);
				break;
			case KeyEvent.VK_SPACE:
				cam = cam.withFirstPerson(!cam.getFirstPerson());
				break;
			case KeyEvent.VK_R:
				cam = cam.mulRadius(0.9f);
				break;
			case KeyEvent.VK_F:
				cam = cam.mulRadius(1.1f);
				break;
			case KeyEvent.VK_E:
				objSwitch = (objSwitch + 1) % 7;
				break;
			case KeyEvent.VK_J:
				model = model.mul(new Mat4Transl(0,.5,0));
				break;
			case KeyEvent.VK_K:
				model = model.mul(new Mat4Transl(0,-.5,0));
				break;
			case KeyEvent.VK_O:
				model = model.mul(new Mat4RotX(0.4));
				break;
			case KeyEvent.VK_P:
				model = model.mul(new Mat4RotY(0.4));
				break;
			case KeyEvent.VK_V:
				shaderMode = (shaderMode + 1) % 2;
				break;
			case KeyEvent.VK_B:
				polygonMode = polygonMode == GL2GL3.GL_FILL ? GL2GL3.GL_LINE : GL2GL3.GL_FILL;
				break;
			case KeyEvent.VK_T:
				texturingMode = (texturingMode + 1) % 2;
				System.out.println(texturingMode);
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
						"[WASD] camera movement, [V] change shader, [B] fill mode, [E] change object, [OP] rotation, [JK] translation");
				break;
			case 1:
				textRenderer.drawStr2D(3, height - 15, "PGRF3 - task 1 | Controls: [LMB] camera, " +
						"[WASD] camera movement, [V] change shader, [B] fill mode, [T] texturing mode, [OP] rotation, [JK] translation");
				if(texturingMode == 0) textRenderer.drawStr2D(width - 895, 3, "Parallax mapping");
				if(texturingMode == 1) textRenderer.drawStr2D(width - 895, 3, "Normal mapping");
				break;
		}

		textRenderer.drawStr2D(width - 90, 3, " (c) Pavel Borik");
	}
}