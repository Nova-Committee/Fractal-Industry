package committee.nova.fracdustry.common.handler.event;

import committee.nova.fracdustry.common.ref.BlockRef;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler {
    @SubscribeEvent
    public static void onTab(BuildCreativeModeTabContentsEvent event) {
        final CreativeModeTab tab = event.getTab();
        final BlockRef[] v = BlockRef.values();
        for (BlockRef r : v) if (Objects.equals(r.getTargetTab(), tab)) event.accept(r::getBlock);
    }
}
