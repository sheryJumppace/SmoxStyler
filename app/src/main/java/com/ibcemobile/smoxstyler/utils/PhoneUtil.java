package com.ibcemobile.smoxstyler.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.ibcemobile.smoxstyler.R;

import java.util.HashMap;

public class PhoneUtil {

    public static String getCountryCode(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            return getCountryCodeFromMap(manager.getSimCountryIso().toUpperCase());
        } else {
            return getCountryCodeFromMap(context.getResources().getConfiguration().locale.getCountry());
        }

    }

    public static String getCountryCodeFromMap(String countryCode) {
        HashMap<String, String> countryCodeMap = new HashMap<>();
        countryCodeMap.put("IN", "+91");
        countryCodeMap.put("TJ", "+992");
        countryCodeMap.put("TZ", "+255");
        countryCodeMap.put("TH", "+66");
        countryCodeMap.put("TG", "+228");
        countryCodeMap.put("TK", "+690");
        countryCodeMap.put("TO", "+676");
        countryCodeMap.put("TN", "+216");
        countryCodeMap.put("TR", "+90");
        countryCodeMap.put("TM", "+993");
        countryCodeMap.put("TV", "+688");
        countryCodeMap.put("AE", "+971");
        countryCodeMap.put("UG", "+256");
        countryCodeMap.put("GB", "+44");
        countryCodeMap.put("FR", "+33");
        countryCodeMap.put("UA", "+380");
        countryCodeMap.put("UY", "+598");
        countryCodeMap.put("US", "+1");
        countryCodeMap.put("UZ", "+998");
        countryCodeMap.put("VU", "+678");
        countryCodeMap.put("VA", "+39");
        countryCodeMap.put("VE", "+58");
        countryCodeMap.put("VN", "+84");
        countryCodeMap.put("WF", "+681");
        countryCodeMap.put("YE", "+967");
        countryCodeMap.put("ZM", "+260");
        countryCodeMap.put("ZW", "+263");
        countryCodeMap.put( "MY","+60");
        countryCodeMap.put("PK","+92");
        return countryCodeMap.get(countryCode);
    }

    public static String GetCountryZipCode(Context context){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        if (manager!=null){
            CountryID= manager.getSimCountryIso().toUpperCase();
        }
        if (CountryID.equals("")){
            CountryID=context.getResources().getConfiguration().locale.getCountry();
        }

        String[] rl=context.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }
}