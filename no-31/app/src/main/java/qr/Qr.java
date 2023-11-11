package qr;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import qr.generator.QrCodeGenerator;
import qr.generator.QrImageCanvasFactory;

import java.util.UUID;
import java.util.concurrent.Callable;

@Command(name = "qr", mixinStandardHelpOptions = true, version = "qr 1.0", description = "This challenge is to build your own QR Code Generator")
public class Qr implements Callable<Result> {

    private static int VERBOSE_LEVEL = 0;

    public static int verboseLevel() {
        return VERBOSE_LEVEL;
    }
    public static boolean verbose() {
        return VERBOSE_LEVEL > 0;
    }
    public static boolean verbose1() {
        return VERBOSE_LEVEL >= 1;
    }

    public static boolean verbose2() {
        return VERBOSE_LEVEL >= 2;
    }

    public static boolean verbose3() {
        return VERBOSE_LEVEL >= 3;
    }

    public static boolean verbose4() {
        return VERBOSE_LEVEL >= 4;
    }

    public static void main(String[] args) {
        var compress = new Qr();
        var cmd = new CommandLine(compress);
        var exitCode = cmd.execute(args);
        Result result = cmd.getExecutionResult();
        if (result != null && result.toString() != null) {
            System.out.println(result);
            System.exit(exitCode);
        }
    }

    @Parameters(index = "0", description = "passes a string to create a QR code")
    String data = null;

    @Option(names = "-o", description = "-o specifies an optional output file for the generated code. default random generated name")
    String outputFileName = "qr-"+ UUID.randomUUID()+".png";

    @Option(names = "-q", description = "-q specificies an optional quality name: L M Q H - default Q")
    String quality = "Q";

    @Option(names = "-s", description = "-s specifies an optional size of each pixel - default 5")
    int squareSize = 5;

    @Option(names = "-v", description = "-v specifies verbose mode level 1 included in 2 and 3 - default none")
    boolean verboseLevel1 = false;

    @Option(names = "-vv", description = "-vv specifies verbose mode level 2 included in 3 - default none")
    boolean verboseLevel2 = false;

    @Option(names = "-vvv", description = "-vvv specifies verbose mode level 3 - default none")
    boolean verboseLevel3 = false;

    @Option(names = "-vvvv", description = "-vvvv specifies verbose mode level 4 - default none")
    boolean verboseLevel4 = false;

    @Override
    public Result call() {
        if (data == null) {
            return null;
        }
        VERBOSE_LEVEL = 0;
        if (this.verboseLevel1) VERBOSE_LEVEL = 1;
        if (this.verboseLevel2) VERBOSE_LEVEL = 2;
        if (this.verboseLevel3) VERBOSE_LEVEL = 3;
        if (this.verboseLevel4) VERBOSE_LEVEL = 3;
        var qr = new QrCode(this.data, Quality.valueOf(quality != null ? quality.toUpperCase() : "Q"));
        qr.encode();
        var generator = QrCodeGenerator.buildBestGenerator(qr);
        if (Qr.verbose2())
            generator.canvas().draw();
        var imageCanvas = new QrImageCanvasFactory(this.squareSize).newCanvasFromQrCode(qr);
        generator.canvas().draw(imageCanvas);
        imageCanvas.saveFile(this.outputFileName);
        return new Result(this.outputFileName);
    }
}