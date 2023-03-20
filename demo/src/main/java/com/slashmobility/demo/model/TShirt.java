package com.slashmobility.demo.model;

import lombok.*;

@Data
@AllArgsConstructor
public class TShirt {
  
  private Integer id;
  private String name;
  
  
  @Override
  public String toString() {
    return "TShirt{" + "id=" + this.id + ", name='" + this.name +  '}';
  }
}
