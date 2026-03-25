package io.github.astrophe1027.lemonCasino;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.astrophe1027.lemonCasino.commands.ListSlotMachine;
import io.github.astrophe1027.lemonCasino.commands.RemoveSlotMachine;
import io.github.astrophe1027.lemonCasino.commands.SetSlotMachine;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import static io.github.astrophe1027.lemonCasino.SlotMachine.slotMachineLocation;
import static io.github.astrophe1027.lemonCasino.commands.SetSlotMachine.setSlotMachineTabCompleter;

public final class LemonCasino extends JavaPlugin implements CommandExecutor {
    static Plugin casinoPlugin;
    SlotMachine slotMachine = new SlotMachine();
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(slotMachine, this);
        casinoPlugin = this;
        getCommand("setslotmachine").setExecutor(new SetSlotMachine());
        getCommand("setslotmachine").setTabCompleter(setSlotMachineTabCompleter);
        getCommand("removeslotmachine").setExecutor(new RemoveSlotMachine());
        getCommand("removeslotmachine").setTabCompleter(setSlotMachineTabCompleter);
        getCommand("listslotmachine").setExecutor(new ListSlotMachine());

        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        File file = new File(getDataFolder(), "slotmachinelocation.json");
        if (file.exists()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (Reader reader = new FileReader(file)) {
                // 1. 데이터 타입을 List<Map<String, Object>> 형태로 정의하여 불러옵니다.
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> dataList = gson.fromJson(reader, listType);

                // 기존 리스트 초기화 (slotMachineLocation이 List<Location> 타입이라고 가정)
                slotMachineLocation = new HashSet<>();

                if (dataList != null) {
                    for (Map<String, Object> data : dataList) {
                        String worldName = (String) data.get("world");
                        List<Double> coordinates = (List<Double>) data.get("coordinates");

                        World world = Bukkit.getWorld(worldName);
                        if (world != null && coordinates != null && coordinates.size() >= 3) {
                            Location loc = new Location(world, coordinates.get(0), coordinates.get(1), coordinates.get(2));
                            // 2. 리스트에 위치 객체를 추가합니다.
                            slotMachineLocation.add(loc);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Map.Entry<UUID, Inventory> i : slotMachine.slotMachine.entrySet()) {
            ItemStack[] itemStacks = i.getValue().getContents();
            for (ItemStack is : itemStacks) {
                if(is != null && !is.getItemMeta().isUnbreakable()) {
                    Bukkit.getPlayer(i.getKey()).getInventory().addItem(is);
                }
            }
            i.getValue().close();
        }
        if (slotMachineLocation != null && !slotMachineLocation.isEmpty()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (!getDataFolder().exists()) getDataFolder().mkdirs();

            try (Writer writer = new FileWriter(new File(getDataFolder(), "slotmachinelocation.json"))) {
                // 1. 모든 위치 정보를 담을 리스트 생성
                List<Map<String, Object>> allData = new ArrayList<>();

                // 2. 리스트 내의 모든 Location을 순회하며 변환
                for (Location loc : slotMachineLocation) {
                    if (loc.getWorld() == null) continue;

                    Map<String, Object> data = new HashMap<>();
                    data.put("world", loc.getWorld().getName());
                    data.put("coordinates", List.of(
                            loc.getX(),
                            loc.getY(),
                            loc.getZ()
                    ));
                    allData.add(data);
                }

                // 3. 개별 객체가 아닌 리스트 전체를 JSON으로 저장
                gson.toJson(allData, writer);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
