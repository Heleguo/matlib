package me.matl114.matlib.implement.nms.network;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import io.netty.channel.*;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection.ListenedList;
import me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection.ListenedListImpl;
import me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection.UpdateListener;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.algorithms.designs.event.PriorityEventChannel;
import me.matl114.matlib.algorithms.designs.event.PriorityEventHandler;
import me.matl114.matlib.common.lang.annotations.VisibleForTesting;
import me.matl114.matlib.core.Manager;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.nmsMirror.impl.NMSLevel;

import me.matl114.matlib.nmsMirror.network.ServerConnectionHelper;
import me.matl114.matlib.nmsUtils.network.ConnectionLifeCycle;
import me.matl114.matlib.nmsUtils.network.GamePacket;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.EntityUtils;
import me.matl114.matlib.utils.reflect.LambdaUtils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static me.matl114.matlib.nmsMirror.impl.NMSNetwork.*;

public class PacketEventManager implements Manager, Listener {
    @Getter
    private static PacketEventManager manager;
    private Plugin plugin;
    private boolean registered = false;
    private ObfManager obf;
    private Object serverConnection;

    private List<ChannelFuture> serverChannelCache;
    private List<? extends SimpleChannelInboundHandler<?>> connections;
    private final Set<Channel> injectedChannels = ConcurrentHashMap.newKeySet();
    private final Map<Channel, ClientInformation> currentConnections = new ConcurrentHashMap<>();
    private String injectorS2CName ;
    private String injectorC2SName ;
    private Key paperInternalListenerKey;
    private String serverConnectionInjectorName;
    private String serverNewChannelInjectorName;

    private final Set<BiConsumer<Object, ClientInformation>> internalPacketListeners = new HashSet<>();
    @Getter
    @VisibleForTesting
    private final EnumMap<GamePacket, PriorityEventChannel<PacketEvent>> eventChannels = new EnumMap<>(GamePacket.class);


