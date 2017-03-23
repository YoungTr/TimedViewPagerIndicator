package com.youngtr.timedviewpagerindicator;

/**
 * Created by YoungTr on 17/3/23.
 */

public enum Strings {
    STRINGS1("STRING1"),
    STRINGS2("STRING2"),
    STRINGS3("STRING3");


    private String mStr;

    Strings(String str) {
        mStr = str;
    }

    public String getStr() {
        return mStr;
    }
}
