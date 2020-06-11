package com.rabbitt.momobill.prefsManager;

import android.content.Context;
import android.content.SharedPreferences;

public class IncrementPref {
    public static final String PRODUCT = "PRODUCT";
    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String OPENING_VAL = "OPENING_VAL";



    public static final String CLIENT = "CLIENT";
    public static final String CLIENT_ID = "CLIENT_ID";

    SharedPreferences product_shrp, client_shrp;
    SharedPreferences.Editor product_edit, client_edit;

    public IncrementPref(Context context) {
         product_shrp = context.getSharedPreferences(PRODUCT, Context.MODE_PRIVATE);
         product_edit = product_shrp.edit();

        client_shrp = context.getSharedPreferences(CLIENT, Context.MODE_PRIVATE);
        client_edit = product_shrp.edit();
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

    public void setClientID(String id)
    {
        product_edit.putString(CLIENT_ID, id);
        product_edit.commit();
    }

    public String getClientId()
    {
        return product_shrp.getString(CLIENT_ID, "0");
    }

    public void setOpeningVal(String id)
    {
        product_edit.putString(OPENING_VAL, id);
        product_edit.commit();
    }

    public String getOpeningVal()
    {
        return product_shrp.getString(OPENING_VAL, "0");
    }


}