    public PacketEventManager(){
    }
    private void checkState(){
        Preconditions.checkArgument(registered, "This manager hasn't been initialized");
    }
    @Override
    public PacketEventManager init(Plugin pl, String... path) {
        Preconditions.checkArgument(!registered ,"This manager has been initialized");
        Preconditions.checkArgument(manager == null);
        addToRegistry();
        manager = this;
        this.plugin = pl;
        registered = true;
        String name = pl.getName().toLowerCase(Locale.ROOT);
        this.injectorS2CName = "matlib-connection-injector-s2c#pl_"+name;
        this.injectorC2SName = "matlib-connection-injector-c2s#pl_"+name;
        this.serverConnectionInjectorName = "matlib-server-connection-internal-injector#pl_"+name;
        this.paperInternalListenerKey = Key.key(name,"connection-injector-" + name);
        this.serverNewChannelInjectorName = "matlib-channel-internal-injector#pl_"+name;
        this.obf = ObfManager.getManager();
        this.serverConnection = Env.SERVER_CONNECTION;
        this.serverChannelCache = SERVER_CONNECTION.channelsGetter(this.serverConnection);
        this.connections = SERVER_CONNECTION.getConnections(this.serverConnection);
        this.injectedChannels.clear();
        this.currentConnections.clear();
        try{
            Class<?> itfChannelInitListener = Objects.requireNonNull(ServerConnectionHelper.CHANNEL_INIT_LISTENER_ITF);
            Preconditions.checkArgument(itfChannelInitListener.isInterface());
            Method invokeDynamicMethod = PacketEventManager.class.getDeclaredMethod("injectChannel",Channel.class);
            Object lambda = LambdaUtils.createLambdaBinding(itfChannelInitListener, invokeDynamicMethod).apply(this);
            Preconditions.checkArgument(itfChannelInitListener.isInstance(lambda));
            SERVER_CONNECTION.addListener(paperInternalListenerKey, lambda);
        }catch (Throwable e){
            Debug.logger(e, "Error while trying to inject connection using paper internal API, using default plan ...");
            defaultInjectPlan();
        }
        synchronized (this.connections){
            if(!this.connections.isEmpty()){
                Debug.logger("Looks like you are hot-loading this Manager, injecting into existing connections");
                for (var conn: this.connections){
                    //avoid null or something
                    if(CONNECTION.isConnection(conn)){
                        injectChannel(conn);
                    }
                }
            }
        }
        this.internalPacketListeners.clear();
        initEvents();
        registerFunctional();

        Debug.logger("PacketEvent Manager Enabled");
        return this;
    }
    private void registerFunctional(){
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    private void initEvents(){
        for (var type: GamePacket.values()){
            eventChannels.put(type, new PriorityEventChannel<>("PacketEvent"));
        }
        //start up event service

    }

    public <T> void registerListener(PacketListener listener, T owner){
        EnumMap<GamePacket,List< PriorityEventHandler<Pair<PacketListener, T>, PacketEvent>>> handlerEnumMap = new EnumMap<>(GamePacket.class);

        for (var methods: listener.getClass().getMethods()){
            if(methods.isSynthetic() || methods.isBridge() || Modifier.isStatic(methods.getModifiers())){
                continue;
            }
            if(methods.getParameterCount() != 1){
                continue;
            }
            if(!PacketEvent.class.isAssignableFrom( methods.getParameterTypes()[0])){
                continue;
            }
            var annotation = methods.getAnnotation(PacketHandler.class);
            if(annotation == null) {

                continue;
            }
            GamePacket type = annotation.type();
            int p = annotation.priority();
            boolean ignoreCancel = annotation.ignoreIfCancel();
            try{
                Consumer<PacketEvent> task = LambdaUtils.createLambdaBinding(Consumer.class, methods).apply(listener);
                PriorityEventHandler<Pair<PacketListener, T>,PacketEvent> handlerInstance = new PriorityEventHandler<>(
                    Pair.of(listener, owner),
                    p,
                    ignoreCancel,
                    task
                );
                handlerEnumMap.computeIfAbsent(type, (t)->new ArrayList<>()).add(handlerInstance);
            }catch (Throwable e){
                Debug.logger(e, "Error while registering PacketListener for owner",owner);
            }
        }
        for (var entry: handlerEnumMap.entrySet()){
            PriorityEventChannel<PacketEvent> channel = eventChannels.get(entry.getKey());
            Preconditions.checkNotNull(channel, "Trying to register packetlistener before the channel init");
            for (var handler: entry.getValue()){
                channel.registerHandler(handler);
            }
        }
    }
    public <T> void unregisterAll(T owner){
        for (var channel: eventChannels.values()){
            channel.unregisterAll(o->((Pair)o).getB()== owner);
        }
    }
    public <T> void unregisterAll(PacketListener listener, T owner){
        for (var channel: eventChannels.values()){
            channel.unregisterAll(o->{
                Pair p = (Pair) o;
                return p.getA() == listener && p.getB() == owner;
            });
        }
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerJoin(PlayerJoinEvent event){
        Object handle = EntityUtils.getEntityHandle(event.getPlayer());
        if(handle != null && NMSLevel.PLAYER.isPlayer(handle)){
            Object play = NMSLevel.PLAYER.connectionGetter(handle);
            SimpleChannelInboundHandler<?> connection = PLAY.connectionGetter(play);
            Channel channel = CONNECTION.channelGetter(connection);
            ClientInformation info = this.currentConnections.get(channel);
            if(info != null){

                GameProfile profile = NMSLevel.PLAYER.getGameProfile(handle);
                if(!Objects.equals(profile, info.gameProfile)){
                    info.gameProfile = profile;
                }
                info.player = PLAY.playerGetter(play);
                info.play = play;
                info.connection = connection;
                //finally this is complete, we confirm it
                if(info.state != ConnectionLifeCycle.DISCONNECT){
                    info.state = ConnectionLifeCycle.PLAY;
                }
                info.all = true;
                onClientEnable(info);
            }else {
                //why it is null? maybe mistake is happened
                //or maybe it is internal connection like fake player?
                injectChannel(channel, connection);
            }
        }
    }

    public void onClientEnable(ClientInformation information){
        PacketEvent event = new PacketEvent(GamePacket.CLIENT_REGISTER,information);
        fireEvent(event, GamePacket.CLIENT_REGISTER);
    }

    public void onClientDisable(ClientInformation information){
        PacketEvent event = new PacketEvent(GamePacket.CLIENT_UNREGISTER,information);
        fireEvent(event, GamePacket.CLIENT_UNREGISTER);
    }

    private void defaultInjectPlan(){
        synchronized (this.serverChannelCache){
            ListenedList<ChannelFuture> fakeList =  new ListenedListImpl<>(this.serverChannelCache, UpdateListener.add(this::injectServer), true);
            try{
                Field fields = Arrays.stream(this.serverConnection.getClass().getDeclaredFields())
                    .filter(m->m.getType() == List.class)
                    .filter(m->this.obf.deobfField(m).equals("channels"))
                    .findFirst()
                    .orElseThrow();
                fields.setAccessible(true);
                fields.set(this.serverConnection, fakeList);
                this.serverChannelCache = SERVER_CONNECTION.channelsGetter(this.serverConnection);
                Preconditions.checkArgument(this.serverChannelCache == fakeList);
            }catch (Throwable e){
                Debug.logger(e, "Error while trying to replace original connection list, but luckily we have injected them:",fakeList.getHandle());
            }
        }
    }
    //this is for inject server channel, not to-client channels, we should inject the method below into it and let it run at register to create specific listener for that to-client channel
    private void injectServer(ChannelFuture future){
        Channel channel = future.channel();
        if(this.injectedChannels.contains(channel)){
            //already exists, why?
            return;
        }else {
            ChannelPipeline pipeline = channel.pipeline();
            ChannelHandler handler = pipeline.get(serverConnectionInjectorName);
            if( handler != null){
                pipeline.remove(serverConnectionInjectorName);
            }
            if (pipeline.get("SpigotNettyServerChannelHandler#0") != null) {
                pipeline.addAfter("SpigotNettyServerChannelHandler#0", serverConnectionInjectorName, new S2CChannelListenerInjectorInitializer());
            }
            //Make sure we handle connections after Geyser.
            else if (pipeline.get("floodgate-init") != null) {
                pipeline.addAfter("floodgate-init", serverConnectionInjectorName, new S2CChannelListenerInjectorInitializer());
            }
            //Some forks add a handler which adds the other necessary vanilla handlers like (decoder, encoder, etc...)
            else if (pipeline.get("MinecraftPipeline#0") != null) {
                pipeline.addAfter("MinecraftPipeline#0", serverConnectionInjectorName, new S2CChannelListenerInjectorInitializer());
            }
            //Otherwise, make sure we are first.
            else {
                pipeline.addFirst(serverConnectionInjectorName, new S2CChannelListenerInjectorInitializer());
            }
        }
    }



    private class S2CChannelListenerInjectorInitializer extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
            Channel newInstance = (Channel)msg;
            newInstance.pipeline().addFirst(serverNewChannelInjectorName, new S2CChannelListenerInjector());
            super.channelRead(ctx, msg);
        }
    }
    private class S2CChannelListenerInjector extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            try{
                PacketEventManager.this.injectChannel(ctx.channel());
            }catch (Throwable e){
                exceptionCaught(ctx, e);
            }finally {
                ChannelPipeline pipeline = ctx.pipeline();
                if(pipeline.context(this) != null){
                    pipeline.remove(this);
                }
            }
            super.channelRegistered(ctx);
        }
    }
    protected void injectChannel(Channel channel){
        injectChannel(channel, null);
    }
    private void injectChannel(SimpleChannelInboundHandler<?> connection){
        Preconditions.checkArgument(CONNECTION.isConnection(connection));
        injectChannel(CONNECTION.channelGetter(connection), connection);
    }
    private void injectChannel(Channel channel, SimpleChannelInboundHandler<?> connection){

        ClientInformation client = null;
        if(connection != null){
            Object player = CONNECTION.getPlayer(connection);
            if(player != null){
                client = new ClientInformation(connection, player);
                onClientEnable(client);
            }
        }

        synchronized (channel){
            if(client == null){
                client = new ClientInformation(channel);
            }
            ClientInformation finalClient = client;

            try{
                Preconditions.checkNotNull(channel.pipeline().get("packet_handler"));
                Preconditions.checkArgument(channel.pipeline().get(this.injectorC2SName) == null);
                channel.pipeline().addBefore("packet_handler", this.injectorC2SName, new C2SPacketListener(this, finalClient));
                Preconditions.checkArgument(channel.pipeline().get(this.injectorS2CName) == null);
                channel.pipeline().addBefore("packet_handler", this.injectorS2CName, new S2CPacketListener(this, finalClient));
            }catch (Throwable e){
                Debug.logger(e, "Error while setting packet listeners in client connection!");
            }
            registerClient(channel, finalClient);
            channel.closeFuture().addListener(future -> onDisconnect(finalClient));

        }
    }
    private void registerClient(Channel channel, ClientInformation info){
        synchronized (channel){
            synchronized (this.currentConnections){
                this.currentConnections.put(channel, info);
            }
        }
    }
    private void unregisterClient(Channel channel, ClientInformation info){
        synchronized (channel){
            if(channel.pipeline().get(this.injectorS2CName) != null){
                channel.pipeline().remove(this.injectorS2CName);
            }
            if(channel.pipeline().get(this.injectorC2SName) != null){
                channel.pipeline().remove(this.injectorC2SName);
            }
            synchronized (this.currentConnections){
                this.currentConnections.remove(channel, info);
            }

        }
    }



    protected static class C2SPacketListener extends MessageToMessageDecoder{
        final PacketEventManager manager ;
        final ClientInformation client;

        public C2SPacketListener(PacketEventManager manager, ClientInformation info){
            this.manager = manager;
            this.client = info;
        }
        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, Object object, List list) throws Exception {
            Object object1 = this.manager.onPacketAccept(object, client);
            if(object1 != null){
                list.add(object1);
            }
        }
    }
    protected static class S2CPacketListener extends MessageToMessageEncoder {
        final PacketEventManager manager ;
        final ClientInformation client;

        public S2CPacketListener(PacketEventManager manager, ClientInformation info){
            this.manager = manager;
            this.client = info;
        }
        @Override
        protected void encode(ChannelHandlerContext channelHandlerContext, Object object, List list) throws Exception {
            Object object1 = this.manager.onPacketSend(object, client);
            if(object1 != null){
                list.add(object);
            }
        }
    }
    protected Object onPacketAccept(Object packet, ClientInformation info){
        if(info.ready()){
            packet = handlePacket(packet, info, false);
        }else if(!info.deprecated()){
            handleInternalStateChange(packet, info);
        }
        return packet;
    }
    protected Object onPacketSend(Object packet, ClientInformation info){
        if(info.ready()){
            packet = handlePacket(packet, info, true);
        }else if(!info.deprecated()){
            handleInternalStateChange(packet, info);
        }
        return packet;
    }
    protected Object handlePacket(Object packet, ClientInformation info, boolean s2c){
        PacketEvent event = new PacketEvent(packet, s2c, info, false);
        GamePacket gameType = event.enumType;
        fireEvent(event, GamePacket.ALL_PLAY);
        fireEvent(event, s2c ? GamePacket.ALL_S2C_PLAY :GamePacket.ALL_C2S_PLAY);
        fireEvent(event, gameType);
        if( event.isCancelled()){
            return null;
        }
        //in case of short path
        if(PACKETS.isBundlePacket(packet)){
            var subs = PACKETS.bundlePacket$SubPackets(packet);
            Set<Object> cancelled = null;
            for (var packet0: subs){
                PacketEvent event1 = new PacketEvent(packet0, s2c, info, true);
                GamePacket gameType1 = event1.enumType;
                fireEvent(event1, GamePacket.ALL_PLAY);
                fireEvent(event1, s2c ? GamePacket.ALL_S2C_PLAY :GamePacket.ALL_C2S_PLAY);
                fireEvent(event1, gameType1);
                if(event1.isCancelled()){
                    if(cancelled == null){
                        cancelled = new ReferenceOpenHashSet<>();
                        cancelled.add(packet0);
                    }
                }
            }
            if(cancelled != null){
                List<Object> newSent = new ArrayList<>();
                for (var packet0 : subs){
                    if(!cancelled.contains(packet0)){
                        newSent.add(packet0);
                    }
                }
                return PACKETS.newBundle(newSent);
            }
        }
        return packet;

    }

    private void fireEvent(PacketEvent event, GamePacket type){
        PriorityEventChannel<PacketEvent> eventChannel = eventChannels.get(type);
        if(eventChannel != null){
            eventChannel.dispatch(event);
        }
    }
    private void handleInternalStateChange(Object packet, ClientInformation info){
        for (var internalPacketListener : this.internalPacketListeners) {
            internalPacketListener.accept(packet, info);
        }
        if(PACKETS.isServerboundHelloPacket(packet)){
            //start handshake state
            info.state = ConnectionLifeCycle.LOGIN;
        }else if(PACKETS.isClientboundLoginFinishPacket(packet)){
            //handshake finish, start configuration
            info.gameProfile = PACKETS.clientboundLoginFinishPacket$GameProfile(packet);
            //just ignore CONFIGURATION state as lower version do not have this state
            info.state = ConnectionLifeCycle.PLAY;
        }else if(PACKETS.isClientboundLoginPacket(packet)){
            //finish, the player is initialized here, we can get the server player instance
            info.state = ConnectionLifeCycle.PLAY;
        }
    }

    //for player
    private void uninjectChannel(Channel channel){
        synchronized (channel){

        }
        ClientInformation information = this.currentConnections.get(channel);
        if(information != null){
            unregisterClient(channel,information);
        }

    }
    private void uninjectServer(ChannelFuture future){
        Channel serverChannel = future.channel();
        if(serverChannel.pipeline().get(serverConnectionInjectorName) != null){
            serverChannel.pipeline().remove(serverConnectionInjectorName);
        }
    }


    //for disconnect

    private void onDisconnect(ClientInformation clientInformation){
        clientInformation.state = ConnectionLifeCycle.DISCONNECT;
        onClientDisable(clientInformation);
        synchronized (clientInformation.channel){
            //what
        }
        unregisterClient(clientInformation.channel, clientInformation);

    }


    @Override
    public PacketEventManager reload() {
        deconstruct();
        return init(this.plugin);
    }

    @Override
    public boolean isAutoDisable() {
        return true;
    }

    @Override
    public void deconstruct() {
        manager = null;
        removeFromRegistry();
        unregisterFunctional();
        try{
            SERVER_CONNECTION.removeListener(paperInternalListenerKey);
        }catch (Throwable e){
            //may fail but we choose to ignore it, if init success, then it should be successful
        }
        if(this.serverConnection != null){
            this.serverChannelCache = SERVER_CONNECTION.channelsGetter(this.serverConnection);
            synchronized (serverChannelCache){
                for( var con: serverChannelCache){
                    uninjectServer(con);
                }
            }
        }
        this.serverChannelCache = null;
        this.injectedChannels.clear();
        Set<Channel> channels ;
        synchronized (this.currentConnections){
            channels = new HashSet<>(this.currentConnections.keySet());
        }
        for (var entry: channels){
            uninjectChannel(entry);
        }

        this.currentConnections.clear();
        this.serverConnection = null;
        this.internalPacketListeners.clear();
        this.eventChannels.clear();
        this.registered = false;

    }
    private void unregisterFunctional(){
        HandlerList.unregisterAll(this);
    }



    protected static boolean isFakeChannel(Object channel) {
        if (channel.getClass().getSimpleName().equals("FakeChannel")
            || channel.getClass().getSimpleName().equals("SpoofedChannel")
            || channel.getClass().getSimpleName().equals("EmbeddedChannel")) {
            return true;
        }
        return false;
    }


}
