package net.smart.render.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.smart.render.SRContext;
import net.smart.render.SRUtilities;
import net.smart.render.model.SRModel;
import net.smart.render.model.SRModelRotationRenderer;

public class SRLayerEntityOnShoulder extends LayerEntityOnShoulder {
	private IRenderPlayer irp;

	private static float RadiantToAngle = SRUtilities.RadiantToAngle;
	private static int XYZ = SRModelRotationRenderer.XYZ;
	private static int XZY = SRModelRotationRenderer.XZY;
	private static int YXZ = SRModelRotationRenderer.YXZ;
	private static int YZX = SRModelRotationRenderer.YZX;
	private static int ZXY = SRModelRotationRenderer.ZXY;
	private static int ZYX = SRModelRotationRenderer.ZYX;

	public SRLayerEntityOnShoulder(RenderManager p_i47370_1_, IRenderPlayer irp) {
		super(p_i47370_1_);
		this.irp = irp;
	}

	@Override
	public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		GL11.glPushMatrix();

		SRModel currentModel = SRContext.getPlayerBase(irp.getModelBipedMain()).getRenderModel();
		SRModelRotationRenderer biped = currentModel.bipedOuter;
		SRModelRotationRenderer body = currentModel.bipedBody;
		SRModelRotationRenderer torso = currentModel.bipedTorso;

		GL11.glTranslatef(biped.rotationPointX * scale, biped.rotationPointY * scale, biped.rotationPointZ * scale);
		rotate(biped.rotationOrder, biped.rotateAngleX, biped.rotateAngleY, biped.rotateAngleZ);
		GL11.glTranslatef(biped.offsetX, biped.offsetY, biped.offsetZ);

		GL11.glTranslatef(torso.rotationPointX * scale, torso.rotationPointY * scale, torso.rotationPointZ * scale);
		rotate(torso.rotationOrder, torso.rotateAngleX, torso.rotateAngleY, torso.rotateAngleZ);
		GL11.glTranslatef(torso.offsetX, torso.offsetY, torso.offsetZ);

		GL11.glTranslatef(body.rotationPointX * scale, body.rotationPointY * scale, body.rotationPointZ * scale);
		rotate(body.rotationOrder, body.rotateAngleX, body.rotateAngleY, body.rotateAngleZ);
		GL11.glTranslatef(body.offsetX, body.offsetY, body.offsetZ);

		super.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
				headPitch, scale);
		GL11.glPopMatrix();
	}

	private static void rotate(int rotationOrder, float rotateAngleX, float rotateAngleY, float rotateAngleZ) {
		if ((rotationOrder == ZXY) && rotateAngleY != 0.0F)
			GL11.glRotatef(rotateAngleY * RadiantToAngle, 0.0F, 1.0F, 0.0F);

		if ((rotationOrder == YXZ) && rotateAngleZ != 0.0F)
			GL11.glRotatef(rotateAngleZ * RadiantToAngle, 0.0F, 0.0F, 1.0F);

		if ((rotationOrder == YZX || rotationOrder == YXZ || rotationOrder == ZXY || rotationOrder == ZYX)
				&& rotateAngleX != 0.0F)
			GL11.glRotatef(rotateAngleX * RadiantToAngle, 1.0F, 0.0F, 0.0F);

		if ((rotationOrder == XZY || rotationOrder == ZYX) && rotateAngleY != 0.0F)
			GL11.glRotatef(rotateAngleY * RadiantToAngle, 0.0F, 1.0F, 0.0F);

		if ((rotationOrder == XYZ || rotationOrder == XZY || rotationOrder == YZX || rotationOrder == ZXY
				|| rotationOrder == ZYX) && rotateAngleZ != 0.0F)
			GL11.glRotatef(rotateAngleZ * RadiantToAngle, 0.0F, 0.0F, 1.0F);

		if ((rotationOrder == XYZ || rotationOrder == YXZ || rotationOrder == YZX) && rotateAngleY != 0.0F)
			GL11.glRotatef(rotateAngleY * RadiantToAngle, 0.0F, 1.0F, 0.0F);

		if ((rotationOrder == XYZ || rotationOrder == XZY) && rotateAngleX != 0.0F)
			GL11.glRotatef(rotateAngleX * RadiantToAngle, 1.0F, 0.0F, 0.0F);
	}
}
