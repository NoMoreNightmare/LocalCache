package top.brightsunshine.localcache.core.entry;

public class AofPersistEntry<K, V> {

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数列表
     */
    private Object[] args;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
