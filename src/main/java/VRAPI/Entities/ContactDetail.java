package VRAPI.Entities;

public class ContactDetail {

    private String label;
    private String value;
    private Boolean primary;

    /**
     * Used by RestTemplate and in tests
     */
    public ContactDetail() {
    }

    /**
     * Used to hold email or phone information for a contact in pipedrive
     * @param value is email/phone string representation
     * @param primary is boolean denoting if value is main contact deatil for person
     */
    public ContactDetail(String value, Boolean primary) {
        this.value = value;
        this.primary = primary;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Detail: " + value;
    }
}