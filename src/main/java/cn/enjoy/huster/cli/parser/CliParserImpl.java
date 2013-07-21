package cn.enjoy.huster.cli.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.enjoy.huster.cli.utils.ParserUtils;



public class CliParserImpl implements ParamParser {
  private static final Log LOG = LogFactory.getLog(CliParserImpl.class);
  
  public String[] parse(String[] args, ParamBuilder builder) throws IOException {
    List<String> out = new ArrayList<String>();
    out.add(args[0]);
    for (int idx = 0; idx < args.length; idx++) {
      if (args[idx].equals("--huster-verbose")) {
        builder.ifVerbose = true;
      } else if (args[idx].endsWith("--huster-valgrind")) {
        builder.ifValgrind = true;
      } else if (args[idx].equals("--add-file") || args[idx].equals("--add-archive")) {
        boolean ifFile = args[idx].equals("--add-file");
        idx++;
        if (idx >= args.length) {
          throw new IOException("failed to parse --add-file or --add-archieve");
        }
        
        String fileName = args[idx];
        if (ifFile) {
          builder.addFileList.add(fileName);
        } else {
          builder.addArchiveList.add(fileName);
        }
      } else if (args[idx].equals("--add-env")) {
        //-add-env key=value
        idx++;
        if (idx >= args.length) {
          throw new IOException ("failed to parse --add-env");
        }
        
        String tmp_env =  args[idx];
        if (!tmp_env.contains("=")) {
          throw new IOException("-add-env should be in format KEY=VALUE, but now is: " + tmp_env);
        }
        
        String key = tmp_env.substring(0, tmp_env.indexOf('='));
        String value = tmp_env.substring(tmp_env.indexOf('=' + 1));
        if (!builder.userEnvMap.containsKey(key)) {
          //just insert it
          builder.userEnvMap.put(key, value);
        } else {
          String newValue = ParserUtils.appendEnv(builder.userEnvMap.get(key), value);
          builder.userEnvMap.put(key, newValue);
        }
      } else if (args[idx].equals("--huster-mem")) {
        idx++;
        if (idx >= args.length) {
          throw new IOException ("failed to parse --huster-mem");
        }
        
        int mem = Integer.valueOf(args[idx]);
        if (mem < 64) {
          mem = 64;
          LOG.warn("user specified --huster-mem #value < 64M, we will use 64M instead");
        }
        builder.memorySize = mem;
      } else if (args[idx].equals("--huster-cpu")) {
        idx++;
        if (idx >= args.length) {
          throw new IOException("failed to parse --huster-cpu");
        }
        
        int cpu = Integer.valueOf(args[idx]);
        if (cpu < 0) {
          cpu = 0;
          LOG.warn("user specified --huster-cpu #value < 0, we will use 0 instead");
        }
        builder.cpuNum = cpu;
      } else if (args[idx].equals("--huster-host")) {
        idx++;
        if (idx >= args.length) {
          throw new IOException("failed to parse --huster-host");
        }
        builder.hostExpr = args[idx];
      } else if (StringUtils.equals(args[idx], "--huster-hostfile")) {
        idx++;
        if (idx >= args.length) {
          throw new IOException("failed to parse --huster-hostfile");
        }
        String hostfileName = args[idx];
        BufferedReader br = null;
        try {
          br = new BufferedReader(new FileReader(hostfileName));
        } catch(FileNotFoundException e) {
          throw new IOException("failed to read --huster-hostfile " + hostfileName);
        }
        String line = null;
        String hostlist = "";
        while ((line = br.readLine()) != null) {
          if (line.startsWith("#") || line.trim().isEmpty()) {
            continue;
          }
          if (!hostlist.isEmpty()) {
            hostlist = hostlist + ",";
          }
          hostlist = hostlist + line;
        }
        br.close();
        builder.hostlist = hostlist;
      } else if (StringUtils.equals(args[idx], "--huster-mproc")) {
        idx++;
        if (idx >= args.length) {
          throw new IOException("failed to parse --huster-mproc");
        }
        builder.mproc = Integer.parseInt(args[idx]);
      } else if (StringUtils.equals(args[idx], "--huster-mnode")) {
        idx++;
        if (idx >= args.length) {
          throw new IOException("failed to parse --huster-mnode");
        }
        
        builder.mnode = Integer.parseInt(args[idx]);
      } else {
        out.add(args[idx]);
      }
      
    }
    
    return out.toArray(new String[0]);
  }
}
    
      
