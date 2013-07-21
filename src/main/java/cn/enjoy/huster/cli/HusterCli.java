package cn.enjoy.huster.cli;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.ClientRMProtocol;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.ipc.YarnRPC;

import cn.enjoy.huster.cli.parser.ParamBuilder;
import cn.enjoy.huster.common.HusterConfig;

public class HusterCli {
  private static final Log LOG = LogFactory.getLog(HusterCli.class);
  private final String DEFAULT_HUSTER_DIR_IN_HDFS = "huster";
  
  Configuration conf = null;
  RecordFactory recordFactory = null;
  ParamBuilder paramBuilder = null;
  
  ClientRMProtocol client = null;
  ApplicationId appId = null;
  
  //home/is-preinstall of ompi
  boolean preInstallOmpiBinary;
  String ompiHome;
  
  public HusterCli() {
    this.conf = new Configuration();
    conf.addResource("huster-site.xml");
    this.recordFactory = RecordFactoryProvider.getRecordFactory(null);
    this.paramBuilder = new ParamBuilder();
  }
  
  void createYarnRPC() {
    YarnRPC rpc = YarnRPC.create(this.conf);
    String rmAddressStr = conf.get(YarnConfiguration.RM_ADDRESS, YarnConfiguration.DEFAULT_RM_ADDRESS);
    InetSocketAddress rmAddress = NetUtils.createSocketAddr(rmAddressStr);
    LOG.info("rmAddressStr = " + rmAddressStr);
    this.client = (ClientRMProtocol) rpc.getProxy(ClientRMProtocol.class, rmAddress, conf);
  }
  
  public void init(String[] args) throws IOException {
    createYarnRPC();
    this.paramBuilder.parse(args);
    checkAndSetOmpiHome();
    
  }
  
  /**
   * this method will read from conf/cli, and get/set the final hamster home for
   * AppMaster(mpirun) and launched MPI processes, and will report error when sth.
   * occured.
   * @return hamster home in NM (for mpirun and launched processes)
   * @throws IOException 
   * 
   */
  void checkAndSetOmpiHome() throws IOException {
    this.preInstallOmpiBinary = conf.getBoolean(HusterConfig.OMPI_PREINSTALL_PROPERTY_KEY, false);
    if (preInstallOmpiBinary) {
      this.ompiHome = conf.get(HusterConfig.OMPI_HOME_PROPERTY_KEY);
      if (null == ompiHome) {
        LOG.error("user specified huster is pre-installed in NM, but not set huster home");
        throw new IOException("user specified huster is pre-installed in NM, but not set huster home");
      } else {
        String tarballPath = conf.get(HusterConfig.OMPI_LOCAL_TARBALL_PATH_KEY);
        if (null == tarballPath) {
          LOG.error("user specified huster is not preInstalled, but not specified local tarball path");
          throw new IOException("user specified huster is not preInstalled, but not specified local tarball path");
        }
        ompiHome = HusterConfig.DEFAULT_HUSTER_INSTALL_DIR;
      }
    } 
    
  }
  
  
  public static void main(String[] args) throws IOException {
    HusterCli cli = new HusterCli();
    cli.init(args);
  }
  
  
}
