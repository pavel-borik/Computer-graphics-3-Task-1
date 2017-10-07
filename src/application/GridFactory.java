package application;

import com.jogamp.opengl.GL2GL3;
import com.sun.prism.impl.VertexBuffer;
import oglutils.OGLBuffers;

public class GridFactory {

    public static OGLBuffers generateGrid(GL2GL3 gl, int rows, int columns, TopologyType topologyType) {


        float[] vertexBufferData = new float[2 * rows * columns];
        int[] indexBufferData;


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                vertexBufferData[2*(i*columns+j)] = j/(float) (columns-1);
                vertexBufferData[2*(i*columns+j)+1] = i/(float) (rows-1);
            }
        }

        switch (topologyType) {
            case TRIANGLES:
                indexBufferData = generateIndicesAsTriangles(rows,columns);
                break;
            case TRIANGLE_STRIP:
                indexBufferData = generateIndicesAsTriangleStrip(rows,columns);
                break;
            default:
                indexBufferData = new int[0];
                break;
        }

        // vertex binding description, concise version
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2), // 2 float
        };

        OGLBuffers buffers = new OGLBuffers(gl, vertexBufferData, attributes, indexBufferData);


        for (float f : vertexBufferData ) {
            System.out.print(f + " ");
        }
        System.out.println();
        for (int c : indexBufferData ) {
            System.out.print(c + " ");
        }

        System.out.println();
        System.out.println(indexBufferData.length + " " + vertexBufferData.length);

        return buffers;
    }

    private static int[] generateIndicesAsTriangleStrip(int rows, int columns) {
        int numStripsRequired = rows - 1;
        int numDegensRequired = 2 * (numStripsRequired - 1);
        int verticesPerStrip = 2 * columns;

        int[] indexBufferData = new int[(verticesPerStrip * numStripsRequired)
                + numDegensRequired];

        int offset = 0;

        for (int y = 0; y < rows - 1; y++) {
            if (y > 0) {
                // Degenerate begin: repeat first vertex
                indexBufferData[offset++] =  (y * columns);
            }

            for (int x = 0; x < columns; x++) {
                // One part of the strip
                indexBufferData[offset++] =  ((y * columns) + x);
                indexBufferData[offset++] =  (((y + 1) * columns) + x);
            }

            if (y < rows - 2) {
                // Degenerate end: repeat last vertex
                indexBufferData[offset++] =  (((y + 1) * columns) + (columns - 1));
            }
        }
        return indexBufferData;
    }


    private static int[] generateIndicesAsTriangles(int rows, int columns) {
        int[] indexBufferData = new int[(rows -1)*(columns-1)*6];
        int index = 0;
        for (int i = 0; i < rows -1; i++) {
            for (int j = 0; j < columns -1; j++) {
                indexBufferData[index++] = j + i * columns;
                indexBufferData[index++] = j + 1 + (i+1) * columns;
                indexBufferData[index++] = j + (i+1)*columns;
                indexBufferData[index++] = j + i * columns;
                indexBufferData[index++] = j + 1 + i * columns;
                indexBufferData[index++] = j + 1 + (i+1) * columns;
            }
        }

        return indexBufferData;
    }




}
