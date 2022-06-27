package cn.lelmc.ttp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {

    public static void createData(Player player) {//create
        Path resolve = Ttp.instance.path.resolve(player.getUniqueId() + ".json");
        Location<World> pos = player.getLocation();
        if (!Files.exists(resolve)) {
            try {
                Files.createFile(resolve);

                FileOutputStream fileOutputStream = new FileOutputStream(resolve.toFile());//实例化FileOutputStream
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);//将字符流转换为字节流
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);//创建字符缓冲输出流对象

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("World", pos.getExtent().getName());
                jsonObject.addProperty("x", pos.getX());
                jsonObject.addProperty("y", pos.getY());
                jsonObject.addProperty("z", pos.getZ());

                bufferedWriter.write(jsonObject.toString());
                bufferedWriter.flush();//清空缓冲区，强制输出数据
                bufferedWriter.close();//关闭输出流
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Location<World> data(Player player){
        Path resolve = Ttp.instance.path.resolve(player.getUniqueId() + ".json");
        JsonObject jsonObject;
        try {
            FileInputStream inputStream = new FileInputStream(resolve.toFile());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            jsonObject = new JsonParser().parse(inputStreamReader).getAsJsonObject();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        World world = Sponge.getServer().loadWorld(jsonObject.get("World").getAsString()).get();
        int x = jsonObject.get("x").getAsInt();
        int y = jsonObject.get("y").getAsInt();
        int z = jsonObject.get("z").getAsInt();

        return new Location<>(world, x, y, z);
    }

    public static void delData(Player player){
        File file = Ttp.instance.path.resolve(player.getUniqueId() + ".json").toFile();
        if (file.exists()){
            file.delete();
        }
    }
}
