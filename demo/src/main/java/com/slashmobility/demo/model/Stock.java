package com.slashmobility.demo.model;
import java.util.*;
import lombok.*;


/**
 * @author IndiCasta🧿
 */

@Data
@AllArgsConstructor
public class Stock 
{
  // A Stock is the amount of sales and current inventory by product.
  private Integer idTShirt;
  private Integer sales;
  private Hashtable<Size,Integer> inventory;
}
