package tarun.djangorestclient.com.djangorestclient.model;

/**
 * Model class subclassing the Header class with more Custom header fields.
 */

public class CustomHeader extends Header {

    private String customHeaderType;

    public CustomHeader(String customHeaderType, String headerValue) {
        super(HeaderType.CUSTOM, headerValue);
        this.customHeaderType = customHeaderType;
    }

    public String getCustomHeaderType() {
        return customHeaderType;
    }

    public void setCustomHeaderType(String customHeaderType) {
        this.customHeaderType = customHeaderType;
    }
}
