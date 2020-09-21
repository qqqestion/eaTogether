package sampledata;

import java.util.HashMap;
import java.util.List;

public class Store {

    protected class Element {
        double latitude;
        double longitude;
        String title;
        String sportType;
        String authorName;

        public Element(double inpLat, double inpLong, String inpTitle, String inpSpType, String inpAName) {
            this.latitude = inpLat;
            this.longitude = inpLong;
            this.title = inpTitle;
            this.sportType = inpSpType;
            this.authorName = inpAName;
        }
    }

    public HashMap<String, Element> store;

    public Store() {
        super();
    }

    public void addElement(String key, double inpLat, double inpLong, String inpTitle,
                           String inpSpType, String inpAName) {
        Element element = new Element(inpLat,inpLong,inpTitle,inpSpType,inpAName);
    }

}


