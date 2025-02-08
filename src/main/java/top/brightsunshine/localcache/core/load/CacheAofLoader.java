package top.brightsunshine.localcache.core.load;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.brightsunshine.localcache.annotation.CacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheLoader;
import top.brightsunshine.localcache.core.Cache;
import top.brightsunshine.localcache.core.entry.AofPersistEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CacheAofLoader<K, V> implements ICacheLoader<K, V> {

    private ICache<K, V> cache;

    private String aofPath;

    private static final Map<String, Method> METHOD = new HashMap<>();
    static {
        Method[] methods = Cache.class.getMethods();

        for(Method method : methods){
            CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);

            if(cacheInterceptor != null) {
                // 暂时
                if(cacheInterceptor.persist()) {
                    String methodName = method.getName();
                    int parameterCount = method.getParameterCount();
                    METHOD.put(methodName + parameterCount, method);
                }
            }
        }

    }

    public CacheAofLoader(ICache<K, V> cache, String aofPath) {
        this.cache = cache;
        this.aofPath = aofPath;
    }

    @Override
    public void load() {
        File file = new File(aofPath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            ObjectMapper mapper = new ObjectMapper();
            br.lines().forEach(line -> {

                try {
                    AofPersistEntry entry = mapper.readValue(line, new TypeReference<AofPersistEntry>(){});
                    executeMethod(entry);
                } catch (JsonProcessingException | NoSuchMethodException | InvocationTargetException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeMethod(AofPersistEntry entry) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = entry.getMethod();
        Object[] args = entry.getArgs();
        Method method = METHOD.get(methodName + args.length);
        method.setAccessible(true);
        method.invoke(cache, args);
    }

}
