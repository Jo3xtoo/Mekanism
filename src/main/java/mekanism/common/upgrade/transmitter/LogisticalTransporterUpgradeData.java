package mekanism.common.upgrade.transmitter;

import mekanism.common.tile.transmitter.TileEntityLogisticalTransporter;
import mekanism.common.tile.transmitter.TileEntityTransmitter.ConnectionType;
import net.minecraft.nbt.CompoundNBT;

public class LogisticalTransporterUpgradeData extends TransmitterUpgradeData {

    public final CompoundNBT nbt;

    public LogisticalTransporterUpgradeData(boolean redstoneReactive, ConnectionType[] connectionTypes, TileEntityLogisticalTransporter transmitter) {
        super(redstoneReactive, connectionTypes);
        transmitter.writeToNBT(this.nbt = new CompoundNBT());
    }
}