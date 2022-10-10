package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ETFCurves {
    @JsonProperty("curves")
    private ArrayList<ETFCurve> curves = new ArrayList<>();

    public ETFCurves(@JsonProperty("curves") ArrayList<ETFCurve> curves) {
        this.curves = curves;
    }

    public ArrayList<ETFCurve> getCurves() {
        return curves;
    }

    public void setCurves(ArrayList<ETFCurve> curves) {
        this.curves = curves;
    }
}
