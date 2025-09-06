package whistle;

class Command {
    private final String prodNum;
    private final String quantity;
    private final String sequence;
    private final String hour;
	public Command(String prodNum,String quantity,String hour,String sequence) {
        this.prodNum = prodNum;
        this.quantity = quantity;
        this.sequence = sequence;
        this.hour = hour;
    }
    
    public String getProdNum() {
        return prodNum;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public String getSequence() {
        return sequence;
    }
    
    public String getHour() {
		return hour;
	}
}