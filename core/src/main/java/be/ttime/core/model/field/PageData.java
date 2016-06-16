package be.ttime.core.model.field;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PageData {

    @Expose
    private Map<String, String> dataString = new HashMap<>();
    @Expose
    private Map<String, String[]> dataStringArray = new HashMap<>();

    @Expose
    private Map<String, Integer> dataInteger = new HashMap<>();

    @Expose
    private Map<String, Integer[]> dataIntegerArray = new HashMap<>();

    @Expose
    private Map<String, Double> dataDouble = new HashMap<>();

    @Expose
    private Map<String, Double[]> dataDoubleArray = new HashMap<>();

    @Expose
    private Map<String, Date> dataDate = new HashMap<>();

    @Expose
    private Map<String, Date[]> dataDateArray = new HashMap<>();

    @Expose
    private Map<String, Boolean> dataBoolean = new HashMap<>();

    @Expose
    private Map<String, Boolean[]> dataBooleanArray = new HashMap<>();
}
