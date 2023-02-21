package com.ibcemobile.smoxstyler.utils;

import android.content.Context;
import android.net.Uri;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

public class AmazonUtil {

    private static final int MAX_ERROR_RETRY = 2;
    private static final int CONNECTION_TIMEOUT_IN_MS = 50000;
    private static final int SOCKET_TIMEOUT_IN_MS = 50000;
    private static AmazonS3Client sS3Client;
    private static CognitoCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    // public static final String COGNITO_POOL_ID = "us-east-2:b2c40521-ee1b-4d7f-a47d-2aa5c55fee1b";
     public static final String COGNITO_POOL_ID = "us-east-2:b4593066-4a42-4b01-9dbc-5f60cfb591d6";

    private static CognitoCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCredentialsProvider(COGNITO_POOL_ID, Regions.US_EAST_2);
        }
        return sCredProvider;
    }

    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {

            ClientConfiguration clientConfiguration = new ClientConfiguration()
                    .withMaxErrorRetry(MAX_ERROR_RETRY)
                    .withConnectionTimeout(CONNECTION_TIMEOUT_IN_MS)
                    .withSocketTimeout(SOCKET_TIMEOUT_IN_MS);

            sS3Client = new AmazonS3Client(getCredProvider(context), clientConfiguration);
        }
        return sS3Client;
    }

    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context),
                    context.getApplicationContext());

        }

        return sTransferUtility;
    }

    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[]{
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0; ; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

    public static void fillMap(Map<String, Object> map, TransferObserver observer, boolean isChecked) {
        int progress = (int) ((double) observer.getBytesTransferred() * 100 / observer
                .getBytesTotal());
        map.put("id", observer.getId());
        map.put("checked", isChecked);
        map.put("fileName", observer.getAbsoluteFilePath());
        map.put("progress", progress);
        map.put("bytes",
                getBytesString(observer.getBytesTransferred()) + "/"
                        + getBytesString(observer.getBytesTotal()));
        map.put("state", observer.getState());
        map.put("percentage", progress + "%");
    }
}