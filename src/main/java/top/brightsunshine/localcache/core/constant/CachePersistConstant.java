package top.brightsunshine.localcache.core.constant;

public class CachePersistConstant {
    public static final int NONE_PERSIST = 0;
    public static final int AOF_PERSIST = 1;
    public static final int RDB_PERSIST = 2;


    public static final int AOF_ALWAYS = 0;
    public static final int AOF_EVERYSEC = 1;
    public static final int AOF_THIRTY_SEC = 2;


    public static final String DEFAULT_AOF_PATH = "1.aof";
    public static final String DEFAULT_RDB_PATH = "1.rdb";

}
