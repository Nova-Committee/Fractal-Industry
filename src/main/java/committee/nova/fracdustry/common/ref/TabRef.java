package committee.nova.fracdustry.common.ref;

import committee.nova.fracdustry.FractalIndustry;
import committee.nova.fracdustry.common.ref.api.Ref;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

public enum TabRef implements Ref<CreativeModeTab> {
    MACHINERY(() -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.fracdustry.machinery"))
            .icon(() -> BlockRef.CABLE.getBlock().asItem().getDefaultInstance())
            .displayItems((f, e) -> {
                e.accept(BlockRef.CABLE.getBlock());
            })
            .build());

    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FractalIndustry.MODID);
    private static final Map<TabRef, RegistryObject<CreativeModeTab>> tabRefs = new TreeMap<>();

    public static void init(IEventBus bus) {
        TABS.register(bus);
        final TabRef[] v = TabRef.values();
        for (final TabRef ref : v) tabRefs.put(ref, TABS.register(ref.getId(), ref.sup));
    }

    private final Supplier<CreativeModeTab> sup;

    TabRef(Supplier<CreativeModeTab> sup) {
        this.sup = sup;
    }

    public CreativeModeTab getTab() {
        return getRef();
    }

    @Override
    public String getId() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public CreativeModeTab getRef() {
        return tabRefs.get(this).get();
    }
}
