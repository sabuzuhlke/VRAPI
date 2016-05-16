package VRAPI.ContainerCurrency;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 16/05/2016.
 */
public class QueryResponse {
    private Currency currency;

    public QueryResponse() {
    }

    @XmlElement(name = "Waehrung")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
