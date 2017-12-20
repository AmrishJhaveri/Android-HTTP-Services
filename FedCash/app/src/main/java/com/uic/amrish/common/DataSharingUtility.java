package com.uic.amrish.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amrish on 01-Dec-17.
 */

public class DataSharingUtility {

    private static List<String> calls = new ArrayList<>();
    private static List<String> responses = new ArrayList<>();

    public static void addData(String requestCall, String response) {
        calls.add(requestCall);
        responses.add(response);
    }

    public static String[] getAllServiceCalls() {
        return calls.toArray(new String[calls.size()]);
    }

    public static String getServiceCallResponse(int index) {
        return responses.get(index);
    }

}
