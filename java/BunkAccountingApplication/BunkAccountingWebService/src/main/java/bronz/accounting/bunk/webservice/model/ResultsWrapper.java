package bronz.accounting.bunk.webservice.model;

import java.util.Collection;

/**
 * Created by pmathew on 1/11/16.
 */
public class ResultsWrapper {
    private Object data;
    private int totalCount;

    public ResultsWrapper(final Object data, final int totalCount) {
        this.data = data;
        this.totalCount = totalCount;
    }

    public ResultsWrapper() {
    }

    public ResultsWrapper(final Object data) {
        this.data = data;
        this.totalCount = 1;
    }

    public ResultsWrapper(final Collection<?> resultCollection) {
        this.data = resultCollection;
        this.totalCount = resultCollection.size();
    }

    public Object getData() {
        return data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
