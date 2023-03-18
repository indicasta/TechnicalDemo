package com.slashmobility.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.slashmobility.demo.model.TShirt;

@RestController
public class tShirtsController {

  ArrayList<TShirt> tshirts= new ArrayList<>(List.of(
    new TShirt(1, "V-NECK BASIC SHIRT"),
    new TShirt(2, "CONTRASTING FABRIC-SHIRT"),
    new TShirt( 3, "RAISED PRINT T-SHIRT"),
    new TShirt(4, "PLEATED T-SHIRT"),
    new TShirt(  5, "CONTRASTING LACE T-SHIRT"),
    new TShirt( 6, "SLOGAN T-SHIRT")
  ));
 @GetMapping("api/tshirts")
  public List<TShirt> all()
 {
    return tshirts;
 }
 @GetMapping("api/tshirts/{id}")
  TShirt getById(@PathVariable Integer id) {
    for (TShirt ts : tshirts) {
      if(ts.getId().equals(id))
            return ts;
       }
    return null;
  }
@PostMapping("api/tshirts")
public void addProduct(@RequestBody TShirt t) {
    tshirts.add(t);
}

  @PostMapping("api/hello")
  public String sayHello (@RequestBody JSONObject body) 
  {
       
      String name = (String) body.get("name");
      return "Hola " + name;
  }

}