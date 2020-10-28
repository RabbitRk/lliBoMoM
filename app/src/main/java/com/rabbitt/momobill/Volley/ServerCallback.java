package com.rabbitt.momobill.Volley;

import org.json.JSONObject;

public interface ServerCallback {
    void onSuccess(String result);
    void onError(String result);
}
