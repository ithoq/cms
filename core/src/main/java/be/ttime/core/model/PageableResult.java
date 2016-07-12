package be.ttime.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PageableResult<T> {

    private Collection<T> result;
    private long currentPage;
    private long totalResult;
    private long totalPage;
}
