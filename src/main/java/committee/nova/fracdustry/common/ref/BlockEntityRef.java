package committee.nova.fracdustry.common.ref;

import com.mojang.datafixers.DSL;
import committee.nova.fracdustry.FractalIndustry;
import committee.nova.fracdustry.common.block.entity.impl.CableBlockEntity;
import committee.nova.fracdustry.common.ref.api.Ref;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public enum BlockEntityRef implements Ref<BlockEntityType<?>> {
    CABLE(() -> BlockEntityType.Builder.of(CableBlockEntity::new, BlockRef.CABLE.getBlock()).build(DSL.remainderType()));

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,
            FractalIndustry.MODID);
    private static final Map<BlockEntityRef, RegistryObject<BlockEntityType<?>>> blockEntityRefs = new HashMap<>();

    public static void init(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
        final BlockEntityRef[] v = BlockEntityRef.values();
        for (final BlockEntityRef ref : v) blockEntityRefs.put(ref, BLOCK_ENTITIES.register(ref.getId(), ref.sup));
    }

    private final Supplier<BlockEntityType<?>> sup;

    BlockEntityRef(Supplier<BlockEntityType<?>> sup) {
        this.sup = sup;
    }

    public BlockEntityType<?> getBlockEntity() {
        return getRef();
    }

    @Override
    public String getId() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public BlockEntityType<?> getRef() {
        return blockEntityRefs.get(this).get();
    }
}
