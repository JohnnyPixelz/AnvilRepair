package io.github.johnnypixelz.anvilrepair.tasks;

import io.github.johnnypixelz.anvilrepair.AnvilRepair;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class RepairTask extends BukkitRunnable {
    private Location location;
    private int executions;
    private Item item;
    private PlayerInteractEvent event;
    private ItemStack itemStack;

    public RepairTask(PlayerInteractEvent event) {
        this.location = event.getClickedBlock().getLocation().add(0.5, 1.0, 0.5);
        this.event = event;
        this.itemStack = event.getItem();
        item = event.getPlayer().getWorld().dropItem(location, itemStack);
        item.setVelocity(new Vector(0, 0, 0));
        item.setCustomNameVisible(true);
        item.setPickupDelay(9999);

        if (itemStack.getItemMeta().hasDisplayName()) {
            item.setCustomName(ChatColor.translateAlternateColorCodes('&', AnvilRepair.plugin.getConfig().getString("formats.repairItemWithName").replaceAll("<name>", itemStack.getItemMeta().getDisplayName())));
        } else {
            item.setCustomName(ChatColor.translateAlternateColorCodes('&', AnvilRepair.plugin.getConfig().getString("formats.repairItem")));
        }
    }

    @Override
    public void run() {
        if (executions >= 3) {
            item.remove();
            itemStack.setDurability((short)0);
            event.getPlayer().getInventory().addItem(itemStack);
            cancel();
            return;
        }

        double random1 = ThreadLocalRandom.current().nextDouble(1.9, 2.1);
        item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, 1);
        location.getWorld().playSound(location, Sound.ANVIL_LAND, 1, (float)random1);
        item.setCustomName(item.getCustomName()+ChatColor.translateAlternateColorCodes('&', AnvilRepair.plugin.getConfig().getString("formats.repairItemNamePerTick")));
        executions++;
    }
}
