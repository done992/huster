package cn.enjoy.huster.cli.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MpirunParserImpl implements ParamParser{
  private static final Log LOG = LogFactory.getLog(MpirunParserImpl.class);

  public String[] parse(String[] args, ParamBuilder builder)
      throws IOException {
    List<String> out = new ArrayList<String>();
    
    for (String s : args) {
      if (s.equals("--prefix")) {
        LOG.warn("we found you used --prefix in argument, if so, " + 
            "we highly recommmend you *NOT* use this option, " +  
            "we will manage open-mpi binaries ourself.");
      }
      out.add(s);
    }
    
    return out.toArray(new String[0]);
  }

}
