package committee.nova.fracdustry.common.core.energy;

import com.google.common.collect.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.*;

public class BlockNetwork implements IBlockNetwork {
    private final Map<BlockPos, Set<BlockPos>> components = Maps.newHashMap();
    private final SetMultimap<BlockPos, Direction> connections = Multimaps.newSetMultimap(Maps.newHashMap(), () -> EnumSet.noneOf(Direction.class));

    @Override
    public int size(BlockPos node) {
        return this.components.containsKey(node) ? this.components.get(node).size() : 1;
    }

    @Override
    public BlockPos root(BlockPos node) {
        return this.components.containsKey(node) ? this.components.get(node).iterator().next() : node.immutable();
    }

    @Override
    public void cut(BlockPos node, Direction direction, ConnectivityListener afterSplit) {
        if (this.connections.remove(node, direction)) {
            final BlockPos another = node.offset(direction.getNormal());
            this.connections.remove(another, direction.getOpposite());
            BFSIterator nodeIterator = new BFSIterator(node), anotherIterator = new BFSIterator(another);
            while (nodeIterator.hasNext()) {
                BlockPos next = nodeIterator.next();
                if (!anotherIterator.getSearched().contains(next)) {
                    BFSIterator iterator = anotherIterator;
                    anotherIterator = nodeIterator;
                    nodeIterator = iterator;
                    continue;
                }
                return;
            }
            final Set<BlockPos> primaryComponent = this.components.get(node), secondaryComponent;
            final BlockPos primaryNode = primaryComponent.iterator().next();
            final Set<BlockPos> searched = nodeIterator.getSearched();
            if (searched.contains(primaryNode)) {
                secondaryComponent = Sets.newLinkedHashSet(Sets.difference(primaryComponent, searched));
                primaryComponent.retainAll(searched);
            } else {
                secondaryComponent = searched;
                primaryComponent.removeAll(searched);
            }
            if (secondaryComponent.size() <= 1) {
                secondaryComponent.forEach(this.components::remove);
            } else {
                secondaryComponent.forEach(pos -> this.components.put(pos, secondaryComponent));
            }
            if (primaryComponent.size() <= 1) {
                primaryComponent.forEach(this.components::remove);
            }
            afterSplit.onChange(primaryNode, secondaryComponent.iterator().next());
        }
    }

    @Override
    public void link(BlockPos node, Direction direction, ConnectivityListener beforeMerge) {
        final BlockPos secondary = node.immutable();
        if (this.connections.put(secondary, direction)) {
            final BlockPos primary = node.offset(direction.getNormal());
            this.connections.put(primary, direction.getOpposite());
            final Set<BlockPos> primaryComponent = this.components.get(primary);
            final Set<BlockPos> secondaryComponent = this.components.get(secondary);
            if (primaryComponent == null && secondaryComponent == null) {
                final Set<BlockPos> union = Sets.newLinkedHashSet();
                beforeMerge.onChange(secondary, primary);
                this.components.put(secondary, union);
                this.components.put(primary, union);
                union.add(secondary);
                union.add(primary);
            } else if (primaryComponent == null) {
                beforeMerge.onChange(secondaryComponent.iterator().next(), primary);
                this.components.put(primary, secondaryComponent);
                secondaryComponent.add(primary);
            } else if (secondaryComponent == null) {
                beforeMerge.onChange(primaryComponent.iterator().next(), secondary);
                this.components.put(secondary, primaryComponent);
                primaryComponent.add(secondary);
            } else if (primaryComponent != secondaryComponent) {
                beforeMerge.onChange(primaryComponent.iterator().next(), secondaryComponent.iterator().next());
                final Set<BlockPos> union = Sets.newLinkedHashSet(Sets.union(primaryComponent, secondaryComponent));
                union.forEach(pos -> this.components.put(pos, union));
            }
        }
    }

    public class BFSIterator implements Iterator<BlockPos> {
        private final Set<BlockPos> searched = Sets.newLinkedHashSet();
        private final Queue<BlockPos> queue = Queues.newArrayDeque();

        public BFSIterator(BlockPos node) {
            node = node.immutable();
            this.searched.add(node);
            this.queue.offer(node);
        }

        @Override
        public boolean hasNext() {
            return this.queue.size() > 0;
        }

        @Override
        public BlockPos next() {
            final BlockPos node = this.queue.remove();
            for (Direction direction : BlockNetwork.this.connections.get(node)) {
                final BlockPos another = node.offset(direction.getNormal());
                if (this.searched.add(another)) {
                    this.queue.offer(another);
                }
            }
            return node;
        }

        public Set<BlockPos> getSearched() {
            return this.searched;
        }
    }
}
