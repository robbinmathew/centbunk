package bronz.accounting.bunk.model;

import java.util.Date;

/**
 * Bean to hold the Scraped details.
 */
public class ScannedDetail {
    private int date;
    private String type;
    private String identifier;
    private String comments;
    private String contents;
    private Date createdDate;
    private Date updatedDate;
    private String rawContents;

    public ScannedDetail(){
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setDate(int date){
        this.date = date;
    }

    public int getDate(){
        return date;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getRawContents() {
        return rawContents;
    }

    public void setRawContents(String rawContents) {
        this.rawContents = rawContents;
    }

    @Override
    public String toString() {
        return "\nScannedDetail{" +
                "date=" + date +
                ", type='" + type + '\'' +
                ", identifier='" + identifier + '\'' +
                ", comments='" + comments + '\'' +
                ", contents=" + contents  +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", rawContents=" + rawContents +
                "}\n";
    }
}
