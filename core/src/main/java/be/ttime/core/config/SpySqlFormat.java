package be.ttime.core.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

public class SpySqlFormat implements MessageFormattingStrategy {

    private final Formatter formatter = new BasicFormatterImpl();

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql) {
        //return formatter.format(sql);
        return "#" + now + " | took " + elapsed + "ms | " + category + " | connection " + connectionId + "|" + "\n" + getSql(sql, prepared);
    }

    private String getSql(String sql, String prepared){
        if(!StringUtils.isEmpty(sql)){
            return formatter.format(sql) + "\n";
        } else if (!StringUtils.isEmpty(prepared)){
            return formatter.format(prepared) + '\n';
        } else{
            return "";
        }
    }
}
