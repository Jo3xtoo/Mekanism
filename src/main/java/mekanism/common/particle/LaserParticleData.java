package mekanism.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import javax.annotation.Nonnull;
import mekanism.common.registries.MekanismParticleTypes;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.Direction;

public class LaserParticleData implements IParticleData {

    public final Direction direction;
    public final double distance;
    public final double energy;

    public LaserParticleData(Direction direction, double distance, double energy) {
        this.direction = direction;
        this.distance = distance;
        this.energy = energy;
    }

    @Nonnull
    @Override
    public ParticleType<?> getType() {
        return MekanismParticleTypes.LASER.getParticleType();
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer) {
        buffer.writeEnumValue(direction);
        buffer.writeDouble(distance);
        buffer.writeDouble(energy);
    }

    @Nonnull
    @Override
    public String getParameters() {
        //TODO: check
        return String.format(Locale.ROOT, "%s %d %.2f %.2f", getType().getRegistryName(), direction.ordinal(), this.distance, this.energy);
    }

    public static final IDeserializer<LaserParticleData> DESERIALIZER = new IDeserializer<LaserParticleData>() {
        @Nonnull
        @Override
        public LaserParticleData deserialize(@Nonnull ParticleType<LaserParticleData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            Direction direction = Direction.byIndex(reader.readInt());
            reader.expect(' ');
            double distance = reader.readDouble();
            reader.expect(' ');
            double energy = reader.readDouble();
            return new LaserParticleData(direction, distance, energy);
        }

        @Override
        public LaserParticleData read(@Nonnull ParticleType<LaserParticleData> type, PacketBuffer buf) {
            return new LaserParticleData(buf.readEnumValue(Direction.class), buf.readDouble(), buf.readDouble());
        }
    };
}