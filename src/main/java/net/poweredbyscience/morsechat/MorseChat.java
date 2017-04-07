package net.poweredbyscience.morsechat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by John on 1/29/2015.
 * <p>
 * Some people just wanna watch the world burn, I on the other hand wanna set it on fire.
 */

public class MorseChat extends JavaPlugin implements Listener {
    private static ArrayList<UUID> morsers = new ArrayList<>();
    private static HashMap<Character, String> letterSets = new HashMap<>();
    private static HashMap<String, Pair<String, Pair<String, Set<Player>>>> messageQueue = new HashMap<>();
    private boolean isPlaying = false;
    private Sound clickSound = Sound.UI_BUTTON_CLICK;

    private static String translate(String sentence) {
        char[] normalChars = sentence.toLowerCase().toCharArray();
        StringBuilder encodedMorse = new StringBuilder();
        for (char c : normalChars) {
            if (letterSets.containsKey(c)) {
                encodedMorse.append(letterSets.get(c));
            }
        }
        return encodedMorse.toString();
    }

    private static void addMorse() {
        letterSets.put(' ', "+");
        letterSets.put('.', ".-.-.-");
        letterSets.put(',', "--..--");
        letterSets.put('a', ".-");
        letterSets.put('b', "-...");
        letterSets.put('c', "-.-.");
        letterSets.put('d', "-..");
        letterSets.put('e', ".");
        letterSets.put('f', "..-.");
        letterSets.put('g', "--.");
        letterSets.put('h', "....");
        letterSets.put('i', "..");
        letterSets.put('j', ".---");
        letterSets.put('k', "-.-");
        letterSets.put('l', ".-..");
        letterSets.put('m', "--");
        letterSets.put('n', "-.");
        letterSets.put('o', "---");
        letterSets.put('p', ".--.");
        letterSets.put('q', "--.-");
        letterSets.put('r', ".-.");
        letterSets.put('s', "...");
        letterSets.put('t', "-");
        letterSets.put('u', "..-");
        letterSets.put('v', "...-");
        letterSets.put('w', ".--");
        letterSets.put('x', "-..-");
        letterSets.put('y', "-.--");
        letterSets.put('z', "--..");
    }

    public void onEnable() {
        addMorse();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("morse")) {
            if (sender instanceof Player) {
                UUID pid = ((Player) sender).getUniqueId();
                if (morsers.contains(pid)) {
                    morsers.remove(pid);
                    sender.sendMessage(ChatColor.GREEN + "Successfully disabled Morse Code!");
                } else {
                    morsers.add(pid);
                    sender.sendMessage(ChatColor.GREEN + "Successfully enabled Morse Code!");
                }
            } else {
                sender.sendMessage("You MUST be a player to run this command");
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        ev.getPlayer().sendMessage(ChatColor.AQUA + ">Morse code is currently disabled, to enable it use the command /morse");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        Player p = ev.getPlayer();
        if (isPlaying) {
            messageQueue.put(p.getName(), new Pair<>(translate(ev.getMessage()), new Pair<>(ev.getFormat(), ev.getRecipients())));
            p.sendMessage(ChatColor.AQUA + "Your message has been queued");
        } else {
            for (Player playa : ev.getRecipients()) {
                if (morsers.contains(playa.getUniqueId())) {
                    playa.sendMessage(ChatColor.AQUA + ">Playing message from " + p.getDisplayName());
                    doSound(playa, translate(ev.getMessage()));
                } else
                    playa.sendMessage(String.format(ev.getFormat(), ev.getPlayer().getDisplayName(), ev.getMessage()));
            }
        }
        ev.setCancelled(true);
    }

    private void doSound(final Player p, String message) {
        isPlaying = true;
        final Location loc = p.getLocation();
        final ArrayList<String> morseQueue = new ArrayList<>();
        Collections.addAll(morseQueue, message.split(""));

        new BukkitRunnable() {
            int eresting = 0;

            @Override
            public void run() {
                if (morseQueue.get(eresting).equals(".")) {
                    p.playSound(loc, clickSound, 1, 2);
                }
                if (morseQueue.get(eresting).equals("-")) {
                    p.playSound(loc, clickSound, 1, 1);
                }
                if (morseQueue.get(eresting).equals("+")) {
                    p.playSound(loc, clickSound, 0, 0);
                }
                eresting++;
                if (eresting >= morseQueue.size()) {
                    morseQueue.clear();
                    isPlaying = false;
                    playNext();
                    this.cancel();
                }
            }

            private void playNext() {
                if (!messageQueue.isEmpty()) {
                    String from = messageQueue.keySet().iterator().next();
                    String message = messageQueue.get(from).getFirst();
                    for (Player p : messageQueue.get(from).getSecond().getSecond()) {
                        if (morsers.contains(p.getUniqueId())) {
                            p.sendMessage(ChatColor.AQUA + ">Playing message from " + from);
                            doSound(p, message);
                        } else {
                            String format = messageQueue.get(from).getSecond().getFirst();
                            p.sendMessage(String.format(format, from, message));
                        }
                    }
                    messageQueue.remove(from);
                }
            }
        }.runTaskTimer(this, 5, 5);
    }

    class Pair<U, T> {
        U a;
        T b;

        Pair(U a, T b) {
            this.a = a;
            this.b = b;
        }

        U getFirst() {
            return a;
        }

        T getSecond() {
            return b;
        }
    }

}

