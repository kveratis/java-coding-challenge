/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import lisp.parser.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TensorTests {

    private BufferedReader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = TensorTests.class.getResource("tests/" + testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new BufferedReader(new FileReader(file));
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test
    void create_1dim_ok() throws URISyntaxException, IOException {
        // Arrange
        var dim = 2;
        var initializer = new TensorInitializer() {
            private int i = 0;
            @Override
            public TokenValue get() {
                return new TokenValue(Token.NUMBER_INTEGER, i++);
            }
        };
        var tensor = new Tensor(new int[]{dim}, initializer);

        // Act
        // Assert
        for (int i = 0; i < tensor.dataSize(); i++) {
            assertEquals(i, tensor.get(i).getInteger());
        }
    }

    @Test
    void create_0_ok() throws URISyntaxException, IOException {
        // Arrange
        var dim = 2;
        var tensor = Tensor.Zeros(new int[]{dim});

        // Act
        // Assert
        for (int i = 0; i < tensor.dataSize(); i++) {
            assertEquals(0, tensor.get(i).getDouble());
        }
    }
    @Test
    void create_1int_ok() throws URISyntaxException, IOException {
        // Arrange
        var dim = 2;
        var tensor = Tensor.OnesInt(new int[]{dim});

        // Act
        // Assert
        for (int i = 0; i < tensor.dataSize(); i++) {
            assertEquals(1, tensor.get(i).getInteger());
        }
    }
    @Test
    void create_rnd_ok() throws URISyntaxException, IOException {
        // Arrange
        var dim = 2;
        var tensor = Tensor.Random(new int[]{dim});

        // Act
        // Assert
        for (int i = 0; i < tensor.dataSize(); i++) {
            var val = tensor.get(i).getDouble();
            assertTrue(val >= 0.0 && val < 1.0);
        }
    }

    @Test
    void create_rndInt10_ok() throws URISyntaxException, IOException {
        // Arrange
        var dim = 20;
        var tensor = Tensor.RandomInteger(new int[]{dim}, 10);

        // Act
        // Assert
        for (int i = 0; i < tensor.dataSize(); i++) {
            var val = tensor.get(i).getInteger();
            assertTrue(val >= 0 && val < 10);
        }
    }

    @Test
    void create_rnd2Dim_ok() throws URISyntaxException, IOException {
        // Arrange
        var dim = 10;
        var initializer = new TensorInitializer() {
            private int i = 0;
            @Override
            public TokenValue get() {
                return new TokenValue(Token.NUMBER_INTEGER, i++);
            }
        };
        var tensor = new Tensor(new int[]{dim, dim}, initializer);

        // Act
        // Assert
        for (int x = 0; x < dim; x++) {
            for (int y = 0; y < dim; y++) {
                var val = tensor.get(x, y).getInteger();
                assertEquals(y * 10 + x, val);
            }
        }
    }

    @Test
    void create_rnd4Dim_ok() throws URISyntaxException, IOException {
        // Arrange
        var dim = 10;
        var initializer = new TensorInitializer() {
            private int i = 0;
            @Override
            public TokenValue get() {
                return new TokenValue(Token.NUMBER_INTEGER, i++);
            }
        };
        var tensor = new Tensor(new int[]{dim, dim, dim, dim}, initializer);

        // Act
        // Assert
        for (int x = 0; x < dim; x++) {
            for (int y = 0; y < dim; y++) {
                for (int i = 0; i < dim; i++) {
                    for (int j = 0; j < dim; j++) {
                        var val = tensor.get(x, y, i, j).getInteger();
                    }
                }
            }
        }
    }

    @Test
    void tensor1dim_access2_ok() throws URISyntaxException, IOException {
        // Arrange
        var dim = 20;
        var tensor20 = Tensor.Random(new int[]{dim});
        var tensor20_1 = Tensor.Random(new int[]{dim, 1});

        // Act
        // Assert
        for (int i = 0; i < dim; i++) {
            var val20 = tensor20.get(i).getDouble();
            var val20_1 = tensor20_1.get(i,0).getDouble();
            assertTrue(val20 >= 0.0 && val20 < 1.0);
            assertTrue(val20_1 >= 0.0 && val20_1 < 1.0);
        }
    }
}
