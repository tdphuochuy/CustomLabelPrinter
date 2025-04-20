package paperwork;

public class Product{
	private int quantity;
	private double weight;
	private String code;
	private String type;
	private String trackingNum;
	private int hour;
	private boolean isCombo;
	public Product(String code,String trackingNum,int hour,String type,int quantity,double weight,boolean isCombo)
	{
		this.code = code;
		this.quantity = quantity;
		this.weight = weight;
		this.isCombo = isCombo;
		this.type = type;
		this.trackingNum = trackingNum;
		this.hour = hour;
	}
	
	public double getWeight()
	{
		return weight;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public String getTrackingNum() {
		return trackingNum;
	}

	public void setTrackingNum(String trackingNum) {
		this.trackingNum = trackingNum;
	}
	
	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isCombo() {
		return isCombo;
	}

	public void setCombo(boolean isCombo) {
		this.isCombo = isCombo;
	}
	
	public String toString()
	{
		return String.valueOf(weight);
	}
}