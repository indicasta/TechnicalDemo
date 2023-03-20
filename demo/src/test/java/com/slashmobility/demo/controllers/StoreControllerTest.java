package com.slashmobility.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StoreControllerTest 
{
 
  @Test 
  void testGetScoreSales()
   {
    assertEquals((float)650, StoreController.getScoreSales(5));
    assertEquals((float)20, StoreController.getScoreSales(6));
    assertEquals((float)100, StoreController.getScoreSales(1));
  }

  @Test
  void testGetScoreStockRatio() 
  {
    assertEquals((float)(20+2+20)/190, StoreController.getScoreStockRatio(3));
    assertEquals((float)(25+30+10)/190, StoreController.getScoreStockRatio(4));
  }

  @Test
  void testGlobalScore() {

  }

  @Test
  void testScoreWeights() {

  }

  @Test
  void testSortValue() {

  }

  @Test
  void testStore() {

  }
}
