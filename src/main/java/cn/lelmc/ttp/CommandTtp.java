package cn.lelmc.ttp;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandTtp {

    public static Map<UUID, Location<World>> pos = new HashMap<>();
    public static Map<UUID, Integer> move = new HashMap<>();

    CommandSpec TTP = CommandSpec.builder()
            .permission("ttp.admin")
            .arguments(GenericArguments.seq(
                    GenericArguments.onlyOne(GenericArguments.player(Text.of("玩家ID"))),
                    GenericArguments.world(Text.of("世界名称")),
                    GenericArguments.vector3d(Text.of("坐标位置")),
                    GenericArguments.integer(Text.of("持续时间"))
            ))

            .executor(new comm())
            .build();

    public CommandTtp() {
        Sponge.getCommandManager().register(Ttp.instance, TTP, "ttp");
    }

    @NonnullByDefault
    static class comm implements CommandExecutor {
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Player player = args.<Player>getOne("玩家ID").get();
            WorldProperties world = args.<WorldProperties>getOne("世界名称").get();
            Vector3d v = args.<Vector3d>getOne("坐标位置").get();
            int time = args.<Integer>getOne("持续时间").get();

            Location<World> location = player.getLocation();
            ConfigLoader.createData(player);
            //设置玩家位置

            CommandTtp.move.put(player.getUniqueId(), 1);
            player.setLocation(v, world.getUniqueId());

            if (!CommandTtp.pos.containsKey(player.getUniqueId())) {
                CommandTtp.pos.put(player.getUniqueId(), location);
                //规定时间内返回到原点
                Task.builder().execute(task -> {
                    if (!player.isOnline() || !CommandTtp.pos.containsKey(player.getUniqueId())) {
                        task.cancel();
                        return;
                    }
                    player.setLocation(CommandTtp.pos.get(player.getUniqueId()));
                    player.sendMessage(Text.of("已经把你送回之前的地点"));
                    ConfigLoader.delData(player);
                    CommandTtp.pos.remove(player.getUniqueId());
                }).delay(time, TimeUnit.SECONDS).submit(Ttp.instance);
            }
            return CommandResult.success();
        }
    }

}
