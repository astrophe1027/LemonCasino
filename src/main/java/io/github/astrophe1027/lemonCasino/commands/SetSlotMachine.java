package io.github.astrophe1027.lemonCasino.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.astrophe1027.lemonCasino.SlotMachine.slotMachineLocation;

public class SetSlotMachine implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용 가능합니다.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 3) {
            player.sendMessage("§c사용법: /setslotmachine <x> <y> <z>");
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
            slotMachineLocation.add(new Location(player.getWorld(), x, y, z));

            player.sendMessage(String.format("§a슬롯머신 좌표 고정: [%d, %d, %d]", x, y, z));
        } catch (NumberFormatException e) {
            player.sendMessage("§c좌표에는 정수 또는 '~'만 입력 가능합니다.");
        }

        return true;
    }

    static int parseRelativeInt(int current, String input) {
        if (input.startsWith("~")) {
            if (input.length() == 1) return current;
            return current + Integer.parseInt(input.substring(1));
        }
        return Integer.parseInt(input);
    }

    public static TabCompleter setSlotMachineTabCompleter = (sender, command, alias, args) -> {
        if (!(sender instanceof Player)) return Collections.emptyList();

        Player player = (Player) sender;
        List<String> suggestions = new ArrayList<>();

        if (args.length <= 3) {
            // 바라보고 있는 블록의 정수 좌표 가져오기
            Block target = player.getTargetBlockExact(5);

            if (target != null) {
                if (args.length == 1) suggestions.add(String.valueOf(target.getX()));
                if (args.length == 2) suggestions.add(String.valueOf(target.getY()));
                if (args.length == 3) suggestions.add(String.valueOf(target.getZ()));
            } else {
                // 바라보는 블록이 없으면 ~ 추천
                suggestions.add("~");
            }
        }
        return suggestions;
    };
}
