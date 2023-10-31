package qr;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import java.util.UUID;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import qr.generator.QrCodeGenerator;

@Command(name = "qr", mixinStandardHelpOptions = true, version = "qr 1.0", description = "This challenge is to build your own QR Code Generator")
public class Qr implements Callable<Result> {

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

    @Option(names = "-o", description = "-o specifies an optional output file for the generated code")
    String outputFileName = "qr-"+ UUID.randomUUID()+".png";

    @Option(names = "-q", description = "-q specificies an optional quality name: L M Q H")
    String quality = "Q";

    @Override
    public Result call() {
        if (data == null) {
            return null;
        }
        var qr = new QrCode(this.data, Quality.valueOf(quality != null ? quality : "Q"));
        qr.encode();
        var generator = new QrCodeGenerator(qr).drawBestGenerator();
        generator.canvas().draw();
        return new Result(this.outputFileName);
    }
}