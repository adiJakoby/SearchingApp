package sample;

public class City {

    private String name;
    private String coin;
    private String population;
    private String currency;

    public City(String capital, String country, String code, Long population) {

    }


    public String getCoin() {
        return coin;
    }

    public String getPopulation() {
        return population;
    }

    public String getCurrency() {
        return currency;
    }

    public String getName() {
        return name;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
