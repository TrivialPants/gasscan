package taylorandtanner.gasscanmk1;

/**
 * Created by Taylor.coomer on 10/22/2016.
 */
public class receiptTotals {
    private String totalPrice;
    private String totalGal;
    private String totalPriceGal;
    private String totalMiles;
    private String lastKey = "unassigned";
    private String totalMPG;
    private String deltaPrice = "0";
    private String deltaGal = "0";
    private String deltaPriceGal = "0";
    private String deltaMiles = "0";
    private String baseMiles = "0";
    private String deltaMPG = "0";
    public receiptTotals() {
    }

    public receiptTotals(String totalPrice, String totalGal, String totalPriceGal, String totalMiles, String lastKey, String totalMPG) {
        this.totalPrice = totalPrice;
        this.totalGal = totalGal;
        this.totalPriceGal = totalPriceGal;
        this.totalMiles = totalMiles;
        this.lastKey = lastKey;
        this.totalMPG = totalMPG;
    }


    public String getPrice() {
        return totalPrice;
    }

    public void setPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getGallons() {
        return totalGal;
    }

    public void setGallons(String totalGal) {
        this.totalGal = totalGal;
    }

    public String getPriceGal() {
        return totalPriceGal;
    }

    public void setPriceGal(String totalPriceGal) { this.totalPriceGal = totalPriceGal; }

    public String getMiles() { return totalMiles; }

    public void setMiles(String totalMiles) {
        this.totalMiles = totalMiles;
    }

    public String getKey() { return lastKey; }

    public void setKey(String lastKey) {
        this.lastKey = lastKey;
    }

    public String getMPG() { return totalMPG; }

    public void setMPG(String totalMPG) {
        this.totalMPG = totalMPG;
    }


    public String getDeltaMiles() {
        return deltaMiles;
    }

    public void setDeltaMiles(String deltaMiles) {
        this.deltaMiles = deltaMiles;
    }

    public String getDeltaPriceGal() {
        return deltaPriceGal;
    }

    public void setDeltaPriceGal(String deltaPriceGal) {
        this.deltaPriceGal = deltaPriceGal;
    }

    public String getDeltaGal() {
        return deltaGal;
    }

    public void setDeltaGal(String deltaGal) {
        this.deltaGal = deltaGal;
    }

    public String getDeltaPrice() {
        return deltaPrice;
    }

    public void setDeltaPrice(String deltaPrice) {
        this.deltaPrice = deltaPrice;
    }

    public String getDeltaMPG() {
        return deltaMPG;
    }

    public void setDeltaMPG(String deltaMPG) {
        this.deltaMPG = deltaMPG;
    }

    public String getBaseMiles() {
        return baseMiles;
    }

    public void setBaseMiles(String baseMiles) {
        this.baseMiles = baseMiles;
    }
}