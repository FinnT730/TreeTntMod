package me.finnt730.treetnt;

import net.minecraft.Util;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.stream.Collectors;

@Mod("treetnt")
public class Treetnt {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Treetnt() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void ChangeHitLog(final BlockEvent.BreakEvent e) {
        float rng = new Random().nextFloat();
        BlockState Below = e.getWorld().getBlockState(e.getPos().below());
        if(BlockTags.LOGS.contains(e.getState().getBlock()) && rng > new Random().nextFloat()) {
            e.getWorld().setBlock(e.getPos(), Blocks.TNT.defaultBlockState(), 3);
            Util.wrapThreadWithTaskName("placeAndRemoveRedstoneUnderTNT", () -> {
                e.getWorld().setBlock(e.getPos().below(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
                synchronized (this) {
                    try {
                        wait(100);
                        e.getWorld().setBlock(e.getPos().below(), Below, 3);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }).run();
            e.setCanceled(true);
        }
    }
}
