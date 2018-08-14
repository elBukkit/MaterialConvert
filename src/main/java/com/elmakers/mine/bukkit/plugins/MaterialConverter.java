package com.elmakers.mine.bukkit.plugins;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MaterialConverter extends JavaPlugin implements Listener {
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    private final Map<Integer, Material> materialIdMap = new HashMap<Integer, Material>();

    @SuppressWarnings("deprecation")
    public void onEnable() {
        getCommand("convert").setExecutor(this);
        Object[] allMaterials = Material.AIR.getDeclaringClass().getEnumConstants();
        for (Object o : allMaterials) {
            Material material = (Material)o;
            materialIdMap.put(material.getId(), material);
        }
    }

    public void onDisable() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equals("convert")) return false;
        if (args.length == 0) {
            sendError(sender, "Usage: " + ChatColor.WHITE + "/convert <some string with material ids>");
            return true;
        }

        for (int i = 0; i < args.length; i++) {
            try {
                // handle CSV lists
                String[] pieces = StringUtils.split(args[i], ',');
                for (int j = 0; j < pieces.length; j++) {
                    Material material;
                    try {
                        int materialId = Integer.parseInt(pieces[j]);
                        material = materialIdMap.get(materialId);
                    } catch (Exception notint) {
                        material = Material.getMaterial(pieces[j].toUpperCase(), true);
                    }
                    if (material == null) continue;
                    if (material.isLegacy()) {
                        Material converted = Bukkit.getUnsafe().fromLegacy(material);
                        if (converted != null) {
                            material = converted;
                        }
                    }
                    pieces[j] = "Material." + material.name();
                }
                args[i] = StringUtils.join(pieces, ',');
            } catch (Exception ignore) {
            }
        }
        String converted = StringUtils.join(args, ' ');
        sendMessage(sender, converted);

        return true;
    }

    private void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    private void sendError(CommandSender sender, String string) {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}
