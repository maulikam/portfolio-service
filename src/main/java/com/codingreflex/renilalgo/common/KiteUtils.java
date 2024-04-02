package com.codingreflex.renilalgo.common;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class KiteUtils {

   public static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");

}
