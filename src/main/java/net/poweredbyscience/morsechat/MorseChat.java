package net.poweredbyscience.morsechat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by John on 1/29/2015.
 *
 * Some people just wanna watch the world burn, I on the other hand wanna set it on fire.
 */

public class MorseChat extends JavaPlugin implements Listener {

    public static HashMap<Character, String> letterSets = new HashMap<Character, String>();
    public static HashMap<String, String> messageQueue = new HashMap<String, String>();
    Sound clickSound = Sound.UI_BUTTON_CLICK;
    public boolean isPlaying;

    public void onEnable() {
        addMorse();
        isPlaying = false;
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        Player p = ev.getPlayer();
        if (isPlaying) {
            messageQueue.put(p.getName(), translate(ev.getMessage()));
            p.sendMessage(ChatColor.AQUA + "Your message has been queued");
        } else {
            for (Player playa : Bukkit.getOnlinePlayers()) {
                doSound(playa, translate(ev.getMessage()));
            }
        }
        ev.setCancelled(true);
    }

    public void doSound(final Player p, String message) {
        isPlaying = true;
        final Location loc = p.getLocation();
        final ArrayList<String> morseQueue = new ArrayList<String>();
        for (String key : message.split("")) {
            morseQueue.add(key);
        }

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
        }.runTaskTimer(this, 5,5);
    }

    public void playNext() {
        if (!messageQueue.isEmpty()) {
            String from = messageQueue.keySet().iterator().next();
            String message = messageQueue.get(from);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(ChatColor.AQUA + ">Playing message from " + from);
                doSound(p, message);
            }
            messageQueue.remove(from);
        }
    }

    public static String translate(String sentence) {
        char[] normalChars = sentence.toLowerCase().toCharArray();
        StringBuilder encodedMorse = new StringBuilder();
        for (char c : normalChars) {
            if (letterSets.containsKey(c)) {
                encodedMorse.append(letterSets.get(c));
            }
        }
        return encodedMorse.toString();
    }

    public static void addMorse() {
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

    public static void main(String[] args) {
        addMorse();
        Scanner r =  new Scanner(System.in);
        System.out.println(translate(r.nextLine()));
    }
}
