package com.slashmobility.demo.model;
import java.util.*;
import lombok.*;

@Data
@AllArgsConstructor
public class Store {
    //A store is a list of stocks by id.
    List<Stock> stock;
}
