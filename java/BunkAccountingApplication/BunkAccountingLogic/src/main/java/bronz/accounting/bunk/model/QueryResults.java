package bronz.accounting.bunk.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by pmathew on 12/11/16.
 */
public class QueryResults {
    private Set<String> fields = new TreeSet<String>();
    private Set<String> groupByFields = new TreeSet<String>();
    private Collection results = new ArrayList();

    public Set<String> getGroupByFields() {
        return groupByFields;
    }

    public void setGroupByFields(Set<String> groupByFields) {
        this.groupByFields = groupByFields;
    }

    public Set<String> getFields() {
        return fields;
    }

    public void setFields(Set<String> fields) {
        this.fields = fields;
    }

    public Collection getResults() {
        return results;
    }

    public void setResults(Collection results) {
        this.results = results;
    }

}
