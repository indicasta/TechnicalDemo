package com.slashmobility.demo.controllers;


import com.slashmobility.demo.model.ScoreFunctions;
import com.slashmobility.demo.model.Size;
import com.slashmobility.demo.model.Stock;
import com.slashmobility.demo.model.Store;

import java.io.FileReader;
import java.util.*;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StoreController {

    @GetMapping("api/store")
      public List<Stock> store()
    {
        return getStock();
    }
    private static List<Stock> getStock() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("src/main/java/com/slashmobility/demo/model/store.json"));

            // A JSON object. Key value pairs are unordered. JSONObject supports
            // java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;

            // A JSON array. JSONObject supports java.util.List interface.
            JSONArray tshirtStock = (JSONArray) jsonObject.get("store");

            // An iterator over a collection. Iterator takes the place of Enumeration in the
            // Java Collections Framework.
            // Iterators differ from enumerations in two ways:
            // 1. Iterators allow the caller to remove elements from the underlying
            // collection during the iteration with well-defined semantics.
            // 2. Method names have been improved.
            Iterator<?> iterator = tshirtStock.iterator();
            ArrayList<Stock> store = new ArrayList<Stock>();
            while (iterator.hasNext()) {
                JSONObject e = (JSONObject) iterator.next();
                store.add(jsonObj2Stock(e));
            }
            return store;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Stock jsonObj2Stock(JSONObject e){

        // getting fields
        Integer id = ((Long)e.get("id")).intValue();
        Integer sales = ((Long) e.get("sales")).intValue();
        JSONObject sizes =(JSONObject) e.get("sizes");

        Hashtable<Size,Integer> inventory= new Hashtable<Size,Integer>(){{
           put(Size.SMALL,((Long)sizes.get("SMALL")).intValue());
            put(Size.MEDIUM,((Long)sizes.get("MEDIUM")).intValue());
            put(Size.LARGE,((Long)sizes.get("LARGE")).intValue());
        }};
        Stock st= new Stock(id, sales, inventory);
     

        return st;
    }

    @GetMapping("api/tshirt/score-sale/{id}")
      public static Float getScoreSales(@PathVariable Integer id)
      {
        Store st = new Store(getStock());
      
        for (Stock i : st.getStock()) {
          if(i.getIdTShirt().equals(id))
                return i.getSales().floatValue();
          }
        return null;
      };

    @GetMapping("api/tshirt/score-stock-ratio/{id}")
    public static float getScoreStockRatio (@PathVariable Integer id)
    {
        Store st = new Store(getStock());
        Integer sumSizes, total;
        sumSizes=total=0;
        for (Stock i : st.getStock()) {
          total += i.getInventory().values().stream().mapToInt(Integer::intValue).sum();
          if(i.getIdTShirt().equals(id))
                sumSizes = i.getInventory().values().stream().mapToInt(Integer::intValue).sum();
          }
        return (float)sumSizes/total;
    };

      @GetMapping("api/tshirt/global-score/{id}")
      public float globalScore(@PathVariable Integer id){
        ScoreFunctions[] scoreFunctions= new ScoreFunctions [] {ScoreFunctions.SCORE_SALES, ScoreFunctions.SCORE_RATIO_STOCK};
       return getGlobalTshirtScore(id,scoreFunctions,new Float [] {0.1F, 0.9F});
      }

  
     private float getGlobalTshirtScore(Integer id, ScoreFunctions[] scoreFunctions, Float[] scoreWeights)
      {
        float score = 0;
        float sumScoreWeights =0;
        for (int i = 0; i < scoreFunctions.length; i++) {
          score += scoreFunctions[i].call(id) * scoreWeights[i];
          sumScoreWeights += scoreWeights[i];
        }
        return (score / sumScoreWeights);
      };




    @PostMapping("api/store/sort")
    public float[] scoreWeights (@RequestBody JSONObject body) 
    {
        float getScoreSalesWeight  = (float) body.get("getScoreSales");
        float getScoreStockRatioWeight  = (float) body.get("getScoreStockRatio");
        float[] result = new float[] {getScoreSalesWeight, getScoreStockRatioWeight};
        return result;
    }
    
}