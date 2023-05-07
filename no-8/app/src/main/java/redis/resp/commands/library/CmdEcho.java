package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.types.RespArray;
import redis.resp.types.RespBulkString;
import redis.resp.types.RespError;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespSortedMap;

public class CmdEcho extends RespLibraryFunction {

    protected CmdEcho(RespCommandLibrary library) {
        super("ECHO", library);
    }

    @Override
    public RespResponse execute(RespRequest request) {
        Optional<String> message = request.command.getValue(1);
        if (message.isPresent()) {
            return new RespResponse(new RespBulkString(message.get()));
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'echo' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "message").put("type", "string");
        return new RespSortedMap()
                .put("summary", "Echo the given string")
                .put("since", "1.0.0")
                .put("group", "connection")
                .put("complexity", "O(1)")
                .put("arguments", args);
    }
}