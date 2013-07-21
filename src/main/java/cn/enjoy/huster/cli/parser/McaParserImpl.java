package cn.enjoy.huster.cli.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class McaParserImpl implements ParamParser {
  
  /**
   * a full mca param consists of three components, e.g., -mca ras yarn
   */
  public String[] parse(String[] args, ParamBuilder builder)
      throws IOException {
    List<String> out = new ArrayList<String>();
    // we will ignore the first one, because it should be "mpirun"
    out.add(args[0]);
    
    for (int i =  1; i < args.length; i++) {
      if (args[i].equals("-mca")) {
        i++;
        if (i >= args.length) {
          throw new IOException("invalid mca parameter");
        }
        String key = args[i];
        
        i++;
        if (i >= args.length) {
          throw new IOException("invalid mca parameter");
        }
        String value = args[i];
        
        builder.mcaParamMap.put(key, value);
      } else {
        // add it to output param
        out.add(args[i]);
      }
    }
    
    return out.toArray(new String[0]);
  }

}
