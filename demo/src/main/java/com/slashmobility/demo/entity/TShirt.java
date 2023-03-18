package com.slashmobility.demo.entity;

import java.util.Objects;
import lombok.*;

public class TShirt {
  
  private Integer id;
  private String name;
  
  
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TShirt))
      return false;
    TShirt ts = (TShirt) o;
    return Objects.equals(this.id, ts.id) && Objects.equals(this.name, ts.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.name);
  }

  @Override
  public String toString() {
    return "TShirt{" + "id=" + this.id + ", name='" + this.name +  '}';
  }
}
