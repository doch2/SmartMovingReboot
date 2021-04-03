package com.dochsoft.smartmoving.client.gui;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class InGameTextureHandler {
    private final TextureManager textureManager;

    private final DynamicTexture texture;

    private int[] textureData;

    private ResourceLocation loc;

    public InGameTextureHandler(TextureManager manager, DynamicTexture textureIn) {
        this.textureManager = manager;
        this.texture = textureIn;
        this.textureData = textureIn.getTextureData();
        this.loc = this.textureManager.getDynamicTextureLocation("texture/", this.texture);
    }

    public void updateTextureData(BufferedImage newImage) {
        int[] newImgData = ((DataBufferInt)newImage.getRaster().getDataBuffer()).getData();
        if (this.textureData.length == newImgData.length)
            for (int i = 0; i < newImgData.length; i++)
                this.textureData[i] = newImgData[i];
        this.texture.updateDynamicTexture();
    }

    public ResourceLocation getTextureLocation() {
        return this.loc;
    }
}
