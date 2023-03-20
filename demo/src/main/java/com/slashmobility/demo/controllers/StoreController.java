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

@RestController
public class StoreController {

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
   * @param id
   * @return TShirt
   */
  @GetMapping("api/tshirts/{id}")
    TShirt getById(@PathVariable Integer id) {
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
    private static List<Stock> getStock() {
      /*
      read the json file to obtain json objects one by one and convert them to stock objects and finally
       return all in a List.
      */
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /** 
     * @param e: each json object from file
     * @return Stock
     */
    private static Stock jsonObj2Stock(JSONObject e){

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

    
    /** 
     * @param id of TShirt
     * @return float containing the result of adding all sizes available for this TShirt over total amount of sizes in store.
     */
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

      
      /** 
       * @param body json with weights for score functions
       * @return Map<TShirt, Float> the ordered list of TShirts by score in descendent mode 
       */
      @PostMapping("api/tshirt/sort-by-global-score")
      public Map<TShirt,Float> globalScore(@RequestBody JSONObject body){
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
       * @param id
       * @param scoreFunctions
       * @param scoreWeights
       * @return float, the weighted average of score functions
       */
      private float getGlobalTshirtScore(Integer id, ScoreFunctions[] scoreFunctions, Float[] scoreWeights)
      {
        float score = 0;
        float sumScoreWeights =0;
        for (int i = 0; i < scoreFunctions.length; i++) {
          score += scoreFunctions[i].call(id) * scoreWeights[i];
          sumScoreWeights += scoreWeights[i];
        }
        return (score / sumScoreWeights);
      }

       
       /** 
        * @param globalScore
        * @return Map<TShirt, Float>
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
     * @param body json with weights for score funtions
     * @return Float[]
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