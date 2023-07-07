package org.spoorn.spoornpacks.entity.boat;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spoorn.spoornpacks.SpoornPacks;
import org.spoorn.spoornpacks.entity.SPEntities;
import org.spoorn.spoornpacks.entity.boat.SPBoatRegistry.BoatType;

public class SPBoatEntity extends BoatEntity {

    private static final TrackedData<Integer> SP_BOAT_TYPE = DataTracker.registerData(SPBoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String TYPE_NBT_KEY = "SPBoatType";
    private SPBoatRegistry spBoatRegistry;

    public SPBoatEntity(SPEntities spEntities, SPBoatRegistry spBoatRegistry, BoatType boatType, World world, double x, double y, double z) {
        this(spBoatRegistry, spEntities.getBoatEntityType(boatType.getNamespace()), world);
        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    // SPBoatRegistry is needed on the server side as well.  This constructor is called during the EntityType registration.
    public SPBoatEntity(SPBoatRegistry spBoatRegistry, EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
        this.spBoatRegistry = spBoatRegistry;
    }

    @Override
    public Item asItem() {
        BoatType boatType = this.getSPBoatType();
        // Add boat suffix when fetching the item, as the entity type, model, renderer, etc. does not include the suffix
        return Registries.ITEM.get(new Identifier(boatType.getNamespace(), boatType.getName() + "_boat"));
    }

    public BoatType getSPBoatType() {
        return this.spBoatRegistry.getBoat(this.dataTracker.get(SP_BOAT_TYPE));
    }

    public void setSPBoatType(BoatType boatType) {
        this.dataTracker.set(SP_BOAT_TYPE, boatType.getOrdinal());
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SP_BOAT_TYPE, 0);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString(TYPE_NBT_KEY, this.getSPBoatType().getCustomNbtData());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains(TYPE_NBT_KEY, 8)) {
            try {
                this.setSPBoatType(SpoornPacks.OBJECT_MAPPER.readValue(nbt.getString(TYPE_NBT_KEY), BoatType.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Could not read custom Nbt data for SpoornPacks BoatType nbt=" + nbt.getString(TYPE_NBT_KEY), e);
            }
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
