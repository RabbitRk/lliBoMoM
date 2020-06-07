package com.rabbitt.momobill.prefsManager;

import android.content.Context;
import android.content.SharedPreferences;

public class IncrementPref {
    public static final String PRODUCT = "PRODUCT";
    public static final String PRODUCT_ID = "PRODUCT_ID";

    SharedPreferences product_shrp;
    SharedPreferences.Editor product_edit;

    public IncrementPref(Context context) {
         product_shrp = context.getSharedPreferences(PRODUCT, Context.MODE_PRIVATE);
         product_edit = product_shrp.edit();
    }

    public void setProductID(String id)
    {
        product_edit.putString(PRODUCT_ID, id);
        product_edit.commit();
    }

    public String getProductId()
    {
        return product_shrp.getString(PRODUCT_ID, "0");
    }


}
