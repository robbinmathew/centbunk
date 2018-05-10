package bronz.accounting.bunk.model;

import java.util.Date;

/**
 * Bean to hold the Scraped details.
 */
public class ScrapedDetail {
    public static final String PRICE_CHANGE_TYPE = "PRICE_UPDATE";
    public static final String STOCK_RECEIPT = "STOCK_RECEIPT";
    public static final String HPCL_TRX = "HPCL_TRX";
    public static final String BANK_TRX = "BANK_TRX";


    private Integer pkSlNo;
    private int date;
    private String type;
    private String comments;
    private byte[] contents;
    private Date createdDate;

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
