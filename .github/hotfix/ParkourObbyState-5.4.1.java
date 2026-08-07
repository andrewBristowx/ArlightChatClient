package com.arlight.chatclient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class ParkourObbyState {
   private static long clientTick;
   private static TimedText guide;
   private static TimedText danger;
   private static TimedText safeColor;
   private static TimedText traffic;
   private static TimedText failure;
   private static StageData stage;
   private static final Map<String, Boolean> EFFECTS = new LinkedHashMap<>();
   private static final List<DirectionalFloor> FLOORS = new ArrayList<>();
   private static Champion champion;

   private ParkourObbyState() { }

   public static void accept(byte[] bytes) {
      try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
         if (in.readInt() != 2) return;
         String type = in.readUTF();
         int count = Math.max(0, Math.min(64, in.readInt()));
         List<Object> values = new ArrayList<>(count);
         for (int i = 0; i < count; i++) {
            values.add(switch (in.readByte()) {
               case 1 -> in.readInt();
               case 2 -> in.readBoolean();
               case 3 -> in.readLong();
               default -> in.readUTF();
            });
         }
         handle(type, values);
      } catch (IOException | RuntimeException ignored) { }
   }

   private static void handle(String type, List<Object> v) {
      switch (type) {
         case "STAGE" -> stage = new StageData(integer(v, 0), string(v, 1), string(v, 2), clientTick + 240L);
         case "DANGER" -> danger = timed(string(v, 0), integer(v, 1));
         case "SAFE_COLOR" -> safeColor = timed("COLOR SEGURO: " + prettify(string(v, 0)), integer(v, 1));
         case "TRAFFIC_LIGHT" -> traffic = timed(string(v, 0).equalsIgnoreCase("GREEN") ? "LUZ VERDE · AVANZA" : "LUZ ROJA · DETENTE", integer(v, 1));
         case "EFFECT" -> EFFECTS.put(string(v, 0).toUpperCase(), bool(v, 1));
         case "MECHANIC_GUIDE" -> guide = new TimedText(string(v, 1), string(v, 2), clientTick + Math.max(20, integer(v, 3)));
         case "DIRECTIONAL_FLOOR" -> addFloor(new DirectionalFloor(integer(v, 0), integer(v, 1), integer(v, 2), integer(v, 3), integer(v, 4), string(v, 5), bool(v, 6), clientTick + 1200L));
         case "FAILURE_REASON" -> failure = new TimedText("INTENTO FALLIDO", string(v, 0), clientTick + 90L);
         case "INFINITE_CHAMPION" -> champion = new Champion(uuid(string(v, 0)), string(v, 1), integer(v, 2), integer(v, 3));
         case "CLEAR" -> clearTransient();
         default -> { }
      }
   }

   private static void addFloor(DirectionalFloor floor) {
      FLOORS.removeIf(existing -> existing.sameArea(floor));
      FLOORS.add(floor);
      if (FLOORS.size() > 32) FLOORS.remove(0);
   }

   @SubscribeEvent
   public static void tick(ClientTickEvent.Post event) {
      clientTick++;
      if (Minecraft.getInstance().level == null) clearAll();
      FLOORS.removeIf(floor -> floor.expiresAt < clientTick);
   }

   public static long tick() { return clientTick; }
   public static TimedText guide() { return alive(guide) ? guide : null; }
   public static TimedText danger() { return alive(danger) ? danger : null; }
   public static TimedText safeColor() { return alive(safeColor) ? safeColor : null; }
   public static TimedText traffic() { return alive(traffic) ? traffic : null; }
   public static TimedText failure() { return alive(failure) ? failure : null; }
   public static StageData stage() { return stage != null && stage.expiresAt > clientTick ? stage : null; }
   public static Map<String, Boolean> effects() { return Map.copyOf(EFFECTS); }
   public static List<DirectionalFloor> floors() { return List.copyOf(FLOORS); }
   public static Champion champion() { return champion; }

   public static void clearTransient() {
      guide = danger = safeColor = traffic = failure = null;
      stage = null;
      EFFECTS.clear();
      FLOORS.clear();
   }

   public static void clearAll() { clearTransient(); champion = null; }

   private static TimedText timed(String text, int ticks) { return new TimedText("", text, clientTick + Math.max(20, ticks)); }
   private static boolean alive(TimedText value) { return value != null && value.expiresAt > clientTick; }
   private static String string(List<Object> values, int i) { return i < values.size() ? String.valueOf(values.get(i)) : ""; }
   private static int integer(List<Object> values, int i) { return i < values.size() && values.get(i) instanceof Number number ? number.intValue() : 0; }
   private static boolean bool(List<Object> values, int i) { return i < values.size() && values.get(i) instanceof Boolean value && value; }
   private static UUID uuid(String value) { try { return UUID.fromString(value); } catch (RuntimeException ignored) { return new UUID(0L, 0L); } }
   private static String prettify(String value) { return value.toLowerCase().replace('_', ' '); }

   public record TimedText(String title, String text, long expiresAt) { }
   public record StageData(int index, String stage, String difficulty, long expiresAt) { }
   public record Champion(UUID uuid, String name, int checkpoint, int modules) { }
   public record DirectionalFloor(int minX, int y, int minZ, int maxX, int maxZ, String direction, boolean active, long expiresAt) {
      boolean sameArea(DirectionalFloor other) { return minX == other.minX && y == other.y && minZ == other.minZ && maxX == other.maxX && maxZ == other.maxZ; }
   }
}
