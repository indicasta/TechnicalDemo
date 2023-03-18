package com.slashmobility.demo.model;
import java.util.*;
import lombok.*;

@Data
@AllArgsConstructor
public class Stock {
  private Integer idTShirt;
  private Integer sales;
  private Hashtable<Size,Integer> inventory;
}
