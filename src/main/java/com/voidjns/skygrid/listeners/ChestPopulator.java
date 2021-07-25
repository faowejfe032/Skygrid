package com.voidjns.skygrid.listeners;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

public class ChestPopulator implements Listener {

    HashMap<Material, Double> chestItems = new HashMap<>();

    Random random = new Random();

    public ChestPopulator(FileConfiguration config) {

        for(String key : config.getConfigurationSection("chest-items").getKeys(false)) {

            if (Material.getMaterial(key) != null && Material.getMaterial(key).isItem()) {

                chestItems.put(Material.getMaterial(key), (config.getDouble("chest-items." + key)/100));

            } else System.out.println("[Chest Populator] " + key + " is not a valid item!");

        }

    }

    @EventHandler
    public void onChestSpawn(ChunkLoadEvent e) {

        if(e.isNewChunk()) {

            for (int x = 0; x < 15; x += 4) {

                for (int y = (e.getWorld().getMinHeight()); y < (e.getWorld().getMaxHeight()); y += 4) {

                    for (int z = 0; z < 15; z += 4) {

                        if (e.getChunk().getBlock(x, y, z).getType().equals(Material.CHEST)) {

                            for (Material item : chestItems.keySet()) {

                                if (chestItems.get(item) > random.nextDouble()) {

                                    Chest chestData = ((Chest) e.getChunk().getBlock(x, y, z).getState());
                                    chestData.getBlockInventory().addItem(new ItemStack(item));

                                }

                            }

                        }

                    }

                }

            }

        }

    }

}
