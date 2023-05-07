package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.types.RespArray;
import redis.resp.types.RespBulkString;
import redis.resp.types.RespError;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespSortedMap;
import redis.resp.types.RespType;

public class CmdGet extends RespLibraryFunction {

    protected CmdGet(RespCommandLibrary library) {
        super("GET", library);
    }

    @Override
    public RespResponse execute(RespRequest request) {
        Optional<String> key = request.command.getValue(1);
        if (key.isPresent()) {
            var returnValue = request.cache.get(key.get());
            return new RespResponse(returnValue);
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'get' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "key").put("type", "key");
        return new RespSortedMap()
                .put("summary", "Get the value of a key")
                .put("since", "1.0.0")
                .put("group", "string")
                .put("complexity", "O(1)")
                .put("arguments", args);
    }
}