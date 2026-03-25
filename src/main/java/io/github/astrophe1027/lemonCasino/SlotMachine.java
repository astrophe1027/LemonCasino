package io.github.astrophe1027.lemonCasino;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static io.github.astrophe1027.lemonCasino.LemonCasino.casinoPlugin;

public class SlotMachine implements Listener {

    Map<UUID, Inventory> slotMachine = new HashMap<>();
    Map<UUID, Byte> slotMachineProgress = new HashMap<>();
    Map<UUID, BukkitTask> slotMachineTask = new HashMap<>();
    public static Set<Location> slotMachineLocation = new HashSet<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && slotMachineLocation != null && slotMachineLocation.contains(e.getClickedBlock().getLocation())) {
            Inventory inventory = Bukkit.createInventory(null, 54, Component.text("슬롯머신"));
            slotMachine.put(e.getPlayer().getUniqueId(), inventory);
            ItemStack empty = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
            ItemMeta itemMeta = empty.getItemMeta();
            itemMeta.displayName(Component.text(""));
            itemMeta.setUnbreakable(true);
            itemMeta.setHideTooltip(true);
            empty.setItemMeta(itemMeta);
            red.setItemMeta(itemMeta);
            empty.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            red.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            for (int i = 0; i < 46; i++) {
                inventory.setItem(i, empty);
            }
            inventory.setItem(53, empty);
            inventory.setItem(20, red);
            inventory.setItem(24, red);
            ItemStack lever = new ItemStack(Material.LEVER);
            itemMeta = lever.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            itemMeta.displayName(Component.text("레버").color(TextColor.color(0, 255, 50)).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
            lever.setItemMeta(itemMeta);
            itemMeta.displayName(Component.text("배팅 슬롯").decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
            ItemStack dia = new ItemStack(Material.DIAMOND);
            dia.setItemMeta(itemMeta);
            itemMeta.displayName(Component.text("레버를 당기세요!").decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
            ItemStack cop = new ItemStack(Material.COPPER_INGOT);
            ItemStack iron = new ItemStack(Material.IRON_INGOT);
            ItemStack gold = new ItemStack(Material.GOLD_INGOT);
            cop.setItemMeta(itemMeta);
            iron.setItemMeta(itemMeta);
            gold.setItemMeta(itemMeta);
            inventory.setItem(10, dia);
            inventory.setItem(19, new ItemStack(Material.AIR));
            dia.setItemMeta(itemMeta);
            inventory.setItem(25, lever);
            inventory.setItem(30, dia);
            inventory.setItem(31, cop);
            inventory.setItem(32, iron);
            inventory.setItem(21, cop);
            inventory.setItem(22, iron);
            inventory.setItem(23, gold);
            inventory.setItem(12, iron);
            inventory.setItem(13, gold);
            inventory.setItem(14, dia);
            e.getPlayer().openInventory(slotMachine.get(e.getPlayer().getUniqueId()));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        if(slotMachine.containsValue(e.getInventory())) {
            if(!slotMachineProgress.containsKey(e.getPlayer().getUniqueId())) {
                if (slotMachineTask.containsKey(e.getPlayer().getUniqueId())) {
                    slotMachineTask.get(e.getPlayer().getUniqueId()).cancel();
                    slotMachineTask.remove(e.getPlayer().getUniqueId());
                }
                slotMachineProgress.remove(e.getPlayer().getUniqueId());
                ItemStack[] itemStacks = slotMachine.get(e.getPlayer().getUniqueId()).getContents();
                for (ItemStack is : itemStacks) {
                    if(is != null && !is.getItemMeta().isUnbreakable()) {
                        e.getPlayer().getInventory().addItem(is);
                    }
                }
                slotMachine.remove(e.getPlayer().getUniqueId());
            } else {
                /*
                slotMachine.remove(e.getPlayer().getUniqueId());
                if (slotMachineTask.containsKey(e.getPlayer().getUniqueId())) {
                    slotMachineTask.get(e.getPlayer().getUniqueId()).cancel();
                    slotMachineTask.remove(e.getPlayer().getUniqueId());
                }
                slotMachineProgress.remove(e.getPlayer().getUniqueId());
                e.getPlayer().sendMessage("기계 작동 중간에 탈주시 원금 회수가 불가합니다!");
                */
                Bukkit.getServer().getScheduler().runTask(casinoPlugin, () -> e.getPlayer().openInventory(slotMachine.get(e.getPlayer().getUniqueId())));
            }
        }
    }
    List<ItemStack> items;
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(slotMachine.containsValue(e.getClickedInventory()) && e.getCurrentItem() != null && e.getCurrentItem().getItemMeta().isUnbreakable()) {
            e.setCancelled(true);
            final Player player = (Player) e.getWhoClicked();
            Inventory inventory = e.getInventory();
            if (e.getSlot() == 25) {
                if(!slotMachineProgress.containsKey(player.getUniqueId())) {
                    if (inventory.getItem(19) == null || inventory.getItem(19).getType() == Material.AIR) {
                        player.sendMessage(Component.text("배팅을 해주세요!").color(TextColor.color(255, 0, 6)));
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
                    } else if (inventory.getItem(19).getType() == Material.DRAGON_EGG) {
                        player.sendMessage(Component.text("드래곤의 알은 배팅이 불가합니다.").color(TextColor.color(255, 0, 6)));
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
                    } else {
                        if(items == null) {
                            items = new ArrayList<>();
                            ItemMeta itemMeta = new ItemStack(Material.DIRT).getItemMeta();
                            itemMeta.displayName(Component.text("레버를 당기세요!").decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
                            itemMeta.setUnbreakable(true);
                            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                            ItemStack is = new ItemStack(Material.COPPER_INGOT);
                            is.setItemMeta(itemMeta);
                            items.add(is);
                            is = new ItemStack(Material.IRON_INGOT);
                            is.setItemMeta(itemMeta);
                            items.add(is);
                            is = new ItemStack(Material.GOLD_INGOT);
                            is.setItemMeta(itemMeta);
                            items.add(is);
                            is = new ItemStack(Material.DIAMOND);
                            is.setItemMeta(itemMeta);
                            items.add(is);
                        }
                        player.sendMessage(Component.text("시작").color(TextColor.color(0, 255, 50)));
                        slotMachineProgress.put(player.getUniqueId(), (byte) 0);
                        //초기화 시키기
                        ItemStack cop = items.get(0);
                        ItemStack iron = items.get(1);
                        ItemStack gold = items.get(2);
                        ItemStack dia = items.get(3);
                        inventory.setItem(30, dia);
                        inventory.setItem(31, cop);
                        inventory.setItem(32, iron);
                        inventory.setItem(21, cop);
                        inventory.setItem(22, iron);
                        inventory.setItem(23, gold);
                        inventory.setItem(12, iron);
                        inventory.setItem(13, gold);
                        inventory.setItem(14, dia);
                        slotMachineTask.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(casinoPlugin, () -> {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f);
                            Byte progress = slotMachineProgress.get(player.getUniqueId());

                            switch (progress) {
                                case 0:
                                    inventory.setItem(30, inventory.getItem(21));
                                    inventory.setItem(21, items.get((items.indexOf(inventory.getItem(21))+1) == 4 ? 0 : (items.indexOf(inventory.getItem(21))+1)));
                                    inventory.setItem(12, items.get((items.indexOf(inventory.getItem(21))+1) == 4 ? 0 : (items.indexOf(inventory.getItem(21))+1)));
                                case 1:
                                    inventory.setItem(31, inventory.getItem(22));
                                    inventory.setItem(22, items.get((items.indexOf(inventory.getItem(22))+1) == 4 ? 0 : (items.indexOf(inventory.getItem(22))+1)));
                                    inventory.setItem(13, items.get((items.indexOf(inventory.getItem(22))+1) == 4 ? 0 : (items.indexOf(inventory.getItem(22))+1)));
                                case 2:
                                    inventory.setItem(32, inventory.getItem(23));
                                    inventory.setItem(23, items.get((items.indexOf(inventory.getItem(23))+1) == 4 ? 0 : (items.indexOf(inventory.getItem(23))+1)));
                                    inventory.setItem(14, items.get((items.indexOf(inventory.getItem(23))+1) == 4 ? 0 : (items.indexOf(inventory.getItem(23))+1)));
                            }
                        }, 0, 2));
                    }
                } else {
                    if(slotMachineProgress.get(player.getUniqueId()) < 2) {
                        //랜덤 처리하기
                        switch (slotMachineProgress.get(player.getUniqueId())) {
                            case 0:
                                inventory.setItem(21, items.get(new Random().nextInt(items.size())));
                                inventory.setItem(30, items.get((items.indexOf(inventory.getItem(21))-1) == -1 ? 3 : (items.indexOf(inventory.getItem(21))-1)));
                                inventory.setItem(12, items.get((items.indexOf(inventory.getItem(21))+1) == 4 ? 0 : (items.indexOf(inventory.getItem(21))+1)));
                            case 1:
                                inventory.setItem(22, items.get(new Random().nextInt(items.size())));
                                inventory.setItem(31, items.get((items.indexOf(inventory.getItem(22))-1) == -1 ? 3 : (items.indexOf(inventory.getItem(22))-1)));
                                inventory.setItem(13, items.get((items.indexOf(inventory.getItem(22))+1) == 4 ? 0 : (items.indexOf(inventory.getItem(22))+1)));
                            case 2:
                                inventory.setItem(23, items.get(new Random().nextInt(items.size())));
                                inventory.setItem(32, items.get((items.indexOf(inventory.getItem(23))-1) == -1 ? 3 : (items.indexOf(inventory.getItem(23))-1)));
                                inventory.setItem(14, items.get((items.indexOf(inventory.getItem(23))+1) == 4 ? 0 : (items.indexOf(inventory.getItem(23))+1)));
                        }
                        slotMachineProgress.replace(player.getUniqueId(), (byte) (slotMachineProgress.get(player.getUniqueId())+1));
                        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 6f, 1f);
                    } else {
                        slotMachineProgress.remove(player.getUniqueId());
                        slotMachineTask.get(player.getUniqueId()).cancel();
                        slotMachineTask.remove(player.getUniqueId());
                        //보상코드
                        ItemStack is = inventory.getItem(19);
                        List<Material> slot = new ArrayList<>();
                        slot.add(inventory.getItem(21).getType());
                        slot.add(inventory.getItem(22).getType());
                        slot.add(inventory.getItem(23).getType());
                        if(Collections.frequency(slot, Material.GOLD_INGOT) > 1 || Collections.frequency(slot, Material.DIAMOND) > 1) {
                            if(Collections.frequency(slot, Material.GOLD_INGOT) > 2) {
                                inventory.setItem(19, new ItemStack(Material.MINECART));
                                for (int i = 0; i < 6; i++) {
                                    inventory.addItem(is);
                                }
                                inventory.setItem(19, new ItemStack(Material.AIR));
                                player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
                            } else if(Collections.frequency(slot, Material.DIAMOND) > 2) {
                                inventory.setItem(19, new ItemStack(Material.MINECART));
                                for (int i = 0; i < 7; i++) {
                                    inventory.addItem(is);
                                }
                                inventory.setItem(19, new ItemStack(Material.AIR));
                                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                            } else {
                                inventory.setItem(19, new ItemStack(Material.MINECART));
                                for (int i = 0; i < 2; i++) {
                                    inventory.addItem(is);
                                }
                                inventory.setItem(19, new ItemStack(Material.AIR));
                                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1f, 1f);
                            }
                        } else if(Collections.frequency(slot, Material.COPPER_INGOT) > 2) {
                            inventory.setItem(19, new ItemStack(Material.MINECART));
                            for (int i = 0; i < 4; i++) {
                                inventory.addItem(is);
                            }
                            inventory.setItem(19, new ItemStack(Material.AIR));
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                        } else if(Collections.frequency(slot, Material.IRON_INGOT) > 2) {
                            inventory.setItem(19, new ItemStack(Material.MINECART));
                            for (int i = 0; i < 5; i++) {
                                inventory.addItem(is);
                            }
                            inventory.setItem(19, new ItemStack(Material.AIR));
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                        } else {
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
                            inventory.setItem(19, new ItemStack(Material.AIR));
                        }
                    }
                }
            }
        }
        if(slotMachine.containsValue(e.getInventory()) && e.getSlot() == 19 && slotMachineProgress.containsKey(e.getWhoClicked().getUniqueId())) {
            e.getWhoClicked().sendMessage(Component.text("이미 시작한 후에는 원금 회수가 불가합니다!").color(TextColor.color(255, 0, 6)));
            ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
            e.setCancelled(true);
        }
    }
}
