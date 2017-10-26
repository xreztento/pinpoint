package com.navercorp.pinpoint.web.util;

import org.apache.commons.codec.digest.DigestUtils;
public class CodecUtils {
    public static String getMd5Hex(String in){
        return DigestUtils.md5Hex(in);
    }
}
