package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ETFPoint {
    @JsonProperty("Price")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double price;
    @JsonProperty("ClosingDate")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate closingDate;

    public ETFPoint(@JsonProperty("Price") Double price, @JsonProperty("ClosingDate") LocalDate closingDate) {
        this.price = price;
        this.closingDate = closingDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

//    @Override
//    public String toString() {
//        return "Price: " + price + ", ClosingDate: " + closingDate;
//    }
}
