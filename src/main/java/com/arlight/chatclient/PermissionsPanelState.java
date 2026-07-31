package com.arlight.chatclient;
import net.minecraft.client.Minecraft;import java.nio.charset.StandardCharsets;import java.util.*;
public final class PermissionsPanelState {
 public record Entry(String node,String name,String description){}
 public record Category(String id,String name,String icon,String description,List<Entry> entries){}
 public record OnlinePlayer(UUID uuid,String name){}
 private static final Base64.Decoder B64=Base64.getUrlDecoder();
 private static List<Category> categories=List.of();private static List<String> groups=List.of();private static List<OnlinePlayer> players=List.of();private static String selectedType="",selectedName="",toast="";private static boolean toastError;
 private PermissionsPanelState(){}
 public static void accept(String raw){String[] p=raw.split("\\|",-1);if(p.length==0)return;if(p[0].equals("ERROR")||p[0].equals("SUCCESS")){toast=p.length>1?dec(p[1]):"";toastError=p[0].equals("ERROR");return;}if(!p[0].equals("OPEN")||p.length<6)return;selectedType=dec(p[1]);selectedName=dec(p[2]);categories=parseCategories(p[3]);groups=parseGroups(p[4]);players=parsePlayers(p[5]);Minecraft.getInstance().setScreen(new PermissionsAdminScreen());}
 private static List<Category> parseCategories(String raw){if(raw.isEmpty())return List.of();List<Category> out=new ArrayList<>();for(String c:raw.split(";")){String[] v=c.split(",",5);if(v.length<5)continue;List<Entry> es=new ArrayList<>();if(!v[4].isEmpty())for(String e:v[4].split("\\^")){String[] x=e.split("~",3);if(x.length==3)es.add(new Entry(dec(x[0]),dec(x[1]),dec(x[2])));}out.add(new Category(dec(v[0]),dec(v[1]),dec(v[2]),dec(v[3]),List.copyOf(es)));}return List.copyOf(out);}
 private static List<String> parseGroups(String raw){if(raw.isEmpty())return List.of();return Arrays.stream(raw.split(",")).map(PermissionsPanelState::dec).toList();}
 private static List<OnlinePlayer> parsePlayers(String raw){if(raw.isEmpty())return List.of();List<OnlinePlayer> out=new ArrayList<>();for(String e:raw.split(";")){String[] x=e.split(",",2);if(x.length==2)try{out.add(new OnlinePlayer(UUID.fromString(x[0]),dec(x[1])));}catch(Exception ignored){}}return List.copyOf(out);}
 static String enc(String s){return s==null||s.isEmpty()?"":Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8));}
 private static String dec(String s){try{return s==null||s.isEmpty()?"":new String(B64.decode(s),StandardCharsets.UTF_8);}catch(Exception e){return "";}}
 public static List<Category> categories(){return categories;}public static List<String> groups(){return groups;}public static List<OnlinePlayer> players(){return players;}public static String selectedType(){return selectedType;}public static String selectedName(){return selectedName;}public static String toast(){return toast;}public static boolean toastError(){return toastError;}public static void clearToast(){toast="";}
}
