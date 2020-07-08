package com.example.converter.Models;

public class Exchange {
    private int currencyInput;
    private int currencyOutput;  //коды валют
    private double inputSum;
    private double outputSum;
    private long dateOfRate;
    private long dateCreatedByUser;

    public Exchange(int currencyInput, int currencyOutput, double inputSum, double outputSum,
                    long dateOfRate, long dateCreatedByUser) {
        this.currencyInput = currencyInput;
        this.currencyOutput = currencyOutput;
        this.inputSum = inputSum;
        this.outputSum = outputSum;
        this.dateOfRate = dateOfRate;
        this.dateCreatedByUser = dateCreatedByUser;
    }

    public int getCurrencyInput() {
        return currencyInput;
    }

    public int getCurrencyOutput() {
        return currencyOutput;
    }

    public double getInputSum() {
        return inputSum;
    }

    public double getOutputSum() {
        return outputSum;
    }

    public long getDateOfRate() {
        return dateOfRate;
    }

    public long getDateCreatedByUser() {
        return dateCreatedByUser;
    }

    public static int getIntCodeByString(String code){
        if (code.equals("RUB")) return 1;
        else if(code.equals("USD")) return 2;
        else if(code.equals("EUR")) return 3;
        else if(code.equals("JPY")) return 4;
        else return 0;
    }
}
