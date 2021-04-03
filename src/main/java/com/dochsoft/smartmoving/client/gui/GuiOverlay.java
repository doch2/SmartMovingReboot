package com.dochsoft.smartmoving.client.gui;

import com.dochsoft.smartmoving.proxy.ClientProxy;
import com.dochsoft.smartmoving.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class GuiOverlay extends Gui {
    public static Minecraft minecraft = Minecraft.getMinecraft();
    public static BufferedImage image = null;
    public static ResourceLocation location = null;

    private static boolean firstLoop = true;

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (event.isCanceled() || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
    }
}
