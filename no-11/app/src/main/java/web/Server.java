package web;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "webserver", mixinStandardHelpOptions = true, version = "web 1.0", description = "This challenge is to build your own webserver based on HTTP1.1")
public class Server implements Callable<Result> {

    public static void main(String[] args) {
        var server = new Server();
        var cmd = new CommandLine(server);
        var exitCode = cmd.execute(args);
        cmd.getExecutionResult();
        System.exit(exitCode);
    }

    @Option(names = "-p", description = "-p specifies the port - default 8080")
    int port = 8080;

    @Override
    public Result call() throws Exception {
        System.out.println("done");
        return new Result();
    }
}