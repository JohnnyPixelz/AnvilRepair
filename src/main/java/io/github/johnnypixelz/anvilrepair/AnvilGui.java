package io.github.johnnypixelz.anvilrepair;

import io.github.johnnypixelz.anvilrepair.tasks.RepairTask;
import io.github.johnnypixelz.utilizer.gui.Gui;
import io.github.johnnypixelz.utilizer.gui.GuiItem;
import io.github.johnnypixelz.utilizer.gui.pane.StaticPane;
import io.github.johnnypixelz.utilizer.itemstack.ItemBuilder;
import io.github.johnnypixelz.utilizer.itemstack.PaneType;
import io.github.johnnypixelz.utilizer.itemstack.PremadeItems;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AnvilGui extends Gui {
    private ItemStack itemStack;
    private PlayerInteractEvent event;
    private double cost;
    private boolean purchased;

    public AnvilGui(PlayerInteractEvent event) {
        super(AnvilRepair.plugin, 3, ChatColor.translateAlternateColorCodes('&', AnvilRepair.plugin.getConfig().getString("settings.guiTitle")));
        this.itemStack = event.getItem();
        this.event = event;
        initialize();
        setOnGlobalClick(e -> e.setCancelled(true));
        setOnClose(e -> {
            if (!purchased) event.getPlayer().getInventory().addItem(itemStack);
            purchased = true;
        });
        show(event.getPlayer());
    }

    private void initialize() {
        StaticPane pane = new StaticPane(9,3);
        calculateCost();

        GuiItem cancel = new GuiItem(new ItemBuilder(PremadeItems.getPane(PaneType.RED)).displayname("&cCancel").lore("&7Click here", "&7to cancel").build(), e -> e.getWhoClicked().closeInventory());
        pane.addItem(cancel, 2, 1);

        GuiItem accept = new GuiItem(new ItemBuilder(PremadeItems.getPane(PaneType.GREEN)).displayname("&aAccept").lore("&7Click here", "&7to accept").build(), e -> {
            if (!canPay()) return;
            purchased = true;
            new RepairTask(event).runTaskTimer(AnvilRepair.plugin, 4, 10);
            e.getWhoClicked().closeInventory();
        });
        pane.addItem(accept, 6, 1);

        List<String> guiCost = AnvilRepair.plugin.getConfig().getStringList("formats.guiCost");
        guiCost.replaceAll(string -> string.replaceAll("<price>", String.valueOf(cost)));

        GuiItem item = new GuiItem(new ItemBuilder(event.getItem().clone()).lore(guiCost).build());
        pane.addItem(item, 4, 1);

        addPane(pane);
    }

    private void calculateCost() {
        if (AnvilRepair.plugin.getConfig().getBoolean("settings.payBasePrice")) {
            cost += AnvilRepair.plugin.getConfig().getDouble("prices.basePrice");
        }

        if (AnvilRepair.plugin.getConfig().getBoolean("settings.payPerDurability")) {
            cost += event.getItem().getDurability() * AnvilRepair.plugin.getConfig().getDouble("prices.pricePerDurability");
        }
    }

    private boolean canPay() {
        Economy econ = AnvilRepair.getEconomy();
        if (econ.has(event.getPlayer(), cost)) {
            econ.withdrawPlayer(event.getPlayer(), cost);
            event.getPlayer().sendMessage(AnvilRepair.color(AnvilRepair.plugin.getConfig().getString("messages.acceptMessage").replaceAll("<price>", String.valueOf(cost))));
            return true;
        } else {
            event.getPlayer().sendMessage(AnvilRepair.color(AnvilRepair.plugin.getConfig().getString("messages.notEnoughMoney")));
            return false;
        }
    }
}
