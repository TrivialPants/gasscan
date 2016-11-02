package taylorandtanner.gasscanmk1;

import java.io.Serializable;

/**
 * Created by Taylor.coomer on 10/2/2016.
 */



public class ReceiptEntry implements Serializable{

    private String price;
    private String gallons;
    private String priceGal;
    private String miles;
    private String key;
    private String date;
    private String station;

    public ReceiptEntry() {
    }

    public ReceiptEntry (String price, String gallons, String priceGal, String miles, String key, String date, String station){
        this.price = price;
        this.gallons = gallons;
        this.priceGal = priceGal;
        this.miles = miles;
        this.key = key;
        this.date = date;
        this.station = station;
    }
    public ReceiptEntry (String price, String gallons, String priceGal, String miles, String key, String station){
        this.price = price;
        this.gallons = gallons;
        this.priceGal = priceGal;
        this.miles = miles;
        this.key = key;
        this.date = date;
        this.station = station;
    }
    public ReceiptEntry(String price, String gallons, String priceGal, String miles, String key) {
        this.price = price;
        this.gallons = gallons;
        this.priceGal = priceGal;
        this.miles = miles;
        this.key = key;
        this.date = "unassigned";
        this.station = "unassigned";
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}
