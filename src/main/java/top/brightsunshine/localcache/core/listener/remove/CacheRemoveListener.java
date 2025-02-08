package top.brightsunshine.localcache.core.listener.remove;

import top.brightsunshine.localcache.core.listener.ICacheRemoveListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

public class CacheRemoveListener<K, V> implements ICacheRemoveListener<K, V> {
    private String removeLog = "remove.log";

    @Override
    public void listen(K key, V value, String type) {
        File file = new File(removeLog);
        if(!file.exists()){
            Path path = Paths.get(removeLog);
            Path parent = path.getParent();
            if(parent != null){
                File parentFile = parent.toFile();
                if(!parentFile.exists()){
                    parentFile.mkdirs();
                }
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(removeLog, true));
            String format = MessageFormat.format("Remove key: {0}, value: {1}, type: {2}\n",
                    key, value, type);
            writer.println(format);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(writer != null){
                writer.close();
            }
        }
    }
}
