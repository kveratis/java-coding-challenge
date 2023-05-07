package redis.resp.types;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RespArray extends RespType<RespType[]> {

    public final Long length;

    public RespArray(Long length, RespType[] value) {
        super(value);
        this.length = length;
    }

    public RespArray(Integer length, RespType[] value) {
        super(value);
        this.length = Long.valueOf(length);
    }

    public RespArray(RespType... value) {
        super(value);
        this.length = Long.valueOf(value.length);
    }

    public RespArray(String... value) {
        super(Arrays.asList(value).stream().map(x -> new RespBulkString(x)).toArray(RespBulkString[]::new));
        this.length = Long.valueOf(value.length);
    }

    public RespArray(List<RespType> value) {
        super(value.toArray(RespType[]::new));
        this.length = Long.valueOf(value.size());
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        buffer.append("*").append(this.length).append("\r\n");
        for (RespType respType : value) {
            respType.toRespString(buffer);
        }
    }

    public RespType first() {
        return this.get(0).get();
    }

    public Optional<RespType> second() {
        return this.get(1);
    }

    public Optional<RespType> get(int index) {
        if (value.length > index) {
            return Optional.of(value[index]);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean isCommandType() {
        return true;
    }

    public Optional<String> getSubFunction() {
        var subFunctionElement = this.second();
        return subFunctionElement.isPresent() && !subFunctionElement.get().isCommandType()
                ? subFunctionElement.get().getSubFunction()
                : Optional.empty();
    }

    public boolean hasSubFunction(String subFunction) {
        var subFunctionElement = this.second();
        return (subFunctionElement.isPresent() && subFunctionElement.get().isSubFunction(subFunction));
    }

    public boolean contains(String filter) {
        for (RespType respType : this.value) {
            if (respType.value.equals(filter)) {
                return true;
            }
        }
        return false;
    }

}
