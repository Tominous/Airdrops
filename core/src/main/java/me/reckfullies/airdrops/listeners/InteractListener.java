package me.reckfullies.airdrops.listeners;

import me.reckfullies.airdrops.Airdrops;
import me.reckfullies.airdrops.Package;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class InteractListener implements Listener
{
    private Airdrops pluginInstance;

    public InteractListener(Airdrops pluginInstance)
    {
        this.pluginInstance = pluginInstance;
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler
    private void onRightClick(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            ItemStack item = event.getItem();
            if (item == null)
                return;

            ItemMeta meta = item.getItemMeta();
            if (meta == null)
                return;

            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            NamespacedKey idKey = new NamespacedKey(pluginInstance, "id");
            NamespacedKey packageNameKey = new NamespacedKey(pluginInstance, "package");

            if (dataContainer.has(idKey, PersistentDataType.BYTE))
            {
                String packageName = "";
                if (dataContainer.has(packageNameKey, PersistentDataType.STRING))
                    packageName = dataContainer.get(packageNameKey, PersistentDataType.STRING);

                Package itemPackage = null;
                if (pluginInstance.getPackageIO().checkPackageExists(packageName))
                    itemPackage = pluginInstance.getPackageIO().loadPackage(packageName);

                if (itemPackage != null)
                {
                    if (item.getAmount() > 1)
                        item.setAmount(item.getAmount() - 1);
                    else
                        event.getPlayer().getInventory().remove(item);

                    if (dataContainer.get(idKey, PersistentDataType.BYTE).equals((byte) 1))
                    {
                        // Signal
                        event.getPlayer().sendMessage(ChatColor.GREEN + "Called Signal! - " + ChatColor.YELLOW + itemPackage.getName());

                        Block targetBlock = event.getClickedBlock().getRelative(BlockFace.UP);
                        if (targetBlock.getType() == Material.AIR)
                        {
                            targetBlock.setType(Material.CHEST);

                            Chest chest = (Chest) targetBlock.getState();

                            ItemStack[] contents = new ItemStack[0];
                            chest.getBlockInventory().setContents(itemPackage.getItems().toArray(contents));
                        }
                    }
                    else
                    {
                        // Flare
                        event.getPlayer().sendMessage(ChatColor.GREEN + "Called Flare! - " + ChatColor.RED + itemPackage.getName());

                        calculateDropLocations(getNearbyChunks(event.getPlayer().getLocation(), 2), itemPackage);
                    }
                }

                event.setCancelled(true);
            }
        }
    }

    private List<Chunk> getNearbyChunks(Location location, int radius)
    {
        World world = location.getWorld();
        if (world == null)
            return new ArrayList<>();

        Chunk startingChunk = world.getChunkAt(location);

        List<Chunk> nearbyChunks = new ArrayList<>();
        for (int x = startingChunk.getX() - radius; x <= startingChunk.getX() + radius; x++)
        {
            for (int z = startingChunk.getZ() - radius; z <= startingChunk.getZ() + radius; z++)
            {
                nearbyChunks.add(world.getChunkAt(x, z));
            }
        }

        return nearbyChunks;
    }

    private void calculateDropLocations(List<Chunk> chunks, Package itemPackage)
    {
        new BukkitRunnable()
        {
            int chunksToCheck = chunks.size();
            int chunksChecked = 0;

            List<Location> validBlocks = new ArrayList<>();

            @Override
            public void run()
            {
                if (chunksChecked == chunksToCheck)
                {
                    finishedCalculateDrop(validBlocks, itemPackage);
                    cancel();
                    return;
                }

                Chunk currentChunk = chunks.get(chunksChecked);
                World currentWorld = currentChunk.getWorld();

                if (!currentChunk.isLoaded())
                    currentChunk.load();

                for (int x = 0; x < 16; x++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        Block currentBlock = currentChunk.getBlock(x, 0, z);
                        Block highestBlock = currentWorld.getHighestBlockAt(currentBlock.getX(), currentBlock.getZ());

                        Location correctedLocation = highestBlock.getLocation().subtract(0, 1, 0);

                        if (correctedLocation.getBlock().getType().isSolid())
                            validBlocks.add(correctedLocation);
                    }
                }

                chunksChecked++;
            }
        }.runTaskTimer(pluginInstance, 0L, 1L);
    }

    private void finishedCalculateDrop(List<Location> validBlocks, Package itemPackage)
    {
        for (int i = 0; i < 5; i++)
        {
            Location spawnLocation = validBlocks.get(new Random().nextInt(validBlocks.size()));

            Block spawnBlock = spawnLocation.getBlock().getRelative(BlockFace.UP);
            spawnBlock.setType(Material.CHEST);

            Chest chest = (Chest) spawnBlock.getState();

            ItemStack[] contents = new ItemStack[0];
            chest.getBlockInventory().setContents(itemPackage.getItems().toArray(contents));
        }
    }
}
