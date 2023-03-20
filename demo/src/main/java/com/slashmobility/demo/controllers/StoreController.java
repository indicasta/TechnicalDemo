package com.slashmobility.demo.controllers;

import com.slashmobility.demo.model.ScoreFunctions;
import com.slashmobility.demo.model.Size;
import com.slashmobility.demo.model.Stock;
import com.slashmobility.demo.model.Store;
import com.slashmobility.demo.model.TShirt;
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

/**
 * @author IndiCasta🧿
 */

@RestController
public class StoreController
{

   // Creating an array of TShirt objects.
    ArrayList<TShirt> tshirts= new ArrayList<>(List.of(
      new TShirt(1, "V-NECK BASIC SHIRT"),
      new TShirt(2, "CONTRASTING FABRIC-SHIRT"),
      new TShirt(3, "RAISED PRINT T-SHIRT"),
      new TShirt(4, "PLEATED T-SHIRT"),
      new TShirt(5, "CONTRASTING LACE T-SHIRT"),
      new TShirt(6, "SLOGAN T-SHIRT")
    ));
    
    /** 
     * @return List<TShirt>
     */
    @GetMapping("api/tshirts")
      public List<TShirt> all()
    {
        return tshirts;
    }
    
  /**
   * If the id of the tshirt in the array matches the id passed in, return the tshirt, otherwise return
   * null.
   * 
   * @param id The id of the tshirt you want to get
   * @return A TShirt object
   */
  @GetMapping("api/tshirts/{id}")
    TShirt getById(@PathVariable Integer id) 
    {
      for (TShirt ts : tshirts) {
        if(ts.getId().equals(id))
              return ts;
        }
      return null;
    }

    /** 
     * @return List<Stock>
     */
    @GetMapping("api/store")
      public List<Stock> store()
    {
        return getStock();
    }
    
