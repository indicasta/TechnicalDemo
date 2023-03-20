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
           put(Size.SMALL,((Long)sizes.get("S")).intValue());
            put(Size.MEDIUM,((Long)sizes.get("M")).intValue());
            put(Size.LARGE,((Long)sizes.get("L")).intValue());
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

       public Map<TShirt,Float> sortValue(Hashtable<TShirt,Float> globalScore){

       //Transfer as List and sort it
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
   
    public Float[] scoreWeights (@RequestBody JSONObject body) 
    {
        //read a json object to get weight for each score function
        Float getScoreSalesWeight  = ((Double)body.get("getScoreSales")).floatValue();
        Float getScoreStockRatioWeight  = ((Double)body.get("getScoreStockRatio")).floatValue();
        Float[] result = new Float[] {getScoreSalesWeight, getScoreStockRatioWeight};
        return result;
    }
    
}