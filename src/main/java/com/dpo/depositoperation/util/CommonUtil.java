package com.dpo.depositoperation.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonUtil {
    public static String convertStackTraceToMessage(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
