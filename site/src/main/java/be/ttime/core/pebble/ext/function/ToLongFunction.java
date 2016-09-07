package be.ttime.core.pebble.ext.function;

import com.mitchellbosecke.pebble.extension.Function;
import com.sun.tools.javac.util.List;

import java.util.Map;

public class ToLongFunction implements Function {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Object execute(Map<String, Object> args) {
        Object o = args.get("0");
        if(o==null) return null;
        if(o instanceof String){
            throw new IllegalArgumentException("Argument must be a String");
        }
        Long result = null;
        try {
            Long.parseLong((String) o);
        } catch(NumberFormatException e) {}

        return result;
    }
}

