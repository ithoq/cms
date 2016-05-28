package be.ttime.core.model.field;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;

@Getter
@Setter
public class PageData {

    @Expose
    private HashMap<String, String> dataString = new HashMap<>();

    @Expose
    private HashMap<String, String[]> dataStringArray = new HashMap<>();

    @Expose
    private HashMap<String, Integer> dataInteger = new HashMap<>();

    @Expose
    private HashMap<String, Integer[]> dataIntegerArray = new HashMap<>();

    @Expose
    private HashMap<String, Double> dataDouble = new HashMap<>();

    @Expose
    private HashMap<String, Double[]> dataDoubleArray = new HashMap<>();

    @Expose
    private HashMap<String, Date> dataDate = new HashMap<>();

    @Expose
    private HashMap<String, Date[]> dataDateArray = new HashMap<>();

    @Expose
    private HashMap<String, Boolean> dataBoolean = new HashMap<>();

    @Expose
    private HashMap<String, Boolean[]> dataBooleanArray = new HashMap<>();
}
