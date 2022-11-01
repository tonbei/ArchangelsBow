package com.github.tonbei.archangelsbow;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.tonbei.archangelsbow.config.ABConfig;
import com.github.tonbei.archangelsbow.arrow.HomingArrow;
import com.github.tonbei.archangelsbow.listener.ABCraftListener;
import com.github.tonbei.archangelsbow.listener.ABDamageListener;
import com.github.tonbei.archangelsbow.listener.ABInventoryListener;
import com.github.tonbei.archangelsbow.listener.HitTickArrowListener;
import com.github.tonbei.archangelsbow.listener.InventoryUpdateListener;
import com.github.tonbei.archangelsbow.listener.PlayerGlideListener;
import com.github.tonbei.archangelsbow.listener.ServerTickEndListener;
import com.github.tonbei.archangelsbow.listener.ShootArrowListener;
import com.github.tonbei.archangelsbow.listener.TickArrowLoadListener;
import com.github.tonbei.archangelsbow.manager.ABRecipeManager;
import com.github.tonbei.archangelsbow.manager.InventoryUpdateManager;
import com.github.tonbei.archangelsbow.manager.TickArrowManager;
import com.github.tonbei.archangelsbow.manager.TickTaskManager;
import com.github.tonbei.archangelsbow.packet.GlidingInventoryPacketListener;
import com.github.tonbei.archangelsbow.packet.InventoryUpdatePacketListener;
import com.github.tonbei.archangelsbow.util.ABUtil;
import com.github.tonbei.archangelsbow.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class ArchangelsBow extends JavaPlugin implements Listener {

    private static ArchangelsBow INSTANCE;

    private ABConfig config;
    private ABRecipeManager recipeManager;

    public static ArchangelsBow getInstance() {
        return INSTANCE != null ? INSTANCE : (INSTANCE = getPlugin(ArchangelsBow.class));
    }

    public static ABConfig getABConfig() {
        return getInstance().config;
    }

    @Override
    public void onLoad() {
        try {
            //Arrays.stream(Thread.currentThread().getContextClassLoader().getDefinedPackages()).forEach(aPackage -> getLogger().severe(aPackage.getName()));
            //Thread.currentThread().getContextClassLoader().getResources("net/minecraft/world/entity").asIterator().forEachRemaining(url -> getLogger().severe(url.getPath()));
            listClasses("net.minecraft.world.entity.projectile").forEach(aClass -> getLogger().severe(aClass.getName()));
            Class<?> test = Class.forName("net.minecraft.world.entity.projectile.EntityTippedArrow");
            getLogger().severe(test.descriptorString());
            // ASMで、bytesに格納されたクラスファイルを解析します。
            ClassNode cnode = new ClassNode();
            ClassReader reader = new ClassReader("net.minecraft.world.entity.projectile.EntityTippedArrow");
            reader.accept(cnode, 0);

            // 改変対象のメソッド名です
            String targetMethodName = "tick";

            // 改変対象メソッドの戻り値型および、引数型をあらわします
            String targetMethoddesc = "()V";

            // 対象のメソッドを検索取得します。
            MethodNode mnode = null;
            String mdesc = null;

            for (MethodNode curMnode : cnode.methods) {
                if ((targetMethodName.equals(curMnode.name) && targetMethoddesc.equals(curMnode.desc))) {
                    mnode = curMnode;
                    mdesc = curMnode.desc;
                    break;
                }
            }

            if (mnode != null) {
                Iterator<AbstractInsnNode> iterator = mnode.instructions.iterator();

                while(iterator.hasNext()) {
                    AbstractInsnNode anode = iterator.next();

                    if (anode.getOpcode() == Opcodes.RETURN) {
                        InsnList overrideList = new InsnList();
                        overrideList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        overrideList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/github/tonbei/archangelsbow/ArchangelsBow", "loadInfo", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getObjectType("net/minecraft/client/Minecraft")), false));
                        overrideList.add(new InsnNode(Opcodes.RETURN));
                        mnode.instructions.insertBefore(anode, overrideList);
                    }
                }
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cnode.accept(cw);

            Class<?> neighbor = Class.forName("net.minecraft.world.entity.projectile.EntityTippedArrow");
            ArchangelsBow.class.getModule().addReads(neighbor.getModule());
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandles.Lookup prvlookup = MethodHandles.privateLookupIn(neighbor, lookup);
            prvlookup.defineClass(cw.toByteArray());

        } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <T> T uncheckCall(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<Class<?>> listClasses(String packageName) {

        final String resourceName = packageName.replace('.', '/');
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL root = classLoader.getResource(resourceName);

        if ("file".equals(root.getProtocol())) {
            File[] files = new File(root.getFile()).listFiles((dir, name) -> name.endsWith(".class"));
            return Arrays.asList(files).stream()
                    .map(file -> file.getName())
                    .map(name -> name.replaceAll(".class$", ""))
                    .map(name -> packageName + "." + name)
                    .map(fullName -> uncheckCall(() -> Class.forName(fullName)))
                    .collect(Collectors.toSet());
        }
        if ("jar".equals(root.getProtocol())) {
            try (JarFile jarFile = ((JarURLConnection) root.openConnection()).getJarFile()) {
                return Collections.list(jarFile.entries()).stream()
                        .map(jarEntry -> jarEntry.getName())
                        .filter(name -> name.startsWith(resourceName))
                        .filter(name -> name.endsWith(".class"))
                        .map(name -> name.replace('/', '.').replaceAll(".class$", ""))
                        .map(fullName -> uncheckCall(() -> classLoader.loadClass(fullName)))
                        .collect(Collectors.toSet());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new HashSet<>();
    }

    public static void loadInfo() {
        Bukkit.getLogger().severe("LOADED!!");
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        Log.setLogger(this.getLogger());
        config = new ABConfig(this);
        recipeManager = new ABRecipeManager(this);
        if (config.isEnableCraft()) recipeManager.addRecipe();

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new TickArrowLoadListener(), this);
        pluginManager.registerEvents(new ShootArrowListener(), this);
        pluginManager.registerEvents(new ABInventoryListener(), this);
        pluginManager.registerEvents(new HitTickArrowListener(), this);
        pluginManager.registerEvents(new ABCraftListener(recipeManager), this);
        pluginManager.registerEvents(new PlayerGlideListener(), this);
        pluginManager.registerEvents(new InventoryUpdateListener(), this);
        pluginManager.registerEvents(new ServerTickEndListener(), this);
        pluginManager.registerEvents(new ABDamageListener(), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new GlidingInventoryPacketListener(this));
        ProtocolLibrary.getProtocolManager().addPacketListener(new InventoryUpdatePacketListener(this));

        TickTaskManager.init(this);

        for (World world : Bukkit.getWorlds())
            for (Chunk chunk : world.getLoadedChunks())
                if (chunk.isEntitiesLoaded())
                    for (Entity entity : chunk.getEntities())
                        if (ABUtil.isHomingArrow(entity))
                            TickArrowManager.register(new HomingArrow((Arrow) entity, config.getStartHomingTick(), config.getSearchRange()));
    }

    @Override
    public void onDisable() {
        recipeManager.removeRecipe(false);
        TickArrowManager.clear();
        TickTaskManager.clear();
        InventoryUpdateManager.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) return false;

        if (args[0].equalsIgnoreCase("get")) {
            if (!(sender instanceof Player)) {
                Log.infoSenders("[" + ChatColor.GREEN + "Archangel's Bow" + ChatColor.RESET + "] "
                                + ChatColor.RED + "This command can only be used in-game.", sender);
                return true;
            }

            int level = 1;
            if (args.length >= 2 && args[1].matches("^\\d+$")) {
                level = Integer.parseInt(args[1]);
            }
            ((Player) sender).getInventory().addItem(ABUtil.getArchangelsBow(level));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            config.reloadConfig();

            if (config.isEnableCraft()) {
                recipeManager.addRecipe();
            } else {
                recipeManager.removeRecipe(false);
            }

            Log.infoSenders("[" + ChatColor.GREEN + "Archangel's Bow" + ChatColor.RESET + "] "
                            + ChatColor.GREEN + "Archangel's Bow configuration was reloaded.", sender);
            return true;
        }

        return false;
    }
}
