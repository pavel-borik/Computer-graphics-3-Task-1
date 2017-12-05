package application;

import com.jogamp.opengl.GL2GL3;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import oglutils.OGLBuffers;
import oglutils.ToFloatArray;
import oglutils.ToIntArray;
import org.jetbrains.annotations.NotNull;
import transforms.Vec2D;

public class MeshGenerator {
    public static @NotNull
    OGLBuffers generateGrid(
            final @NotNull GL2GL3 gl,
            final int m, final int n,
            final @NotNull String shaderName) {

        Seq<Vec2D> vertices = Stream.range(0, m).flatMap(
                (final Integer r) -> Stream.range(0, n).map(
                        (final Integer c) ->
                                new Vec2D(c / (n - 1.0), r / (m - 1.0))
                )
        );

        Seq<Tuple2<Integer, Integer>> offsets =
                Array.of(0, 0, 1, 1, 0, 1).zip(Array.of(0, 1, 0, 0, 1, 1));
        Seq<Integer> indices = Stream.range(0, m - 1).flatMap(
                (final Integer r) -> Stream.range(0, n - 1).flatMap(
                        (final Integer c) -> offsets.map(
                                (final Tuple2<Integer, Integer> offset) ->
                                        (r + offset._1) * n + c + offset._2
                        )
                )
        );

        final OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib(shaderName, 2),
        };

        return new OGLBuffers(
                gl,
                ToFloatArray.convert(vertices.toJavaList()),
                attributes,
                ToIntArray.convert(indices.toJavaList()));
    }

    public static @NotNull OGLBuffers generateGridAsTriangleStrip(
            final @NotNull GL2GL3 gl,
            final int m, final int n,
            final @NotNull String shaderName) {

        float[] vertexBufferData = new float[2 * m * n];


        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                vertexBufferData[2*(i*n+j)] = j/(float) (n-1);
                vertexBufferData[2*(i*n+j)+1] = i/(float) (m-1);
            }
        }
        int numStripsRequired = m - 1;
        int numDegensRequired = 2 * (numStripsRequired - 1);
        int verticesPerStrip = 2 * n;

        int[] indexBufferData = new int[(verticesPerStrip * numStripsRequired)
                + numDegensRequired];

        int offset = 0;

        for (int y = 0; y < m - 1; y++) {
            if (y > 0) {
                // Degenerate begin: repeat first vertex
                indexBufferData[offset++] =  (y * n);
            }

            for (int x = 0; x < n; x++) {
                // One part of the strip
                indexBufferData[offset++] =  ((y * n) + x);
                indexBufferData[offset++] =  (((y + 1) * n) + x);
            }

            if (y < m - 2) {
                // Degenerate end: repeat last vertex
                indexBufferData[offset++] =  (((y + 1) * n) + (n - 1));
            }
        }

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib(shaderName, 2),
        };

        OGLBuffers buffers = new OGLBuffers(gl, vertexBufferData, attributes, indexBufferData);

        return buffers;
    }

}

