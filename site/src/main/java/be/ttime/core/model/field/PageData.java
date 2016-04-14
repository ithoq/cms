package be.ttime.core.model.field;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class PageData {

    @Expose
    private HashMap<String, String> data = new HashMap<>();

    @Expose
    private HashMap<String, List<String>> dataArray = new HashMap<>();
}
