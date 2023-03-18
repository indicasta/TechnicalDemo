package com.slashmobility.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.slashmobility.demo.entity.TShirt;

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
 @GetMapping("/tshirts")
  public List<TShirt> all()
 {
    return tshirts;
 }


}
