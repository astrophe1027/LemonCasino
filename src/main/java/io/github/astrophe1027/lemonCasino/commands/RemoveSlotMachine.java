package io.github.astrophe1027.lemonCasino.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.astrophe1027.lemonCasino.SlotMachine.slotMachineLocation;
import static io.github.astrophe1027.lemonCasino.commands.SetSlotMachine.parseRelativeInt;

public class RemoveSlotMachine implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용 가능합니다.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 3) {
            player.sendMessage("§c사용법: /removeslotmachine <x> <y> <z>");
            return true;
        }

        try {
            // 플레이어의 현재 위치를 정수로 변환 (소수점 버림)
            int currentX = player.getLocation().getBlockX();
            int currentY = player.getLocation().getBlockY();
            int currentZ = player.getLocation().getBlockZ();

            // 입력값 파싱 (상대좌표 ~ 포함)
            int x = parseRelativeInt(currentX, args[0]);
            int y = parseRelativeInt(currentY, args[1]);
            int z = parseRelativeInt(currentZ, args[2]);

            // Location 객체 생성 (정수값으로 전달)
            if (slotMachineLocation.contains(new Location(player.getWorld(), x, y, z))) {
                slotMachineLocation.remove(new Location(player.getWorld(), x, y, z));
                player.sendMessage(String.format("§a슬롯머신 좌표 삭제: [%d, %d, %d]", x, y, z));
            }

        } catch (NumberFormatException e) {
            player.sendMessage("§c좌표에는 정수 또는 '~'만 입력 가능합니다.");
        }

        return true;
    }
}
