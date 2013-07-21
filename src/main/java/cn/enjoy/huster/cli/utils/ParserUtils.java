package cn.enjoy.huster.cli.utils;

import java.util.ArrayList;
import java.util.List;

public class ParserUtils {
  
  public static String[] removeEmpty(String[] before) {
    List<String> after = new ArrayList<String>();
    for (String s : before) {
      if (!s.isEmpty()) {
        after.add(s);
      }
    }
    return after.toArray(new String[0]);    
  } 
  
  public static String appendEnv(String oldEnvStr, String append) {
    if (oldEnvStr.isEmpty() || oldEnvStr == null) {
      return append;
    }
    
    if (append.isEmpty() || append == null) {
      return oldEnvStr;
    }
    
    if (oldEnvStr.contains(":")) {
      return oldEnvStr + append;
    } else {
      return oldEnvStr + ":" + append;
    }
  }
  
  public static String convertArgsToCMD(String[] args) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < args.length-1; i++) {
      sb.append(args[i]).append(" ");;
    }
    sb.append(args[args.length-1]);
    return sb.toString();
  }
}
