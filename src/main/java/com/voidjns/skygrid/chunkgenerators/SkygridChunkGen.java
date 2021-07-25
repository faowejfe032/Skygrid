package com.voidjns.skygrid.chunkgenerators;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class SkygridChunkGen extends ChunkGenerator {

    ChunkGen overworldChunkGen, netherChunkGen, endChunkGen;

    public SkygridChunkGen(FileConfiguration config) {

        boolean onePoint17Plus = true;

        try {

            ChunkData.class.getMethod("getMaxHeight");
            ChunkData.class.getMethod("getMinHeight");

        } catch (NoSuchMethodException e) {

            onePoint17Plus = false;

        }

        overworldChunkGen = new OverworldChunkGen(config, onePoint17Plus);
        netherChunkGen = new NetherChunkGen(config, onePoint17Plus);
        endChunkGen = new EndChunkGen(config, onePoint17Plus);

    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biome) {

        ChunkData chunkData = Bukkit.createChunkData(world);

        chunkData = switch (world.getEnvironment()) {

            case NORMAL, CUSTOM -> overworldChunkGen.generateChunkData(chunkData);
            case NETHER -> netherChunkGen.generateChunkData(chunkData);
            case THE_END -> endChunkGen.generateChunkData(chunkData);

        };

        return chunkData;

    }

}
