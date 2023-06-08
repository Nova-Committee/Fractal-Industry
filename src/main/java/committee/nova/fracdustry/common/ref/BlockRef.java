package committee.nova.fracdustry.common.ref;

import committee.nova.fracdustry.FractalIndustry;
import committee.nova.fracdustry.common.block.impl.CableBlock;
import committee.nova.fracdustry.common.ref.api.Ref;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public enum BlockRef implements Ref<Block> {
    BAUXITE_ORE(createNormalOre(), getVanilla(CreativeModeTabs.BUILDING_BLOCKS)),
    CASSITERITE_ORE(createNormalOre(), getVanilla(CreativeModeTabs.BUILDING_BLOCKS)),
    ILMENITE_ORE(createNormalOre(), getVanilla(CreativeModeTabs.BUILDING_BLOCKS)),
    RARE_EARTH_ORE(createNormalOre(), getVanilla(CreativeModeTabs.BUILDING_BLOCKS)),
    SPODUMENE_ORE(createNormalOre(), getVanilla(CreativeModeTabs.BUILDING_BLOCKS)),
    DEEPSLATE_BAUXITE_ORE(createDeepslate(), getVanilla(CreativeModeTabs.BUILDING_BLOCKS)),
    DEEPSLATE_CASSITERITE_ORE(createDeepslate(), getVanilla(CreativeModeTabs.BUILDING_BLOCKS)),
    DEEPSLATE_ILMENITE_ORE(createDeepslate(), getVanilla(CreativeModeTabs.BUILDING_BLOCKS)),
    DEEPSLATE_RARE_EARTH_ORE(createDeepslate(), getVanilla(CreativeModeTabs.BUILDING_BLOCKS)),
    DEEPSLATE_SPODUMENE_ORE(createDeepslate(), getVanilla(CreativeModeTabs.BUILDING_BLOCKS)),
    CABLE(CableBlock::new);

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FractalIndustry.MODID);
    private static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FractalIndustry.MODID);
    private static final Map<BlockRef, RegistryObject<Block>> blockRefs = new HashMap<>();

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ITEMS.register(bus);
        final BlockRef[] v = BlockRef.values();
        for (final BlockRef ref : v) blockRefs.put(ref, BLOCKS.register(ref.getId(), ref.block));
        for (final BlockRef ref : v)
            BLOCK_ITEMS.register(ref.getId(), () -> new BlockItem(ref.getBlock(), new Item.Properties()));
    }

    private static CreativeModeTab getVanilla(ResourceKey<CreativeModeTab> key) {
        return Objects.requireNonNullElse(BuiltInRegistries.CREATIVE_MODE_TAB.get(key),
                BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.BUILDING_BLOCKS));
    }

    private static Supplier<Block> createNormalOre() {
        return () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE));
    }

    private static Supplier<Block> createDeepslate() {
        return () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE));
    }

    private final Supplier<Block> block;
    private final CreativeModeTab tab;

    BlockRef(Supplier<Block> block, CreativeModeTab tab) {
        this.block = block;
        this.tab = tab;
    }

    BlockRef(Supplier<Block> block) {
        this(block, null);
    }

    public Block getBlock() {
        return getRef();
    }

    @Override
    public String getId() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public Block getRef() {
        return blockRefs.get(this).get();
    }

    @Nullable
    public CreativeModeTab getTargetTab() {
        return tab;
    }
}
