package taylorandtanner.gasscanmk1;

/**
 * Created by Taylor.coomer on 10/2/2016.
 */



public class ReceiptEntry {

    private String price;
    private String gallons;
    private String priceGal;
    private String miles;

    public ReceiptEntry() {
    }

    public ReceiptEntry(String price, String gallons, String priceGal, String miles) {
        this.price = price;
        this.gallons = gallons;
        this.priceGal = priceGal;
        this.miles = miles;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGallons() {
        return gallons;
    }

    public void setGallons(String gallons) {
        this.gallons = gallons;
    }

    public String getPriceGal() {
        return priceGal;
    }

    public void setPriceGal(String priceGal) { this.priceGal = priceGal; }

    public String getMiles() { return miles; }

    public void setMiles(String miles) {
        this.miles = miles;
    }

}
