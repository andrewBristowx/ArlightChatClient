package com.arlight.chatclient;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
public final class LuckyWaitingOverlay {
    private static boolean visible; private static int players=1,maxPlayers=8; private static String map="Por elegir"; private static Object connectionIdentity;
    private LuckyWaitingOverlay(){}
    public static int players(){return players;} public static int maxPlayers(){return maxPlayers;} public static String map(){return map;} public static boolean isVisible(){return visible;}
    public static void accept(String command){if(command==null)return;String[] p=command.split("\\|",-1);if(p.length==0)return;if("HIDE".equalsIgnoreCase(p[0])){hide();return;}if(!"SHOW".equalsIgnoreCase(p[0])&&!"UPDATE".equalsIgnoreCase(p[0]))return;players=p.length>1?parse(p[1],1):1;maxPlayers=p.length>2?parse(p[2],8):8;map=p.length>3&&!p[3].isBlank()?p[3]:"Por elegir";visible=true;Minecraft mc=Minecraft.getInstance();connectionIdentity=mc.getConnection();HudVisibilityController.acquire("lucky_waiting");if(!(mc.screen instanceof LuckyWaitingScreen))mc.setScreen(new LuckyWaitingScreen());}
    public static void leaveQueue(){Minecraft mc=Minecraft.getInstance();if(mc.getConnection()!=null)mc.getConnection().sendCommand("lucky leave");hide();}
    public static void hide(){visible=false;Minecraft mc=Minecraft.getInstance();if(mc.screen instanceof LuckyWaitingScreen)mc.setScreen(null);HudVisibilityController.release("lucky_waiting");}
    @SubscribeEvent public static void onLogout(ClientPlayerNetworkEvent.LoggingOut e){visible=false;players=1;maxPlayers=8;map="Por elegir";connectionIdentity=null;HudVisibilityController.release("lucky_waiting");}
    public static void validateConnection(){Minecraft mc=Minecraft.getInstance();Object current=mc.getConnection();if(connectionIdentity!=null&&connectionIdentity!=current)hide();connectionIdentity=current;}
    private static int parse(String value,int fallback){try{return Integer.parseInt(value);}catch(NumberFormatException e){return fallback;}}
}
