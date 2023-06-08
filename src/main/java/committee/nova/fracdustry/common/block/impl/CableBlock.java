package committee.nova.fracdustry.common.block.impl;

import com.google.common.collect.Maps;
import committee.nova.fracdustry.common.block.entity.impl.CableBlockEntity;
import committee.nova.fracdustry.common.core.energy.EnergyNetwork;
import committee.nova.fracdustry.common.ref.BlockRef;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class CableBlock extends Block implements EntityBlock {
    public static final Map<Direction, BooleanProperty> PROPERTY_MAP;

    static {
        Map<Direction, BooleanProperty> map = Maps.newEnumMap(Direction.class);
        map.put(Direction.NORTH, BlockStateProperties.NORTH);
        map.put(Direction.EAST, BlockStateProperties.EAST);
        map.put(Direction.SOUTH, BlockStateProperties.SOUTH);
        map.put(Direction.WEST, BlockStateProperties.WEST);
        map.put(Direction.UP, BlockStateProperties.UP);
        map.put(Direction.DOWN, BlockStateProperties.DOWN);
        PROPERTY_MAP = Collections.unmodifiableMap(map);
    }

    public CableBlock() {
        super(Properties.of().sound(SoundType.WOOL).noCollission().noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> list, TooltipFlag flags) {
        list.add(Component.translatable("tooltips.fracdustry.energy_cable").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_MAP.values().toArray(new BooleanProperty[0]));
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return Block.box(4, 4, 4, 12, 12, 12);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = this.defaultBlockState();
        for (Direction facing : Direction.values()) {
            final Level world = ctx.getLevel();
            final BlockPos facingPos = ctx.getClickedPos().offset(facing.getNormal());
            final BlockState facingState = world.getBlockState(facingPos);
            state = state.setValue(PROPERTY_MAP.get(facing), this.canConnect(world, facing.getOpposite(), facingPos, facingState));
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        return state.setValue(PROPERTY_MAP.get(facing), this.canConnect(world, facing.getOpposite(), facingPos, facingState));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        if (world.isClientSide()) return;
        final BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof CableBlockEntity)
            EnergyNetwork.Factory.get(world).enableBlock(pos, tileEntity::setChanged);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }


    private boolean canConnect(LevelAccessor world, Direction facing, BlockPos pos, BlockState state) {
        if (state.getBlock().equals(BlockRef.CABLE.getBlock())) return true;
        final BlockEntity tileEntity = world.getBlockEntity(pos);
        return tileEntity != null && tileEntity.getCapability(ForgeCapabilities.ENERGY, facing).isPresent();
    }
}
