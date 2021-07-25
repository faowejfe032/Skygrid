package com.voidjns.skygrid.chunkgenerators;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EndChunkGen implements ChunkGen {

    private List<Material> blocks = new ArrayList<>();
    private List<Double> probabilities = new ArrayList<>();

    private Material defaultBlock;

    private Random random = new Random();

    private boolean onePoint17Plus;

    public EndChunkGen(FileConfiguration config, boolean onePoint17Plus) {

        this.onePoint17Plus = onePoint17Plus;

        defaultBlock = Material.getMaterial(config.getString("end-default"));

        double totalProbability = 0;

        for(String key : config.getConfigurationSection("end").getKeys(false)) {

            if (Material.getMaterial(key) != null && Material.getMaterial(key).isBlock()) {

                blocks.add(Material.getMaterial(key));
                probabilities.add(totalProbability += (config.getDouble("end." + key) / 100));


            } else System.out.println("[End Generator] " + key + " is not a valid block!");

        }

        if(totalProbability < 1) {

            System.out.println(
                    "[End Generator] " + (1 - totalProbability) * 100 + "% of probability has not been allocated. "
                            + (1 - totalProbability) * 100 + "% of blocks will spawn as the default block."
            );

        }

        else if(totalProbability > 1) {

            System.out.println(
                    "[End Generator] " + (totalProbability - 1) * 100 + "% of probability has been over-allocated. "
                            + "Some of the blocks might not spawn."
            );

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

                            //if(block.equals(Material.GRASS_BLOCK)) handleGrassBlock(x, y, z, chunkData);

                            break;

                        }

                        if(blocks.indexOf(block) == (blocks.size()-1)) chunkData.setBlock(x, y, z, defaultBlock);

                    }

                }

            }

        }

        return chunkData;

    }

}
