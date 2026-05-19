package com.example.practic_14.Classes;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.example.practic_14.ICallbackResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.annotation.Documented;

public class SendCommon extends AsyncTask<Void, Void, Void> {
    public String Url = "", Code;
    public EditText TbEmail;
    ICallbackResponse CallbackResponse, CallbackError;
    public SendCommon(EditText tbEmail, ICallbackResponse callbackResponse, ICallbackResponse callbackError){
        this.TbEmail = tbEmail;
        this.CallbackResponse = callbackResponse;
        this.CallbackError = callbackError;
    }

    @Override
    protected Void doInBackground(Void... Voids){
        try {
            Document Response = Jsoup.connect(Url + "?Email=" + TbEmail.getText())
                    .ignoreContentType(true)
                    .get();
            Code = Response.text();
        }
        catch (IOException ex){
            Log.e("Errors", ex.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        if(Code == null)
            CallbackError.returner("Error");
        else
            CallbackResponse.returner(Code);
    }
}
