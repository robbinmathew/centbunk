package bronz.accounting.bunk.model;

public class ScrappedData {
    private int date;
    private String message;
    private String type;
    private String identifier;
    private byte[] contents;
    private byte[] rawContents;

    public ScrappedData(){
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public byte[] getRawContents() {
        return rawContents;
    }

    public void setRawContents(byte[] rawContents) {
        this.rawContents = rawContents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }


}
