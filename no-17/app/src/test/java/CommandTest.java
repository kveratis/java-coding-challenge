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

import memcached.commands.AddCommand;
import memcached.commands.AppendCommand;
import memcached.commands.CasCommand;
import memcached.commands.Command;
import memcached.commands.GetCommand;
import memcached.commands.PrependCommand;
import memcached.commands.ReplaceCommand;
import memcached.commands.SetCommand;
import memcached.commands.ValidationCode;
import memcached.server.cache.CacheContext;
import memcached.server.cache.MemCache;

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
        var cmd = new SetCommand(key, "hello", 0, 0, false);
        var cache = new MemCache();

        // Act
        var context = new CacheContext(cache, cmd);

        // Assert
        assertEquals(true, context.isAlive());
        assertEquals(false, context.isExpired());
    }

    @Test
    void setcmd_exp3s_expectok() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cmd = new SetCommand(key, "hello", 0, 3, false);
        var cache = new MemCache();
        var context = new CacheContext(cache, cmd);

        // Act
        Thread.sleep(1000);

        // Assert
        assertEquals(true, context.isAlive());
        assertEquals(false, context.isExpired());
    }

    @Test
    void setcmd_exp1s_expectnotok_after2s() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmd = new SetCommand(key, "hello", 0, 1, false);
        var context = new CacheContext(cache, cmd);
        // Act
        Thread.sleep(2000);

        // Assert
        assertEquals(false, context.isAlive());
        assertEquals(true, context.isExpired());
    }

    @Test
    void addcmd_empy_expectok() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmd = new AddCommand(key, "hello", 0, 0, false);

        // Act
        var responseAfterSet = cache.set(cmd);

        // Assert
        assertEquals(true, responseAfterSet.isPresent());
        assertEquals(ValidationCode.STORED, responseAfterSet.get());
    }

    @Test
    void addcmd_exists_expectnotok() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmd = new AddCommand(key, "hello", 0, 0, false);
        var responseAfterSet = cache.set(cmd);

        // Act
        var responseAfterSet2 = cache.set(cmd);

        // Assert
        assertEquals(true, responseAfterSet2.isPresent());
        assertEquals(ValidationCode.NOT_STORED, responseAfterSet2.get());
    }

    @Test
    void replacecmd_empty_expectnook() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmd = new ReplaceCommand(key, "hello", 0, 0, false);

        // Act
        var responseAfterSet = cache.set(cmd);

        // Assert
        assertEquals(true, responseAfterSet.isPresent());
        assertEquals(ValidationCode.NOT_STORED, responseAfterSet.get());
    }

    @Test
    void replacecmd_withexisting_expectnook() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmdSet = new SetCommand(key, "hello1", 0, 0, false);

        var cmdReplace = new ReplaceCommand(key, "hello-2", 0, 0, false);

        // Act
        var responseAfterSet1 = cache.set(cmdSet);
        var responseAfterSet2 = cache.set(cmdReplace);

        // Assert
        assertEquals(true, responseAfterSet1.isPresent());
        assertEquals(true, responseAfterSet2.isPresent());
        assertEquals(ValidationCode.STORED, responseAfterSet2.get());
    }

    @Test
    void cascmd_withempty_expectok() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();

        var cmdCas = new CasCommand(key, "hello-2", 0, 0, 1, false);

        // Act
        var responseAfterSet2 = cache.set(cmdCas);

        // Assert
        assertEquals(true, responseAfterSet2.isPresent());
        assertEquals(ValidationCode.NOT_FOUND, responseAfterSet2.get());
    }

    @Test
    void cascmd_withexisting_expectok() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmdSet = new SetCommand(key, "hello1", 0, 0, false);
        var responseAfterSet1 = cache.set(cmdSet);

        var cmdCas = new CasCommand(key, "hello-2", 0, 0, 1, false);

        // Act
        var responseAfterSet2 = cache.set(cmdCas);

        // Assert
        assertEquals(true, responseAfterSet1.isPresent());
        assertEquals(true, responseAfterSet2.isPresent());
        assertEquals(ValidationCode.STORED, responseAfterSet2.get());
    }

    @Test
    void cascmd_withexistingcas10_expectnotok() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmdSet = new SetCommand(key, "hello1", 0, 0, false);
        var responseAfterSet1 = cache.set(cmdSet);

        var cmdCas = new CasCommand(key, "hello-2", 0, 0, 10, false);

        // Act
        var responseAfterSet2 = cache.set(cmdCas);

        // Assert
        assertEquals(true, responseAfterSet1.isPresent());
        assertEquals(true, responseAfterSet2.isPresent());
        assertEquals(ValidationCode.EXISTS, responseAfterSet2.get());
    }

    @Test
    void appendcmd_empty_expectnotok() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmd = new AppendCommand(key, "hello", 0, 0, false);

        // Act
        var responseAfterSet = cache.set(cmd);

        // Assert
        assertEquals(true, responseAfterSet.isPresent());
        assertEquals(ValidationCode.NOT_STORED, responseAfterSet.get());
    }

    @Test
    void appendcmd_withexisting_expectnook() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmdSet = new SetCommand(key, "1234", 0, 0, false);

        var cmdAppend = new AppendCommand(key, "567890", 0, 0, false);
        var cmdGet = new GetCommand(key);

        // Act
        var responseAfterSet1 = cache.set(cmdSet);
        var responseAfterAppend2 = cache.set(cmdAppend);
        var responseAfterGet3 = cache.get(cmdGet);

        // Assert
        assertEquals(true, responseAfterSet1.isPresent());

        assertEquals(true, responseAfterAppend2.isPresent());
        assertEquals(ValidationCode.STORED, responseAfterAppend2.get());

        assertEquals(true, responseAfterGet3.isPresent());
        assertEquals("1234567890", responseAfterGet3.get().data.data);
    }

    @Test
    void prependcmd_empty_expectnotok() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmd = new PrependCommand(key, "hello", 0, 0, false);

        // Act
        var responseAfterSet = cache.set(cmd);

        // Assert
        assertEquals(true, responseAfterSet.isPresent());
        assertEquals(ValidationCode.NOT_STORED, responseAfterSet.get());
    }

    @Test
    void prependcmd_withexisting_expectnook() throws URISyntaxException, IOException, InterruptedException {

        // Arrange
        var key = randomKey("asdf");
        var cache = new MemCache();
        var cmdSet = new SetCommand(key, "1234", 0, 0, false);

        var cmdAppend = new PrependCommand(key, "567890", 0, 0, false);
        var cmdGet = new GetCommand(key);

        // Act
        var responseAfterSet1 = cache.set(cmdSet);
        var responseAfterAppend2 = cache.set(cmdAppend);
        var responseAfterGet3 = cache.get(cmdGet);

        // Assert
        assertEquals(true, responseAfterSet1.isPresent());

        assertEquals(true, responseAfterAppend2.isPresent());
        assertEquals(ValidationCode.STORED, responseAfterAppend2.get());

        assertEquals(true, responseAfterGet3.isPresent());
        assertEquals("5678901234", responseAfterGet3.get().data.data);
    }
}
