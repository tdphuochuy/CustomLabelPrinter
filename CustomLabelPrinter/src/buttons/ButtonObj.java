package buttons;

public class ButtonObj {
    private String productCode;
    private String quantity;
    private boolean enabled;
    public ButtonObj(String productCode, String quantity,boolean enabled) {
        this.productCode = productCode;
        this.quantity = quantity;
        this.enabled = enabled;
    }

    // Getters and setters
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
