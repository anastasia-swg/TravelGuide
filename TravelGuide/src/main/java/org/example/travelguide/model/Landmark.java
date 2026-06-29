package org.example.travelguide.model;

public class Landmark {
    private int id;
    private String name;
    private String city;
    private double latitude;
    private double longitude;
    private double rating;
    private String category;

    public Landmark(int new_id, String new_name, String new_city, double new_latitude, double new_longitude, double Rating, String category){
        this.id=new_id;
        this.city=new_city;
        this.name=new_name;
        this.latitude=new_latitude;
        this.longitude=new_longitude;
        this.rating=Rating;
        this.category= category;
    }

    public Landmark() {}


    public void printAll(){
        System.out.println("id-" + this.id+
                "        \ncity-" + this.city+
                "        \nname-" +this.name+
                "        \nlatitude-" +this.latitude+
                "        \nlongitude-" + this.longitude+
                "        \nrating-"+this.rating+"\n");

    }

    public String getName(){return name;}

    public String getCity(){return city;}

    public int getId(){return id;}

    public String getCategory(){return category;}

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public double getRating(){ return  rating;}


}