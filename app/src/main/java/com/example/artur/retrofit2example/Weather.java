package com.example.artur.retrofit2example;



public class Weather {

    private Coord coord;
    private int cod;
    private String name;
    private Main main;
    private Sys sys;


    public Weather(Coord coord, int code, String name, Main main) {

        this.coord = coord;
        this.cod = code;
        this.name = name;
        this.main = main;
    }


    public int getCod() { return cod; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    class Main {

        double temp;
        double pressure;
        int humidity;

        public Main(double temp, int pressure, int humidity) {

            this.temp = temp;
            this.pressure = pressure;
            this.humidity = humidity;
        }

        public double getTemp() { return temp; }
    }


    class Coord {

        double lat;
        double lon;

        public Coord(double lat, double lon) {

            this.lat = lat;
            this.lon = lon;
        }

        public double getLat() { return lat; }
        public double getLon() { return lon; }
    }


    class Sys {

        String country;
        long sunrise;
        long sunset;
        public String getCountry() { return country; }
        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
    }


    public double getLat() { return coord.getLat(); }
    public double getLon() { return coord.getLon(); }
    public double getTemp() { return main.getTemp(); }
    public String getCountry() { return sys.getCountry(); }
    public long getSunrise() { return sys.getSunrise(); }
    public long getSunset() { return sys.getSunset(); }

}