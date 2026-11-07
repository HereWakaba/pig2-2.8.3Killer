package kakiku;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod("pig2killer")
public final class K implements Command<CommandSourceStack> {
    static K SELF;
    public K() {
        SELF = this;
        MinecraftForge.EVENT_BUS.register(K.class);
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("pig2killer")
                .then(Commands.literal("kill").executes(SELF)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        if (!context.getSource().hasPermission(2)) {
            return 0;
        }
        try {
            Class<?> pig2 = Class.forName("kakiku.pig2mod.entity.Pig2");
            Field oshimai = pig2.getDeclaredField("gOshimai");
            oshimai.setAccessible(true);
            oshimai.setBoolean(null, true);
            Field alive = pig2.getDeclaredField("gAliveServerPigs");
            alive.setAccessible(true);
            ((Set<?>) alive.get(null)).clear();
        } catch (Throwable ignored) {
        }
        MinecraftServer server = context.getSource().getServer();
        for (ServerLevel level : server.getAllLevels()) {
            ArrayList<Entity> targets = new ArrayList<Entity>();
            for (Entity e : level.getEntities().getAll()) {
                if (e instanceof LivingEntity && !(e instanceof Player)) {
                    targets.add(e);
                }
            }
            for (Entity e : targets) {
                e.discard();
            }
        }
        return 1;
    }
}
