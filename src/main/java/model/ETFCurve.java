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
    private Double standardDeviation;

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

    public Double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(Double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    @Override
    public int compareTo(ETFCurve curve) {
        if(this.gradient > curve.getGradient())
            return -1;
        else if (this.gradient < curve.getGradient())
            return 1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return "CurveId: " + curveId + ", CurveData " + curveString;
    }
}
