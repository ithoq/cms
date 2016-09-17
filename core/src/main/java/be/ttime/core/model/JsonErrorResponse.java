package be.ttime.core.model;


import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class JsonErrorResponse {

    @Expose
    private List<Map> errors = new ArrayList<>();
}
