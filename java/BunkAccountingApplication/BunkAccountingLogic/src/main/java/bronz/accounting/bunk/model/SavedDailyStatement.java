package bronz.accounting.bunk.model;

import bronz.utilities.general.Pair;
import bronz.utilities.general.Triple;

import java.math.BigDecimal;
import java.util.*;

/**
 * Bean to hold the saved daily statement.
 */
public class SavedDailyStatement {
    private int date;
    private String message;
    private byte[] contents;

    public SavedDailyStatement(){
    }

    public void setDate(int date){
        this.date = date;
    }

    public int getDate(){
        return date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }
}
