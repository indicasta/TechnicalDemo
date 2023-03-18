package com.slashmobility.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class tShirtsController {
  
   @GetMapping("/")
  // List<TShirt> all() {
  //   return store.findAll();
  // }
	public String hello(){
		return String.format("Hello Beauty!");
	}
}
