package calemiutils.tileentity;

import calemiutils.block.BlockNetworkGate;
import calemiutils.init.InitTileEntityTypes;
import calemiutils.security.ISecurity;
import calemiutils.tileentity.base.INetwork;
import net.minecraft.util.Direction;

public class TileEntityNetworkGate extends TileEntityNetworkCable implements INetwork, ISecurity {

    private boolean hasChanged = false;

    public TileEntityNetworkGate () {
        super(InitTileEntityTypes.NETWORK_GATE.get());
    }

    @Override
    public void tick () {
        super.tick();

        if (world != null) {

            if (world.isBlockPowered(getPos())) {

                if (!hasChanged) {
                    setState(false);
                }

                hasChanged = true;
            }

            else {

                if (hasChanged) {
                    setState(true);
                }

                hasChanged = false;
            }
        }
    }

    private void setState (boolean value) {
        if (world != null) BlockNetworkGate.setState(value, world, pos);
    }

    @Override
    public Direction[] getConnectedDirections () {

        if (getLocation() != null && getLocation().getBlock() instanceof BlockNetworkGate) {

            BlockNetworkGate gate = (BlockNetworkGate) getLocation().getBlock();

            if (getLocation().getForgeBlockState().getBlockState().get(BlockNetworkGate.CONNECTED)) {
                return Direction.values();
            }
        }

        return new Direction[] {};
    }
}
