/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Constants {

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    public static final String pdburl ="https://reward-converter-app.firebaseio.com";
    public static final String dbsrno = "convsr";
    public static final String dburl ="https://reward-converter-app-8ac90.firebaseio.com";
    public static final String dbuser = "users";
    public static final String dbucountry = "country";
    public static final String dbuemail = "email";
    public static final String dbuname = "name";

    public static final String dbupaymethod = "pay";
    public static final String dbupaydetails = "paydetails";
    public static final String dbcrnt = "acurrent";
    public static final String dbamt = "amtpaid";
    public static final String dbdatec = "date";
    public static final String dbmainsc = "mainstatus";
    public static final String dbprimary = "primarystatus";
    public static final String dbsecondary = "secondarystatus";
    public static final String dbtrid = "trid";
    public static final String dbuserlist = "list";

 //https://reward-converter-app-8ac90.firebaseio.com/
 public static final String PREFPURCH = "sp_purch";
    public static final String tridPREFPURCH = "tridsp";
    public static final String datePREFPURCH = "datesp";
    public static final String itemPREFPURCH = "paidsp";

    public static final String Urlpaymentdets = "https://docs.google.com/forms/d/e/1FAIpQLScGTXzWv-6KCfHGcCJgYCvbsIF6jokdAsViKt6kdG7zpvVbUA/formResponse";
    public static final String Urlrequestdets ="https://docs.google.com/forms/d/1dzJutz1iyzr3feSeOi7hGJIZYBTXtuT3d10KksDPp_U/formResponse";


    public static  boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;

    }

    public static void showSnack(View root, String snacktitle){
        Snackbar snackbar = Snackbar.make(root, snacktitle, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static class DialogUtils{
        public static ProgressDialog showprgdialog(Context context){
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
            return progressDialog;
        }
    }

}
