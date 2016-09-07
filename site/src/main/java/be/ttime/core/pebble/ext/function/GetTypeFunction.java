package be.ttime.core.pebble.ext.function;

import com.mitchellbosecke.pebble.extension.Function;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class GetTypeFunction implements Function {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Object execute(Map<String, Object> args) {
        Object o = args.get("0");

        if(o == null){
            return "null";
        } else if(o instanceof Integer){
            return "Integer";
        } else if(o instanceof String){
            return "String";
        } else if(o instanceof Long){
            return "Long";
        } else if(o instanceof Array){
            return "Array";
        } else if(o instanceof Map){
            return "Map";
        } else if(o instanceof Collection){
            return "Collection";
        } else if(o instanceof Boolean){
            return "Boolean";
        }

        return "Unknow";

    }
}
