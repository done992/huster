package cn.enjoy.huster.common;

public interface HusterConfig {
  public static final String HUSTER_CONFIG_PREFIX = "cn.enjoy.huster.";
  
  /* Huster Properties */
  /* Home of huster */
  public final String OMPI_HOME_PROPERTY_KEY = HUSTER_CONFIG_PREFIX + "ompi.home";
  public final String OMPI_LOCAL_TARBALL_PATH_KEY = HUSTER_CONFIG_PREFIX + "ompi.tarball.path";
  
  /* Do we pre-installed huster in nodes in YARN cluster? 
   * by default, we will consider all nodes have huster installed in huster.home
   */
  public final String OMPI_PREINSTALL_PROPERTY_KEY = HUSTER_CONFIG_PREFIX + "ompi.preinstall";
  public final boolean OMPI_PREINSTALL_DEFAULT_VALUE = false; 
    
  public final String HUSTER_ENABLED_LOGKEYS_KEY = HUSTER_CONFIG_PREFIX + "enabled.logkeys";
  
  /*
   * configuration names for allocation strategy
   */
  public static final String ALLOCATION_STRATEGY_KEY = HUSTER_CONFIG_PREFIX + "allocation.strategy.name";
  public static final String PROBABILITY_BASED_ALLOCATION_STRATEGY = "probability-based";
  public static final String DEFAULT_HAMSTER_ALLOCATION_STRATEGY = PROBABILITY_BASED_ALLOCATION_STRATEGY;
  public static final String USER_POLICY_DRIVEN_ALLOCATION_STRATEGY = "user-driven";
  public static final String USER_POLICY_HOST_LIST_KEY = HUSTER_CONFIG_PREFIX + "user.policy.hostlist";
  public static final String USER_POLICY_MPROC_KEY = HUSTER_CONFIG_PREFIX + "user.policy.mproc";
  public static final String USER_POLICY_MNODE_KEY = HUSTER_CONFIG_PREFIX + "user.policy.mnode";

  /**
   * how many times that user will wait for log aggregation finished (in ms)
   */
  public final String HUSTER_LOG_AGGREGATION_WAIT_TIME = HUSTER_CONFIG_PREFIX + "log.waittime";
  public final int DEFAULT_HUSTER_LOG_WAIT_TIME = 5000;
    
  /* hnp expire time */
  public static final String HUSTER_HNP_LIVENESS_EXPIRE_TIME = HUSTER_CONFIG_PREFIX + "hnp.liveness.expiry-internal-ms";
  public static final int DEFAULT_HUSTER_HNP_LIVENESS_EXPIRE_TIME = 60 * 1000 * 10;
  
  /* hnp pull interval */
  public static final String HUSTER_ALLOCATOR_PULL_INTERVAL_TIME = HUSTER_CONFIG_PREFIX + "pull.rm-interval-ms";
  public static final int DEFAULT_HUSTER_ALLOCATOR_PULL_INTERVAL_TIME = 5000; //5 sec
  
  /* $HOME env of huster */
  public final String HUSTER_HOME_ENV_KEY = "HUSTER_CODE_HOME";
  public final String DEFAULT_PID_ROOT_DIR = "/tmp/hamster-pid";
  
  /**
   * default un-tar dir in NM for AM & launched processes when user specified *not* pre-install
   * YARN will un-tar hamster binaries to this directory
   */
  public final String DEFAULT_OMPI_INSTALL_DIR = "huster-ompi";
  public final String DEFAULT_HUSTER_INSTALL_DIR = "huster-core";
  
  public final String YARN_PB_FILE_ENV_KEY = "YARN_PB_FILE";
  
  /* file name for serialized pb file */  
  public static final String DEFAULT_LOCALRESOURCE__SERIALIZED_FILENAME = "huster_localresource_pb";
  
  /* file name for serialized configuration file */
  public static final String DEFAULT_LOCALCONF_SERIALIZED_FILENAME = "hamster_localconf_serilized";
  
  /* umbilical port for HNP connect AM */
  public static final String AM_UMBILICAL_PORT_ENV_KEY = "AM_UMBILICAL_PORT";
  
  /* envs for resource specified when launching containers */
  public static final String HUSTER_MEM_ENV_KEY = "HUSTER_MEM";
  public static final String HUSTER_CPU_ENV_KEY = "HUSTER_CPU";
  public static final int DEFAULT_HUSTER_MEM =  1024;
  public static final int DEFAULT_HUSTER_CPU = 1;
  
  //64M is minimum memory can be specified
  public static final int MINIMUM_HUSTER_MEM = 64;
   
  
  /* default value of how many proc in one node when we don't have enough resource */
  public static final int DEFAULT_N_PROC_IN_ONE_NODE = 16;
}
