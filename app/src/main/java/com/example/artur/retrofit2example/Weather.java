package com.example.artur.retrofit2example;



class Weather {

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


    int getCod() { return cod; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    private class Main {

        double temp;
        double pressure;
        int humidity;

        public Main(double temp, int pressure, int humidity) {

            this.temp = temp;
            this.pressure = pressure;
            this.humidity = humidity;
        }

        double getTemp() { return temp; }
    }


    private class Coord {

        double lat;
        double lon;

        public Coord(double lat, double lon) {

            this.lat = lat;
            this.lon = lon;
        }

        double getLat() { return lat; }
        double getLon() { return lon; }
    }


    private class Sys {

        String country;
        long sunrise;
        long sunset;
        String getCountry() { return country; }
        long getSunrise() { return sunrise; }
        long getSunset() { return sunset; }
    }


    double getLat() { return coord.getLat(); }
    double getLon() { return coord.getLon(); }
    double getTemp() { return main.getTemp(); }
    String getCountry() { return sys.getCountry(); }
    long getSunrise() { return sys.getSunrise(); }
    long getSunset() { return sys.getSunset(); }

}