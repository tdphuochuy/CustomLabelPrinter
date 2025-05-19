package buttons;

public class TableEntry {
    private String hour;
    private String sequence;

    public TableEntry(String hour, String sequence) {
        this.hour = hour;
        this.sequence = sequence;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "Hour: " + hour + ", Seq: " + sequence;
    }
}
