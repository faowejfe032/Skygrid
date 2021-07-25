package com.voidjns.skygrid.chunkgenerators;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import java.util.*;
import java.util.List;

public class OverworldChunkGen implements ChunkGen {

    private List<Material> blocks = new ArrayList<>();
    private List<Double> probabilities = new ArrayList<>();

    private List<Material> grassBlockAccessories = new ArrayList<>();
    private List<Double> grassBlockProbabilities = new ArrayList<>();

    private List<Material> farmlandAccessories = new ArrayList<>();
    private List<Double> farmlandProbabilities = new ArrayList<>();

    private Material defaultBlock;

    private Random random = new Random();

    private boolean onePoint17Plus;

    public OverworldChunkGen(FileConfiguration config, boolean onePointSeventeenPlus) {

        this.onePoint17Plus = onePointSeventeenPlus;

        defaultBlock = Material.getMaterial(config.getString("overworld-default"));

        double totalProbability = 0;

        for(String key : config.getConfigurationSection("overworld").getKeys(false)) {

            if (Material.getMaterial(key) != null && Material.getMaterial(key).isBlock()) {

                blocks.add(Material.getMaterial(key));
                probabilities.add(totalProbability += (config.getDouble("overworld." + key) / 100));


            } else System.out.println("[Overworld Generator] " + key + " is not a valid block!");

        }

        if(totalProbability < 1) {

            System.out.println(
                    "[Overworld Generator] " + (1 - totalProbability) * 100 + "% of probability has not been allocated. "
                    + (1 - totalProbability) + "% of blocks will spawn as the default block."
            );

        }

        else if(totalProbability > 1) {

            System.out.println(
                    "[Overworld Generator] " + (totalProbability - 1) * 100 + "% of probability has been over-allocated. "
                    + "Some of the blocks might not spawn."
            );

        }

        totalProbability = 0;

        for(String key : config.getConfigurationSection("grassblock-accessories").getKeys(false)) {

            if (Material.getMaterial(key) != null && Material.getMaterial(key).isBlock()) {

                grassBlockAccessories.add(Material.getMaterial(key));
                grassBlockProbabilities.add(totalProbability += (config.getDouble("grassblock-accessories." + key) / 100));


            } else System.out.println("[Overworld Generator (Grass Block Accessories)] " + key + " is not a valid block!");

        }

        totalProbability = 0;

        for(String key : config.getConfigurationSection("farmland-accessories").getKeys(false)) {

            if (Material.getMaterial(key) != null && Material.getMaterial(key).isBlock()) {

                farmlandAccessories.add(Material.getMaterial(key));
                farmlandProbabilities.add(totalProbability += (config.getDouble("farmland-accessories." + key) / 100));


            } else System.out.println("[Overworld Generator (Farmland Accessories)] " + key + " is not a valid block!");

        }

    }

    @Override
    public ChunkData generateChunkData(ChunkData chunkData) {

        double rand;
        int minHeight = (onePoint17Plus ? chunkData.getMinHeight() : 0);
        int maxHeight = (onePoint17Plus ? chunkData.getMaxHeight() : 256);

        for(int x=0; x<15; x+=4) {

            for(int y=minHeight; y<maxHeight; y+=4) {

                for(int z=0; z<15; z+=4) {

                    rand = random.nextDouble();

                    for(Material block : blocks) {

                        if(probabilities.get(blocks.indexOf(block)) >= rand) {

                            chunkData.setBlock(x, y, z, block);

                            if(block.equals(Material.GRASS_BLOCK)) handleGrassBlock(x, y, z, chunkData);
                            else if(block.equals(Material.FARMLAND)) handleFarmland(x, y, z, chunkData);

                        } else if(blocks.indexOf(block) == (blocks.size()-1)) chunkData.setBlock(x, y, z, defaultBlock);

                    }

                }

            }

        }

        return chunkData;

    }

    private void handleGrassBlock(int x, int y, int z, ChunkData chunkData) {

        double rand = random.nextDouble();

        for(Material block : grassBlockAccessories) {

            if(grassBlockProbabilities.get(grassBlockAccessories.indexOf(block)) >= rand) {

                chunkData.setBlock(x, y+1, z, block);

                break;

            }

        }

    }

    private void handleFarmland(int x, int y, int z, ChunkData chunkData) {

        double rand = random.nextDouble();

        for(Material block : farmlandAccessories) {

            if(farmlandProbabilities.get(farmlandAccessories.indexOf(block)) >= rand) {

                chunkData.setBlock(x, y+1, z, block);

                break;

            }

        }

    }

}
