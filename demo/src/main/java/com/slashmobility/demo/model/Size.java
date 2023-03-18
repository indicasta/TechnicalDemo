package com.slashmobility.demo.model;

public enum Size {
    SMALL, MEDIUM, LARGE;

    static public boolean isDefinedSize(String aName) {
        Size[] s = Size.values();
        for (Size size : s)
            if (size.toString().equals(aName))
                return true;
        return false;
    }

}