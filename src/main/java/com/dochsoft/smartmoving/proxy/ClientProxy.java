package com.dochsoft.smartmoving.proxy;

import com.dochsoft.smartmoving.Smartmovingmod;
import com.dochsoft.smartmoving.client.gui.GuiOverlay;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.nio.charset.Charset;

public class ClientProxy extends CommonProxy {


    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(new GuiOverlay());

        MinecraftForge.EVENT_BUS.register(new Smartmovingmod());
    }

    @Override
    public void postinit(FMLPostInitializationEvent event) {
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @SubscribeEvent
    public void onClientCustomPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
    }

}
