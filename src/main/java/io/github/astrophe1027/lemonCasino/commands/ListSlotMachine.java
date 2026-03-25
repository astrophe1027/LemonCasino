package io.github.astrophe1027.lemonCasino.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.astrophe1027.lemonCasino.SlotMachine.slotMachineLocation;

public class ListSlotMachine implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;
        if (slotMachineLocation.isEmpty()) {
            sender.sendMessage(Component.text("슬롯머신이 존재하지 않습니다.").color(TextColor.color(0xFF0006)).decoration(TextDecoration.BOLD, true));
        } else {
            for (Location location : slotMachineLocation) {
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
                String world = location.getWorld().getName();
                sender.sendMessage(Component.text(String.format("%s (%d, %d, %d)", world, x, y, z)).decoration(TextDecoration.BOLD, true).color(TextColor.color(0x00FF32)));
            }
        }
        return true;
    }
}
