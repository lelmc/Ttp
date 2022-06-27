package cn.lelmc.ttp;

import com.google.inject.Inject;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "ttp",
        name = "Ttp",
        description = "pokepmp",
        authors = {
                "lelmc"
        }
)
public class Ttp {
    public static Ttp instance;

    @Inject
    @ConfigDir(sharedRoot = false)
    public Path path;

    @Listener
    public void onServerStart(GameStartedServerEvent event) throws IOException {
        instance = this;
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        new CommandTtp();
    }

    @Listener
    public void Join(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        File file = Ttp.instance.path.resolve(player.getUniqueId() + ".json").toFile();
        if (!file.exists()) {
            return;
        }

        Location<World> data = ConfigLoader.data(player);
        player.setLocation(data);
        ConfigLoader.delData(player);
    }

    @Listener
    public void onCommandSend(SendCommandEvent event, @First Player player) {
        if (CommandTtp.pos.containsKey(player.getUniqueId())){
            if (event.getCommand().contains("home")){
                player.sendMessage(Text.of("§c你目前不能在这里设置家."));
                event.setCancelled(true);
            }
        }
        if (event.getCommand().contains("back") && CommandTtp.move.containsKey(player.getUniqueId())){
            player.sendMessage(Text.of("§c无法将你返回到之前的位置"));
            event.setCancelled(true);
        }
    }

    @Listener
    public void Teleport(MoveEntityEvent.Teleport event, @First Player player){
        if (CommandTtp.move.containsKey(player.getUniqueId())){
            CommandTtp.move.put(player.getUniqueId(), CommandTtp.move.get(player.getUniqueId()) + 1);
            Integer i = CommandTtp.move.get(player.getUniqueId());
            if (i == 3){
                CommandTtp.pos.remove(player.getUniqueId());
                ConfigLoader.delData(player);
            }
            if (i == 4){
                CommandTtp.move.remove(player.getUniqueId());
            }
        }
    }


}