    /** 
     * @return List<Stock> from json file
     */
    /**
     * It reads the json file to obtain json objects one by one and convert them to stock objects and
     * finally return all in a List
     * 
     * @return A list of Stock objects.
     */
    private static List<Stock> getStock() 
    {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("src/main/java/com/slashmobility/demo/model/store.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray tshirtStock = (JSONArray) jsonObject.get("store");
            Iterator<?> iterator = tshirtStock.iterator();
            ArrayList<Stock> store = new ArrayList<Stock>();
            while (iterator.hasNext()) {
                JSONObject e = (JSONObject) iterator.next();
                store.add(jsonObj2Stock(e));
            }
            return store;
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
    * It takes a JSONObject and returns a Stock object
    * 
    * @param e JSONObject
    * @return A Stock
    */
    private static Stock jsonObj2Stock(JSONObject e)
     {
        // getting fields
        Integer id = ((Long)e.get("id")).intValue();
        Integer sales = ((Long) e.get("sales")).intValue();
        JSONObject sizes =(JSONObject) e.get("sizes");

        Hashtable<Size,Integer> inventory= new Hashtable<Size,Integer>(){{
            put(Size.SMALL,((Long)sizes.get("S")).intValue());
            put(Size.MEDIUM,((Long)sizes.get("M")).intValue());
            put(Size.LARGE,((Long)sizes.get("L")).intValue());
        }};
        Stock st= new Stock(id, sales, inventory);
        return st;
    }
   
    /** 
     * @param id of TShirt
     * @return Float containing the sales of specified TShirt
     */
    @GetMapping("api/tshirt/score/sale/{id}")
      public static Float getScoreSales(@PathVariable Integer id)
      {
        Store st = new Store(getStock());
      
        for (Stock stock: st.getStock()){
          if(stock.getIdTShirt().equals(id))
                return stock.getSales().floatValue();
          }
        return null;
      };

    
    /** 
     * @param id of TShirt
     * @return float containing the result of adding all sizes available for this TShirt over total amount of sizes in store.
     */
   // A method that returns the ratio of the sizes of a tshirt over the total amount of sizes in the
   // store.
    @GetMapping("api/tshirt/score/stock-ratio/{id}")
    public static float getScoreStockRatio (@PathVariable Integer id)
    {
        Store st = new Store(getStock());
        Integer sumSizes, total;
        sumSizes=total=0;
        for (Stock stock: st.getStock()) {
          total += stock.getInventory().values().stream().mapToInt(Integer::intValue).sum();
          if(stock.getIdTShirt().equals(id))
                sumSizes = stock.getInventory().values().stream().mapToInt(Integer::intValue).sum();
          }
        return (float)sumSizes/total;
    };
     
      /** 
       * @param body json with weights for score functions
       * @return Map<TShirt, Float> the ordered list of TShirts by global score in descendent mode 
       */
     /**
      * It takes a JSON object as input, and returns a hash structure where keys are TShirts and values
      * are corresponding total score. 
      * 
      * The JSON object is a list of weights for each score function. 
      * 
      * The score functions are: 
      * 
      * - SCORE_SALES: the number of sales of a TShirt
      * - SCORE_RATIO_STOCK: the ratio of the stock of a TShirt to the total stock of all TShirts
      * 
      * The weights are used to calculate the total score of a TShirt. 
      * 
      * The total score of a TShirt is the sum of the scores of each score function multiplied by the
      * corresponding weight. 
      * 
      * The weights are normalized so that the sum of all weights is 1. 
      * 
      * The function returns a hash structure where keys are TShirts and values are corresponding total
      * score. 
      * 
      * The hash structure is sorted by value.
      * 
      * @param body a JSON object containing the weights of the score functions.
      * @return A map of TShirt and Float.
      */
      @PostMapping("api/tshirt/sort")
      public Map<TShirt,Float> globalScore(@RequestBody JSONObject body) 
      {
        //return a hash structure where keys are TShirts and values are corresponding total score. 
        ScoreFunctions[] scoreFunctions= new ScoreFunctions [] {ScoreFunctions.SCORE_SALES, ScoreFunctions.SCORE_RATIO_STOCK};
        Store st = new Store(getStock());
        Hashtable<TShirt,Float> globalScore = new Hashtable<TShirt,Float>(tshirts.size());
        for (Stock i : st.getStock()) {
          globalScore.put(getById(i.getIdTShirt()) ,getGlobalTshirtScore(i.getIdTShirt(),scoreFunctions,scoreWeights(body)));
        }
        return sortValue(globalScore);
      }

      /**
       * > This function takes an id, an array of functions, and an array of weights, and returns the
       * weighted average of the results of the functions
       * 
       * @param id The id of the t-shirt
       * @param scoreFunctions An array of functions that return a score for a given id.
       * @param scoreWeights The weights of each score function.
       * @return The score of the tshirt.
       */
      private float getGlobalTshirtScore(Integer id, ScoreFunctions[] scoreFunctions, Float[] scoreWeights)
      {
        float score = 0;
        float sumScoreWeights = 0;
        for (int i = 0; i < scoreFunctions.length; i++) {
          score += scoreFunctions[i].call(id) * scoreWeights[i];
          sumScoreWeights += scoreWeights[i];
        }
        return (score / sumScoreWeights);
      }

       /**
       * // Java
       * public Map<TShirt,Float> sortValue(Hashtable<TShirt,Float> globalScore)
       * 
       * @param globalScore Hashtable containing the global score of each TShirt.
       * @return A Map of TShirt and Float.
       */
       public Map<TShirt,Float> sortValue(Hashtable<TShirt,Float> globalScore)
       {
      //Sorting the Hashtable by values containing he global score of the TSHirt as values.
       ArrayList<Map.Entry<TShirt,Float>> score= new ArrayList<Map.Entry<TShirt,Float>> (globalScore.entrySet());
       Collections.sort(score, new Comparator<Map.Entry<TShirt, Float>>(){

         public int compare(Map.Entry<TShirt, Float> o1, Map.Entry<TShirt, Float> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }});

        Map<TShirt, Float> mapSortedByValues = new LinkedHashMap<TShirt, Float>();
        //put all sorted entries in LinkedHashMap
        for( Map.Entry<TShirt, Float> entry : score ){
            mapSortedByValues.put(entry.getKey(), entry.getValue());
        }
        return mapSortedByValues;
       }
   
    /**
      * This function takes in a JSON object, reads the values of the two keys, and returns an array of
      * floats
      * 
      * @param body the JSON object that contains the weight for each score function
      * @return A JSON object with two keys, getScoreSales and getScoreStockRatio, and their
      * corresponding values.
      */
      public Float[] scoreWeights (@RequestBody JSONObject body) 
      {
          //read a json object to get weight for each score function
          Float getScoreSalesWeight  = ((Double)body.get("getScoreSales")).floatValue();
          Float getScoreStockRatioWeight  = ((Double)body.get("getScoreStockRatio")).floatValue();
          Float[] result = new Float[] {getScoreSalesWeight, getScoreStockRatioWeight};
          return result;
      }
    
}