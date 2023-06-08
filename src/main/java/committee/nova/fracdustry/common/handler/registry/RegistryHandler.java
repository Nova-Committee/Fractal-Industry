package committee.nova.fracdustry.common.handler.registry;

import committee.nova.fracdustry.common.ref.BlockEntityRef;
import committee.nova.fracdustry.common.ref.BlockRef;
import committee.nova.fracdustry.common.ref.TabRef;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RegistryHandler {
    public static void init() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockRef.init(bus);
        BlockEntityRef.init(bus);
        // Item
        TabRef.init(bus);
    }
}
