package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ETFCurve implements Comparable<ETFCurve> {
    @JsonProperty("curveId")
    private String curveId;
    @JsonProperty("curveData")
    private ArrayList<ETFPoint> curveData;
    private String curveString;
    private Double gradient;
    private Double variation;

    public ETFCurve(@JsonProperty("curveId") String curveId, @JsonProperty("curveData") ArrayList<ETFPoint> curveData, String curveString) {
        this.curveId = curveId;
        this.curveData = curveData;
        this.curveString = curveString;
    }

    public String getCurveId() {
        return curveId;
    }

    public void setCurveId(String curveId) {
        this.curveId = curveId;
    }

    public ArrayList<ETFPoint> getCurveData() {
        return curveData;
    }

    public void setCurveData(ArrayList<ETFPoint> curveData) {
        this.curveData = curveData;
    }

    public Double getGradient() {
        return gradient;
    }

    public void setGradient(Double gradient) {
        this.gradient = gradient;
    }

    public Double getVariation() {
        return variation;
    }

    public void setVariation(Double variation) {
        this.variation = variation;
    }

    @Override
    public int compareTo(ETFCurve curve) {
        if(this.gradient > curve.getGradient()) return -1;
        else if (this.gradient == curve.getGradient()) return 0;
        else return 1;
    }

    @Override
    public String toString() {
        return "CurveId: " + curveId + ", CurveData " + curveString;
    }
}
