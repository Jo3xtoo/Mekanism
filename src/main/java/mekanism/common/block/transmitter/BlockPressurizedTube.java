package mekanism.common.block.transmitter;

import mekanism.api.block.IHasTileEntity;
import mekanism.common.block.interfaces.ITieredBlock;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tier.TubeTier;
import mekanism.common.tile.transmitter.TileEntityPressurizedTube;
import net.minecraft.tileentity.TileEntityType;

public class BlockPressurizedTube extends BlockSmallTransmitter implements ITieredBlock<TubeTier>, IHasTileEntity<TileEntityPressurizedTube> {

    private final TubeTier tier;

    public BlockPressurizedTube(TubeTier tier) {
        this.tier = tier;
    }

    @Override
    public TubeTier getTier() {
        return tier;
    }

    @Override
    public TileEntityType<TileEntityPressurizedTube> getTileType() {
        switch (tier) {
            case ADVANCED:
                return MekanismTileEntityTypes.ADVANCED_PRESSURIZED_TUBE.getTileEntityType();
            case ELITE:
                return MekanismTileEntityTypes.ELITE_PRESSURIZED_TUBE.getTileEntityType();
            case ULTIMATE:
                return MekanismTileEntityTypes.ULTIMATE_PRESSURIZED_TUBE.getTileEntityType();
            case BASIC:
            default:
                return MekanismTileEntityTypes.BASIC_PRESSURIZED_TUBE.getTileEntityType();
        }
    }
}