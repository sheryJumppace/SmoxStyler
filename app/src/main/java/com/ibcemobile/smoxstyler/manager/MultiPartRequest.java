package com.ibcemobile.smoxstyler.manager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kris on 6/9/2017.
 */

public class MultiPartRequest extends Request<String> {
    private static final String FILE_PART_NAME = "uploadFile";
    private final Listener<String> mListener;
    //private final MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final ArrayList<File> mImageFile;
    private final ArrayList<String> mFileName;
    private final Map<String, String> mParams;
    protected Map<String, String> headers;

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public MultiPartRequest(int method, String url, ArrayList<File> file, ArrayList<String> fileName, Map<String, String> params, Listener<String> listener,
                            ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mImageFile = file;
        mFileName = fileName;
        mParams = params;
        //   buildMultipartEntity();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        headers.put("Accept", "application/json");

        return headers;
    }

    @Override
    public String getBodyContentType() {
        //  String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return "contentTypeHeader";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        /*
        try {
           // mBuilder.build().writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }
        */
        return bos.toByteArray();
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    /*  private void buildMultipartEntity(){
          //mBuilder.addBinaryBody(FILE_PART_NAME, mImageFile, ContentType.create("image/jpeg"), mFileName);
          for (int i = 0; i < mImageFile.size(); i ++) {
              //mBuilder.addBinaryBody(String.format("%s[%d]", FILE_PART_NAME, i), mImageFile.get(i), ContentType.create("image/jpeg"), mFileName.get(i));
              ContentType contentType = ContentType.create(getMimeType(mFileName.get(i)));
              mBuilder.addBinaryBody(String.format("f_%d", i), mImageFile.get(i), contentType, mFileName.get(i));
          }
          mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
          mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
          for(Map.Entry<String, String> entry : mParams.entrySet()) {
              String key = entry.getKey();
              String value = entry.getValue();
              mBuilder.addPart(key, new StringBody(value, ContentType.TEXT_PLAIN));
          }
      }*/
    private String getMimeType(String fileName) {
        String[] filenameArray = fileName.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];

        extension = extension.toLowerCase();
        String mime = "image/jpg";
        if (extension.equals("flv")) {
            mime = "video/x-flv";
        } else if (extension.equals("mp4")) {
            mime = "video/mp4";
        } else if (extension.equals("m3u8")) {
            mime = "application/x-mpegURL";
        } else if (extension.equals("ts")) {
            mime = "video/MP2T";
        } else if (extension.equals("3gp")) {
            mime = "video/3gpp";
        } else if (extension.equals("mov")) {
            mime = "video/quicktime";
        } else if (extension.equals("avi")) {
            mime = "video/x-msvideo";
        } else if (extension.equals("wmv")) {
            mime = "video/x-ms-wmv";
        } else {
            mime = "image/jpg";
        }
        return mime;
    }
}
