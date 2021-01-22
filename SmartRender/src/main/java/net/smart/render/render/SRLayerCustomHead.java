package net.smart.render.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.EntityLivingBase;

public class SRLayerCustomHead extends LayerCustomHead {
	private ModelRenderer head;

	public SRLayerCustomHead(ModelRenderer p_i46120_1_) {
		super(p_i46120_1_);
		this.head = p_i46120_1_;
	}

	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		GL11.glPushMatrix();
		GL11.glRotatef(head.rotateAngleX, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(head.rotateAngleY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(head.rotateAngleZ, 0.0F, 0.0F, 1.0F);
		super.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
				headPitch, scale);
		GL11.glPopMatrix();
	}
}
