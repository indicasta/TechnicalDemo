package com.slashmobility.demo.model;

import java.util.function.Function;

import com.slashmobility.demo.controllers.StoreController;

public enum ScoreFunctions {
    SCORE_SALES(StoreController::getScoreSales),
    SCORE_RATIO_STOCK(StoreController::getScoreSales);

    Function<Integer,Float> function;

    private ScoreFunctions(Function<Integer, Float> function) {
        this.function = function;
    }

    public Float call(Integer input) {
        return this.function.apply(input);
    }
}