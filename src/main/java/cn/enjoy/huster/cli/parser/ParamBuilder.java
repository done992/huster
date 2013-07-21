package cn.enjoy.huster.cli.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;

import cn.enjoy.huster.cli.utils.ParserUtils;

public class ParamBuilder {
  private final static Log LOG = LogFactory.getLog(ParamBuilder.class);
  
  String[] outArgs;
  
  /* parameters for mca <K,V>*/
  Map<String, String> mcaParamMap = null;
  
  //parameters for ompi <K, V>
  Map<String, String> ompiParamMap = null;
  
  /* parameters for huster cli, like -debug, etc. */
  Map<String, String> cliParamMap = null;
  
  /* parameters for addFile/Archieve, etc. */
  List<String> addFileList = null;
  List<String> addArchiveList = null;
  
  /* parameters for user's application */
  List<String> userParamList = null;
  
  /* parameters for user's env */
  Map<String, String> userEnvMap = null;
  
  boolean ifMpiApp = false;
  boolean ifVerbose = false;
  // use valgrind to debug mpirun
  boolean ifValgrind = false;
  int memorySize = -1;
  int cpuNum = -1;
  
  String hostExpr = null;
  String hostlist = null;
  
  int mproc = -1;
  int mnode = -1;
  
  public ParamBuilder() {
    this.mcaParamMap = new HashMap<String, String>();
    this.ompiParamMap = new HashMap<String, String>();
    this.cliParamMap = new HashMap<String, String>();
    this.addFileList = new ArrayList<String>();
    this.addArchiveList = new ArrayList<String>();
    this.userParamList = new ArrayList<String>();
    this.userEnvMap = new HashMap<String, String>();
  }  
 
  
  
  private boolean checkIsMpiApp(String[] args) {
    if (args[0].endsWith("mpirun") || args[0].endsWith("orterun")) {
      return true;
    } else {
      return false;
    }
  }
  
  private void checkAndThrow(String[] args) throws IOException {
    if (!ifMpiApp) {
      LOG.error("you must call \"huster mpirun ...\", we don't support other application now!");
      throw new IOException("you must call \"huster mpirun ...\", we don't support other application now!");
    }
  }
  

  public String[] parse(String[] args) throws IOException {
    if (args == null || args.length == 0) {
      throw new IOException("input parameter is empty!");
    }
    
    this.ifMpiApp = checkIsMpiApp(args);
    
    if (!this.ifMpiApp) {
      LOG.warn("this is not a MPI application");
      this.outArgs = args;
      return args;
    }
    
    String[] curArgs = ParserUtils.removeEmpty(args);
    
    ParamParser parser = null;
    // parse mpirun args
    parser = new MpirunParserImpl();
    curArgs = parser.parse(curArgs, this);
    
    //parse mca args
    parser = new McaParserImpl();
    curArgs = parser.parse(curArgs, this);
    
    //parse cli parameters
    parser = new CliParserImpl();
    curArgs = parser.parse(curArgs, this);
    
    outArgs = curArgs;
    
    
    checkAndThrow(outArgs);
    
    return outArgs;
  }
  
  
  public List<String> getAddFileList() {
    return this.addFileList;
  }
  
  public List<String> getAddArchiveFileList() {
    return this.addArchiveList;
  }
  
  public Map<String, String> getUserEnvMap() {
    return this.userEnvMap;
  }
  
  public boolean isVerbose() {
    return this.ifVerbose;
  }
  
  public boolean isValgrind() {
    return this.isValgrind();
  }
  
  public void setMcaParam(String key, String value) {
    this.mcaParamMap.put(key, value);
  }
  
  public void unsetMcaParam(String key, String value) {
    if (this.mcaParamMap.containsKey(key)) {
      this.mcaParamMap.remove(key);
    }
  }
  
  public String[] getUserParam(ContainerLaunchContext ctx) {
    if (!ifMpiApp) {
      List<String> userParamList = new ArrayList<String>();
      for (String s : this.outArgs) {
        userParamList.add(s);
      }
      
      userParamList.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout");
      userParamList.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr");
      return userParamList.toArray(new String[0]);
    }
    
    // add huster one, it should be "mpirun"
    List<String> userParamList = new ArrayList<String>();
    userParamList.add("$JAVA_HOME/bin/java");
    
    /*
     * we need to specify JVM param for running AM, because we will do a "fork"
     * in AM, so we have to use at most 1/2 mem of total, but luckily, it should
     * be enough for us. :)
     */
    int xmx = 512;
    int xms = 16;
    userParamList.add(String.format("-Xmx%dM -Xms%dM", xmx, xms));
    
    // userParamList.add("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=\"8111\"");
    userParamList.add("-cp");
    
    
    if (ctx == null) {
      userParamList.add("huster-core.jar");
    } else {
      
      userParamList.add(ctx.getEnvironment().get("CLASSPATH"));
    }
    
    userParamList.add("cn.enjoy.huster.appmaster.HusterAppMaster");
    if (ifValgrind) {
      userParamList.add("valgrind");
      userParamList.add("--tool=memcheck");
      userParamList.add("--track-origins=yes");
      userParamList.add("--leak-check=full");
    }
    
    userParamList.add("mpirun");
    
    //add mca params to select modules
    this.mcaParamMap.put("ras", "yarn");
    this.mcaParamMap.put("plm", "yarn");
    this.mcaParamMap.put("odls", "yarn");
    
    //append mca parameters
    for (Entry<String, String> e : this.mcaParamMap.entrySet()) {
      userParamList.add("-mca");
      userParamList.add(e.getKey());
      userParamList.add(e.getValue());
    }
    
    //append other user's parameters
    for (String s : this.outArgs) {
      userParamList.add(s);
    }
  
   //append log output
    userParamList.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout");
    userParamList.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr");
    
    return userParamList.toArray(new String[0]);
  }
  
  
  public String getUserCliCMD(ContainerLaunchContext ctx) {
    String[] args = getUserParam(ctx);
    return ParserUtils.convertArgsToCMD(args);
  }

}

