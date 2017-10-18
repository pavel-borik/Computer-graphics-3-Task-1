package application;

import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import oglutils.*;
import transforms.*;

import java.awt.event.*;

public class Renderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	int width, height, ox, oy;

	OGLBuffers buffers;
	OGLTextRenderer textRenderer;

	int shaderProgram, locProjMat, locModelMat, locViewMat, locEye, locObj;

	OGLTexture2D texture;
	OGLTexture2D.Viewer textureViewer;

	int objSwitch = 0;
	Vec3D eye;

	Camera cam = new Camera();
	Mat4 model, view, proj;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		OGLUtils.shaderCheck(gl);
		
		OGLUtils.printOGLparameters(gl);
		
		textRenderer = new OGLTextRenderer(gl, glDrawable.getSurfaceWidth(), glDrawable.getSurfaceHeight());

		shaderProgram = ShaderUtils.loadProgram(gl, "/application/phong");

		buffers = GridFactory.generateGrid(gl, 50, 50, TopologyType.TRIANGLE_STRIP);

		locModelMat = gl.glGetUniformLocation(shaderProgram, "modelMat");
		locViewMat = gl.glGetUniformLocation(shaderProgram, "viewMat");
		locProjMat = gl.glGetUniformLocation(shaderProgram, "projMat");
		locObj = gl.glGetUniformLocation(shaderProgram, "object");

		locEye = gl.glGetUniformLocation(shaderProgram, "eyePos");

		texture = new OGLTexture2D(gl, "/textures/jupiter.jpg");

		cam = cam.withPosition(new Vec3D(25, 25, 5))
				.withAzimuth(Math.PI * 1.25)
				.withZenith(Math.PI * -0.05);
		view = cam.getViewMatrix();

		model = new Mat4Identity();
		eye = cam.getEye();

		gl.glEnable(GL2GL3.GL_DEPTH_TEST);
		textureViewer = new OGLTexture2D.Viewer(gl);
	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		
		gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);

		gl.glUseProgram(shaderProgram);

		gl.glUniformMatrix4fv(locModelMat, 1, false,
				ToFloatArray.convert(model), 0);

		gl.glUniformMatrix4fv(locViewMat, 1, false,
				ToFloatArray.convert(view), 0);

		gl.glUniformMatrix4fv(locProjMat, 1, false,
				ToFloatArray.convert(proj), 0);

		gl.glUniform1i(locObj,objSwitch);
		gl.glUniform3fv(locEye, 1, ToFloatArray.convert(eye), 0);

		texture.bind(shaderProgram, "texture0", 0);


		gl.glPolygonMode(GL2GL3.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

		//buffers.draw(GL2GL3.GL_TRIANGLES, shaderProgram);
		buffers.draw(GL2GL3.GL_TRIANGLE_STRIP, shaderProgram);

		textureViewer.view(texture, -1, -1, 0.5);
		textRenderer.drawStr2D(3, height - 20, "PGRF3 - task 1");
		textRenderer.drawStr2D(width - 90, 3, " (c) Pavel Borik");

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
		view = cam.getViewMatrix();
		eye = cam.getEye();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				cam = cam.forward(0.5);
				view = cam.getViewMatrix();
				break;
			case KeyEvent.VK_D:
				cam = cam.right(0.5);
				view = cam.getViewMatrix();
				break;
			case KeyEvent.VK_S:
				cam = cam.backward(0.5);
				view = cam.getViewMatrix();
				break;
			case KeyEvent.VK_A:
				cam = cam.left(0.5);
				view = cam.getViewMatrix();
				break;
			case KeyEvent.VK_CONTROL:
				cam = cam.down(0.5);
				view = cam.getViewMatrix();
				break;
			case KeyEvent.VK_SHIFT:
				cam = cam.up(0.5);
				view = cam.getViewMatrix();
				break;
			case KeyEvent.VK_SPACE:
				cam = cam.withFirstPerson(!cam.getFirstPerson());
				view = cam.getViewMatrix();
				break;
			case KeyEvent.VK_R:
				cam = cam.mulRadius(0.9f);
				view = cam.getViewMatrix();
				break;
			case KeyEvent.VK_F:
				cam = cam.mulRadius(1.1f);
				view = cam.getViewMatrix();
				break;
			case KeyEvent.VK_K:
				objSwitch = (objSwitch + 1) % 4;
				System.out.println(objSwitch);
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

}