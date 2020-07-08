package com.example.converter.Models;

/**
 * Курсы валют к рублю
 */

public class Rate {
    private double EUR;
    private double USD;
    private double JPY100;  //100 йен

    public Rate() {
    }

    public Rate(double EUR, double USD, double JPY100) {
        this.EUR = EUR;
        this.USD = USD;
        this.JPY100 = JPY100;
    }

    public double getEUR() {
        return EUR;
    }

    public double getUSD() {
        return USD;
    }

    public double getJPY100() {
        return JPY100;
    }

    public void setEUR(double EUR) {
        this.EUR = EUR;
    }

    public void setUSD(double USD) {
        this.USD = USD;
    }

    public void setJPY100(double JPY100) {
        this.JPY100 = JPY100;
    }

    public void setCurrency(int code, double sum) {
        switch (code) {
            case 2:
                this.USD = sum;
                break;
            case 3:
                this.EUR = sum;
                break;
            case 4:
                this.JPY100 = sum;
        }
    }

    public double getCostByCode(String code) {
        if ("USD".equals(code)) {
            return this.getUSD();
        } else if ("EUR".equals(code)) {
            return this.getEUR();
        } else if ("JPY".equals(code)) {
            return this.getJPY100()/100;
        }
        return 1;
    }
}
