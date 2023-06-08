package committee.nova.fracdustry.common.block.entity.impl;

import committee.nova.fracdustry.common.core.energy.EnergyNetwork;
import committee.nova.fracdustry.common.ref.BlockEntityRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CableBlockEntity extends BlockEntity {
    private Integer tmpEnergy = null;

    public CableBlockEntity(BlockEntityType<?> t, BlockPos p, BlockState s) {
        super(t, p, s);
    }

    public CableBlockEntity(BlockPos p, BlockState s) {
        this(BlockEntityRef.CABLE.getBlockEntity(), p, s);
    }

    private final LazyOptional<IEnergyStorage> lazyOptional = LazyOptional.of(() -> new IEnergyStorage() {
        private final EnergyNetwork network = EnergyNetwork.Factory.get(CableBlockEntity.this.level);

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            final int energy = this.getEnergyStored();
            final int diff = Math.min(500, Math.min(this.getMaxEnergyStored() - energy, maxReceive));
            if (!simulate) {
                this.network.addEnergy(CableBlockEntity.this.worldPosition, diff);
                if (diff != 0) {
                    CableBlockEntity.this.setChanged();
                }
            }
            return diff;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            final int energy = this.getEnergyStored();
            final int diff = Math.min(500, Math.min(energy, maxExtract));
            if (!simulate) {
                this.network.addEnergy(CableBlockEntity.this.worldPosition, -diff);
                if (diff != 0) CableBlockEntity.this.setChanged();
            }
            return diff;
        }

        @Override
        public int getEnergyStored() {
            return Math.min(this.getMaxEnergyStored(), this.network.getNetworkEnergy(CableBlockEntity.this.worldPosition));
        }

        @Override
        public int getMaxEnergyStored() {
            return 1000 * this.network.getNetworkSize(CableBlockEntity.this.worldPosition);
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    });

    @Override
    public void load(CompoundTag tag) {
        this.tmpEnergy = tag.getInt("fd_energy");
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("fd_energy", EnergyNetwork.Factory.get(this.level).getSharedEnergy(this.worldPosition));
        super.saveAdditional(tag);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return Objects.equals(cap, ForgeCapabilities.ENERGY) ? this.lazyOptional.cast() : super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        if (this.level != null && !this.level.isClientSide) {
            final EnergyNetwork network = EnergyNetwork.Factory.get(level);
            if (this.tmpEnergy != null) {
                final int diff = this.tmpEnergy - network.getSharedEnergy(this.worldPosition);
                network.addEnergy(this.worldPosition, diff);
                this.tmpEnergy = null;
            }
            network.enableBlock(this.worldPosition, this::setChanged);
        }
        super.onLoad();
    }

    @Override
    public void onChunkUnloaded() {
        if (this.level != null && !this.level.isClientSide) {
            final EnergyNetwork network = EnergyNetwork.Factory.get(this.level);
            network.disableBlock(this.worldPosition, this::setChanged);
        }
        super.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide) {
            final EnergyNetwork network = EnergyNetwork.Factory.get(this.level);
            network.disableBlock(this.worldPosition, () ->
            {
                int diff = network.getSharedEnergy(this.worldPosition);
                network.addEnergy(this.worldPosition, -diff);
                this.setChanged();
            });
        }
        super.setRemoved();
    }
}
