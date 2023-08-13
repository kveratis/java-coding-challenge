/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import dns.DnsServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

class DnServerTest {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = DnServerTest.class.getResource("tests/"+testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new FileReader(file);
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test
    void sendDns8888_dnsgooglecom_expect() throws IOException {
        //Arrange
        var server = "8.8.8.8";
        var port = 53;
        var dnsServer = new DnsServer(server, port);

        var msg = "00160100000100000000000003646e7306676f6f676c6503636f6d0000010001";
        //Act
        var received = dnsServer.sendAndReceive(msg);
        
        //Assert

        assertEquals(DnsMessageTests.nospace("0016 8180 0001 0002 0000 0000 03646E73 06676F6F676C65 03636F6D 0000 0100 01C00C0001000100000384000408080404C00C0001000100000384000408080808"),received);
    }


}
