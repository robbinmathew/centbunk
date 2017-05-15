package bronz.accounting.bunk.webservice.model;

import java.util.List;

import bronz.accounting.bunk.party.model.Party;

/**
 * Created by pmathew on 5/13/17.
 */
public class UiUpdateParties {
    private List<Party> parties;
    private List<Party> employees;

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }

    public List<Party> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Party> employees) {
        this.employees = employees;
    }
}
