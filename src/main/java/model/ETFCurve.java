package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;

public class ETFCurve {
    @JsonProperty("curveId")
    private String curveId;
    @JsonProperty("curveData")
    private ArrayList<ETFPoint> curveData;

    private String curveString;

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

    @Override
    public String toString() {
        return "CurveId: " + curveId + ", CurveData " + curveString;
    }
}
