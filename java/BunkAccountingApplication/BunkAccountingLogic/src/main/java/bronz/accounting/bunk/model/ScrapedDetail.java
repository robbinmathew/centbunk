package bronz.accounting.bunk.model;

/**
 * Bean to hold the Scraped details.
 */
public class ScrapedDetail {
    private Integer pkSlNo;
    private int date;
    private String type;
    private String comments;
    private byte[] contents;

    public ScrapedDetail(){
    }

    public Integer getPkSlNo() {
        return pkSlNo;
    }

    public void setPkSlNo(Integer pkSlNo) {
        this.pkSlNo = pkSlNo;
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

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }
}
