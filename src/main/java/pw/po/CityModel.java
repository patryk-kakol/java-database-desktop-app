package pw.po;

public class CityModel {

    private String id;
    private String name;
    private String countryCode;
    private String district;
    private String population;


    public CityModel(String id, String name, String countryCode, String district, String population) {
        this.id = id;
        this.name = name;
        this.countryCode = countryCode;
        this.district = district;
        this.population = population;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getDistrict() {
        return district;
    }

    public String getPopulation() {
        return population;
    }
}
