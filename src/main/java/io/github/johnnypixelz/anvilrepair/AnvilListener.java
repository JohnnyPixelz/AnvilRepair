package io.github.johnnypixelz.anvilrepair;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {

    @EventHandler
    public void onAnvilRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getClickedBlock().getType().equals(Material.ANVIL)) return;

        event.setCancelled(true);
        if (event.getItem() == null || event.getItem().getType() == Material.AIR) {
            event.getPlayer().sendMessage(AnvilRepair.color(AnvilRepair.plugin.getConfig().getString("messages.notHoldingAnything")));
            return;
        }

        if (!AnvilRepair.plugin.getConfig().getIntegerList("repairableItems").contains(event.getItem().getType().getId())) {
            event.getPlayer().sendMessage(AnvilRepair.color(AnvilRepair.plugin.getConfig().getString("messages.notRepairable")));
            return;
        }

        if (event.getItem().getDurability() == 0) {
            event.getPlayer().sendMessage(AnvilRepair.color(AnvilRepair.plugin.getConfig().getString("messages.alreadyRepaired")));
            return;
        }

        event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
        new AnvilGui(event);
    }
}
