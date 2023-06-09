package com.slashmobility.demo.model;

import lombok.*;


/**
 * @author IndiCasta🧿
 */

@Data
@AllArgsConstructor
public class TShirt {
  
  private Integer id;
  private String name;
  
  
  @Override
  // Overriding the toString method of the Object class.
  public String toString() 
  {
    return "TShirt{" + "id=" + this.id + ", name='" + this.name +  '}';
  }
}
