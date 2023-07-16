/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import memcached.commands.Command;
import memcached.commands.GetCommand;
import memcached.commands.SetCommand;

class CommandTest {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = CommandTest.class.getResource("tests/" + testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new FileReader(file);
    }

    private String randomKey(String prefix) {
        var rnd = ((int) Math.floor(Math.random() * 10000));
        return String.format("%s%d", prefix, rnd);
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test
    void getcmd_simple_expectsok() throws URISyntaxException, IOException {

        // Arrange
        var key = randomKey("asdf");

        // Act
        var cmd = new GetCommand(key);

        // Assert
        assertEquals("get", cmd.type);
        assertEquals(key, cmd.key);

    }

    @Test
    void getcmd_2keys_expectsok() throws URISyntaxException, IOException {

        // Arrange
        var key = randomKey("asdf");
        var key2 = randomKey("asdf");
        String[] keys = { key, key2, key };

        // Act
        var cmd = new GetCommand(key, key2, key);

        // Assert
        assertEquals("get", cmd.type);
        assertEquals(key, cmd.key);
        assertArrayEquals(keys, cmd.keys);

    }

    @Test
    void setcmd_simple_expectsNoValue() throws URISyntaxException, IOException {

        // Arrange
        var key = randomKey("asdf");
        var value = randomKey("test");

        // Act
        var cmd = new SetCommand(key, value);

        // Assert
        assertEquals("set", cmd.type);
        assertEquals(key, cmd.key);
        assertEquals(value, cmd.data.data);
        assertEquals(0, cmd.flags());
        assertEquals(0, cmd.exptime());
        assertEquals(false, cmd.noreply());

    }

    @Test
    void setcmd_complexnoreply_expectsNoValue() throws URISyntaxException, IOException {

        // Arrange
        var key = randomKey("asdf");
        var value = randomKey("test");

        // Act
        var cmd = new SetCommand(key, value, 47, 300, true);

        // Assert
        assertEquals("set", cmd.type);
        assertEquals(key, cmd.key);
        assertEquals(value, cmd.data.data);
        assertEquals(47, cmd.flags());
        assertEquals(300, cmd.exptime());
        assertEquals(true, cmd.noreply());

    }

    @Test
    void anycmd_simple_expectsok() throws URISyntaxException, IOException {

        // Arrange
        var key = randomKey("asdf");

        // Act
        var cmd = Command.parse(String.format("any %s 0 1 2 3 4 noreply", key));

        // Assert
        assertEquals("any", cmd.type);
        assertEquals(key, cmd.key);
        assertEquals("noreply", cmd.parameterLast().get());
        assertEquals("4", cmd.parameter(4).get());
    }

    @Test
    void setcmd_noexp_expectok() throws URISyntaxException, IOException {

        // Arrange
        var key = randomKey("asdf");

        // Act
        var cmd = new SetCommand(key, "hello", 0, 0, false);

        // Assert
        assertEquals(true, cmd.isAlive());
        assertEquals(false, cmd.isExpired());
    }

    @Test
    void setcmd_exp3s_expectok() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");

        // Act
        var cmd = new SetCommand(key, "hello", 0, 3, false);
        Thread.currentThread().sleep(1000);

        // Assert
        assertEquals(true, cmd.isAlive());
        assertEquals(false, cmd.isExpired());
    }

    @Test
    void setcmd_exp1s_expectnotok_after2s() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");

        // Act
        var cmd = new SetCommand(key, "hello", 0, 1, false);
        Thread.currentThread().sleep(2000);

        // Assert
        assertEquals(false, cmd.isAlive());
        assertEquals(true, cmd.isExpired());
    }

}
