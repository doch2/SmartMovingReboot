// ==================================================================
// This file is part of Render Player API.
//
// Render Player API is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// Render Player API is distributed in the hope that it will be
// useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License and the GNU General Public License along with Render
// Player API. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package api.player.render;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.lang.ref.*;
import java.lang.reflect.*;

public final class RenderPlayerAPI
{
	private final static Class<?>[] Class = new Class[] { RenderPlayerAPI.class };
	private final static Class<?>[] Classes = new Class[] { RenderPlayerAPI.class, String.class };

	private static boolean isCreated;
	private static final Logger logger = Logger.getLogger("RenderPlayerAPI");

	private static void log(String text)
	{
		System.out.println(text);
		logger.fine(text);
	}

	public static void register(String id, Class<?> baseClass)
	{
		register(id, baseClass, null);
	}

	public static void register(String id, Class<?> baseClass, RenderPlayerBaseSorting baseSorting)
	{
		try
		{
			register(baseClass, id, baseSorting);
		}
		catch(RuntimeException exception)
		{
			if(id != null)
				log("Render Player: failed to register id '" + id + "'");
			else
				log("Render Player: failed to register RenderPlayerBase");

			throw exception;
		}
	}

	private static void register(Class<?> baseClass, String id, RenderPlayerBaseSorting baseSorting)
	{
		if(!isCreated)
		{
			try
			{
				Method mandatory = net.minecraft.client.renderer.entity.RenderPlayer.class.getMethod("getRenderPlayerBase", String.class);
				if (mandatory.getReturnType() != RenderPlayerBase.class)
					throw new NoSuchMethodException(RenderPlayerBase.class.getName() + " " + net.minecraft.client.renderer.entity.RenderPlayer.class.getName() + ".getRenderPlayerBase(" + String.class.getName() + ")");
			}
			catch(NoSuchMethodException exception)
			{
				String[] errorMessageParts = new String[]
				{
					"========================================",
					"The API \"Render Player\" version " + api.player.forge.RenderPlayerAPIPlugin.VERSION + " of the mod \"Render Player API Core " + api.player.forge.RenderPlayerAPIPlugin.VERSION + "\" can not be created!",
					"----------------------------------------",
					"Mandatory member method \"{0} getRenderPlayerBase({3})\" not found in class \"{1}\".",
					"There are three scenarios this can happen:",
					"* Minecraft Forge is missing a Render Player API Core which Minecraft version matches its own.",
					"  Download and install the latest Render Player API Core for the Minecraft version you were trying to run.",
					"* The code of the class \"{2}\" of Render Player API Core has been modified beyond recognition by another Minecraft Forge coremod.",
					"  Try temporary deinstallation of other core mods to find the culprit and deinstall it permanently to fix this specific problem.",
					"* Render Player API Core has not been installed correctly.",
					"  Deinstall Render Player API Core and install it again following the installation instructions in the readme file.",
					"========================================"
				};

				String baseRenderPlayerClassName = RenderPlayerBase.class.getName();
				String targetClassName = net.minecraft.client.renderer.entity.RenderPlayer.class.getName();
				String targetClassFileName = targetClassName.replace(".", File.separator);
				String stringClassName = String.class.getName();

				for(int i=0; i<errorMessageParts.length; i++)
					errorMessageParts[i] = MessageFormat.format(errorMessageParts[i], baseRenderPlayerClassName, targetClassName, targetClassFileName, stringClassName);

				for(String errorMessagePart : errorMessageParts)
					logger.severe(errorMessagePart);

				for(String errorMessagePart : errorMessageParts)
					System.err.println(errorMessagePart);

				String errorMessage = "\n\n";
				for(String errorMessagePart : errorMessageParts)
					errorMessage += "\t" + errorMessagePart + "\n";

				throw new RuntimeException(errorMessage, exception);
			}

			log("Render Player " + api.player.forge.RenderPlayerAPIPlugin.VERSION + " Created");
			isCreated = true;
		}

		if(id == null)
			throw new NullPointerException("Argument 'id' can not be null");
		if(baseClass == null)
			throw new NullPointerException("Argument 'baseClass' can not be null");

		Constructor<?> allreadyRegistered = allBaseConstructors.get(id);
		if(allreadyRegistered != null)
			throw new IllegalArgumentException("The class '" + baseClass.getName() + "' can not be registered with the id '" + id + "' because the class '" + allreadyRegistered.getDeclaringClass().getName() + "' has allready been registered with the same id");

		Constructor<?> baseConstructor;
		try
		{
			baseConstructor = baseClass.getDeclaredConstructor(Classes);
		}
		catch (Throwable t)
		{
			try
			{
				baseConstructor = baseClass.getDeclaredConstructor(Class);
			}
			catch(Throwable s)
			{
				throw new IllegalArgumentException("Can not find necessary constructor with one argument of type '" + RenderPlayerAPI.class.getName() + "' and eventually a second argument of type 'String' in the class '" + baseClass.getName() + "'", t);
			}
		}

		allBaseConstructors.put(id, baseConstructor);

		if(baseSorting != null)
		{
			addSorting(id, allBaseBeforeLocalConstructingSuperiors, baseSorting.getBeforeLocalConstructingSuperiors());
			addSorting(id, allBaseBeforeLocalConstructingInferiors, baseSorting.getBeforeLocalConstructingInferiors());
			addSorting(id, allBaseAfterLocalConstructingSuperiors, baseSorting.getAfterLocalConstructingSuperiors());
			addSorting(id, allBaseAfterLocalConstructingInferiors, baseSorting.getAfterLocalConstructingInferiors());

			addDynamicSorting(id, allBaseBeforeDynamicSuperiors, baseSorting.getDynamicBeforeSuperiors());
			addDynamicSorting(id, allBaseBeforeDynamicInferiors, baseSorting.getDynamicBeforeInferiors());
			addDynamicSorting(id, allBaseOverrideDynamicSuperiors, baseSorting.getDynamicOverrideSuperiors());
			addDynamicSorting(id, allBaseOverrideDynamicInferiors, baseSorting.getDynamicOverrideInferiors());
			addDynamicSorting(id, allBaseAfterDynamicSuperiors, baseSorting.getDynamicAfterSuperiors());
			addDynamicSorting(id, allBaseAfterDynamicInferiors, baseSorting.getDynamicAfterInferiors());

			addSorting(id, allBaseBeforeAddLayerSuperiors, baseSorting.getBeforeAddLayerSuperiors());
			addSorting(id, allBaseBeforeAddLayerInferiors, baseSorting.getBeforeAddLayerInferiors());
			addSorting(id, allBaseOverrideAddLayerSuperiors, baseSorting.getOverrideAddLayerSuperiors());
			addSorting(id, allBaseOverrideAddLayerInferiors, baseSorting.getOverrideAddLayerInferiors());
			addSorting(id, allBaseAfterAddLayerSuperiors, baseSorting.getAfterAddLayerSuperiors());
			addSorting(id, allBaseAfterAddLayerInferiors, baseSorting.getAfterAddLayerInferiors());

			addSorting(id, allBaseBeforeBindEntityTextureSuperiors, baseSorting.getBeforeBindEntityTextureSuperiors());
			addSorting(id, allBaseBeforeBindEntityTextureInferiors, baseSorting.getBeforeBindEntityTextureInferiors());
			addSorting(id, allBaseOverrideBindEntityTextureSuperiors, baseSorting.getOverrideBindEntityTextureSuperiors());
			addSorting(id, allBaseOverrideBindEntityTextureInferiors, baseSorting.getOverrideBindEntityTextureInferiors());
			addSorting(id, allBaseAfterBindEntityTextureSuperiors, baseSorting.getAfterBindEntityTextureSuperiors());
			addSorting(id, allBaseAfterBindEntityTextureInferiors, baseSorting.getAfterBindEntityTextureInferiors());

			addSorting(id, allBaseBeforeBindTextureSuperiors, baseSorting.getBeforeBindTextureSuperiors());
			addSorting(id, allBaseBeforeBindTextureInferiors, baseSorting.getBeforeBindTextureInferiors());
			addSorting(id, allBaseOverrideBindTextureSuperiors, baseSorting.getOverrideBindTextureSuperiors());
			addSorting(id, allBaseOverrideBindTextureInferiors, baseSorting.getOverrideBindTextureInferiors());
			addSorting(id, allBaseAfterBindTextureSuperiors, baseSorting.getAfterBindTextureSuperiors());
			addSorting(id, allBaseAfterBindTextureInferiors, baseSorting.getAfterBindTextureInferiors());

			addSorting(id, allBaseBeforeCanRenderNameSuperiors, baseSorting.getBeforeCanRenderNameSuperiors());
			addSorting(id, allBaseBeforeCanRenderNameInferiors, baseSorting.getBeforeCanRenderNameInferiors());
			addSorting(id, allBaseOverrideCanRenderNameSuperiors, baseSorting.getOverrideCanRenderNameSuperiors());
			addSorting(id, allBaseOverrideCanRenderNameInferiors, baseSorting.getOverrideCanRenderNameInferiors());
			addSorting(id, allBaseAfterCanRenderNameSuperiors, baseSorting.getAfterCanRenderNameSuperiors());
			addSorting(id, allBaseAfterCanRenderNameInferiors, baseSorting.getAfterCanRenderNameInferiors());

			addSorting(id, allBaseBeforeDoRenderSuperiors, baseSorting.getBeforeDoRenderSuperiors());
			addSorting(id, allBaseBeforeDoRenderInferiors, baseSorting.getBeforeDoRenderInferiors());
			addSorting(id, allBaseOverrideDoRenderSuperiors, baseSorting.getOverrideDoRenderSuperiors());
			addSorting(id, allBaseOverrideDoRenderInferiors, baseSorting.getOverrideDoRenderInferiors());
			addSorting(id, allBaseAfterDoRenderSuperiors, baseSorting.getAfterDoRenderSuperiors());
			addSorting(id, allBaseAfterDoRenderInferiors, baseSorting.getAfterDoRenderInferiors());

			addSorting(id, allBaseBeforeDoRenderShadowAndFireSuperiors, baseSorting.getBeforeDoRenderShadowAndFireSuperiors());
			addSorting(id, allBaseBeforeDoRenderShadowAndFireInferiors, baseSorting.getBeforeDoRenderShadowAndFireInferiors());
			addSorting(id, allBaseOverrideDoRenderShadowAndFireSuperiors, baseSorting.getOverrideDoRenderShadowAndFireSuperiors());
			addSorting(id, allBaseOverrideDoRenderShadowAndFireInferiors, baseSorting.getOverrideDoRenderShadowAndFireInferiors());
			addSorting(id, allBaseAfterDoRenderShadowAndFireSuperiors, baseSorting.getAfterDoRenderShadowAndFireSuperiors());
			addSorting(id, allBaseAfterDoRenderShadowAndFireInferiors, baseSorting.getAfterDoRenderShadowAndFireInferiors());

			addSorting(id, allBaseBeforeGetColorMultiplierSuperiors, baseSorting.getBeforeGetColorMultiplierSuperiors());
			addSorting(id, allBaseBeforeGetColorMultiplierInferiors, baseSorting.getBeforeGetColorMultiplierInferiors());
			addSorting(id, allBaseOverrideGetColorMultiplierSuperiors, baseSorting.getOverrideGetColorMultiplierSuperiors());
			addSorting(id, allBaseOverrideGetColorMultiplierInferiors, baseSorting.getOverrideGetColorMultiplierInferiors());
			addSorting(id, allBaseAfterGetColorMultiplierSuperiors, baseSorting.getAfterGetColorMultiplierSuperiors());
			addSorting(id, allBaseAfterGetColorMultiplierInferiors, baseSorting.getAfterGetColorMultiplierInferiors());

			addSorting(id, allBaseBeforeGetDeathMaxRotationSuperiors, baseSorting.getBeforeGetDeathMaxRotationSuperiors());
			addSorting(id, allBaseBeforeGetDeathMaxRotationInferiors, baseSorting.getBeforeGetDeathMaxRotationInferiors());
			addSorting(id, allBaseOverrideGetDeathMaxRotationSuperiors, baseSorting.getOverrideGetDeathMaxRotationSuperiors());
			addSorting(id, allBaseOverrideGetDeathMaxRotationInferiors, baseSorting.getOverrideGetDeathMaxRotationInferiors());
			addSorting(id, allBaseAfterGetDeathMaxRotationSuperiors, baseSorting.getAfterGetDeathMaxRotationSuperiors());
			addSorting(id, allBaseAfterGetDeathMaxRotationInferiors, baseSorting.getAfterGetDeathMaxRotationInferiors());

			addSorting(id, allBaseBeforeGetEntityTextureSuperiors, baseSorting.getBeforeGetEntityTextureSuperiors());
			addSorting(id, allBaseBeforeGetEntityTextureInferiors, baseSorting.getBeforeGetEntityTextureInferiors());
			addSorting(id, allBaseOverrideGetEntityTextureSuperiors, baseSorting.getOverrideGetEntityTextureSuperiors());
			addSorting(id, allBaseOverrideGetEntityTextureInferiors, baseSorting.getOverrideGetEntityTextureInferiors());
			addSorting(id, allBaseAfterGetEntityTextureSuperiors, baseSorting.getAfterGetEntityTextureSuperiors());
			addSorting(id, allBaseAfterGetEntityTextureInferiors, baseSorting.getAfterGetEntityTextureInferiors());

			addSorting(id, allBaseBeforeGetFontRendererFromRenderManagerSuperiors, baseSorting.getBeforeGetFontRendererFromRenderManagerSuperiors());
			addSorting(id, allBaseBeforeGetFontRendererFromRenderManagerInferiors, baseSorting.getBeforeGetFontRendererFromRenderManagerInferiors());
			addSorting(id, allBaseOverrideGetFontRendererFromRenderManagerSuperiors, baseSorting.getOverrideGetFontRendererFromRenderManagerSuperiors());
			addSorting(id, allBaseOverrideGetFontRendererFromRenderManagerInferiors, baseSorting.getOverrideGetFontRendererFromRenderManagerInferiors());
			addSorting(id, allBaseAfterGetFontRendererFromRenderManagerSuperiors, baseSorting.getAfterGetFontRendererFromRenderManagerSuperiors());
			addSorting(id, allBaseAfterGetFontRendererFromRenderManagerInferiors, baseSorting.getAfterGetFontRendererFromRenderManagerInferiors());

			addSorting(id, allBaseBeforeGetMainModelSuperiors, baseSorting.getBeforeGetMainModelSuperiors());
			addSorting(id, allBaseBeforeGetMainModelInferiors, baseSorting.getBeforeGetMainModelInferiors());
			addSorting(id, allBaseOverrideGetMainModelSuperiors, baseSorting.getOverrideGetMainModelSuperiors());
			addSorting(id, allBaseOverrideGetMainModelInferiors, baseSorting.getOverrideGetMainModelInferiors());
			addSorting(id, allBaseAfterGetMainModelSuperiors, baseSorting.getAfterGetMainModelSuperiors());
			addSorting(id, allBaseAfterGetMainModelInferiors, baseSorting.getAfterGetMainModelInferiors());

			addSorting(id, allBaseBeforeGetRenderManagerSuperiors, baseSorting.getBeforeGetRenderManagerSuperiors());
			addSorting(id, allBaseBeforeGetRenderManagerInferiors, baseSorting.getBeforeGetRenderManagerInferiors());
			addSorting(id, allBaseOverrideGetRenderManagerSuperiors, baseSorting.getOverrideGetRenderManagerSuperiors());
			addSorting(id, allBaseOverrideGetRenderManagerInferiors, baseSorting.getOverrideGetRenderManagerInferiors());
			addSorting(id, allBaseAfterGetRenderManagerSuperiors, baseSorting.getAfterGetRenderManagerSuperiors());
			addSorting(id, allBaseAfterGetRenderManagerInferiors, baseSorting.getAfterGetRenderManagerInferiors());

			addSorting(id, allBaseBeforeGetSwingProgressSuperiors, baseSorting.getBeforeGetSwingProgressSuperiors());
			addSorting(id, allBaseBeforeGetSwingProgressInferiors, baseSorting.getBeforeGetSwingProgressInferiors());
			addSorting(id, allBaseOverrideGetSwingProgressSuperiors, baseSorting.getOverrideGetSwingProgressSuperiors());
			addSorting(id, allBaseOverrideGetSwingProgressInferiors, baseSorting.getOverrideGetSwingProgressInferiors());
			addSorting(id, allBaseAfterGetSwingProgressSuperiors, baseSorting.getAfterGetSwingProgressSuperiors());
			addSorting(id, allBaseAfterGetSwingProgressInferiors, baseSorting.getAfterGetSwingProgressInferiors());

			addSorting(id, allBaseBeforeGetTeamColorSuperiors, baseSorting.getBeforeGetTeamColorSuperiors());
			addSorting(id, allBaseBeforeGetTeamColorInferiors, baseSorting.getBeforeGetTeamColorInferiors());
			addSorting(id, allBaseOverrideGetTeamColorSuperiors, baseSorting.getOverrideGetTeamColorSuperiors());
			addSorting(id, allBaseOverrideGetTeamColorInferiors, baseSorting.getOverrideGetTeamColorInferiors());
			addSorting(id, allBaseAfterGetTeamColorSuperiors, baseSorting.getAfterGetTeamColorSuperiors());
			addSorting(id, allBaseAfterGetTeamColorInferiors, baseSorting.getAfterGetTeamColorInferiors());

			addSorting(id, allBaseBeforeHandleRotationFloatSuperiors, baseSorting.getBeforeHandleRotationFloatSuperiors());
			addSorting(id, allBaseBeforeHandleRotationFloatInferiors, baseSorting.getBeforeHandleRotationFloatInferiors());
			addSorting(id, allBaseOverrideHandleRotationFloatSuperiors, baseSorting.getOverrideHandleRotationFloatSuperiors());
			addSorting(id, allBaseOverrideHandleRotationFloatInferiors, baseSorting.getOverrideHandleRotationFloatInferiors());
			addSorting(id, allBaseAfterHandleRotationFloatSuperiors, baseSorting.getAfterHandleRotationFloatSuperiors());
			addSorting(id, allBaseAfterHandleRotationFloatInferiors, baseSorting.getAfterHandleRotationFloatInferiors());

			addSorting(id, allBaseBeforeInterpolateRotationSuperiors, baseSorting.getBeforeInterpolateRotationSuperiors());
			addSorting(id, allBaseBeforeInterpolateRotationInferiors, baseSorting.getBeforeInterpolateRotationInferiors());
			addSorting(id, allBaseOverrideInterpolateRotationSuperiors, baseSorting.getOverrideInterpolateRotationSuperiors());
			addSorting(id, allBaseOverrideInterpolateRotationInferiors, baseSorting.getOverrideInterpolateRotationInferiors());
			addSorting(id, allBaseAfterInterpolateRotationSuperiors, baseSorting.getAfterInterpolateRotationSuperiors());
			addSorting(id, allBaseAfterInterpolateRotationInferiors, baseSorting.getAfterInterpolateRotationInferiors());

			addSorting(id, allBaseBeforeIsMultipassSuperiors, baseSorting.getBeforeIsMultipassSuperiors());
			addSorting(id, allBaseBeforeIsMultipassInferiors, baseSorting.getBeforeIsMultipassInferiors());
			addSorting(id, allBaseOverrideIsMultipassSuperiors, baseSorting.getOverrideIsMultipassSuperiors());
			addSorting(id, allBaseOverrideIsMultipassInferiors, baseSorting.getOverrideIsMultipassInferiors());
			addSorting(id, allBaseAfterIsMultipassSuperiors, baseSorting.getAfterIsMultipassSuperiors());
			addSorting(id, allBaseAfterIsMultipassInferiors, baseSorting.getAfterIsMultipassInferiors());

			addSorting(id, allBaseBeforeIsVisibleSuperiors, baseSorting.getBeforeIsVisibleSuperiors());
			addSorting(id, allBaseBeforeIsVisibleInferiors, baseSorting.getBeforeIsVisibleInferiors());
			addSorting(id, allBaseOverrideIsVisibleSuperiors, baseSorting.getOverrideIsVisibleSuperiors());
			addSorting(id, allBaseOverrideIsVisibleInferiors, baseSorting.getOverrideIsVisibleInferiors());
			addSorting(id, allBaseAfterIsVisibleSuperiors, baseSorting.getAfterIsVisibleSuperiors());
			addSorting(id, allBaseAfterIsVisibleInferiors, baseSorting.getAfterIsVisibleInferiors());

			addSorting(id, allBaseBeforePreRenderCallbackSuperiors, baseSorting.getBeforePreRenderCallbackSuperiors());
			addSorting(id, allBaseBeforePreRenderCallbackInferiors, baseSorting.getBeforePreRenderCallbackInferiors());
			addSorting(id, allBaseOverridePreRenderCallbackSuperiors, baseSorting.getOverridePreRenderCallbackSuperiors());
			addSorting(id, allBaseOverridePreRenderCallbackInferiors, baseSorting.getOverridePreRenderCallbackInferiors());
			addSorting(id, allBaseAfterPreRenderCallbackSuperiors, baseSorting.getAfterPreRenderCallbackSuperiors());
			addSorting(id, allBaseAfterPreRenderCallbackInferiors, baseSorting.getAfterPreRenderCallbackInferiors());

			addSorting(id, allBaseBeforePrepareScaleSuperiors, baseSorting.getBeforePrepareScaleSuperiors());
			addSorting(id, allBaseBeforePrepareScaleInferiors, baseSorting.getBeforePrepareScaleInferiors());
			addSorting(id, allBaseOverridePrepareScaleSuperiors, baseSorting.getOverridePrepareScaleSuperiors());
			addSorting(id, allBaseOverridePrepareScaleInferiors, baseSorting.getOverridePrepareScaleInferiors());
			addSorting(id, allBaseAfterPrepareScaleSuperiors, baseSorting.getAfterPrepareScaleSuperiors());
			addSorting(id, allBaseAfterPrepareScaleInferiors, baseSorting.getAfterPrepareScaleInferiors());

			addSorting(id, allBaseBeforeRenderEntityNameSuperiors, baseSorting.getBeforeRenderEntityNameSuperiors());
			addSorting(id, allBaseBeforeRenderEntityNameInferiors, baseSorting.getBeforeRenderEntityNameInferiors());
			addSorting(id, allBaseOverrideRenderEntityNameSuperiors, baseSorting.getOverrideRenderEntityNameSuperiors());
			addSorting(id, allBaseOverrideRenderEntityNameInferiors, baseSorting.getOverrideRenderEntityNameInferiors());
			addSorting(id, allBaseAfterRenderEntityNameSuperiors, baseSorting.getAfterRenderEntityNameSuperiors());
			addSorting(id, allBaseAfterRenderEntityNameInferiors, baseSorting.getAfterRenderEntityNameInferiors());

			addSorting(id, allBaseBeforeRenderLayersSuperiors, baseSorting.getBeforeRenderLayersSuperiors());
			addSorting(id, allBaseBeforeRenderLayersInferiors, baseSorting.getBeforeRenderLayersInferiors());
			addSorting(id, allBaseOverrideRenderLayersSuperiors, baseSorting.getOverrideRenderLayersSuperiors());
			addSorting(id, allBaseOverrideRenderLayersInferiors, baseSorting.getOverrideRenderLayersInferiors());
			addSorting(id, allBaseAfterRenderLayersSuperiors, baseSorting.getAfterRenderLayersSuperiors());
			addSorting(id, allBaseAfterRenderLayersInferiors, baseSorting.getAfterRenderLayersInferiors());

			addSorting(id, allBaseBeforeRenderLeftArmSuperiors, baseSorting.getBeforeRenderLeftArmSuperiors());
			addSorting(id, allBaseBeforeRenderLeftArmInferiors, baseSorting.getBeforeRenderLeftArmInferiors());
			addSorting(id, allBaseOverrideRenderLeftArmSuperiors, baseSorting.getOverrideRenderLeftArmSuperiors());
			addSorting(id, allBaseOverrideRenderLeftArmInferiors, baseSorting.getOverrideRenderLeftArmInferiors());
			addSorting(id, allBaseAfterRenderLeftArmSuperiors, baseSorting.getAfterRenderLeftArmSuperiors());
			addSorting(id, allBaseAfterRenderLeftArmInferiors, baseSorting.getAfterRenderLeftArmInferiors());

			addSorting(id, allBaseBeforeRenderLivingAtSuperiors, baseSorting.getBeforeRenderLivingAtSuperiors());
			addSorting(id, allBaseBeforeRenderLivingAtInferiors, baseSorting.getBeforeRenderLivingAtInferiors());
			addSorting(id, allBaseOverrideRenderLivingAtSuperiors, baseSorting.getOverrideRenderLivingAtSuperiors());
			addSorting(id, allBaseOverrideRenderLivingAtInferiors, baseSorting.getOverrideRenderLivingAtInferiors());
			addSorting(id, allBaseAfterRenderLivingAtSuperiors, baseSorting.getAfterRenderLivingAtSuperiors());
			addSorting(id, allBaseAfterRenderLivingAtInferiors, baseSorting.getAfterRenderLivingAtInferiors());

			addSorting(id, allBaseBeforeRenderLivingLabelSuperiors, baseSorting.getBeforeRenderLivingLabelSuperiors());
			addSorting(id, allBaseBeforeRenderLivingLabelInferiors, baseSorting.getBeforeRenderLivingLabelInferiors());
			addSorting(id, allBaseOverrideRenderLivingLabelSuperiors, baseSorting.getOverrideRenderLivingLabelSuperiors());
			addSorting(id, allBaseOverrideRenderLivingLabelInferiors, baseSorting.getOverrideRenderLivingLabelInferiors());
			addSorting(id, allBaseAfterRenderLivingLabelSuperiors, baseSorting.getAfterRenderLivingLabelSuperiors());
			addSorting(id, allBaseAfterRenderLivingLabelInferiors, baseSorting.getAfterRenderLivingLabelInferiors());

			addSorting(id, allBaseBeforeRenderModelSuperiors, baseSorting.getBeforeRenderModelSuperiors());
			addSorting(id, allBaseBeforeRenderModelInferiors, baseSorting.getBeforeRenderModelInferiors());
			addSorting(id, allBaseOverrideRenderModelSuperiors, baseSorting.getOverrideRenderModelSuperiors());
			addSorting(id, allBaseOverrideRenderModelInferiors, baseSorting.getOverrideRenderModelInferiors());
			addSorting(id, allBaseAfterRenderModelSuperiors, baseSorting.getAfterRenderModelSuperiors());
			addSorting(id, allBaseAfterRenderModelInferiors, baseSorting.getAfterRenderModelInferiors());

			addSorting(id, allBaseBeforeRenderMultipassSuperiors, baseSorting.getBeforeRenderMultipassSuperiors());
			addSorting(id, allBaseBeforeRenderMultipassInferiors, baseSorting.getBeforeRenderMultipassInferiors());
			addSorting(id, allBaseOverrideRenderMultipassSuperiors, baseSorting.getOverrideRenderMultipassSuperiors());
			addSorting(id, allBaseOverrideRenderMultipassInferiors, baseSorting.getOverrideRenderMultipassInferiors());
			addSorting(id, allBaseAfterRenderMultipassSuperiors, baseSorting.getAfterRenderMultipassSuperiors());
			addSorting(id, allBaseAfterRenderMultipassInferiors, baseSorting.getAfterRenderMultipassInferiors());

			addSorting(id, allBaseBeforeRenderNameSuperiors, baseSorting.getBeforeRenderNameSuperiors());
			addSorting(id, allBaseBeforeRenderNameInferiors, baseSorting.getBeforeRenderNameInferiors());
			addSorting(id, allBaseOverrideRenderNameSuperiors, baseSorting.getOverrideRenderNameSuperiors());
			addSorting(id, allBaseOverrideRenderNameInferiors, baseSorting.getOverrideRenderNameInferiors());
			addSorting(id, allBaseAfterRenderNameSuperiors, baseSorting.getAfterRenderNameSuperiors());
			addSorting(id, allBaseAfterRenderNameInferiors, baseSorting.getAfterRenderNameInferiors());

			addSorting(id, allBaseBeforeRenderRightArmSuperiors, baseSorting.getBeforeRenderRightArmSuperiors());
			addSorting(id, allBaseBeforeRenderRightArmInferiors, baseSorting.getBeforeRenderRightArmInferiors());
			addSorting(id, allBaseOverrideRenderRightArmSuperiors, baseSorting.getOverrideRenderRightArmSuperiors());
			addSorting(id, allBaseOverrideRenderRightArmInferiors, baseSorting.getOverrideRenderRightArmInferiors());
			addSorting(id, allBaseAfterRenderRightArmSuperiors, baseSorting.getAfterRenderRightArmSuperiors());
			addSorting(id, allBaseAfterRenderRightArmInferiors, baseSorting.getAfterRenderRightArmInferiors());

			addSorting(id, allBaseBeforeRotateCorpseSuperiors, baseSorting.getBeforeRotateCorpseSuperiors());
			addSorting(id, allBaseBeforeRotateCorpseInferiors, baseSorting.getBeforeRotateCorpseInferiors());
			addSorting(id, allBaseOverrideRotateCorpseSuperiors, baseSorting.getOverrideRotateCorpseSuperiors());
			addSorting(id, allBaseOverrideRotateCorpseInferiors, baseSorting.getOverrideRotateCorpseInferiors());
			addSorting(id, allBaseAfterRotateCorpseSuperiors, baseSorting.getAfterRotateCorpseSuperiors());
			addSorting(id, allBaseAfterRotateCorpseInferiors, baseSorting.getAfterRotateCorpseInferiors());

			addSorting(id, allBaseBeforeSetBrightnessSuperiors, baseSorting.getBeforeSetBrightnessSuperiors());
			addSorting(id, allBaseBeforeSetBrightnessInferiors, baseSorting.getBeforeSetBrightnessInferiors());
			addSorting(id, allBaseOverrideSetBrightnessSuperiors, baseSorting.getOverrideSetBrightnessSuperiors());
			addSorting(id, allBaseOverrideSetBrightnessInferiors, baseSorting.getOverrideSetBrightnessInferiors());
			addSorting(id, allBaseAfterSetBrightnessSuperiors, baseSorting.getAfterSetBrightnessSuperiors());
			addSorting(id, allBaseAfterSetBrightnessInferiors, baseSorting.getAfterSetBrightnessInferiors());

			addSorting(id, allBaseBeforeSetDoRenderBrightnessSuperiors, baseSorting.getBeforeSetDoRenderBrightnessSuperiors());
			addSorting(id, allBaseBeforeSetDoRenderBrightnessInferiors, baseSorting.getBeforeSetDoRenderBrightnessInferiors());
			addSorting(id, allBaseOverrideSetDoRenderBrightnessSuperiors, baseSorting.getOverrideSetDoRenderBrightnessSuperiors());
			addSorting(id, allBaseOverrideSetDoRenderBrightnessInferiors, baseSorting.getOverrideSetDoRenderBrightnessInferiors());
			addSorting(id, allBaseAfterSetDoRenderBrightnessSuperiors, baseSorting.getAfterSetDoRenderBrightnessSuperiors());
			addSorting(id, allBaseAfterSetDoRenderBrightnessInferiors, baseSorting.getAfterSetDoRenderBrightnessInferiors());

			addSorting(id, allBaseBeforeSetModelVisibilitiesSuperiors, baseSorting.getBeforeSetModelVisibilitiesSuperiors());
			addSorting(id, allBaseBeforeSetModelVisibilitiesInferiors, baseSorting.getBeforeSetModelVisibilitiesInferiors());
			addSorting(id, allBaseOverrideSetModelVisibilitiesSuperiors, baseSorting.getOverrideSetModelVisibilitiesSuperiors());
			addSorting(id, allBaseOverrideSetModelVisibilitiesInferiors, baseSorting.getOverrideSetModelVisibilitiesInferiors());
			addSorting(id, allBaseAfterSetModelVisibilitiesSuperiors, baseSorting.getAfterSetModelVisibilitiesSuperiors());
			addSorting(id, allBaseAfterSetModelVisibilitiesInferiors, baseSorting.getAfterSetModelVisibilitiesInferiors());

			addSorting(id, allBaseBeforeSetRenderOutlinesSuperiors, baseSorting.getBeforeSetRenderOutlinesSuperiors());
			addSorting(id, allBaseBeforeSetRenderOutlinesInferiors, baseSorting.getBeforeSetRenderOutlinesInferiors());
			addSorting(id, allBaseOverrideSetRenderOutlinesSuperiors, baseSorting.getOverrideSetRenderOutlinesSuperiors());
			addSorting(id, allBaseOverrideSetRenderOutlinesInferiors, baseSorting.getOverrideSetRenderOutlinesInferiors());
			addSorting(id, allBaseAfterSetRenderOutlinesSuperiors, baseSorting.getAfterSetRenderOutlinesSuperiors());
			addSorting(id, allBaseAfterSetRenderOutlinesInferiors, baseSorting.getAfterSetRenderOutlinesInferiors());

			addSorting(id, allBaseBeforeSetScoreTeamColorSuperiors, baseSorting.getBeforeSetScoreTeamColorSuperiors());
			addSorting(id, allBaseBeforeSetScoreTeamColorInferiors, baseSorting.getBeforeSetScoreTeamColorInferiors());
			addSorting(id, allBaseOverrideSetScoreTeamColorSuperiors, baseSorting.getOverrideSetScoreTeamColorSuperiors());
			addSorting(id, allBaseOverrideSetScoreTeamColorInferiors, baseSorting.getOverrideSetScoreTeamColorInferiors());
			addSorting(id, allBaseAfterSetScoreTeamColorSuperiors, baseSorting.getAfterSetScoreTeamColorSuperiors());
			addSorting(id, allBaseAfterSetScoreTeamColorInferiors, baseSorting.getAfterSetScoreTeamColorInferiors());

			addSorting(id, allBaseBeforeShouldRenderSuperiors, baseSorting.getBeforeShouldRenderSuperiors());
			addSorting(id, allBaseBeforeShouldRenderInferiors, baseSorting.getBeforeShouldRenderInferiors());
			addSorting(id, allBaseOverrideShouldRenderSuperiors, baseSorting.getOverrideShouldRenderSuperiors());
			addSorting(id, allBaseOverrideShouldRenderInferiors, baseSorting.getOverrideShouldRenderInferiors());
			addSorting(id, allBaseAfterShouldRenderSuperiors, baseSorting.getAfterShouldRenderSuperiors());
			addSorting(id, allBaseAfterShouldRenderInferiors, baseSorting.getAfterShouldRenderInferiors());

			addSorting(id, allBaseBeforeTransformHeldFull3DItemLayerSuperiors, baseSorting.getBeforeTransformHeldFull3DItemLayerSuperiors());
			addSorting(id, allBaseBeforeTransformHeldFull3DItemLayerInferiors, baseSorting.getBeforeTransformHeldFull3DItemLayerInferiors());
			addSorting(id, allBaseOverrideTransformHeldFull3DItemLayerSuperiors, baseSorting.getOverrideTransformHeldFull3DItemLayerSuperiors());
			addSorting(id, allBaseOverrideTransformHeldFull3DItemLayerInferiors, baseSorting.getOverrideTransformHeldFull3DItemLayerInferiors());
			addSorting(id, allBaseAfterTransformHeldFull3DItemLayerSuperiors, baseSorting.getAfterTransformHeldFull3DItemLayerSuperiors());
			addSorting(id, allBaseAfterTransformHeldFull3DItemLayerInferiors, baseSorting.getAfterTransformHeldFull3DItemLayerInferiors());

			addSorting(id, allBaseBeforeUnsetBrightnessSuperiors, baseSorting.getBeforeUnsetBrightnessSuperiors());
			addSorting(id, allBaseBeforeUnsetBrightnessInferiors, baseSorting.getBeforeUnsetBrightnessInferiors());
			addSorting(id, allBaseOverrideUnsetBrightnessSuperiors, baseSorting.getOverrideUnsetBrightnessSuperiors());
			addSorting(id, allBaseOverrideUnsetBrightnessInferiors, baseSorting.getOverrideUnsetBrightnessInferiors());
			addSorting(id, allBaseAfterUnsetBrightnessSuperiors, baseSorting.getAfterUnsetBrightnessSuperiors());
			addSorting(id, allBaseAfterUnsetBrightnessInferiors, baseSorting.getAfterUnsetBrightnessInferiors());

			addSorting(id, allBaseBeforeUnsetScoreTeamColorSuperiors, baseSorting.getBeforeUnsetScoreTeamColorSuperiors());
			addSorting(id, allBaseBeforeUnsetScoreTeamColorInferiors, baseSorting.getBeforeUnsetScoreTeamColorInferiors());
			addSorting(id, allBaseOverrideUnsetScoreTeamColorSuperiors, baseSorting.getOverrideUnsetScoreTeamColorSuperiors());
			addSorting(id, allBaseOverrideUnsetScoreTeamColorInferiors, baseSorting.getOverrideUnsetScoreTeamColorInferiors());
			addSorting(id, allBaseAfterUnsetScoreTeamColorSuperiors, baseSorting.getAfterUnsetScoreTeamColorSuperiors());
			addSorting(id, allBaseAfterUnsetScoreTeamColorInferiors, baseSorting.getAfterUnsetScoreTeamColorInferiors());

		}

		addMethod(id, baseClass, beforeLocalConstructingHookTypes, "beforeLocalConstructing", net.minecraft.client.renderer.entity.RenderManager.class, boolean.class);
		addMethod(id, baseClass, afterLocalConstructingHookTypes, "afterLocalConstructing", net.minecraft.client.renderer.entity.RenderManager.class, boolean.class);


		addMethod(id, baseClass, beforeAddLayerHookTypes, "beforeAddLayer", net.minecraft.client.renderer.entity.layers.LayerRenderer.class);
		addMethod(id, baseClass, overrideAddLayerHookTypes, "addLayer", net.minecraft.client.renderer.entity.layers.LayerRenderer.class);
		addMethod(id, baseClass, afterAddLayerHookTypes, "afterAddLayer", net.minecraft.client.renderer.entity.layers.LayerRenderer.class);

		addMethod(id, baseClass, beforeBindEntityTextureHookTypes, "beforeBindEntityTexture", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, overrideBindEntityTextureHookTypes, "bindEntityTexture", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, afterBindEntityTextureHookTypes, "afterBindEntityTexture", net.minecraft.client.entity.AbstractClientPlayer.class);

		addMethod(id, baseClass, beforeBindTextureHookTypes, "beforeBindTexture", net.minecraft.util.ResourceLocation.class);
		addMethod(id, baseClass, overrideBindTextureHookTypes, "bindTexture", net.minecraft.util.ResourceLocation.class);
		addMethod(id, baseClass, afterBindTextureHookTypes, "afterBindTexture", net.minecraft.util.ResourceLocation.class);

		addMethod(id, baseClass, beforeCanRenderNameHookTypes, "beforeCanRenderName", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, overrideCanRenderNameHookTypes, "canRenderName", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, afterCanRenderNameHookTypes, "afterCanRenderName", net.minecraft.client.entity.AbstractClientPlayer.class);

		addMethod(id, baseClass, beforeDoRenderHookTypes, "beforeDoRender", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, float.class, float.class);
		addMethod(id, baseClass, overrideDoRenderHookTypes, "doRender", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, float.class, float.class);
		addMethod(id, baseClass, afterDoRenderHookTypes, "afterDoRender", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, float.class, float.class);

		addMethod(id, baseClass, beforeDoRenderShadowAndFireHookTypes, "beforeDoRenderShadowAndFire", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, float.class, float.class);
		addMethod(id, baseClass, overrideDoRenderShadowAndFireHookTypes, "doRenderShadowAndFire", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, float.class, float.class);
		addMethod(id, baseClass, afterDoRenderShadowAndFireHookTypes, "afterDoRenderShadowAndFire", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, float.class, float.class);

		addMethod(id, baseClass, beforeGetColorMultiplierHookTypes, "beforeGetColorMultiplier", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class);
		addMethod(id, baseClass, overrideGetColorMultiplierHookTypes, "getColorMultiplier", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class);
		addMethod(id, baseClass, afterGetColorMultiplierHookTypes, "afterGetColorMultiplier", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class);

		addMethod(id, baseClass, beforeGetDeathMaxRotationHookTypes, "beforeGetDeathMaxRotation", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, overrideGetDeathMaxRotationHookTypes, "getDeathMaxRotation", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, afterGetDeathMaxRotationHookTypes, "afterGetDeathMaxRotation", net.minecraft.client.entity.AbstractClientPlayer.class);

		addMethod(id, baseClass, beforeGetEntityTextureHookTypes, "beforeGetEntityTexture", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, overrideGetEntityTextureHookTypes, "getEntityTexture", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, afterGetEntityTextureHookTypes, "afterGetEntityTexture", net.minecraft.client.entity.AbstractClientPlayer.class);

		addMethod(id, baseClass, beforeGetFontRendererFromRenderManagerHookTypes, "beforeGetFontRendererFromRenderManager");
		addMethod(id, baseClass, overrideGetFontRendererFromRenderManagerHookTypes, "getFontRendererFromRenderManager");
		addMethod(id, baseClass, afterGetFontRendererFromRenderManagerHookTypes, "afterGetFontRendererFromRenderManager");

		addMethod(id, baseClass, beforeGetMainModelHookTypes, "beforeGetMainModel");
		addMethod(id, baseClass, overrideGetMainModelHookTypes, "getMainModel");
		addMethod(id, baseClass, afterGetMainModelHookTypes, "afterGetMainModel");

		addMethod(id, baseClass, beforeGetRenderManagerHookTypes, "beforeGetRenderManager");
		addMethod(id, baseClass, overrideGetRenderManagerHookTypes, "getRenderManager");
		addMethod(id, baseClass, afterGetRenderManagerHookTypes, "afterGetRenderManager");

		addMethod(id, baseClass, beforeGetSwingProgressHookTypes, "beforeGetSwingProgress", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);
		addMethod(id, baseClass, overrideGetSwingProgressHookTypes, "getSwingProgress", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);
		addMethod(id, baseClass, afterGetSwingProgressHookTypes, "afterGetSwingProgress", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);

		addMethod(id, baseClass, beforeGetTeamColorHookTypes, "beforeGetTeamColor", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, overrideGetTeamColorHookTypes, "getTeamColor", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, afterGetTeamColorHookTypes, "afterGetTeamColor", net.minecraft.client.entity.AbstractClientPlayer.class);

		addMethod(id, baseClass, beforeHandleRotationFloatHookTypes, "beforeHandleRotationFloat", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);
		addMethod(id, baseClass, overrideHandleRotationFloatHookTypes, "handleRotationFloat", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);
		addMethod(id, baseClass, afterHandleRotationFloatHookTypes, "afterHandleRotationFloat", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);

		addMethod(id, baseClass, beforeInterpolateRotationHookTypes, "beforeInterpolateRotation", float.class, float.class, float.class);
		addMethod(id, baseClass, overrideInterpolateRotationHookTypes, "interpolateRotation", float.class, float.class, float.class);
		addMethod(id, baseClass, afterInterpolateRotationHookTypes, "afterInterpolateRotation", float.class, float.class, float.class);

		addMethod(id, baseClass, beforeIsMultipassHookTypes, "beforeIsMultipass");
		addMethod(id, baseClass, overrideIsMultipassHookTypes, "isMultipass");
		addMethod(id, baseClass, afterIsMultipassHookTypes, "afterIsMultipass");

		addMethod(id, baseClass, beforeIsVisibleHookTypes, "beforeIsVisible", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, overrideIsVisibleHookTypes, "isVisible", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, afterIsVisibleHookTypes, "afterIsVisible", net.minecraft.client.entity.AbstractClientPlayer.class);

		addMethod(id, baseClass, beforePreRenderCallbackHookTypes, "beforePreRenderCallback", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);
		addMethod(id, baseClass, overridePreRenderCallbackHookTypes, "preRenderCallback", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);
		addMethod(id, baseClass, afterPreRenderCallbackHookTypes, "afterPreRenderCallback", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);

		addMethod(id, baseClass, beforePrepareScaleHookTypes, "beforePrepareScale", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);
		addMethod(id, baseClass, overridePrepareScaleHookTypes, "prepareScale", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);
		addMethod(id, baseClass, afterPrepareScaleHookTypes, "afterPrepareScale", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);

		addMethod(id, baseClass, beforeRenderEntityNameHookTypes, "beforeRenderEntityName", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, String.class, double.class);
		addMethod(id, baseClass, overrideRenderEntityNameHookTypes, "renderEntityName", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, String.class, double.class);
		addMethod(id, baseClass, afterRenderEntityNameHookTypes, "afterRenderEntityName", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, String.class, double.class);

		addMethod(id, baseClass, beforeRenderLayersHookTypes, "beforeRenderLayers", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class);
		addMethod(id, baseClass, overrideRenderLayersHookTypes, "renderLayers", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class);
		addMethod(id, baseClass, afterRenderLayersHookTypes, "afterRenderLayers", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class);

		addMethod(id, baseClass, beforeRenderLeftArmHookTypes, "beforeRenderLeftArm", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, overrideRenderLeftArmHookTypes, "renderLeftArm", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, afterRenderLeftArmHookTypes, "afterRenderLeftArm", net.minecraft.client.entity.AbstractClientPlayer.class);

		addMethod(id, baseClass, beforeRenderLivingAtHookTypes, "beforeRenderLivingAt", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class);
		addMethod(id, baseClass, overrideRenderLivingAtHookTypes, "renderLivingAt", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class);
		addMethod(id, baseClass, afterRenderLivingAtHookTypes, "afterRenderLivingAt", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class);

		addMethod(id, baseClass, beforeRenderLivingLabelHookTypes, "beforeRenderLivingLabel", net.minecraft.client.entity.AbstractClientPlayer.class, String.class, double.class, double.class, double.class, int.class);
		addMethod(id, baseClass, overrideRenderLivingLabelHookTypes, "renderLivingLabel", net.minecraft.client.entity.AbstractClientPlayer.class, String.class, double.class, double.class, double.class, int.class);
		addMethod(id, baseClass, afterRenderLivingLabelHookTypes, "afterRenderLivingLabel", net.minecraft.client.entity.AbstractClientPlayer.class, String.class, double.class, double.class, double.class, int.class);

		addMethod(id, baseClass, beforeRenderModelHookTypes, "beforeRenderModel", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class, float.class, float.class, float.class, float.class);
		addMethod(id, baseClass, overrideRenderModelHookTypes, "renderModel", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class, float.class, float.class, float.class, float.class);
		addMethod(id, baseClass, afterRenderModelHookTypes, "afterRenderModel", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class, float.class, float.class, float.class, float.class);

		addMethod(id, baseClass, beforeRenderMultipassHookTypes, "beforeRenderMultipass", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, float.class, float.class);
		addMethod(id, baseClass, overrideRenderMultipassHookTypes, "renderMultipass", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, float.class, float.class);
		addMethod(id, baseClass, afterRenderMultipassHookTypes, "afterRenderMultipass", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class, float.class, float.class);

		addMethod(id, baseClass, beforeRenderNameHookTypes, "beforeRenderName", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class);
		addMethod(id, baseClass, overrideRenderNameHookTypes, "renderName", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class);
		addMethod(id, baseClass, afterRenderNameHookTypes, "afterRenderName", net.minecraft.client.entity.AbstractClientPlayer.class, double.class, double.class, double.class);

		addMethod(id, baseClass, beforeRenderRightArmHookTypes, "beforeRenderRightArm", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, overrideRenderRightArmHookTypes, "renderRightArm", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, afterRenderRightArmHookTypes, "afterRenderRightArm", net.minecraft.client.entity.AbstractClientPlayer.class);

		addMethod(id, baseClass, beforeRotateCorpseHookTypes, "beforeRotateCorpse", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class, float.class);
		addMethod(id, baseClass, overrideRotateCorpseHookTypes, "rotateCorpse", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class, float.class);
		addMethod(id, baseClass, afterRotateCorpseHookTypes, "afterRotateCorpse", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, float.class, float.class);

		addMethod(id, baseClass, beforeSetBrightnessHookTypes, "beforeSetBrightness", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, boolean.class);
		addMethod(id, baseClass, overrideSetBrightnessHookTypes, "setBrightness", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, boolean.class);
		addMethod(id, baseClass, afterSetBrightnessHookTypes, "afterSetBrightness", net.minecraft.client.entity.AbstractClientPlayer.class, float.class, boolean.class);

		addMethod(id, baseClass, beforeSetDoRenderBrightnessHookTypes, "beforeSetDoRenderBrightness", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);
		addMethod(id, baseClass, overrideSetDoRenderBrightnessHookTypes, "setDoRenderBrightness", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);
		addMethod(id, baseClass, afterSetDoRenderBrightnessHookTypes, "afterSetDoRenderBrightness", net.minecraft.client.entity.AbstractClientPlayer.class, float.class);

		addMethod(id, baseClass, beforeSetModelVisibilitiesHookTypes, "beforeSetModelVisibilities", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, overrideSetModelVisibilitiesHookTypes, "setModelVisibilities", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, afterSetModelVisibilitiesHookTypes, "afterSetModelVisibilities", net.minecraft.client.entity.AbstractClientPlayer.class);

		addMethod(id, baseClass, beforeSetRenderOutlinesHookTypes, "beforeSetRenderOutlines", boolean.class);
		addMethod(id, baseClass, overrideSetRenderOutlinesHookTypes, "setRenderOutlines", boolean.class);
		addMethod(id, baseClass, afterSetRenderOutlinesHookTypes, "afterSetRenderOutlines", boolean.class);

		addMethod(id, baseClass, beforeSetScoreTeamColorHookTypes, "beforeSetScoreTeamColor", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, overrideSetScoreTeamColorHookTypes, "setScoreTeamColor", net.minecraft.client.entity.AbstractClientPlayer.class);
		addMethod(id, baseClass, afterSetScoreTeamColorHookTypes, "afterSetScoreTeamColor", net.minecraft.client.entity.AbstractClientPlayer.class);

		addMethod(id, baseClass, beforeShouldRenderHookTypes, "beforeShouldRender", net.minecraft.client.entity.AbstractClientPlayer.class, net.minecraft.client.renderer.culling.ICamera.class, double.class, double.class, double.class);
		addMethod(id, baseClass, overrideShouldRenderHookTypes, "shouldRender", net.minecraft.client.entity.AbstractClientPlayer.class, net.minecraft.client.renderer.culling.ICamera.class, double.class, double.class, double.class);
		addMethod(id, baseClass, afterShouldRenderHookTypes, "afterShouldRender", net.minecraft.client.entity.AbstractClientPlayer.class, net.minecraft.client.renderer.culling.ICamera.class, double.class, double.class, double.class);

		addMethod(id, baseClass, beforeTransformHeldFull3DItemLayerHookTypes, "beforeTransformHeldFull3DItemLayer");
		addMethod(id, baseClass, overrideTransformHeldFull3DItemLayerHookTypes, "transformHeldFull3DItemLayer");
		addMethod(id, baseClass, afterTransformHeldFull3DItemLayerHookTypes, "afterTransformHeldFull3DItemLayer");

		addMethod(id, baseClass, beforeUnsetBrightnessHookTypes, "beforeUnsetBrightness");
		addMethod(id, baseClass, overrideUnsetBrightnessHookTypes, "unsetBrightness");
		addMethod(id, baseClass, afterUnsetBrightnessHookTypes, "afterUnsetBrightness");

		addMethod(id, baseClass, beforeUnsetScoreTeamColorHookTypes, "beforeUnsetScoreTeamColor");
		addMethod(id, baseClass, overrideUnsetScoreTeamColorHookTypes, "unsetScoreTeamColor");
		addMethod(id, baseClass, afterUnsetScoreTeamColorHookTypes, "afterUnsetScoreTeamColor");


		addDynamicMethods(id, baseClass);

		addDynamicKeys(id, baseClass, beforeDynamicHookMethods, beforeDynamicHookTypes);
		addDynamicKeys(id, baseClass, overrideDynamicHookMethods, overrideDynamicHookTypes);
		addDynamicKeys(id, baseClass, afterDynamicHookMethods, afterDynamicHookTypes);

		initialize();

		for(IRenderPlayerAPI instance : getAllInstancesList())
			instance.getRenderPlayerAPI().attachRenderPlayerBase(id);

		System.out.println("Render Player: registered " + id);
		logger.fine("Render Player: registered class '" + baseClass.getName() + "' with id '" + id + "'");

		initialized = false;
	}

	public static boolean unregister(String id)
	{
		if(id == null)
			return false;

		Constructor<?> constructor = allBaseConstructors.remove(id);
		if(constructor == null)
			return false;

		for(IRenderPlayerAPI instance : getAllInstancesList())
			instance.getRenderPlayerAPI().detachRenderPlayerBase(id);

		beforeLocalConstructingHookTypes.remove(id);
		afterLocalConstructingHookTypes.remove(id);

		allBaseBeforeAddLayerSuperiors.remove(id);
		allBaseBeforeAddLayerInferiors.remove(id);
		allBaseOverrideAddLayerSuperiors.remove(id);
		allBaseOverrideAddLayerInferiors.remove(id);
		allBaseAfterAddLayerSuperiors.remove(id);
		allBaseAfterAddLayerInferiors.remove(id);

		beforeAddLayerHookTypes.remove(id);
		overrideAddLayerHookTypes.remove(id);
		afterAddLayerHookTypes.remove(id);

		allBaseBeforeBindEntityTextureSuperiors.remove(id);
		allBaseBeforeBindEntityTextureInferiors.remove(id);
		allBaseOverrideBindEntityTextureSuperiors.remove(id);
		allBaseOverrideBindEntityTextureInferiors.remove(id);
		allBaseAfterBindEntityTextureSuperiors.remove(id);
		allBaseAfterBindEntityTextureInferiors.remove(id);

		beforeBindEntityTextureHookTypes.remove(id);
		overrideBindEntityTextureHookTypes.remove(id);
		afterBindEntityTextureHookTypes.remove(id);

		allBaseBeforeBindTextureSuperiors.remove(id);
		allBaseBeforeBindTextureInferiors.remove(id);
		allBaseOverrideBindTextureSuperiors.remove(id);
		allBaseOverrideBindTextureInferiors.remove(id);
		allBaseAfterBindTextureSuperiors.remove(id);
		allBaseAfterBindTextureInferiors.remove(id);

		beforeBindTextureHookTypes.remove(id);
		overrideBindTextureHookTypes.remove(id);
		afterBindTextureHookTypes.remove(id);

		allBaseBeforeCanRenderNameSuperiors.remove(id);
		allBaseBeforeCanRenderNameInferiors.remove(id);
		allBaseOverrideCanRenderNameSuperiors.remove(id);
		allBaseOverrideCanRenderNameInferiors.remove(id);
		allBaseAfterCanRenderNameSuperiors.remove(id);
		allBaseAfterCanRenderNameInferiors.remove(id);

		beforeCanRenderNameHookTypes.remove(id);
		overrideCanRenderNameHookTypes.remove(id);
		afterCanRenderNameHookTypes.remove(id);

		allBaseBeforeDoRenderSuperiors.remove(id);
		allBaseBeforeDoRenderInferiors.remove(id);
		allBaseOverrideDoRenderSuperiors.remove(id);
		allBaseOverrideDoRenderInferiors.remove(id);
		allBaseAfterDoRenderSuperiors.remove(id);
		allBaseAfterDoRenderInferiors.remove(id);

		beforeDoRenderHookTypes.remove(id);
		overrideDoRenderHookTypes.remove(id);
		afterDoRenderHookTypes.remove(id);

		allBaseBeforeDoRenderShadowAndFireSuperiors.remove(id);
		allBaseBeforeDoRenderShadowAndFireInferiors.remove(id);
		allBaseOverrideDoRenderShadowAndFireSuperiors.remove(id);
		allBaseOverrideDoRenderShadowAndFireInferiors.remove(id);
		allBaseAfterDoRenderShadowAndFireSuperiors.remove(id);
		allBaseAfterDoRenderShadowAndFireInferiors.remove(id);

		beforeDoRenderShadowAndFireHookTypes.remove(id);
		overrideDoRenderShadowAndFireHookTypes.remove(id);
		afterDoRenderShadowAndFireHookTypes.remove(id);

		allBaseBeforeGetColorMultiplierSuperiors.remove(id);
		allBaseBeforeGetColorMultiplierInferiors.remove(id);
		allBaseOverrideGetColorMultiplierSuperiors.remove(id);
		allBaseOverrideGetColorMultiplierInferiors.remove(id);
		allBaseAfterGetColorMultiplierSuperiors.remove(id);
		allBaseAfterGetColorMultiplierInferiors.remove(id);

		beforeGetColorMultiplierHookTypes.remove(id);
		overrideGetColorMultiplierHookTypes.remove(id);
		afterGetColorMultiplierHookTypes.remove(id);

		allBaseBeforeGetDeathMaxRotationSuperiors.remove(id);
		allBaseBeforeGetDeathMaxRotationInferiors.remove(id);
		allBaseOverrideGetDeathMaxRotationSuperiors.remove(id);
		allBaseOverrideGetDeathMaxRotationInferiors.remove(id);
		allBaseAfterGetDeathMaxRotationSuperiors.remove(id);
		allBaseAfterGetDeathMaxRotationInferiors.remove(id);

		beforeGetDeathMaxRotationHookTypes.remove(id);
		overrideGetDeathMaxRotationHookTypes.remove(id);
		afterGetDeathMaxRotationHookTypes.remove(id);

		allBaseBeforeGetEntityTextureSuperiors.remove(id);
		allBaseBeforeGetEntityTextureInferiors.remove(id);
		allBaseOverrideGetEntityTextureSuperiors.remove(id);
		allBaseOverrideGetEntityTextureInferiors.remove(id);
		allBaseAfterGetEntityTextureSuperiors.remove(id);
		allBaseAfterGetEntityTextureInferiors.remove(id);

		beforeGetEntityTextureHookTypes.remove(id);
		overrideGetEntityTextureHookTypes.remove(id);
		afterGetEntityTextureHookTypes.remove(id);

		allBaseBeforeGetFontRendererFromRenderManagerSuperiors.remove(id);
		allBaseBeforeGetFontRendererFromRenderManagerInferiors.remove(id);
		allBaseOverrideGetFontRendererFromRenderManagerSuperiors.remove(id);
		allBaseOverrideGetFontRendererFromRenderManagerInferiors.remove(id);
		allBaseAfterGetFontRendererFromRenderManagerSuperiors.remove(id);
		allBaseAfterGetFontRendererFromRenderManagerInferiors.remove(id);

		beforeGetFontRendererFromRenderManagerHookTypes.remove(id);
		overrideGetFontRendererFromRenderManagerHookTypes.remove(id);
		afterGetFontRendererFromRenderManagerHookTypes.remove(id);

		allBaseBeforeGetMainModelSuperiors.remove(id);
		allBaseBeforeGetMainModelInferiors.remove(id);
		allBaseOverrideGetMainModelSuperiors.remove(id);
		allBaseOverrideGetMainModelInferiors.remove(id);
		allBaseAfterGetMainModelSuperiors.remove(id);
		allBaseAfterGetMainModelInferiors.remove(id);

		beforeGetMainModelHookTypes.remove(id);
		overrideGetMainModelHookTypes.remove(id);
		afterGetMainModelHookTypes.remove(id);

		allBaseBeforeGetRenderManagerSuperiors.remove(id);
		allBaseBeforeGetRenderManagerInferiors.remove(id);
		allBaseOverrideGetRenderManagerSuperiors.remove(id);
		allBaseOverrideGetRenderManagerInferiors.remove(id);
		allBaseAfterGetRenderManagerSuperiors.remove(id);
		allBaseAfterGetRenderManagerInferiors.remove(id);

		beforeGetRenderManagerHookTypes.remove(id);
		overrideGetRenderManagerHookTypes.remove(id);
		afterGetRenderManagerHookTypes.remove(id);

		allBaseBeforeGetSwingProgressSuperiors.remove(id);
		allBaseBeforeGetSwingProgressInferiors.remove(id);
		allBaseOverrideGetSwingProgressSuperiors.remove(id);
		allBaseOverrideGetSwingProgressInferiors.remove(id);
		allBaseAfterGetSwingProgressSuperiors.remove(id);
		allBaseAfterGetSwingProgressInferiors.remove(id);

		beforeGetSwingProgressHookTypes.remove(id);
		overrideGetSwingProgressHookTypes.remove(id);
		afterGetSwingProgressHookTypes.remove(id);

		allBaseBeforeGetTeamColorSuperiors.remove(id);
		allBaseBeforeGetTeamColorInferiors.remove(id);
		allBaseOverrideGetTeamColorSuperiors.remove(id);
		allBaseOverrideGetTeamColorInferiors.remove(id);
		allBaseAfterGetTeamColorSuperiors.remove(id);
		allBaseAfterGetTeamColorInferiors.remove(id);

		beforeGetTeamColorHookTypes.remove(id);
		overrideGetTeamColorHookTypes.remove(id);
		afterGetTeamColorHookTypes.remove(id);

		allBaseBeforeHandleRotationFloatSuperiors.remove(id);
		allBaseBeforeHandleRotationFloatInferiors.remove(id);
		allBaseOverrideHandleRotationFloatSuperiors.remove(id);
		allBaseOverrideHandleRotationFloatInferiors.remove(id);
		allBaseAfterHandleRotationFloatSuperiors.remove(id);
		allBaseAfterHandleRotationFloatInferiors.remove(id);

		beforeHandleRotationFloatHookTypes.remove(id);
		overrideHandleRotationFloatHookTypes.remove(id);
		afterHandleRotationFloatHookTypes.remove(id);

		allBaseBeforeInterpolateRotationSuperiors.remove(id);
		allBaseBeforeInterpolateRotationInferiors.remove(id);
		allBaseOverrideInterpolateRotationSuperiors.remove(id);
		allBaseOverrideInterpolateRotationInferiors.remove(id);
		allBaseAfterInterpolateRotationSuperiors.remove(id);
		allBaseAfterInterpolateRotationInferiors.remove(id);

		beforeInterpolateRotationHookTypes.remove(id);
		overrideInterpolateRotationHookTypes.remove(id);
		afterInterpolateRotationHookTypes.remove(id);

		allBaseBeforeIsMultipassSuperiors.remove(id);
		allBaseBeforeIsMultipassInferiors.remove(id);
		allBaseOverrideIsMultipassSuperiors.remove(id);
		allBaseOverrideIsMultipassInferiors.remove(id);
		allBaseAfterIsMultipassSuperiors.remove(id);
		allBaseAfterIsMultipassInferiors.remove(id);

		beforeIsMultipassHookTypes.remove(id);
		overrideIsMultipassHookTypes.remove(id);
		afterIsMultipassHookTypes.remove(id);

		allBaseBeforeIsVisibleSuperiors.remove(id);
		allBaseBeforeIsVisibleInferiors.remove(id);
		allBaseOverrideIsVisibleSuperiors.remove(id);
		allBaseOverrideIsVisibleInferiors.remove(id);
		allBaseAfterIsVisibleSuperiors.remove(id);
		allBaseAfterIsVisibleInferiors.remove(id);

		beforeIsVisibleHookTypes.remove(id);
		overrideIsVisibleHookTypes.remove(id);
		afterIsVisibleHookTypes.remove(id);

		allBaseBeforePreRenderCallbackSuperiors.remove(id);
		allBaseBeforePreRenderCallbackInferiors.remove(id);
		allBaseOverridePreRenderCallbackSuperiors.remove(id);
		allBaseOverridePreRenderCallbackInferiors.remove(id);
		allBaseAfterPreRenderCallbackSuperiors.remove(id);
		allBaseAfterPreRenderCallbackInferiors.remove(id);

		beforePreRenderCallbackHookTypes.remove(id);
		overridePreRenderCallbackHookTypes.remove(id);
		afterPreRenderCallbackHookTypes.remove(id);

		allBaseBeforePrepareScaleSuperiors.remove(id);
		allBaseBeforePrepareScaleInferiors.remove(id);
		allBaseOverridePrepareScaleSuperiors.remove(id);
		allBaseOverridePrepareScaleInferiors.remove(id);
		allBaseAfterPrepareScaleSuperiors.remove(id);
		allBaseAfterPrepareScaleInferiors.remove(id);

		beforePrepareScaleHookTypes.remove(id);
		overridePrepareScaleHookTypes.remove(id);
		afterPrepareScaleHookTypes.remove(id);

		allBaseBeforeRenderEntityNameSuperiors.remove(id);
		allBaseBeforeRenderEntityNameInferiors.remove(id);
		allBaseOverrideRenderEntityNameSuperiors.remove(id);
		allBaseOverrideRenderEntityNameInferiors.remove(id);
		allBaseAfterRenderEntityNameSuperiors.remove(id);
		allBaseAfterRenderEntityNameInferiors.remove(id);

		beforeRenderEntityNameHookTypes.remove(id);
		overrideRenderEntityNameHookTypes.remove(id);
		afterRenderEntityNameHookTypes.remove(id);

		allBaseBeforeRenderLayersSuperiors.remove(id);
		allBaseBeforeRenderLayersInferiors.remove(id);
		allBaseOverrideRenderLayersSuperiors.remove(id);
		allBaseOverrideRenderLayersInferiors.remove(id);
		allBaseAfterRenderLayersSuperiors.remove(id);
		allBaseAfterRenderLayersInferiors.remove(id);

		beforeRenderLayersHookTypes.remove(id);
		overrideRenderLayersHookTypes.remove(id);
		afterRenderLayersHookTypes.remove(id);

		allBaseBeforeRenderLeftArmSuperiors.remove(id);
		allBaseBeforeRenderLeftArmInferiors.remove(id);
		allBaseOverrideRenderLeftArmSuperiors.remove(id);
		allBaseOverrideRenderLeftArmInferiors.remove(id);
		allBaseAfterRenderLeftArmSuperiors.remove(id);
		allBaseAfterRenderLeftArmInferiors.remove(id);

		beforeRenderLeftArmHookTypes.remove(id);
		overrideRenderLeftArmHookTypes.remove(id);
		afterRenderLeftArmHookTypes.remove(id);

		allBaseBeforeRenderLivingAtSuperiors.remove(id);
		allBaseBeforeRenderLivingAtInferiors.remove(id);
		allBaseOverrideRenderLivingAtSuperiors.remove(id);
		allBaseOverrideRenderLivingAtInferiors.remove(id);
		allBaseAfterRenderLivingAtSuperiors.remove(id);
		allBaseAfterRenderLivingAtInferiors.remove(id);

		beforeRenderLivingAtHookTypes.remove(id);
		overrideRenderLivingAtHookTypes.remove(id);
		afterRenderLivingAtHookTypes.remove(id);

		allBaseBeforeRenderLivingLabelSuperiors.remove(id);
		allBaseBeforeRenderLivingLabelInferiors.remove(id);
		allBaseOverrideRenderLivingLabelSuperiors.remove(id);
		allBaseOverrideRenderLivingLabelInferiors.remove(id);
		allBaseAfterRenderLivingLabelSuperiors.remove(id);
		allBaseAfterRenderLivingLabelInferiors.remove(id);

		beforeRenderLivingLabelHookTypes.remove(id);
		overrideRenderLivingLabelHookTypes.remove(id);
		afterRenderLivingLabelHookTypes.remove(id);

		allBaseBeforeRenderModelSuperiors.remove(id);
		allBaseBeforeRenderModelInferiors.remove(id);
		allBaseOverrideRenderModelSuperiors.remove(id);
		allBaseOverrideRenderModelInferiors.remove(id);
		allBaseAfterRenderModelSuperiors.remove(id);
		allBaseAfterRenderModelInferiors.remove(id);

		beforeRenderModelHookTypes.remove(id);
		overrideRenderModelHookTypes.remove(id);
		afterRenderModelHookTypes.remove(id);

		allBaseBeforeRenderMultipassSuperiors.remove(id);
		allBaseBeforeRenderMultipassInferiors.remove(id);
		allBaseOverrideRenderMultipassSuperiors.remove(id);
		allBaseOverrideRenderMultipassInferiors.remove(id);
		allBaseAfterRenderMultipassSuperiors.remove(id);
		allBaseAfterRenderMultipassInferiors.remove(id);

		beforeRenderMultipassHookTypes.remove(id);
		overrideRenderMultipassHookTypes.remove(id);
		afterRenderMultipassHookTypes.remove(id);

		allBaseBeforeRenderNameSuperiors.remove(id);
		allBaseBeforeRenderNameInferiors.remove(id);
		allBaseOverrideRenderNameSuperiors.remove(id);
		allBaseOverrideRenderNameInferiors.remove(id);
		allBaseAfterRenderNameSuperiors.remove(id);
		allBaseAfterRenderNameInferiors.remove(id);

		beforeRenderNameHookTypes.remove(id);
		overrideRenderNameHookTypes.remove(id);
		afterRenderNameHookTypes.remove(id);

		allBaseBeforeRenderRightArmSuperiors.remove(id);
		allBaseBeforeRenderRightArmInferiors.remove(id);
		allBaseOverrideRenderRightArmSuperiors.remove(id);
		allBaseOverrideRenderRightArmInferiors.remove(id);
		allBaseAfterRenderRightArmSuperiors.remove(id);
		allBaseAfterRenderRightArmInferiors.remove(id);

		beforeRenderRightArmHookTypes.remove(id);
		overrideRenderRightArmHookTypes.remove(id);
		afterRenderRightArmHookTypes.remove(id);

		allBaseBeforeRotateCorpseSuperiors.remove(id);
		allBaseBeforeRotateCorpseInferiors.remove(id);
		allBaseOverrideRotateCorpseSuperiors.remove(id);
		allBaseOverrideRotateCorpseInferiors.remove(id);
		allBaseAfterRotateCorpseSuperiors.remove(id);
		allBaseAfterRotateCorpseInferiors.remove(id);

		beforeRotateCorpseHookTypes.remove(id);
		overrideRotateCorpseHookTypes.remove(id);
		afterRotateCorpseHookTypes.remove(id);

		allBaseBeforeSetBrightnessSuperiors.remove(id);
		allBaseBeforeSetBrightnessInferiors.remove(id);
		allBaseOverrideSetBrightnessSuperiors.remove(id);
		allBaseOverrideSetBrightnessInferiors.remove(id);
		allBaseAfterSetBrightnessSuperiors.remove(id);
		allBaseAfterSetBrightnessInferiors.remove(id);

		beforeSetBrightnessHookTypes.remove(id);
		overrideSetBrightnessHookTypes.remove(id);
		afterSetBrightnessHookTypes.remove(id);

		allBaseBeforeSetDoRenderBrightnessSuperiors.remove(id);
		allBaseBeforeSetDoRenderBrightnessInferiors.remove(id);
		allBaseOverrideSetDoRenderBrightnessSuperiors.remove(id);
		allBaseOverrideSetDoRenderBrightnessInferiors.remove(id);
		allBaseAfterSetDoRenderBrightnessSuperiors.remove(id);
		allBaseAfterSetDoRenderBrightnessInferiors.remove(id);

		beforeSetDoRenderBrightnessHookTypes.remove(id);
		overrideSetDoRenderBrightnessHookTypes.remove(id);
		afterSetDoRenderBrightnessHookTypes.remove(id);

		allBaseBeforeSetModelVisibilitiesSuperiors.remove(id);
		allBaseBeforeSetModelVisibilitiesInferiors.remove(id);
		allBaseOverrideSetModelVisibilitiesSuperiors.remove(id);
		allBaseOverrideSetModelVisibilitiesInferiors.remove(id);
		allBaseAfterSetModelVisibilitiesSuperiors.remove(id);
		allBaseAfterSetModelVisibilitiesInferiors.remove(id);

		beforeSetModelVisibilitiesHookTypes.remove(id);
		overrideSetModelVisibilitiesHookTypes.remove(id);
		afterSetModelVisibilitiesHookTypes.remove(id);

		allBaseBeforeSetRenderOutlinesSuperiors.remove(id);
		allBaseBeforeSetRenderOutlinesInferiors.remove(id);
		allBaseOverrideSetRenderOutlinesSuperiors.remove(id);
		allBaseOverrideSetRenderOutlinesInferiors.remove(id);
		allBaseAfterSetRenderOutlinesSuperiors.remove(id);
		allBaseAfterSetRenderOutlinesInferiors.remove(id);

		beforeSetRenderOutlinesHookTypes.remove(id);
		overrideSetRenderOutlinesHookTypes.remove(id);
		afterSetRenderOutlinesHookTypes.remove(id);

		allBaseBeforeSetScoreTeamColorSuperiors.remove(id);
		allBaseBeforeSetScoreTeamColorInferiors.remove(id);
		allBaseOverrideSetScoreTeamColorSuperiors.remove(id);
		allBaseOverrideSetScoreTeamColorInferiors.remove(id);
		allBaseAfterSetScoreTeamColorSuperiors.remove(id);
		allBaseAfterSetScoreTeamColorInferiors.remove(id);

		beforeSetScoreTeamColorHookTypes.remove(id);
		overrideSetScoreTeamColorHookTypes.remove(id);
		afterSetScoreTeamColorHookTypes.remove(id);

		allBaseBeforeShouldRenderSuperiors.remove(id);
		allBaseBeforeShouldRenderInferiors.remove(id);
		allBaseOverrideShouldRenderSuperiors.remove(id);
		allBaseOverrideShouldRenderInferiors.remove(id);
		allBaseAfterShouldRenderSuperiors.remove(id);
		allBaseAfterShouldRenderInferiors.remove(id);

		beforeShouldRenderHookTypes.remove(id);
		overrideShouldRenderHookTypes.remove(id);
		afterShouldRenderHookTypes.remove(id);

		allBaseBeforeTransformHeldFull3DItemLayerSuperiors.remove(id);
		allBaseBeforeTransformHeldFull3DItemLayerInferiors.remove(id);
		allBaseOverrideTransformHeldFull3DItemLayerSuperiors.remove(id);
		allBaseOverrideTransformHeldFull3DItemLayerInferiors.remove(id);
		allBaseAfterTransformHeldFull3DItemLayerSuperiors.remove(id);
		allBaseAfterTransformHeldFull3DItemLayerInferiors.remove(id);

		beforeTransformHeldFull3DItemLayerHookTypes.remove(id);
		overrideTransformHeldFull3DItemLayerHookTypes.remove(id);
		afterTransformHeldFull3DItemLayerHookTypes.remove(id);

		allBaseBeforeUnsetBrightnessSuperiors.remove(id);
		allBaseBeforeUnsetBrightnessInferiors.remove(id);
		allBaseOverrideUnsetBrightnessSuperiors.remove(id);
		allBaseOverrideUnsetBrightnessInferiors.remove(id);
		allBaseAfterUnsetBrightnessSuperiors.remove(id);
		allBaseAfterUnsetBrightnessInferiors.remove(id);

		beforeUnsetBrightnessHookTypes.remove(id);
		overrideUnsetBrightnessHookTypes.remove(id);
		afterUnsetBrightnessHookTypes.remove(id);

		allBaseBeforeUnsetScoreTeamColorSuperiors.remove(id);
		allBaseBeforeUnsetScoreTeamColorInferiors.remove(id);
		allBaseOverrideUnsetScoreTeamColorSuperiors.remove(id);
		allBaseOverrideUnsetScoreTeamColorInferiors.remove(id);
		allBaseAfterUnsetScoreTeamColorSuperiors.remove(id);
		allBaseAfterUnsetScoreTeamColorInferiors.remove(id);

		beforeUnsetScoreTeamColorHookTypes.remove(id);
		overrideUnsetScoreTeamColorHookTypes.remove(id);
		afterUnsetScoreTeamColorHookTypes.remove(id);

		for(IRenderPlayerAPI instance : getAllInstancesList())
			instance.getRenderPlayerAPI().updateRenderPlayerBases();

		Iterator<String> iterator = keysToVirtualIds.keySet().iterator();
		while(iterator.hasNext())
		{
			String key = iterator.next();
			if(keysToVirtualIds.get(key).equals(id))
				keysToVirtualIds.remove(key);
		}

		boolean otherFound = false;
		Class<?> type = constructor.getDeclaringClass();

		iterator = allBaseConstructors.keySet().iterator();
		while(iterator.hasNext())
		{
			String otherId = iterator.next();
			Class<?> otherType = allBaseConstructors.get(otherId).getDeclaringClass();
			if(!otherId.equals(id) && otherType.equals(type))
			{
				otherFound = true;
				break;
			}
		}

		if(!otherFound)
		{
			dynamicTypes.remove(type);

			virtualDynamicHookMethods.remove(type);

			beforeDynamicHookMethods.remove(type);
			overrideDynamicHookMethods.remove(type);
			afterDynamicHookMethods.remove(type);
		}

		removeDynamicHookTypes(id, beforeDynamicHookTypes);
		removeDynamicHookTypes(id, overrideDynamicHookTypes);
		removeDynamicHookTypes(id, afterDynamicHookTypes);

		allBaseBeforeDynamicSuperiors.remove(id);
		allBaseBeforeDynamicInferiors.remove(id);
		allBaseOverrideDynamicSuperiors.remove(id);
		allBaseOverrideDynamicInferiors.remove(id);
		allBaseAfterDynamicSuperiors.remove(id);
		allBaseAfterDynamicInferiors.remove(id);

		log("RenderPlayerAPI: unregistered id '" + id + "'");

		return true;
	}

	public static void removeDynamicHookTypes(String id, Map<String, List<String>> map)
	{
		Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext())
			map.get(keys.next()).remove(id);
	}

	public static Set<String> getRegisteredIds()
	{
		return unmodifiableAllIds;
	}

	private static void addSorting(String id, Map<String, String[]> map, String[] values)
	{
		if(values != null && values.length > 0)
			map.put(id, values);
	}

	private static void addDynamicSorting(String id, Map<String, Map<String, String[]>> map, Map<String, String[]> values)
	{
		if(values != null && values.size() > 0)
			map.put(id, values);
	}

	private static boolean addMethod(String id, Class<?> baseClass, List<String> list, String methodName, Class<?>... _parameterTypes)
	{
		try
		{
			Method method = baseClass.getMethod(methodName, _parameterTypes);
			boolean isOverridden = method.getDeclaringClass() != RenderPlayerBase.class;
			if(isOverridden)
				list.add(id);
			return isOverridden;
		}
		catch(Exception e)
		{
			throw new RuntimeException("Can not reflect method '" + methodName + "' of class '" + baseClass.getName() + "'", e);
		}
	}

	private static void addDynamicMethods(String id, Class<?> baseClass)
	{
		if(!dynamicTypes.add(baseClass))
			return;

		Map<String, Method> virtuals = null;
		Map<String, Method> befores = null;
		Map<String, Method> overrides = null;
		Map<String, Method> afters = null;

		Method[] methods = baseClass.getDeclaredMethods();
		for(int i=0; i<methods.length; i++)
		{
			Method method = methods[i];
			if(method.getDeclaringClass() != baseClass)
				continue;

			int modifiers = method.getModifiers();
			if(Modifier.isAbstract(modifiers))
				continue;

			if(Modifier.isStatic(modifiers))
				continue;

			String name = method.getName();
			if(name.length() < 7 || !name.substring(0, 7).equalsIgnoreCase("dynamic"))
				continue;
			else
				name = name.substring(7);

			while(name.charAt(0) == '_')
				name = name.substring(1);

			boolean before = false;
			boolean virtual = false;
			boolean override = false;
			boolean after = false;

			if(name.substring(0, 7).equalsIgnoreCase("virtual"))
			{
				virtual = true;
				name = name.substring(7);
			}
			else
			{
				if(name.length() >= 8 && name.substring(0, 8).equalsIgnoreCase("override"))
				{
					name = name.substring(8);
					override = true;
				}
				else if(name.length() >= 6 && name.substring(0, 6).equalsIgnoreCase("before"))
				{
					before = true;
					name = name.substring(6);
				}
				else if(name.length() >= 5 && name.substring(0, 5).equalsIgnoreCase("after"))
				{
					after = true;
					name = name.substring(5);
				}
			}

			if(name.length() >= 1 && (before || virtual || override || after))
				name = name.substring(0,1).toLowerCase() + name.substring(1);

			while(name.charAt(0) == '_')
				name = name.substring(1);

			if(name.length() == 0)
				throw new RuntimeException("Can not process dynamic hook method with no key");

			keys.add(name);

			if(virtual)
			{
				if(keysToVirtualIds.containsKey(name))
					throw new RuntimeException("Can not process more than one dynamic virtual method");

				keysToVirtualIds.put(name, id);
				virtuals = addDynamicMethod(name, method, virtuals);
			}
			else if(before)
				befores = addDynamicMethod(name, method, befores);
			else if(after)
				afters = addDynamicMethod(name, method, afters);
			else
				overrides = addDynamicMethod(name, method, overrides);
		}

		if(virtuals != null)
			virtualDynamicHookMethods.put(baseClass, virtuals);
		if(befores != null)
			beforeDynamicHookMethods.put(baseClass, befores);
		if(overrides != null)
			overrideDynamicHookMethods.put(baseClass, overrides);
		if(afters != null)
			afterDynamicHookMethods.put(baseClass, afters);
	}

	private static void addDynamicKeys(String id, Class<?> baseClass,  Map<Class<?>, Map<String, Method>> dynamicHookMethods, Map<String, List<String>> dynamicHookTypes)
	{
		Map<String, Method> methods = dynamicHookMethods.get(baseClass);
		if(methods == null || methods.size() == 0)
			return;

		Iterator<String> keys = methods.keySet().iterator();
		while(keys.hasNext())
		{
			String key = keys.next();
			if(!dynamicHookTypes.containsKey(key))
				dynamicHookTypes.put(key, new ArrayList<String>(1));
			dynamicHookTypes.get(key).add(id);
		}
	}

	private static Map<String, Method> addDynamicMethod(String key, Method method, Map<String, Method> methods)
	{
		if(methods == null)
			methods = new HashMap<String, Method>();
		if(methods.containsKey(key))
			throw new RuntimeException("method with key '" + key + "' allready exists");
		methods.put(key, method);
		return methods;
	}

	public static RenderPlayerAPI create(IRenderPlayerAPI renderPlayer)
	{
		if(allBaseConstructors.size() > 0 && !initialized)
			initialize();
		return new RenderPlayerAPI(renderPlayer);
	}

	private static void initialize()
	{
		sortBases(beforeLocalConstructingHookTypes, allBaseBeforeLocalConstructingSuperiors, allBaseBeforeLocalConstructingInferiors, "beforeLocalConstructing");
		sortBases(afterLocalConstructingHookTypes, allBaseAfterLocalConstructingSuperiors, allBaseAfterLocalConstructingInferiors, "afterLocalConstructing");

		Iterator<String> keyIterator = keys.iterator();
		while(keyIterator.hasNext())
		{
			String key = keyIterator.next();
			sortDynamicBases(beforeDynamicHookTypes, allBaseBeforeDynamicSuperiors, allBaseBeforeDynamicInferiors, key);
			sortDynamicBases(overrideDynamicHookTypes, allBaseOverrideDynamicSuperiors, allBaseOverrideDynamicInferiors, key);
			sortDynamicBases(afterDynamicHookTypes, allBaseAfterDynamicSuperiors, allBaseAfterDynamicInferiors, key);
		}

		sortBases(beforeAddLayerHookTypes, allBaseBeforeAddLayerSuperiors, allBaseBeforeAddLayerInferiors, "beforeAddLayer");
		sortBases(overrideAddLayerHookTypes, allBaseOverrideAddLayerSuperiors, allBaseOverrideAddLayerInferiors, "overrideAddLayer");
		sortBases(afterAddLayerHookTypes, allBaseAfterAddLayerSuperiors, allBaseAfterAddLayerInferiors, "afterAddLayer");

		sortBases(beforeBindEntityTextureHookTypes, allBaseBeforeBindEntityTextureSuperiors, allBaseBeforeBindEntityTextureInferiors, "beforeBindEntityTexture");
		sortBases(overrideBindEntityTextureHookTypes, allBaseOverrideBindEntityTextureSuperiors, allBaseOverrideBindEntityTextureInferiors, "overrideBindEntityTexture");
		sortBases(afterBindEntityTextureHookTypes, allBaseAfterBindEntityTextureSuperiors, allBaseAfterBindEntityTextureInferiors, "afterBindEntityTexture");

		sortBases(beforeBindTextureHookTypes, allBaseBeforeBindTextureSuperiors, allBaseBeforeBindTextureInferiors, "beforeBindTexture");
		sortBases(overrideBindTextureHookTypes, allBaseOverrideBindTextureSuperiors, allBaseOverrideBindTextureInferiors, "overrideBindTexture");
		sortBases(afterBindTextureHookTypes, allBaseAfterBindTextureSuperiors, allBaseAfterBindTextureInferiors, "afterBindTexture");

		sortBases(beforeCanRenderNameHookTypes, allBaseBeforeCanRenderNameSuperiors, allBaseBeforeCanRenderNameInferiors, "beforeCanRenderName");
		sortBases(overrideCanRenderNameHookTypes, allBaseOverrideCanRenderNameSuperiors, allBaseOverrideCanRenderNameInferiors, "overrideCanRenderName");
		sortBases(afterCanRenderNameHookTypes, allBaseAfterCanRenderNameSuperiors, allBaseAfterCanRenderNameInferiors, "afterCanRenderName");

		sortBases(beforeDoRenderHookTypes, allBaseBeforeDoRenderSuperiors, allBaseBeforeDoRenderInferiors, "beforeDoRender");
		sortBases(overrideDoRenderHookTypes, allBaseOverrideDoRenderSuperiors, allBaseOverrideDoRenderInferiors, "overrideDoRender");
		sortBases(afterDoRenderHookTypes, allBaseAfterDoRenderSuperiors, allBaseAfterDoRenderInferiors, "afterDoRender");

		sortBases(beforeDoRenderShadowAndFireHookTypes, allBaseBeforeDoRenderShadowAndFireSuperiors, allBaseBeforeDoRenderShadowAndFireInferiors, "beforeDoRenderShadowAndFire");
		sortBases(overrideDoRenderShadowAndFireHookTypes, allBaseOverrideDoRenderShadowAndFireSuperiors, allBaseOverrideDoRenderShadowAndFireInferiors, "overrideDoRenderShadowAndFire");
		sortBases(afterDoRenderShadowAndFireHookTypes, allBaseAfterDoRenderShadowAndFireSuperiors, allBaseAfterDoRenderShadowAndFireInferiors, "afterDoRenderShadowAndFire");

		sortBases(beforeGetColorMultiplierHookTypes, allBaseBeforeGetColorMultiplierSuperiors, allBaseBeforeGetColorMultiplierInferiors, "beforeGetColorMultiplier");
		sortBases(overrideGetColorMultiplierHookTypes, allBaseOverrideGetColorMultiplierSuperiors, allBaseOverrideGetColorMultiplierInferiors, "overrideGetColorMultiplier");
		sortBases(afterGetColorMultiplierHookTypes, allBaseAfterGetColorMultiplierSuperiors, allBaseAfterGetColorMultiplierInferiors, "afterGetColorMultiplier");

		sortBases(beforeGetDeathMaxRotationHookTypes, allBaseBeforeGetDeathMaxRotationSuperiors, allBaseBeforeGetDeathMaxRotationInferiors, "beforeGetDeathMaxRotation");
		sortBases(overrideGetDeathMaxRotationHookTypes, allBaseOverrideGetDeathMaxRotationSuperiors, allBaseOverrideGetDeathMaxRotationInferiors, "overrideGetDeathMaxRotation");
		sortBases(afterGetDeathMaxRotationHookTypes, allBaseAfterGetDeathMaxRotationSuperiors, allBaseAfterGetDeathMaxRotationInferiors, "afterGetDeathMaxRotation");

		sortBases(beforeGetEntityTextureHookTypes, allBaseBeforeGetEntityTextureSuperiors, allBaseBeforeGetEntityTextureInferiors, "beforeGetEntityTexture");
		sortBases(overrideGetEntityTextureHookTypes, allBaseOverrideGetEntityTextureSuperiors, allBaseOverrideGetEntityTextureInferiors, "overrideGetEntityTexture");
		sortBases(afterGetEntityTextureHookTypes, allBaseAfterGetEntityTextureSuperiors, allBaseAfterGetEntityTextureInferiors, "afterGetEntityTexture");

		sortBases(beforeGetFontRendererFromRenderManagerHookTypes, allBaseBeforeGetFontRendererFromRenderManagerSuperiors, allBaseBeforeGetFontRendererFromRenderManagerInferiors, "beforeGetFontRendererFromRenderManager");
		sortBases(overrideGetFontRendererFromRenderManagerHookTypes, allBaseOverrideGetFontRendererFromRenderManagerSuperiors, allBaseOverrideGetFontRendererFromRenderManagerInferiors, "overrideGetFontRendererFromRenderManager");
		sortBases(afterGetFontRendererFromRenderManagerHookTypes, allBaseAfterGetFontRendererFromRenderManagerSuperiors, allBaseAfterGetFontRendererFromRenderManagerInferiors, "afterGetFontRendererFromRenderManager");

		sortBases(beforeGetMainModelHookTypes, allBaseBeforeGetMainModelSuperiors, allBaseBeforeGetMainModelInferiors, "beforeGetMainModel");
		sortBases(overrideGetMainModelHookTypes, allBaseOverrideGetMainModelSuperiors, allBaseOverrideGetMainModelInferiors, "overrideGetMainModel");
		sortBases(afterGetMainModelHookTypes, allBaseAfterGetMainModelSuperiors, allBaseAfterGetMainModelInferiors, "afterGetMainModel");

		sortBases(beforeGetRenderManagerHookTypes, allBaseBeforeGetRenderManagerSuperiors, allBaseBeforeGetRenderManagerInferiors, "beforeGetRenderManager");
		sortBases(overrideGetRenderManagerHookTypes, allBaseOverrideGetRenderManagerSuperiors, allBaseOverrideGetRenderManagerInferiors, "overrideGetRenderManager");
		sortBases(afterGetRenderManagerHookTypes, allBaseAfterGetRenderManagerSuperiors, allBaseAfterGetRenderManagerInferiors, "afterGetRenderManager");

		sortBases(beforeGetSwingProgressHookTypes, allBaseBeforeGetSwingProgressSuperiors, allBaseBeforeGetSwingProgressInferiors, "beforeGetSwingProgress");
		sortBases(overrideGetSwingProgressHookTypes, allBaseOverrideGetSwingProgressSuperiors, allBaseOverrideGetSwingProgressInferiors, "overrideGetSwingProgress");
		sortBases(afterGetSwingProgressHookTypes, allBaseAfterGetSwingProgressSuperiors, allBaseAfterGetSwingProgressInferiors, "afterGetSwingProgress");

		sortBases(beforeGetTeamColorHookTypes, allBaseBeforeGetTeamColorSuperiors, allBaseBeforeGetTeamColorInferiors, "beforeGetTeamColor");
		sortBases(overrideGetTeamColorHookTypes, allBaseOverrideGetTeamColorSuperiors, allBaseOverrideGetTeamColorInferiors, "overrideGetTeamColor");
		sortBases(afterGetTeamColorHookTypes, allBaseAfterGetTeamColorSuperiors, allBaseAfterGetTeamColorInferiors, "afterGetTeamColor");

		sortBases(beforeHandleRotationFloatHookTypes, allBaseBeforeHandleRotationFloatSuperiors, allBaseBeforeHandleRotationFloatInferiors, "beforeHandleRotationFloat");
		sortBases(overrideHandleRotationFloatHookTypes, allBaseOverrideHandleRotationFloatSuperiors, allBaseOverrideHandleRotationFloatInferiors, "overrideHandleRotationFloat");
		sortBases(afterHandleRotationFloatHookTypes, allBaseAfterHandleRotationFloatSuperiors, allBaseAfterHandleRotationFloatInferiors, "afterHandleRotationFloat");

		sortBases(beforeInterpolateRotationHookTypes, allBaseBeforeInterpolateRotationSuperiors, allBaseBeforeInterpolateRotationInferiors, "beforeInterpolateRotation");
		sortBases(overrideInterpolateRotationHookTypes, allBaseOverrideInterpolateRotationSuperiors, allBaseOverrideInterpolateRotationInferiors, "overrideInterpolateRotation");
		sortBases(afterInterpolateRotationHookTypes, allBaseAfterInterpolateRotationSuperiors, allBaseAfterInterpolateRotationInferiors, "afterInterpolateRotation");

		sortBases(beforeIsMultipassHookTypes, allBaseBeforeIsMultipassSuperiors, allBaseBeforeIsMultipassInferiors, "beforeIsMultipass");
		sortBases(overrideIsMultipassHookTypes, allBaseOverrideIsMultipassSuperiors, allBaseOverrideIsMultipassInferiors, "overrideIsMultipass");
		sortBases(afterIsMultipassHookTypes, allBaseAfterIsMultipassSuperiors, allBaseAfterIsMultipassInferiors, "afterIsMultipass");

		sortBases(beforeIsVisibleHookTypes, allBaseBeforeIsVisibleSuperiors, allBaseBeforeIsVisibleInferiors, "beforeIsVisible");
		sortBases(overrideIsVisibleHookTypes, allBaseOverrideIsVisibleSuperiors, allBaseOverrideIsVisibleInferiors, "overrideIsVisible");
		sortBases(afterIsVisibleHookTypes, allBaseAfterIsVisibleSuperiors, allBaseAfterIsVisibleInferiors, "afterIsVisible");

		sortBases(beforePreRenderCallbackHookTypes, allBaseBeforePreRenderCallbackSuperiors, allBaseBeforePreRenderCallbackInferiors, "beforePreRenderCallback");
		sortBases(overridePreRenderCallbackHookTypes, allBaseOverridePreRenderCallbackSuperiors, allBaseOverridePreRenderCallbackInferiors, "overridePreRenderCallback");
		sortBases(afterPreRenderCallbackHookTypes, allBaseAfterPreRenderCallbackSuperiors, allBaseAfterPreRenderCallbackInferiors, "afterPreRenderCallback");

		sortBases(beforePrepareScaleHookTypes, allBaseBeforePrepareScaleSuperiors, allBaseBeforePrepareScaleInferiors, "beforePrepareScale");
		sortBases(overridePrepareScaleHookTypes, allBaseOverridePrepareScaleSuperiors, allBaseOverridePrepareScaleInferiors, "overridePrepareScale");
		sortBases(afterPrepareScaleHookTypes, allBaseAfterPrepareScaleSuperiors, allBaseAfterPrepareScaleInferiors, "afterPrepareScale");

		sortBases(beforeRenderEntityNameHookTypes, allBaseBeforeRenderEntityNameSuperiors, allBaseBeforeRenderEntityNameInferiors, "beforeRenderEntityName");
		sortBases(overrideRenderEntityNameHookTypes, allBaseOverrideRenderEntityNameSuperiors, allBaseOverrideRenderEntityNameInferiors, "overrideRenderEntityName");
		sortBases(afterRenderEntityNameHookTypes, allBaseAfterRenderEntityNameSuperiors, allBaseAfterRenderEntityNameInferiors, "afterRenderEntityName");

		sortBases(beforeRenderLayersHookTypes, allBaseBeforeRenderLayersSuperiors, allBaseBeforeRenderLayersInferiors, "beforeRenderLayers");
		sortBases(overrideRenderLayersHookTypes, allBaseOverrideRenderLayersSuperiors, allBaseOverrideRenderLayersInferiors, "overrideRenderLayers");
		sortBases(afterRenderLayersHookTypes, allBaseAfterRenderLayersSuperiors, allBaseAfterRenderLayersInferiors, "afterRenderLayers");

		sortBases(beforeRenderLeftArmHookTypes, allBaseBeforeRenderLeftArmSuperiors, allBaseBeforeRenderLeftArmInferiors, "beforeRenderLeftArm");
		sortBases(overrideRenderLeftArmHookTypes, allBaseOverrideRenderLeftArmSuperiors, allBaseOverrideRenderLeftArmInferiors, "overrideRenderLeftArm");
		sortBases(afterRenderLeftArmHookTypes, allBaseAfterRenderLeftArmSuperiors, allBaseAfterRenderLeftArmInferiors, "afterRenderLeftArm");

		sortBases(beforeRenderLivingAtHookTypes, allBaseBeforeRenderLivingAtSuperiors, allBaseBeforeRenderLivingAtInferiors, "beforeRenderLivingAt");
		sortBases(overrideRenderLivingAtHookTypes, allBaseOverrideRenderLivingAtSuperiors, allBaseOverrideRenderLivingAtInferiors, "overrideRenderLivingAt");
		sortBases(afterRenderLivingAtHookTypes, allBaseAfterRenderLivingAtSuperiors, allBaseAfterRenderLivingAtInferiors, "afterRenderLivingAt");

		sortBases(beforeRenderLivingLabelHookTypes, allBaseBeforeRenderLivingLabelSuperiors, allBaseBeforeRenderLivingLabelInferiors, "beforeRenderLivingLabel");
		sortBases(overrideRenderLivingLabelHookTypes, allBaseOverrideRenderLivingLabelSuperiors, allBaseOverrideRenderLivingLabelInferiors, "overrideRenderLivingLabel");
		sortBases(afterRenderLivingLabelHookTypes, allBaseAfterRenderLivingLabelSuperiors, allBaseAfterRenderLivingLabelInferiors, "afterRenderLivingLabel");

		sortBases(beforeRenderModelHookTypes, allBaseBeforeRenderModelSuperiors, allBaseBeforeRenderModelInferiors, "beforeRenderModel");
		sortBases(overrideRenderModelHookTypes, allBaseOverrideRenderModelSuperiors, allBaseOverrideRenderModelInferiors, "overrideRenderModel");
		sortBases(afterRenderModelHookTypes, allBaseAfterRenderModelSuperiors, allBaseAfterRenderModelInferiors, "afterRenderModel");

		sortBases(beforeRenderMultipassHookTypes, allBaseBeforeRenderMultipassSuperiors, allBaseBeforeRenderMultipassInferiors, "beforeRenderMultipass");
		sortBases(overrideRenderMultipassHookTypes, allBaseOverrideRenderMultipassSuperiors, allBaseOverrideRenderMultipassInferiors, "overrideRenderMultipass");
		sortBases(afterRenderMultipassHookTypes, allBaseAfterRenderMultipassSuperiors, allBaseAfterRenderMultipassInferiors, "afterRenderMultipass");

		sortBases(beforeRenderNameHookTypes, allBaseBeforeRenderNameSuperiors, allBaseBeforeRenderNameInferiors, "beforeRenderName");
		sortBases(overrideRenderNameHookTypes, allBaseOverrideRenderNameSuperiors, allBaseOverrideRenderNameInferiors, "overrideRenderName");
		sortBases(afterRenderNameHookTypes, allBaseAfterRenderNameSuperiors, allBaseAfterRenderNameInferiors, "afterRenderName");

		sortBases(beforeRenderRightArmHookTypes, allBaseBeforeRenderRightArmSuperiors, allBaseBeforeRenderRightArmInferiors, "beforeRenderRightArm");
		sortBases(overrideRenderRightArmHookTypes, allBaseOverrideRenderRightArmSuperiors, allBaseOverrideRenderRightArmInferiors, "overrideRenderRightArm");
		sortBases(afterRenderRightArmHookTypes, allBaseAfterRenderRightArmSuperiors, allBaseAfterRenderRightArmInferiors, "afterRenderRightArm");

		sortBases(beforeRotateCorpseHookTypes, allBaseBeforeRotateCorpseSuperiors, allBaseBeforeRotateCorpseInferiors, "beforeRotateCorpse");
		sortBases(overrideRotateCorpseHookTypes, allBaseOverrideRotateCorpseSuperiors, allBaseOverrideRotateCorpseInferiors, "overrideRotateCorpse");
		sortBases(afterRotateCorpseHookTypes, allBaseAfterRotateCorpseSuperiors, allBaseAfterRotateCorpseInferiors, "afterRotateCorpse");

		sortBases(beforeSetBrightnessHookTypes, allBaseBeforeSetBrightnessSuperiors, allBaseBeforeSetBrightnessInferiors, "beforeSetBrightness");
		sortBases(overrideSetBrightnessHookTypes, allBaseOverrideSetBrightnessSuperiors, allBaseOverrideSetBrightnessInferiors, "overrideSetBrightness");
		sortBases(afterSetBrightnessHookTypes, allBaseAfterSetBrightnessSuperiors, allBaseAfterSetBrightnessInferiors, "afterSetBrightness");

		sortBases(beforeSetDoRenderBrightnessHookTypes, allBaseBeforeSetDoRenderBrightnessSuperiors, allBaseBeforeSetDoRenderBrightnessInferiors, "beforeSetDoRenderBrightness");
		sortBases(overrideSetDoRenderBrightnessHookTypes, allBaseOverrideSetDoRenderBrightnessSuperiors, allBaseOverrideSetDoRenderBrightnessInferiors, "overrideSetDoRenderBrightness");
		sortBases(afterSetDoRenderBrightnessHookTypes, allBaseAfterSetDoRenderBrightnessSuperiors, allBaseAfterSetDoRenderBrightnessInferiors, "afterSetDoRenderBrightness");

		sortBases(beforeSetModelVisibilitiesHookTypes, allBaseBeforeSetModelVisibilitiesSuperiors, allBaseBeforeSetModelVisibilitiesInferiors, "beforeSetModelVisibilities");
		sortBases(overrideSetModelVisibilitiesHookTypes, allBaseOverrideSetModelVisibilitiesSuperiors, allBaseOverrideSetModelVisibilitiesInferiors, "overrideSetModelVisibilities");
		sortBases(afterSetModelVisibilitiesHookTypes, allBaseAfterSetModelVisibilitiesSuperiors, allBaseAfterSetModelVisibilitiesInferiors, "afterSetModelVisibilities");

		sortBases(beforeSetRenderOutlinesHookTypes, allBaseBeforeSetRenderOutlinesSuperiors, allBaseBeforeSetRenderOutlinesInferiors, "beforeSetRenderOutlines");
		sortBases(overrideSetRenderOutlinesHookTypes, allBaseOverrideSetRenderOutlinesSuperiors, allBaseOverrideSetRenderOutlinesInferiors, "overrideSetRenderOutlines");
		sortBases(afterSetRenderOutlinesHookTypes, allBaseAfterSetRenderOutlinesSuperiors, allBaseAfterSetRenderOutlinesInferiors, "afterSetRenderOutlines");

		sortBases(beforeSetScoreTeamColorHookTypes, allBaseBeforeSetScoreTeamColorSuperiors, allBaseBeforeSetScoreTeamColorInferiors, "beforeSetScoreTeamColor");
		sortBases(overrideSetScoreTeamColorHookTypes, allBaseOverrideSetScoreTeamColorSuperiors, allBaseOverrideSetScoreTeamColorInferiors, "overrideSetScoreTeamColor");
		sortBases(afterSetScoreTeamColorHookTypes, allBaseAfterSetScoreTeamColorSuperiors, allBaseAfterSetScoreTeamColorInferiors, "afterSetScoreTeamColor");

		sortBases(beforeShouldRenderHookTypes, allBaseBeforeShouldRenderSuperiors, allBaseBeforeShouldRenderInferiors, "beforeShouldRender");
		sortBases(overrideShouldRenderHookTypes, allBaseOverrideShouldRenderSuperiors, allBaseOverrideShouldRenderInferiors, "overrideShouldRender");
		sortBases(afterShouldRenderHookTypes, allBaseAfterShouldRenderSuperiors, allBaseAfterShouldRenderInferiors, "afterShouldRender");

		sortBases(beforeTransformHeldFull3DItemLayerHookTypes, allBaseBeforeTransformHeldFull3DItemLayerSuperiors, allBaseBeforeTransformHeldFull3DItemLayerInferiors, "beforeTransformHeldFull3DItemLayer");
		sortBases(overrideTransformHeldFull3DItemLayerHookTypes, allBaseOverrideTransformHeldFull3DItemLayerSuperiors, allBaseOverrideTransformHeldFull3DItemLayerInferiors, "overrideTransformHeldFull3DItemLayer");
		sortBases(afterTransformHeldFull3DItemLayerHookTypes, allBaseAfterTransformHeldFull3DItemLayerSuperiors, allBaseAfterTransformHeldFull3DItemLayerInferiors, "afterTransformHeldFull3DItemLayer");

		sortBases(beforeUnsetBrightnessHookTypes, allBaseBeforeUnsetBrightnessSuperiors, allBaseBeforeUnsetBrightnessInferiors, "beforeUnsetBrightness");
		sortBases(overrideUnsetBrightnessHookTypes, allBaseOverrideUnsetBrightnessSuperiors, allBaseOverrideUnsetBrightnessInferiors, "overrideUnsetBrightness");
		sortBases(afterUnsetBrightnessHookTypes, allBaseAfterUnsetBrightnessSuperiors, allBaseAfterUnsetBrightnessInferiors, "afterUnsetBrightness");

		sortBases(beforeUnsetScoreTeamColorHookTypes, allBaseBeforeUnsetScoreTeamColorSuperiors, allBaseBeforeUnsetScoreTeamColorInferiors, "beforeUnsetScoreTeamColor");
		sortBases(overrideUnsetScoreTeamColorHookTypes, allBaseOverrideUnsetScoreTeamColorSuperiors, allBaseOverrideUnsetScoreTeamColorInferiors, "overrideUnsetScoreTeamColor");
		sortBases(afterUnsetScoreTeamColorHookTypes, allBaseAfterUnsetScoreTeamColorSuperiors, allBaseAfterUnsetScoreTeamColorInferiors, "afterUnsetScoreTeamColor");

		initialized = true;
	}

	private static List<IRenderPlayerAPI> getAllInstancesList()
	{
		List<IRenderPlayerAPI> result = new ArrayList<IRenderPlayerAPI>();
		for(Iterator<WeakReference<IRenderPlayerAPI>> iterator = allInstances.iterator(); iterator.hasNext();)
		{
			IRenderPlayerAPI instance = iterator.next().get();
			if(instance != null)
				result.add(instance);
			else
				iterator.remove();
		}
		return result;
	}

	private static List<WeakReference<IRenderPlayerAPI>> allInstances = new ArrayList<WeakReference<IRenderPlayerAPI>>();

	public static net.minecraft.client.renderer.entity.RenderPlayer[] getAllInstances()
	{
		List<IRenderPlayerAPI> allInstances = getAllInstancesList();
		return allInstances.toArray(new net.minecraft.client.renderer.entity.RenderPlayer[allInstances.size()]);
	}

	public static void beforeLocalConstructing(IRenderPlayerAPI renderPlayer, net.minecraft.client.renderer.entity.RenderManager paramRenderManager, boolean paramBoolean)
	{
		RenderPlayerAPI renderPlayerAPI = renderPlayer.getRenderPlayerAPI();
		if(renderPlayerAPI != null)
			renderPlayerAPI.load();

		allInstances.add(new WeakReference<IRenderPlayerAPI>(renderPlayer));

		if(renderPlayerAPI != null)
			renderPlayerAPI.beforeLocalConstructing(paramRenderManager, paramBoolean);
	}

	public static void afterLocalConstructing(IRenderPlayerAPI renderPlayer, net.minecraft.client.renderer.entity.RenderManager paramRenderManager, boolean paramBoolean)
	{
		RenderPlayerAPI renderPlayerAPI = renderPlayer.getRenderPlayerAPI();
		if(renderPlayerAPI != null)
			renderPlayerAPI.afterLocalConstructing(paramRenderManager, paramBoolean);
	}

	public static RenderPlayerBase getRenderPlayerBase(IRenderPlayerAPI renderPlayer, String baseId)
	{
		RenderPlayerAPI renderPlayerAPI = renderPlayer.getRenderPlayerAPI();
		if(renderPlayerAPI != null)
			return renderPlayerAPI.getRenderPlayerBase(baseId);
		return null;
	}

	public static Set<String> getRenderPlayerBaseIds(IRenderPlayerAPI renderPlayer)
	{
		RenderPlayerAPI renderPlayerAPI = renderPlayer.getRenderPlayerAPI();
		Set<String> result = null;
		if(renderPlayerAPI != null)
			result = renderPlayerAPI.getRenderPlayerBaseIds();
		else
			result = Collections.<String>emptySet();
		return result;
	}

	public static Object dynamic(IRenderPlayerAPI renderPlayer, String key, Object[] parameters)
	{
		RenderPlayerAPI renderPlayerAPI = renderPlayer.getRenderPlayerAPI();
		if(renderPlayerAPI != null)
			return renderPlayerAPI.dynamic(key, parameters);
		return null;
	}

	private static void sortBases(List<String> list, Map<String, String[]> allBaseSuperiors, Map<String, String[]> allBaseInferiors, String methodName)
	{
		new RenderPlayerBaseSorter(list, allBaseSuperiors, allBaseInferiors, methodName).Sort();
	}

	private final static Map<String, String[]> EmptySortMap = Collections.unmodifiableMap(new HashMap<String, String[]>());

	private static void sortDynamicBases(Map<String, List<String>> lists, Map<String, Map<String, String[]>> allBaseSuperiors, Map<String, Map<String, String[]>> allBaseInferiors, String key)
	{
		List<String> types = lists.get(key);
		if(types != null && types.size() > 1)
			sortBases(types, getDynamicSorters(key, types, allBaseSuperiors), getDynamicSorters(key, types, allBaseInferiors), key);
	}

	private static Map<String, String[]> getDynamicSorters(String key, List<String> toSort, Map<String, Map<String, String[]>> allBaseValues)
	{
		Map<String, String[]> superiors = null;

		Iterator<String> ids = toSort.iterator();
		while(ids.hasNext())
		{
			String id = ids.next();
			Map<String, String[]> idSuperiors = allBaseValues.get(id);
			if(idSuperiors == null)
				continue;

			String[] keySuperiorIds = idSuperiors.get(key);
			if(keySuperiorIds != null && keySuperiorIds.length > 0)
			{
				if(superiors == null)
					superiors = new HashMap<String, String[]>(1);
				superiors.put(id, keySuperiorIds);
			}
		}

		return superiors != null ? superiors : EmptySortMap;
	}

	private RenderPlayerAPI(IRenderPlayerAPI renderPlayer)
	{
		this.renderPlayer = renderPlayer;
	}

	private void load()
	{
		Iterator<String> iterator = allBaseConstructors.keySet().iterator();
		while(iterator.hasNext())
		{
			String id = iterator.next();
			RenderPlayerBase toAttach = createRenderPlayerBase(id);
			toAttach.beforeBaseAttach(false);
			allBaseObjects.put(id, toAttach);
			baseObjectsToId.put(toAttach, id);
		}

		beforeLocalConstructingHooks = create(beforeLocalConstructingHookTypes);
		afterLocalConstructingHooks = create(afterLocalConstructingHookTypes);

		updateRenderPlayerBases();

		iterator = allBaseObjects.keySet().iterator();
		while(iterator.hasNext())
			allBaseObjects.get(iterator.next()).afterBaseAttach(false);
	}

	private RenderPlayerBase createRenderPlayerBase(String id)
	{
		Constructor<?> contructor = allBaseConstructors.get(id);

		RenderPlayerBase base;
		try
		{
			if(contructor.getParameterTypes().length == 1)
				base = (RenderPlayerBase)contructor.newInstance(this);
			else
				base = (RenderPlayerBase)contructor.newInstance(this, id);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Exception while creating a RenderPlayerBase of type '" + contructor.getDeclaringClass() + "'", e);
		}
		return base;
	}

	private void updateRenderPlayerBases()
	{
		beforeAddLayerHooks = create(beforeAddLayerHookTypes);
		overrideAddLayerHooks = create(overrideAddLayerHookTypes);
		afterAddLayerHooks = create(afterAddLayerHookTypes);
		isAddLayerModded =
			beforeAddLayerHooks != null ||
			overrideAddLayerHooks != null ||
			afterAddLayerHooks != null;

		beforeBindEntityTextureHooks = create(beforeBindEntityTextureHookTypes);
		overrideBindEntityTextureHooks = create(overrideBindEntityTextureHookTypes);
		afterBindEntityTextureHooks = create(afterBindEntityTextureHookTypes);
		isBindEntityTextureModded =
			beforeBindEntityTextureHooks != null ||
			overrideBindEntityTextureHooks != null ||
			afterBindEntityTextureHooks != null;

		beforeBindTextureHooks = create(beforeBindTextureHookTypes);
		overrideBindTextureHooks = create(overrideBindTextureHookTypes);
		afterBindTextureHooks = create(afterBindTextureHookTypes);
		isBindTextureModded =
			beforeBindTextureHooks != null ||
			overrideBindTextureHooks != null ||
			afterBindTextureHooks != null;

		beforeCanRenderNameHooks = create(beforeCanRenderNameHookTypes);
		overrideCanRenderNameHooks = create(overrideCanRenderNameHookTypes);
		afterCanRenderNameHooks = create(afterCanRenderNameHookTypes);
		isCanRenderNameModded =
			beforeCanRenderNameHooks != null ||
			overrideCanRenderNameHooks != null ||
			afterCanRenderNameHooks != null;

		beforeDoRenderHooks = create(beforeDoRenderHookTypes);
		overrideDoRenderHooks = create(overrideDoRenderHookTypes);
		afterDoRenderHooks = create(afterDoRenderHookTypes);
		isDoRenderModded =
			beforeDoRenderHooks != null ||
			overrideDoRenderHooks != null ||
			afterDoRenderHooks != null;

		beforeDoRenderShadowAndFireHooks = create(beforeDoRenderShadowAndFireHookTypes);
		overrideDoRenderShadowAndFireHooks = create(overrideDoRenderShadowAndFireHookTypes);
		afterDoRenderShadowAndFireHooks = create(afterDoRenderShadowAndFireHookTypes);
		isDoRenderShadowAndFireModded =
			beforeDoRenderShadowAndFireHooks != null ||
			overrideDoRenderShadowAndFireHooks != null ||
			afterDoRenderShadowAndFireHooks != null;

		beforeGetColorMultiplierHooks = create(beforeGetColorMultiplierHookTypes);
		overrideGetColorMultiplierHooks = create(overrideGetColorMultiplierHookTypes);
		afterGetColorMultiplierHooks = create(afterGetColorMultiplierHookTypes);
		isGetColorMultiplierModded =
			beforeGetColorMultiplierHooks != null ||
			overrideGetColorMultiplierHooks != null ||
			afterGetColorMultiplierHooks != null;

		beforeGetDeathMaxRotationHooks = create(beforeGetDeathMaxRotationHookTypes);
		overrideGetDeathMaxRotationHooks = create(overrideGetDeathMaxRotationHookTypes);
		afterGetDeathMaxRotationHooks = create(afterGetDeathMaxRotationHookTypes);
		isGetDeathMaxRotationModded =
			beforeGetDeathMaxRotationHooks != null ||
			overrideGetDeathMaxRotationHooks != null ||
			afterGetDeathMaxRotationHooks != null;

		beforeGetEntityTextureHooks = create(beforeGetEntityTextureHookTypes);
		overrideGetEntityTextureHooks = create(overrideGetEntityTextureHookTypes);
		afterGetEntityTextureHooks = create(afterGetEntityTextureHookTypes);
		isGetEntityTextureModded =
			beforeGetEntityTextureHooks != null ||
			overrideGetEntityTextureHooks != null ||
			afterGetEntityTextureHooks != null;

		beforeGetFontRendererFromRenderManagerHooks = create(beforeGetFontRendererFromRenderManagerHookTypes);
		overrideGetFontRendererFromRenderManagerHooks = create(overrideGetFontRendererFromRenderManagerHookTypes);
		afterGetFontRendererFromRenderManagerHooks = create(afterGetFontRendererFromRenderManagerHookTypes);
		isGetFontRendererFromRenderManagerModded =
			beforeGetFontRendererFromRenderManagerHooks != null ||
			overrideGetFontRendererFromRenderManagerHooks != null ||
			afterGetFontRendererFromRenderManagerHooks != null;

		beforeGetMainModelHooks = create(beforeGetMainModelHookTypes);
		overrideGetMainModelHooks = create(overrideGetMainModelHookTypes);
		afterGetMainModelHooks = create(afterGetMainModelHookTypes);
		isGetMainModelModded =
			beforeGetMainModelHooks != null ||
			overrideGetMainModelHooks != null ||
			afterGetMainModelHooks != null;

		beforeGetRenderManagerHooks = create(beforeGetRenderManagerHookTypes);
		overrideGetRenderManagerHooks = create(overrideGetRenderManagerHookTypes);
		afterGetRenderManagerHooks = create(afterGetRenderManagerHookTypes);
		isGetRenderManagerModded =
			beforeGetRenderManagerHooks != null ||
			overrideGetRenderManagerHooks != null ||
			afterGetRenderManagerHooks != null;

		beforeGetSwingProgressHooks = create(beforeGetSwingProgressHookTypes);
		overrideGetSwingProgressHooks = create(overrideGetSwingProgressHookTypes);
		afterGetSwingProgressHooks = create(afterGetSwingProgressHookTypes);
		isGetSwingProgressModded =
			beforeGetSwingProgressHooks != null ||
			overrideGetSwingProgressHooks != null ||
			afterGetSwingProgressHooks != null;

		beforeGetTeamColorHooks = create(beforeGetTeamColorHookTypes);
		overrideGetTeamColorHooks = create(overrideGetTeamColorHookTypes);
		afterGetTeamColorHooks = create(afterGetTeamColorHookTypes);
		isGetTeamColorModded =
			beforeGetTeamColorHooks != null ||
			overrideGetTeamColorHooks != null ||
			afterGetTeamColorHooks != null;

		beforeHandleRotationFloatHooks = create(beforeHandleRotationFloatHookTypes);
		overrideHandleRotationFloatHooks = create(overrideHandleRotationFloatHookTypes);
		afterHandleRotationFloatHooks = create(afterHandleRotationFloatHookTypes);
		isHandleRotationFloatModded =
			beforeHandleRotationFloatHooks != null ||
			overrideHandleRotationFloatHooks != null ||
			afterHandleRotationFloatHooks != null;

		beforeInterpolateRotationHooks = create(beforeInterpolateRotationHookTypes);
		overrideInterpolateRotationHooks = create(overrideInterpolateRotationHookTypes);
		afterInterpolateRotationHooks = create(afterInterpolateRotationHookTypes);
		isInterpolateRotationModded =
			beforeInterpolateRotationHooks != null ||
			overrideInterpolateRotationHooks != null ||
			afterInterpolateRotationHooks != null;

		beforeIsMultipassHooks = create(beforeIsMultipassHookTypes);
		overrideIsMultipassHooks = create(overrideIsMultipassHookTypes);
		afterIsMultipassHooks = create(afterIsMultipassHookTypes);
		isIsMultipassModded =
			beforeIsMultipassHooks != null ||
			overrideIsMultipassHooks != null ||
			afterIsMultipassHooks != null;

		beforeIsVisibleHooks = create(beforeIsVisibleHookTypes);
		overrideIsVisibleHooks = create(overrideIsVisibleHookTypes);
		afterIsVisibleHooks = create(afterIsVisibleHookTypes);
		isIsVisibleModded =
			beforeIsVisibleHooks != null ||
			overrideIsVisibleHooks != null ||
			afterIsVisibleHooks != null;

		beforePreRenderCallbackHooks = create(beforePreRenderCallbackHookTypes);
		overridePreRenderCallbackHooks = create(overridePreRenderCallbackHookTypes);
		afterPreRenderCallbackHooks = create(afterPreRenderCallbackHookTypes);
		isPreRenderCallbackModded =
			beforePreRenderCallbackHooks != null ||
			overridePreRenderCallbackHooks != null ||
			afterPreRenderCallbackHooks != null;

		beforePrepareScaleHooks = create(beforePrepareScaleHookTypes);
		overridePrepareScaleHooks = create(overridePrepareScaleHookTypes);
		afterPrepareScaleHooks = create(afterPrepareScaleHookTypes);
		isPrepareScaleModded =
			beforePrepareScaleHooks != null ||
			overridePrepareScaleHooks != null ||
			afterPrepareScaleHooks != null;

		beforeRenderEntityNameHooks = create(beforeRenderEntityNameHookTypes);
		overrideRenderEntityNameHooks = create(overrideRenderEntityNameHookTypes);
		afterRenderEntityNameHooks = create(afterRenderEntityNameHookTypes);
		isRenderEntityNameModded =
			beforeRenderEntityNameHooks != null ||
			overrideRenderEntityNameHooks != null ||
			afterRenderEntityNameHooks != null;

		beforeRenderLayersHooks = create(beforeRenderLayersHookTypes);
		overrideRenderLayersHooks = create(overrideRenderLayersHookTypes);
		afterRenderLayersHooks = create(afterRenderLayersHookTypes);
		isRenderLayersModded =
			beforeRenderLayersHooks != null ||
			overrideRenderLayersHooks != null ||
			afterRenderLayersHooks != null;

		beforeRenderLeftArmHooks = create(beforeRenderLeftArmHookTypes);
		overrideRenderLeftArmHooks = create(overrideRenderLeftArmHookTypes);
		afterRenderLeftArmHooks = create(afterRenderLeftArmHookTypes);
		isRenderLeftArmModded =
			beforeRenderLeftArmHooks != null ||
			overrideRenderLeftArmHooks != null ||
			afterRenderLeftArmHooks != null;

		beforeRenderLivingAtHooks = create(beforeRenderLivingAtHookTypes);
		overrideRenderLivingAtHooks = create(overrideRenderLivingAtHookTypes);
		afterRenderLivingAtHooks = create(afterRenderLivingAtHookTypes);
		isRenderLivingAtModded =
			beforeRenderLivingAtHooks != null ||
			overrideRenderLivingAtHooks != null ||
			afterRenderLivingAtHooks != null;

		beforeRenderLivingLabelHooks = create(beforeRenderLivingLabelHookTypes);
		overrideRenderLivingLabelHooks = create(overrideRenderLivingLabelHookTypes);
		afterRenderLivingLabelHooks = create(afterRenderLivingLabelHookTypes);
		isRenderLivingLabelModded =
			beforeRenderLivingLabelHooks != null ||
			overrideRenderLivingLabelHooks != null ||
			afterRenderLivingLabelHooks != null;

		beforeRenderModelHooks = create(beforeRenderModelHookTypes);
		overrideRenderModelHooks = create(overrideRenderModelHookTypes);
		afterRenderModelHooks = create(afterRenderModelHookTypes);
		isRenderModelModded =
			beforeRenderModelHooks != null ||
			overrideRenderModelHooks != null ||
			afterRenderModelHooks != null;

		beforeRenderMultipassHooks = create(beforeRenderMultipassHookTypes);
		overrideRenderMultipassHooks = create(overrideRenderMultipassHookTypes);
		afterRenderMultipassHooks = create(afterRenderMultipassHookTypes);
		isRenderMultipassModded =
			beforeRenderMultipassHooks != null ||
			overrideRenderMultipassHooks != null ||
			afterRenderMultipassHooks != null;

		beforeRenderNameHooks = create(beforeRenderNameHookTypes);
		overrideRenderNameHooks = create(overrideRenderNameHookTypes);
		afterRenderNameHooks = create(afterRenderNameHookTypes);
		isRenderNameModded =
			beforeRenderNameHooks != null ||
			overrideRenderNameHooks != null ||
			afterRenderNameHooks != null;

		beforeRenderRightArmHooks = create(beforeRenderRightArmHookTypes);
		overrideRenderRightArmHooks = create(overrideRenderRightArmHookTypes);
		afterRenderRightArmHooks = create(afterRenderRightArmHookTypes);
		isRenderRightArmModded =
			beforeRenderRightArmHooks != null ||
			overrideRenderRightArmHooks != null ||
			afterRenderRightArmHooks != null;

		beforeRotateCorpseHooks = create(beforeRotateCorpseHookTypes);
		overrideRotateCorpseHooks = create(overrideRotateCorpseHookTypes);
		afterRotateCorpseHooks = create(afterRotateCorpseHookTypes);
		isRotateCorpseModded =
			beforeRotateCorpseHooks != null ||
			overrideRotateCorpseHooks != null ||
			afterRotateCorpseHooks != null;

		beforeSetBrightnessHooks = create(beforeSetBrightnessHookTypes);
		overrideSetBrightnessHooks = create(overrideSetBrightnessHookTypes);
		afterSetBrightnessHooks = create(afterSetBrightnessHookTypes);
		isSetBrightnessModded =
			beforeSetBrightnessHooks != null ||
			overrideSetBrightnessHooks != null ||
			afterSetBrightnessHooks != null;

		beforeSetDoRenderBrightnessHooks = create(beforeSetDoRenderBrightnessHookTypes);
		overrideSetDoRenderBrightnessHooks = create(overrideSetDoRenderBrightnessHookTypes);
		afterSetDoRenderBrightnessHooks = create(afterSetDoRenderBrightnessHookTypes);
		isSetDoRenderBrightnessModded =
			beforeSetDoRenderBrightnessHooks != null ||
			overrideSetDoRenderBrightnessHooks != null ||
			afterSetDoRenderBrightnessHooks != null;

		beforeSetModelVisibilitiesHooks = create(beforeSetModelVisibilitiesHookTypes);
		overrideSetModelVisibilitiesHooks = create(overrideSetModelVisibilitiesHookTypes);
		afterSetModelVisibilitiesHooks = create(afterSetModelVisibilitiesHookTypes);
		isSetModelVisibilitiesModded =
			beforeSetModelVisibilitiesHooks != null ||
			overrideSetModelVisibilitiesHooks != null ||
			afterSetModelVisibilitiesHooks != null;

		beforeSetRenderOutlinesHooks = create(beforeSetRenderOutlinesHookTypes);
		overrideSetRenderOutlinesHooks = create(overrideSetRenderOutlinesHookTypes);
		afterSetRenderOutlinesHooks = create(afterSetRenderOutlinesHookTypes);
		isSetRenderOutlinesModded =
			beforeSetRenderOutlinesHooks != null ||
			overrideSetRenderOutlinesHooks != null ||
			afterSetRenderOutlinesHooks != null;

		beforeSetScoreTeamColorHooks = create(beforeSetScoreTeamColorHookTypes);
		overrideSetScoreTeamColorHooks = create(overrideSetScoreTeamColorHookTypes);
		afterSetScoreTeamColorHooks = create(afterSetScoreTeamColorHookTypes);
		isSetScoreTeamColorModded =
			beforeSetScoreTeamColorHooks != null ||
			overrideSetScoreTeamColorHooks != null ||
			afterSetScoreTeamColorHooks != null;

		beforeShouldRenderHooks = create(beforeShouldRenderHookTypes);
		overrideShouldRenderHooks = create(overrideShouldRenderHookTypes);
		afterShouldRenderHooks = create(afterShouldRenderHookTypes);
		isShouldRenderModded =
			beforeShouldRenderHooks != null ||
			overrideShouldRenderHooks != null ||
			afterShouldRenderHooks != null;

		beforeTransformHeldFull3DItemLayerHooks = create(beforeTransformHeldFull3DItemLayerHookTypes);
		overrideTransformHeldFull3DItemLayerHooks = create(overrideTransformHeldFull3DItemLayerHookTypes);
		afterTransformHeldFull3DItemLayerHooks = create(afterTransformHeldFull3DItemLayerHookTypes);
		isTransformHeldFull3DItemLayerModded =
			beforeTransformHeldFull3DItemLayerHooks != null ||
			overrideTransformHeldFull3DItemLayerHooks != null ||
			afterTransformHeldFull3DItemLayerHooks != null;

		beforeUnsetBrightnessHooks = create(beforeUnsetBrightnessHookTypes);
		overrideUnsetBrightnessHooks = create(overrideUnsetBrightnessHookTypes);
		afterUnsetBrightnessHooks = create(afterUnsetBrightnessHookTypes);
		isUnsetBrightnessModded =
			beforeUnsetBrightnessHooks != null ||
			overrideUnsetBrightnessHooks != null ||
			afterUnsetBrightnessHooks != null;

		beforeUnsetScoreTeamColorHooks = create(beforeUnsetScoreTeamColorHookTypes);
		overrideUnsetScoreTeamColorHooks = create(overrideUnsetScoreTeamColorHookTypes);
		afterUnsetScoreTeamColorHooks = create(afterUnsetScoreTeamColorHookTypes);
		isUnsetScoreTeamColorModded =
			beforeUnsetScoreTeamColorHooks != null ||
			overrideUnsetScoreTeamColorHooks != null ||
			afterUnsetScoreTeamColorHooks != null;

	}

	private void attachRenderPlayerBase(String id)
	{
        RenderPlayerBase toAttach = createRenderPlayerBase(id);
		toAttach.beforeBaseAttach(true);
		allBaseObjects.put(id, toAttach);
		updateRenderPlayerBases();
		toAttach.afterBaseAttach(true);
	}

	private void detachRenderPlayerBase(String id)
	{
		RenderPlayerBase toDetach = allBaseObjects.get(id);
		toDetach.beforeBaseDetach(true);
		allBaseObjects.remove(id);
		toDetach.afterBaseDetach(true);
	}

	private RenderPlayerBase[] create(List<String> types)
	{
		if(types.isEmpty())
			return null;

		RenderPlayerBase[] result = new RenderPlayerBase[types.size()];
		for(int i = 0; i < result.length; i++)
			result[i] = getRenderPlayerBase(types.get(i));
		return result;
	}

	private void beforeLocalConstructing(net.minecraft.client.renderer.entity.RenderManager paramRenderManager, boolean paramBoolean)
	{
		if(beforeLocalConstructingHooks != null)
			for(int i = beforeLocalConstructingHooks.length - 1; i >= 0 ; i--)
				beforeLocalConstructingHooks[i].beforeLocalConstructing(paramRenderManager, paramBoolean);
		beforeLocalConstructingHooks = null;
	}

	private void afterLocalConstructing(net.minecraft.client.renderer.entity.RenderManager paramRenderManager, boolean paramBoolean)
	{
		if(afterLocalConstructingHooks != null)
			for(int i = 0; i < afterLocalConstructingHooks.length; i++)
				afterLocalConstructingHooks[i].afterLocalConstructing(paramRenderManager, paramBoolean);
		afterLocalConstructingHooks = null;
	}

	public RenderPlayerBase getRenderPlayerBase(String id)
	{
		return allBaseObjects.get(id);
	}

	public Set<String> getRenderPlayerBaseIds()
	{
		return unmodifiableAllBaseIds;
	}

	public Object dynamic(String key, Object[] parameters)
	{
		key = key.replace('.', '_').replace(' ', '_');
		executeAll(key, parameters, beforeDynamicHookTypes, beforeDynamicHookMethods, true);
		Object result = dynamicOverwritten(key, parameters, null);
		executeAll(key, parameters, afterDynamicHookTypes, afterDynamicHookMethods, false);
		return result;
	}

	public Object dynamicOverwritten(String key, Object[] parameters, RenderPlayerBase overwriter)
	{
		List<String> overrideIds = overrideDynamicHookTypes.get(key);

		String id = null;
		if(overrideIds != null)
			if(overwriter != null)
			{
				id = baseObjectsToId.get(overwriter);
				int index = overrideIds.indexOf(id);
				if(index > 0)
					id = overrideIds.get(index - 1);
				else
					id = null;
			}
			else if(overrideIds.size() > 0)
				id = overrideIds.get(overrideIds.size() - 1);

		Map<Class<?>, Map<String, Method>> methodMap;

		if(id == null)
		{
			id = keysToVirtualIds.get(key);
			if(id == null)
				return null;
			methodMap = virtualDynamicHookMethods;
		}
		else
			methodMap = overrideDynamicHookMethods;

		Map<String, Method> methods = methodMap.get(allBaseConstructors.get(id).getDeclaringClass());
		if(methods == null)
			return null;

		Method method = methods.get(key);
		if(method == null)
			return null;

		return execute(getRenderPlayerBase(id), method, parameters);
	}

	private void executeAll(String key, Object[] parameters, Map<String, List<String>> dynamicHookTypes, Map<Class<?>, Map<String, Method>> dynamicHookMethods, boolean reverse)
	{
		List<String> beforeIds = dynamicHookTypes.get(key);
		if(beforeIds == null)
			return;

		for(int i= reverse ? beforeIds.size() - 1 : 0; reverse ? i >= 0 : i < beforeIds.size(); i = i + (reverse ? -1 : 1))
		{
			String id = beforeIds.get(i);
			RenderPlayerBase base = getRenderPlayerBase(id);
			Class<?> type = base.getClass();

			Map<String, Method> methods = dynamicHookMethods.get(type);
			if(methods == null)
				continue;

			Method method = methods.get(key);
			if(method == null)
				continue;

			execute(base, method, parameters);
		}
	}

	private Object execute(RenderPlayerBase base, Method method, Object[] parameters)
	{
		try
		{
			return method.invoke(base, parameters);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Exception while invoking dynamic method", e);
		}
	}

	public static boolean addLayer(IRenderPlayerAPI target, net.minecraft.client.renderer.entity.layers.LayerRenderer paramLayerRenderer)
	{
		boolean _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isAddLayerModded)
			_result = renderPlayerAPI.addLayer(paramLayerRenderer);
		else
			_result = target.localAddLayer(paramLayerRenderer);
		return _result;
	}

	private boolean addLayer(net.minecraft.client.renderer.entity.layers.LayerRenderer paramLayerRenderer)
	{
		if(beforeAddLayerHooks != null)
			for(int i = beforeAddLayerHooks.length - 1; i >= 0 ; i--)
				beforeAddLayerHooks[i].beforeAddLayer(paramLayerRenderer);

		boolean _result;
		if(overrideAddLayerHooks != null)
			_result = overrideAddLayerHooks[overrideAddLayerHooks.length - 1].addLayer(paramLayerRenderer);
		else
			_result = renderPlayer.localAddLayer(paramLayerRenderer);

		if(afterAddLayerHooks != null)
			for(int i = 0; i < afterAddLayerHooks.length; i++)
				afterAddLayerHooks[i].afterAddLayer(paramLayerRenderer);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenAddLayer(RenderPlayerBase overWriter)
	{
		if (overrideAddLayerHooks == null)
			return overWriter;

		for(int i = 0; i < overrideAddLayerHooks.length; i++)
			if(overrideAddLayerHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideAddLayerHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeAddLayerHookTypes = new LinkedList<String>();
	private final static List<String> overrideAddLayerHookTypes = new LinkedList<String>();
	private final static List<String> afterAddLayerHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeAddLayerHooks;
	private RenderPlayerBase[] overrideAddLayerHooks;
	private RenderPlayerBase[] afterAddLayerHooks;

	public boolean isAddLayerModded;

	private static final Map<String, String[]> allBaseBeforeAddLayerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeAddLayerInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideAddLayerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideAddLayerInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterAddLayerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterAddLayerInferiors = new Hashtable<String, String[]>(0);

	public static boolean bindEntityTexture(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		boolean _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isBindEntityTextureModded)
			_result = renderPlayerAPI.bindEntityTexture(paramAbstractClientPlayer);
		else
			_result = target.localBindEntityTexture(paramAbstractClientPlayer);
		return _result;
	}

	private boolean bindEntityTexture(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		if(beforeBindEntityTextureHooks != null)
			for(int i = beforeBindEntityTextureHooks.length - 1; i >= 0 ; i--)
				beforeBindEntityTextureHooks[i].beforeBindEntityTexture(paramAbstractClientPlayer);

		boolean _result;
		if(overrideBindEntityTextureHooks != null)
			_result = overrideBindEntityTextureHooks[overrideBindEntityTextureHooks.length - 1].bindEntityTexture(paramAbstractClientPlayer);
		else
			_result = renderPlayer.localBindEntityTexture(paramAbstractClientPlayer);

		if(afterBindEntityTextureHooks != null)
			for(int i = 0; i < afterBindEntityTextureHooks.length; i++)
				afterBindEntityTextureHooks[i].afterBindEntityTexture(paramAbstractClientPlayer);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenBindEntityTexture(RenderPlayerBase overWriter)
	{
		if (overrideBindEntityTextureHooks == null)
			return overWriter;

		for(int i = 0; i < overrideBindEntityTextureHooks.length; i++)
			if(overrideBindEntityTextureHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideBindEntityTextureHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeBindEntityTextureHookTypes = new LinkedList<String>();
	private final static List<String> overrideBindEntityTextureHookTypes = new LinkedList<String>();
	private final static List<String> afterBindEntityTextureHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeBindEntityTextureHooks;
	private RenderPlayerBase[] overrideBindEntityTextureHooks;
	private RenderPlayerBase[] afterBindEntityTextureHooks;

	public boolean isBindEntityTextureModded;

	private static final Map<String, String[]> allBaseBeforeBindEntityTextureSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeBindEntityTextureInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideBindEntityTextureSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideBindEntityTextureInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterBindEntityTextureSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterBindEntityTextureInferiors = new Hashtable<String, String[]>(0);

	public static void bindTexture(IRenderPlayerAPI target, net.minecraft.util.ResourceLocation paramResourceLocation)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isBindTextureModded)
			renderPlayerAPI.bindTexture(paramResourceLocation);
		else
			target.localBindTexture(paramResourceLocation);
	}

	private void bindTexture(net.minecraft.util.ResourceLocation paramResourceLocation)
	{
		if(beforeBindTextureHooks != null)
			for(int i = beforeBindTextureHooks.length - 1; i >= 0 ; i--)
				beforeBindTextureHooks[i].beforeBindTexture(paramResourceLocation);

		if(overrideBindTextureHooks != null)
			overrideBindTextureHooks[overrideBindTextureHooks.length - 1].bindTexture(paramResourceLocation);
		else
			renderPlayer.localBindTexture(paramResourceLocation);

		if(afterBindTextureHooks != null)
			for(int i = 0; i < afterBindTextureHooks.length; i++)
				afterBindTextureHooks[i].afterBindTexture(paramResourceLocation);

	}

	protected RenderPlayerBase GetOverwrittenBindTexture(RenderPlayerBase overWriter)
	{
		if (overrideBindTextureHooks == null)
			return overWriter;

		for(int i = 0; i < overrideBindTextureHooks.length; i++)
			if(overrideBindTextureHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideBindTextureHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeBindTextureHookTypes = new LinkedList<String>();
	private final static List<String> overrideBindTextureHookTypes = new LinkedList<String>();
	private final static List<String> afterBindTextureHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeBindTextureHooks;
	private RenderPlayerBase[] overrideBindTextureHooks;
	private RenderPlayerBase[] afterBindTextureHooks;

	public boolean isBindTextureModded;

	private static final Map<String, String[]> allBaseBeforeBindTextureSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeBindTextureInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideBindTextureSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideBindTextureInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterBindTextureSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterBindTextureInferiors = new Hashtable<String, String[]>(0);

	public static boolean canRenderName(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		boolean _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isCanRenderNameModded)
			_result = renderPlayerAPI.canRenderName(paramAbstractClientPlayer);
		else
			_result = target.localCanRenderName(paramAbstractClientPlayer);
		return _result;
	}

	private boolean canRenderName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		if(beforeCanRenderNameHooks != null)
			for(int i = beforeCanRenderNameHooks.length - 1; i >= 0 ; i--)
				beforeCanRenderNameHooks[i].beforeCanRenderName(paramAbstractClientPlayer);

		boolean _result;
		if(overrideCanRenderNameHooks != null)
			_result = overrideCanRenderNameHooks[overrideCanRenderNameHooks.length - 1].canRenderName(paramAbstractClientPlayer);
		else
			_result = renderPlayer.localCanRenderName(paramAbstractClientPlayer);

		if(afterCanRenderNameHooks != null)
			for(int i = 0; i < afterCanRenderNameHooks.length; i++)
				afterCanRenderNameHooks[i].afterCanRenderName(paramAbstractClientPlayer);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenCanRenderName(RenderPlayerBase overWriter)
	{
		if (overrideCanRenderNameHooks == null)
			return overWriter;

		for(int i = 0; i < overrideCanRenderNameHooks.length; i++)
			if(overrideCanRenderNameHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideCanRenderNameHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeCanRenderNameHookTypes = new LinkedList<String>();
	private final static List<String> overrideCanRenderNameHookTypes = new LinkedList<String>();
	private final static List<String> afterCanRenderNameHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeCanRenderNameHooks;
	private RenderPlayerBase[] overrideCanRenderNameHooks;
	private RenderPlayerBase[] afterCanRenderNameHooks;

	public boolean isCanRenderNameModded;

	private static final Map<String, String[]> allBaseBeforeCanRenderNameSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeCanRenderNameInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideCanRenderNameSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideCanRenderNameInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterCanRenderNameSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterCanRenderNameInferiors = new Hashtable<String, String[]>(0);

	public static void doRender(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isDoRenderModded)
			renderPlayerAPI.doRender(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
		else
			target.localDoRender(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
	}

	private void doRender(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
		if(beforeDoRenderHooks != null)
			for(int i = beforeDoRenderHooks.length - 1; i >= 0 ; i--)
				beforeDoRenderHooks[i].beforeDoRender(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

		if(overrideDoRenderHooks != null)
			overrideDoRenderHooks[overrideDoRenderHooks.length - 1].doRender(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
		else
			renderPlayer.localDoRender(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

		if(afterDoRenderHooks != null)
			for(int i = 0; i < afterDoRenderHooks.length; i++)
				afterDoRenderHooks[i].afterDoRender(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

	}

	protected RenderPlayerBase GetOverwrittenDoRender(RenderPlayerBase overWriter)
	{
		if (overrideDoRenderHooks == null)
			return overWriter;

		for(int i = 0; i < overrideDoRenderHooks.length; i++)
			if(overrideDoRenderHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideDoRenderHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeDoRenderHookTypes = new LinkedList<String>();
	private final static List<String> overrideDoRenderHookTypes = new LinkedList<String>();
	private final static List<String> afterDoRenderHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeDoRenderHooks;
	private RenderPlayerBase[] overrideDoRenderHooks;
	private RenderPlayerBase[] afterDoRenderHooks;

	public boolean isDoRenderModded;

	private static final Map<String, String[]> allBaseBeforeDoRenderSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeDoRenderInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideDoRenderSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideDoRenderInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterDoRenderSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterDoRenderInferiors = new Hashtable<String, String[]>(0);

	public static void doRenderShadowAndFire(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isDoRenderShadowAndFireModded)
			renderPlayerAPI.doRenderShadowAndFire(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
		else
			target.localDoRenderShadowAndFire(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
	}

	private void doRenderShadowAndFire(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
		if(beforeDoRenderShadowAndFireHooks != null)
			for(int i = beforeDoRenderShadowAndFireHooks.length - 1; i >= 0 ; i--)
				beforeDoRenderShadowAndFireHooks[i].beforeDoRenderShadowAndFire(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

		if(overrideDoRenderShadowAndFireHooks != null)
			overrideDoRenderShadowAndFireHooks[overrideDoRenderShadowAndFireHooks.length - 1].doRenderShadowAndFire(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
		else
			renderPlayer.localDoRenderShadowAndFire(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

		if(afterDoRenderShadowAndFireHooks != null)
			for(int i = 0; i < afterDoRenderShadowAndFireHooks.length; i++)
				afterDoRenderShadowAndFireHooks[i].afterDoRenderShadowAndFire(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

	}

	protected RenderPlayerBase GetOverwrittenDoRenderShadowAndFire(RenderPlayerBase overWriter)
	{
		if (overrideDoRenderShadowAndFireHooks == null)
			return overWriter;

		for(int i = 0; i < overrideDoRenderShadowAndFireHooks.length; i++)
			if(overrideDoRenderShadowAndFireHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideDoRenderShadowAndFireHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeDoRenderShadowAndFireHookTypes = new LinkedList<String>();
	private final static List<String> overrideDoRenderShadowAndFireHookTypes = new LinkedList<String>();
	private final static List<String> afterDoRenderShadowAndFireHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeDoRenderShadowAndFireHooks;
	private RenderPlayerBase[] overrideDoRenderShadowAndFireHooks;
	private RenderPlayerBase[] afterDoRenderShadowAndFireHooks;

	public boolean isDoRenderShadowAndFireModded;

	private static final Map<String, String[]> allBaseBeforeDoRenderShadowAndFireSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeDoRenderShadowAndFireInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideDoRenderShadowAndFireSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideDoRenderShadowAndFireInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterDoRenderShadowAndFireSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterDoRenderShadowAndFireInferiors = new Hashtable<String, String[]>(0);

	public static int getColorMultiplier(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2)
	{
		int _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isGetColorMultiplierModded)
			_result = renderPlayerAPI.getColorMultiplier(paramAbstractClientPlayer, paramFloat1, paramFloat2);
		else
			_result = target.localGetColorMultiplier(paramAbstractClientPlayer, paramFloat1, paramFloat2);
		return _result;
	}

	private int getColorMultiplier(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2)
	{
		if(beforeGetColorMultiplierHooks != null)
			for(int i = beforeGetColorMultiplierHooks.length - 1; i >= 0 ; i--)
				beforeGetColorMultiplierHooks[i].beforeGetColorMultiplier(paramAbstractClientPlayer, paramFloat1, paramFloat2);

		int _result;
		if(overrideGetColorMultiplierHooks != null)
			_result = overrideGetColorMultiplierHooks[overrideGetColorMultiplierHooks.length - 1].getColorMultiplier(paramAbstractClientPlayer, paramFloat1, paramFloat2);
		else
			_result = renderPlayer.localGetColorMultiplier(paramAbstractClientPlayer, paramFloat1, paramFloat2);

		if(afterGetColorMultiplierHooks != null)
			for(int i = 0; i < afterGetColorMultiplierHooks.length; i++)
				afterGetColorMultiplierHooks[i].afterGetColorMultiplier(paramAbstractClientPlayer, paramFloat1, paramFloat2);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenGetColorMultiplier(RenderPlayerBase overWriter)
	{
		if (overrideGetColorMultiplierHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetColorMultiplierHooks.length; i++)
			if(overrideGetColorMultiplierHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetColorMultiplierHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetColorMultiplierHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetColorMultiplierHookTypes = new LinkedList<String>();
	private final static List<String> afterGetColorMultiplierHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeGetColorMultiplierHooks;
	private RenderPlayerBase[] overrideGetColorMultiplierHooks;
	private RenderPlayerBase[] afterGetColorMultiplierHooks;

	public boolean isGetColorMultiplierModded;

	private static final Map<String, String[]> allBaseBeforeGetColorMultiplierSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetColorMultiplierInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetColorMultiplierSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetColorMultiplierInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetColorMultiplierSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetColorMultiplierInferiors = new Hashtable<String, String[]>(0);

	public static float getDeathMaxRotation(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		float _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isGetDeathMaxRotationModded)
			_result = renderPlayerAPI.getDeathMaxRotation(paramAbstractClientPlayer);
		else
			_result = target.localGetDeathMaxRotation(paramAbstractClientPlayer);
		return _result;
	}

	private float getDeathMaxRotation(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		if(beforeGetDeathMaxRotationHooks != null)
			for(int i = beforeGetDeathMaxRotationHooks.length - 1; i >= 0 ; i--)
				beforeGetDeathMaxRotationHooks[i].beforeGetDeathMaxRotation(paramAbstractClientPlayer);

		float _result;
		if(overrideGetDeathMaxRotationHooks != null)
			_result = overrideGetDeathMaxRotationHooks[overrideGetDeathMaxRotationHooks.length - 1].getDeathMaxRotation(paramAbstractClientPlayer);
		else
			_result = renderPlayer.localGetDeathMaxRotation(paramAbstractClientPlayer);

		if(afterGetDeathMaxRotationHooks != null)
			for(int i = 0; i < afterGetDeathMaxRotationHooks.length; i++)
				afterGetDeathMaxRotationHooks[i].afterGetDeathMaxRotation(paramAbstractClientPlayer);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenGetDeathMaxRotation(RenderPlayerBase overWriter)
	{
		if (overrideGetDeathMaxRotationHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetDeathMaxRotationHooks.length; i++)
			if(overrideGetDeathMaxRotationHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetDeathMaxRotationHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetDeathMaxRotationHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetDeathMaxRotationHookTypes = new LinkedList<String>();
	private final static List<String> afterGetDeathMaxRotationHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeGetDeathMaxRotationHooks;
	private RenderPlayerBase[] overrideGetDeathMaxRotationHooks;
	private RenderPlayerBase[] afterGetDeathMaxRotationHooks;

	public boolean isGetDeathMaxRotationModded;

	private static final Map<String, String[]> allBaseBeforeGetDeathMaxRotationSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetDeathMaxRotationInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetDeathMaxRotationSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetDeathMaxRotationInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetDeathMaxRotationSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetDeathMaxRotationInferiors = new Hashtable<String, String[]>(0);

	public static net.minecraft.util.ResourceLocation getEntityTexture(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		net.minecraft.util.ResourceLocation _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isGetEntityTextureModded)
			_result = renderPlayerAPI.getEntityTexture(paramAbstractClientPlayer);
		else
			_result = target.localGetEntityTexture(paramAbstractClientPlayer);
		return _result;
	}

	private net.minecraft.util.ResourceLocation getEntityTexture(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		if(beforeGetEntityTextureHooks != null)
			for(int i = beforeGetEntityTextureHooks.length - 1; i >= 0 ; i--)
				beforeGetEntityTextureHooks[i].beforeGetEntityTexture(paramAbstractClientPlayer);

		net.minecraft.util.ResourceLocation _result;
		if(overrideGetEntityTextureHooks != null)
			_result = overrideGetEntityTextureHooks[overrideGetEntityTextureHooks.length - 1].getEntityTexture(paramAbstractClientPlayer);
		else
			_result = renderPlayer.localGetEntityTexture(paramAbstractClientPlayer);

		if(afterGetEntityTextureHooks != null)
			for(int i = 0; i < afterGetEntityTextureHooks.length; i++)
				afterGetEntityTextureHooks[i].afterGetEntityTexture(paramAbstractClientPlayer);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenGetEntityTexture(RenderPlayerBase overWriter)
	{
		if (overrideGetEntityTextureHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetEntityTextureHooks.length; i++)
			if(overrideGetEntityTextureHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetEntityTextureHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetEntityTextureHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetEntityTextureHookTypes = new LinkedList<String>();
	private final static List<String> afterGetEntityTextureHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeGetEntityTextureHooks;
	private RenderPlayerBase[] overrideGetEntityTextureHooks;
	private RenderPlayerBase[] afterGetEntityTextureHooks;

	public boolean isGetEntityTextureModded;

	private static final Map<String, String[]> allBaseBeforeGetEntityTextureSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetEntityTextureInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetEntityTextureSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetEntityTextureInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetEntityTextureSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetEntityTextureInferiors = new Hashtable<String, String[]>(0);

	public static net.minecraft.client.gui.FontRenderer getFontRendererFromRenderManager(IRenderPlayerAPI target)
	{
		net.minecraft.client.gui.FontRenderer _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isGetFontRendererFromRenderManagerModded)
			_result = renderPlayerAPI.getFontRendererFromRenderManager();
		else
			_result = target.localGetFontRendererFromRenderManager();
		return _result;
	}

	private net.minecraft.client.gui.FontRenderer getFontRendererFromRenderManager()
	{
		if(beforeGetFontRendererFromRenderManagerHooks != null)
			for(int i = beforeGetFontRendererFromRenderManagerHooks.length - 1; i >= 0 ; i--)
				beforeGetFontRendererFromRenderManagerHooks[i].beforeGetFontRendererFromRenderManager();

		net.minecraft.client.gui.FontRenderer _result;
		if(overrideGetFontRendererFromRenderManagerHooks != null)
			_result = overrideGetFontRendererFromRenderManagerHooks[overrideGetFontRendererFromRenderManagerHooks.length - 1].getFontRendererFromRenderManager();
		else
			_result = renderPlayer.localGetFontRendererFromRenderManager();

		if(afterGetFontRendererFromRenderManagerHooks != null)
			for(int i = 0; i < afterGetFontRendererFromRenderManagerHooks.length; i++)
				afterGetFontRendererFromRenderManagerHooks[i].afterGetFontRendererFromRenderManager();

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenGetFontRendererFromRenderManager(RenderPlayerBase overWriter)
	{
		if (overrideGetFontRendererFromRenderManagerHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetFontRendererFromRenderManagerHooks.length; i++)
			if(overrideGetFontRendererFromRenderManagerHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetFontRendererFromRenderManagerHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetFontRendererFromRenderManagerHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetFontRendererFromRenderManagerHookTypes = new LinkedList<String>();
	private final static List<String> afterGetFontRendererFromRenderManagerHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeGetFontRendererFromRenderManagerHooks;
	private RenderPlayerBase[] overrideGetFontRendererFromRenderManagerHooks;
	private RenderPlayerBase[] afterGetFontRendererFromRenderManagerHooks;

	public boolean isGetFontRendererFromRenderManagerModded;

	private static final Map<String, String[]> allBaseBeforeGetFontRendererFromRenderManagerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetFontRendererFromRenderManagerInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetFontRendererFromRenderManagerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetFontRendererFromRenderManagerInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetFontRendererFromRenderManagerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetFontRendererFromRenderManagerInferiors = new Hashtable<String, String[]>(0);

	public static net.minecraft.client.model.ModelPlayer getMainModel(IRenderPlayerAPI target)
	{
		net.minecraft.client.model.ModelPlayer _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isGetMainModelModded)
			_result = renderPlayerAPI.getMainModel();
		else
			_result = target.localGetMainModel();
		return _result;
	}

	private net.minecraft.client.model.ModelPlayer getMainModel()
	{
		if(beforeGetMainModelHooks != null)
			for(int i = beforeGetMainModelHooks.length - 1; i >= 0 ; i--)
				beforeGetMainModelHooks[i].beforeGetMainModel();

		net.minecraft.client.model.ModelPlayer _result;
		if(overrideGetMainModelHooks != null)
			_result = overrideGetMainModelHooks[overrideGetMainModelHooks.length - 1].getMainModel();
		else
			_result = renderPlayer.localGetMainModel();

		if(afterGetMainModelHooks != null)
			for(int i = 0; i < afterGetMainModelHooks.length; i++)
				afterGetMainModelHooks[i].afterGetMainModel();

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenGetMainModel(RenderPlayerBase overWriter)
	{
		if (overrideGetMainModelHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetMainModelHooks.length; i++)
			if(overrideGetMainModelHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetMainModelHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetMainModelHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetMainModelHookTypes = new LinkedList<String>();
	private final static List<String> afterGetMainModelHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeGetMainModelHooks;
	private RenderPlayerBase[] overrideGetMainModelHooks;
	private RenderPlayerBase[] afterGetMainModelHooks;

	public boolean isGetMainModelModded;

	private static final Map<String, String[]> allBaseBeforeGetMainModelSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetMainModelInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetMainModelSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetMainModelInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetMainModelSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetMainModelInferiors = new Hashtable<String, String[]>(0);

	public static net.minecraft.client.renderer.entity.RenderManager getRenderManager(IRenderPlayerAPI target)
	{
		net.minecraft.client.renderer.entity.RenderManager _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isGetRenderManagerModded)
			_result = renderPlayerAPI.getRenderManager();
		else
			_result = target.localGetRenderManager();
		return _result;
	}

	private net.minecraft.client.renderer.entity.RenderManager getRenderManager()
	{
		if(beforeGetRenderManagerHooks != null)
			for(int i = beforeGetRenderManagerHooks.length - 1; i >= 0 ; i--)
				beforeGetRenderManagerHooks[i].beforeGetRenderManager();

		net.minecraft.client.renderer.entity.RenderManager _result;
		if(overrideGetRenderManagerHooks != null)
			_result = overrideGetRenderManagerHooks[overrideGetRenderManagerHooks.length - 1].getRenderManager();
		else
			_result = renderPlayer.localGetRenderManager();

		if(afterGetRenderManagerHooks != null)
			for(int i = 0; i < afterGetRenderManagerHooks.length; i++)
				afterGetRenderManagerHooks[i].afterGetRenderManager();

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenGetRenderManager(RenderPlayerBase overWriter)
	{
		if (overrideGetRenderManagerHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetRenderManagerHooks.length; i++)
			if(overrideGetRenderManagerHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetRenderManagerHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetRenderManagerHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetRenderManagerHookTypes = new LinkedList<String>();
	private final static List<String> afterGetRenderManagerHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeGetRenderManagerHooks;
	private RenderPlayerBase[] overrideGetRenderManagerHooks;
	private RenderPlayerBase[] afterGetRenderManagerHooks;

	public boolean isGetRenderManagerModded;

	private static final Map<String, String[]> allBaseBeforeGetRenderManagerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetRenderManagerInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetRenderManagerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetRenderManagerInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetRenderManagerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetRenderManagerInferiors = new Hashtable<String, String[]>(0);

	public static float getSwingProgress(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		float _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isGetSwingProgressModded)
			_result = renderPlayerAPI.getSwingProgress(paramAbstractClientPlayer, paramFloat);
		else
			_result = target.localGetSwingProgress(paramAbstractClientPlayer, paramFloat);
		return _result;
	}

	private float getSwingProgress(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		if(beforeGetSwingProgressHooks != null)
			for(int i = beforeGetSwingProgressHooks.length - 1; i >= 0 ; i--)
				beforeGetSwingProgressHooks[i].beforeGetSwingProgress(paramAbstractClientPlayer, paramFloat);

		float _result;
		if(overrideGetSwingProgressHooks != null)
			_result = overrideGetSwingProgressHooks[overrideGetSwingProgressHooks.length - 1].getSwingProgress(paramAbstractClientPlayer, paramFloat);
		else
			_result = renderPlayer.localGetSwingProgress(paramAbstractClientPlayer, paramFloat);

		if(afterGetSwingProgressHooks != null)
			for(int i = 0; i < afterGetSwingProgressHooks.length; i++)
				afterGetSwingProgressHooks[i].afterGetSwingProgress(paramAbstractClientPlayer, paramFloat);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenGetSwingProgress(RenderPlayerBase overWriter)
	{
		if (overrideGetSwingProgressHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetSwingProgressHooks.length; i++)
			if(overrideGetSwingProgressHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetSwingProgressHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetSwingProgressHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetSwingProgressHookTypes = new LinkedList<String>();
	private final static List<String> afterGetSwingProgressHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeGetSwingProgressHooks;
	private RenderPlayerBase[] overrideGetSwingProgressHooks;
	private RenderPlayerBase[] afterGetSwingProgressHooks;

	public boolean isGetSwingProgressModded;

	private static final Map<String, String[]> allBaseBeforeGetSwingProgressSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetSwingProgressInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetSwingProgressSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetSwingProgressInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetSwingProgressSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetSwingProgressInferiors = new Hashtable<String, String[]>(0);

	public static int getTeamColor(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		int _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isGetTeamColorModded)
			_result = renderPlayerAPI.getTeamColor(paramAbstractClientPlayer);
		else
			_result = target.localGetTeamColor(paramAbstractClientPlayer);
		return _result;
	}

	private int getTeamColor(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		if(beforeGetTeamColorHooks != null)
			for(int i = beforeGetTeamColorHooks.length - 1; i >= 0 ; i--)
				beforeGetTeamColorHooks[i].beforeGetTeamColor(paramAbstractClientPlayer);

		int _result;
		if(overrideGetTeamColorHooks != null)
			_result = overrideGetTeamColorHooks[overrideGetTeamColorHooks.length - 1].getTeamColor(paramAbstractClientPlayer);
		else
			_result = renderPlayer.localGetTeamColor(paramAbstractClientPlayer);

		if(afterGetTeamColorHooks != null)
			for(int i = 0; i < afterGetTeamColorHooks.length; i++)
				afterGetTeamColorHooks[i].afterGetTeamColor(paramAbstractClientPlayer);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenGetTeamColor(RenderPlayerBase overWriter)
	{
		if (overrideGetTeamColorHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetTeamColorHooks.length; i++)
			if(overrideGetTeamColorHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetTeamColorHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetTeamColorHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetTeamColorHookTypes = new LinkedList<String>();
	private final static List<String> afterGetTeamColorHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeGetTeamColorHooks;
	private RenderPlayerBase[] overrideGetTeamColorHooks;
	private RenderPlayerBase[] afterGetTeamColorHooks;

	public boolean isGetTeamColorModded;

	private static final Map<String, String[]> allBaseBeforeGetTeamColorSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetTeamColorInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetTeamColorSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetTeamColorInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetTeamColorSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetTeamColorInferiors = new Hashtable<String, String[]>(0);

	public static float handleRotationFloat(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		float _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isHandleRotationFloatModded)
			_result = renderPlayerAPI.handleRotationFloat(paramAbstractClientPlayer, paramFloat);
		else
			_result = target.localHandleRotationFloat(paramAbstractClientPlayer, paramFloat);
		return _result;
	}

	private float handleRotationFloat(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		if(beforeHandleRotationFloatHooks != null)
			for(int i = beforeHandleRotationFloatHooks.length - 1; i >= 0 ; i--)
				beforeHandleRotationFloatHooks[i].beforeHandleRotationFloat(paramAbstractClientPlayer, paramFloat);

		float _result;
		if(overrideHandleRotationFloatHooks != null)
			_result = overrideHandleRotationFloatHooks[overrideHandleRotationFloatHooks.length - 1].handleRotationFloat(paramAbstractClientPlayer, paramFloat);
		else
			_result = renderPlayer.localHandleRotationFloat(paramAbstractClientPlayer, paramFloat);

		if(afterHandleRotationFloatHooks != null)
			for(int i = 0; i < afterHandleRotationFloatHooks.length; i++)
				afterHandleRotationFloatHooks[i].afterHandleRotationFloat(paramAbstractClientPlayer, paramFloat);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenHandleRotationFloat(RenderPlayerBase overWriter)
	{
		if (overrideHandleRotationFloatHooks == null)
			return overWriter;

		for(int i = 0; i < overrideHandleRotationFloatHooks.length; i++)
			if(overrideHandleRotationFloatHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideHandleRotationFloatHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeHandleRotationFloatHookTypes = new LinkedList<String>();
	private final static List<String> overrideHandleRotationFloatHookTypes = new LinkedList<String>();
	private final static List<String> afterHandleRotationFloatHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeHandleRotationFloatHooks;
	private RenderPlayerBase[] overrideHandleRotationFloatHooks;
	private RenderPlayerBase[] afterHandleRotationFloatHooks;

	public boolean isHandleRotationFloatModded;

	private static final Map<String, String[]> allBaseBeforeHandleRotationFloatSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeHandleRotationFloatInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideHandleRotationFloatSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideHandleRotationFloatInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterHandleRotationFloatSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterHandleRotationFloatInferiors = new Hashtable<String, String[]>(0);

	public static float interpolateRotation(IRenderPlayerAPI target, float paramFloat1, float paramFloat2, float paramFloat3)
	{
		float _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isInterpolateRotationModded)
			_result = renderPlayerAPI.interpolateRotation(paramFloat1, paramFloat2, paramFloat3);
		else
			_result = target.localInterpolateRotation(paramFloat1, paramFloat2, paramFloat3);
		return _result;
	}

	private float interpolateRotation(float paramFloat1, float paramFloat2, float paramFloat3)
	{
		if(beforeInterpolateRotationHooks != null)
			for(int i = beforeInterpolateRotationHooks.length - 1; i >= 0 ; i--)
				beforeInterpolateRotationHooks[i].beforeInterpolateRotation(paramFloat1, paramFloat2, paramFloat3);

		float _result;
		if(overrideInterpolateRotationHooks != null)
			_result = overrideInterpolateRotationHooks[overrideInterpolateRotationHooks.length - 1].interpolateRotation(paramFloat1, paramFloat2, paramFloat3);
		else
			_result = renderPlayer.localInterpolateRotation(paramFloat1, paramFloat2, paramFloat3);

		if(afterInterpolateRotationHooks != null)
			for(int i = 0; i < afterInterpolateRotationHooks.length; i++)
				afterInterpolateRotationHooks[i].afterInterpolateRotation(paramFloat1, paramFloat2, paramFloat3);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenInterpolateRotation(RenderPlayerBase overWriter)
	{
		if (overrideInterpolateRotationHooks == null)
			return overWriter;

		for(int i = 0; i < overrideInterpolateRotationHooks.length; i++)
			if(overrideInterpolateRotationHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideInterpolateRotationHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeInterpolateRotationHookTypes = new LinkedList<String>();
	private final static List<String> overrideInterpolateRotationHookTypes = new LinkedList<String>();
	private final static List<String> afterInterpolateRotationHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeInterpolateRotationHooks;
	private RenderPlayerBase[] overrideInterpolateRotationHooks;
	private RenderPlayerBase[] afterInterpolateRotationHooks;

	public boolean isInterpolateRotationModded;

	private static final Map<String, String[]> allBaseBeforeInterpolateRotationSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeInterpolateRotationInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideInterpolateRotationSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideInterpolateRotationInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterInterpolateRotationSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterInterpolateRotationInferiors = new Hashtable<String, String[]>(0);

	public static boolean isMultipass(IRenderPlayerAPI target)
	{
		boolean _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isIsMultipassModded)
			_result = renderPlayerAPI.isMultipass();
		else
			_result = target.localIsMultipass();
		return _result;
	}

	private boolean isMultipass()
	{
		if(beforeIsMultipassHooks != null)
			for(int i = beforeIsMultipassHooks.length - 1; i >= 0 ; i--)
				beforeIsMultipassHooks[i].beforeIsMultipass();

		boolean _result;
		if(overrideIsMultipassHooks != null)
			_result = overrideIsMultipassHooks[overrideIsMultipassHooks.length - 1].isMultipass();
		else
			_result = renderPlayer.localIsMultipass();

		if(afterIsMultipassHooks != null)
			for(int i = 0; i < afterIsMultipassHooks.length; i++)
				afterIsMultipassHooks[i].afterIsMultipass();

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenIsMultipass(RenderPlayerBase overWriter)
	{
		if (overrideIsMultipassHooks == null)
			return overWriter;

		for(int i = 0; i < overrideIsMultipassHooks.length; i++)
			if(overrideIsMultipassHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideIsMultipassHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeIsMultipassHookTypes = new LinkedList<String>();
	private final static List<String> overrideIsMultipassHookTypes = new LinkedList<String>();
	private final static List<String> afterIsMultipassHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeIsMultipassHooks;
	private RenderPlayerBase[] overrideIsMultipassHooks;
	private RenderPlayerBase[] afterIsMultipassHooks;

	public boolean isIsMultipassModded;

	private static final Map<String, String[]> allBaseBeforeIsMultipassSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeIsMultipassInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideIsMultipassSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideIsMultipassInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterIsMultipassSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterIsMultipassInferiors = new Hashtable<String, String[]>(0);

	public static boolean isVisible(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		boolean _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isIsVisibleModded)
			_result = renderPlayerAPI.isVisible(paramAbstractClientPlayer);
		else
			_result = target.localIsVisible(paramAbstractClientPlayer);
		return _result;
	}

	private boolean isVisible(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		if(beforeIsVisibleHooks != null)
			for(int i = beforeIsVisibleHooks.length - 1; i >= 0 ; i--)
				beforeIsVisibleHooks[i].beforeIsVisible(paramAbstractClientPlayer);

		boolean _result;
		if(overrideIsVisibleHooks != null)
			_result = overrideIsVisibleHooks[overrideIsVisibleHooks.length - 1].isVisible(paramAbstractClientPlayer);
		else
			_result = renderPlayer.localIsVisible(paramAbstractClientPlayer);

		if(afterIsVisibleHooks != null)
			for(int i = 0; i < afterIsVisibleHooks.length; i++)
				afterIsVisibleHooks[i].afterIsVisible(paramAbstractClientPlayer);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenIsVisible(RenderPlayerBase overWriter)
	{
		if (overrideIsVisibleHooks == null)
			return overWriter;

		for(int i = 0; i < overrideIsVisibleHooks.length; i++)
			if(overrideIsVisibleHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideIsVisibleHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeIsVisibleHookTypes = new LinkedList<String>();
	private final static List<String> overrideIsVisibleHookTypes = new LinkedList<String>();
	private final static List<String> afterIsVisibleHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeIsVisibleHooks;
	private RenderPlayerBase[] overrideIsVisibleHooks;
	private RenderPlayerBase[] afterIsVisibleHooks;

	public boolean isIsVisibleModded;

	private static final Map<String, String[]> allBaseBeforeIsVisibleSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeIsVisibleInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideIsVisibleSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideIsVisibleInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterIsVisibleSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterIsVisibleInferiors = new Hashtable<String, String[]>(0);

	public static void preRenderCallback(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isPreRenderCallbackModded)
			renderPlayerAPI.preRenderCallback(paramAbstractClientPlayer, paramFloat);
		else
			target.localPreRenderCallback(paramAbstractClientPlayer, paramFloat);
	}

	private void preRenderCallback(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		if(beforePreRenderCallbackHooks != null)
			for(int i = beforePreRenderCallbackHooks.length - 1; i >= 0 ; i--)
				beforePreRenderCallbackHooks[i].beforePreRenderCallback(paramAbstractClientPlayer, paramFloat);

		if(overridePreRenderCallbackHooks != null)
			overridePreRenderCallbackHooks[overridePreRenderCallbackHooks.length - 1].preRenderCallback(paramAbstractClientPlayer, paramFloat);
		else
			renderPlayer.localPreRenderCallback(paramAbstractClientPlayer, paramFloat);

		if(afterPreRenderCallbackHooks != null)
			for(int i = 0; i < afterPreRenderCallbackHooks.length; i++)
				afterPreRenderCallbackHooks[i].afterPreRenderCallback(paramAbstractClientPlayer, paramFloat);

	}

	protected RenderPlayerBase GetOverwrittenPreRenderCallback(RenderPlayerBase overWriter)
	{
		if (overridePreRenderCallbackHooks == null)
			return overWriter;

		for(int i = 0; i < overridePreRenderCallbackHooks.length; i++)
			if(overridePreRenderCallbackHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overridePreRenderCallbackHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforePreRenderCallbackHookTypes = new LinkedList<String>();
	private final static List<String> overridePreRenderCallbackHookTypes = new LinkedList<String>();
	private final static List<String> afterPreRenderCallbackHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforePreRenderCallbackHooks;
	private RenderPlayerBase[] overridePreRenderCallbackHooks;
	private RenderPlayerBase[] afterPreRenderCallbackHooks;

	public boolean isPreRenderCallbackModded;

	private static final Map<String, String[]> allBaseBeforePreRenderCallbackSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforePreRenderCallbackInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverridePreRenderCallbackSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverridePreRenderCallbackInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterPreRenderCallbackSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterPreRenderCallbackInferiors = new Hashtable<String, String[]>(0);

	public static float prepareScale(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		float _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isPrepareScaleModded)
			_result = renderPlayerAPI.prepareScale(paramAbstractClientPlayer, paramFloat);
		else
			_result = target.localPrepareScale(paramAbstractClientPlayer, paramFloat);
		return _result;
	}

	private float prepareScale(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		if(beforePrepareScaleHooks != null)
			for(int i = beforePrepareScaleHooks.length - 1; i >= 0 ; i--)
				beforePrepareScaleHooks[i].beforePrepareScale(paramAbstractClientPlayer, paramFloat);

		float _result;
		if(overridePrepareScaleHooks != null)
			_result = overridePrepareScaleHooks[overridePrepareScaleHooks.length - 1].prepareScale(paramAbstractClientPlayer, paramFloat);
		else
			_result = renderPlayer.localPrepareScale(paramAbstractClientPlayer, paramFloat);

		if(afterPrepareScaleHooks != null)
			for(int i = 0; i < afterPrepareScaleHooks.length; i++)
				afterPrepareScaleHooks[i].afterPrepareScale(paramAbstractClientPlayer, paramFloat);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenPrepareScale(RenderPlayerBase overWriter)
	{
		if (overridePrepareScaleHooks == null)
			return overWriter;

		for(int i = 0; i < overridePrepareScaleHooks.length; i++)
			if(overridePrepareScaleHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overridePrepareScaleHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforePrepareScaleHookTypes = new LinkedList<String>();
	private final static List<String> overridePrepareScaleHookTypes = new LinkedList<String>();
	private final static List<String> afterPrepareScaleHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforePrepareScaleHooks;
	private RenderPlayerBase[] overridePrepareScaleHooks;
	private RenderPlayerBase[] afterPrepareScaleHooks;

	public boolean isPrepareScaleModded;

	private static final Map<String, String[]> allBaseBeforePrepareScaleSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforePrepareScaleInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverridePrepareScaleSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverridePrepareScaleInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterPrepareScaleSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterPrepareScaleInferiors = new Hashtable<String, String[]>(0);

	public static void renderEntityName(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, String paramString, double paramDouble4)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isRenderEntityNameModded)
			renderPlayerAPI.renderEntityName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramString, paramDouble4);
		else
			target.localRenderEntityName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramString, paramDouble4);
	}

	private void renderEntityName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, String paramString, double paramDouble4)
	{
		if(beforeRenderEntityNameHooks != null)
			for(int i = beforeRenderEntityNameHooks.length - 1; i >= 0 ; i--)
				beforeRenderEntityNameHooks[i].beforeRenderEntityName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramString, paramDouble4);

		if(overrideRenderEntityNameHooks != null)
			overrideRenderEntityNameHooks[overrideRenderEntityNameHooks.length - 1].renderEntityName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramString, paramDouble4);
		else
			renderPlayer.localRenderEntityName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramString, paramDouble4);

		if(afterRenderEntityNameHooks != null)
			for(int i = 0; i < afterRenderEntityNameHooks.length; i++)
				afterRenderEntityNameHooks[i].afterRenderEntityName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramString, paramDouble4);

	}

	protected RenderPlayerBase GetOverwrittenRenderEntityName(RenderPlayerBase overWriter)
	{
		if (overrideRenderEntityNameHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderEntityNameHooks.length; i++)
			if(overrideRenderEntityNameHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderEntityNameHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderEntityNameHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderEntityNameHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderEntityNameHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeRenderEntityNameHooks;
	private RenderPlayerBase[] overrideRenderEntityNameHooks;
	private RenderPlayerBase[] afterRenderEntityNameHooks;

	public boolean isRenderEntityNameModded;

	private static final Map<String, String[]> allBaseBeforeRenderEntityNameSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderEntityNameInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderEntityNameSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderEntityNameInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderEntityNameSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderEntityNameInferiors = new Hashtable<String, String[]>(0);

	public static void renderLayers(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isRenderLayersModded)
			renderPlayerAPI.renderLayers(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7);
		else
			target.localRenderLayers(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7);
	}

	private void renderLayers(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7)
	{
		if(beforeRenderLayersHooks != null)
			for(int i = beforeRenderLayersHooks.length - 1; i >= 0 ; i--)
				beforeRenderLayersHooks[i].beforeRenderLayers(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7);

		if(overrideRenderLayersHooks != null)
			overrideRenderLayersHooks[overrideRenderLayersHooks.length - 1].renderLayers(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7);
		else
			renderPlayer.localRenderLayers(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7);

		if(afterRenderLayersHooks != null)
			for(int i = 0; i < afterRenderLayersHooks.length; i++)
				afterRenderLayersHooks[i].afterRenderLayers(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7);

	}

	protected RenderPlayerBase GetOverwrittenRenderLayers(RenderPlayerBase overWriter)
	{
		if (overrideRenderLayersHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderLayersHooks.length; i++)
			if(overrideRenderLayersHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderLayersHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderLayersHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderLayersHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderLayersHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeRenderLayersHooks;
	private RenderPlayerBase[] overrideRenderLayersHooks;
	private RenderPlayerBase[] afterRenderLayersHooks;

	public boolean isRenderLayersModded;

	private static final Map<String, String[]> allBaseBeforeRenderLayersSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderLayersInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderLayersSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderLayersInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderLayersSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderLayersInferiors = new Hashtable<String, String[]>(0);

	public static void renderLeftArm(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isRenderLeftArmModded)
			renderPlayerAPI.renderLeftArm(paramAbstractClientPlayer);
		else
			target.localRenderLeftArm(paramAbstractClientPlayer);
	}

	private void renderLeftArm(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		if(beforeRenderLeftArmHooks != null)
			for(int i = beforeRenderLeftArmHooks.length - 1; i >= 0 ; i--)
				beforeRenderLeftArmHooks[i].beforeRenderLeftArm(paramAbstractClientPlayer);

		if(overrideRenderLeftArmHooks != null)
			overrideRenderLeftArmHooks[overrideRenderLeftArmHooks.length - 1].renderLeftArm(paramAbstractClientPlayer);
		else
			renderPlayer.localRenderLeftArm(paramAbstractClientPlayer);

		if(afterRenderLeftArmHooks != null)
			for(int i = 0; i < afterRenderLeftArmHooks.length; i++)
				afterRenderLeftArmHooks[i].afterRenderLeftArm(paramAbstractClientPlayer);

	}

	protected RenderPlayerBase GetOverwrittenRenderLeftArm(RenderPlayerBase overWriter)
	{
		if (overrideRenderLeftArmHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderLeftArmHooks.length; i++)
			if(overrideRenderLeftArmHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderLeftArmHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderLeftArmHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderLeftArmHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderLeftArmHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeRenderLeftArmHooks;
	private RenderPlayerBase[] overrideRenderLeftArmHooks;
	private RenderPlayerBase[] afterRenderLeftArmHooks;

	public boolean isRenderLeftArmModded;

	private static final Map<String, String[]> allBaseBeforeRenderLeftArmSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderLeftArmInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderLeftArmSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderLeftArmInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderLeftArmSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderLeftArmInferiors = new Hashtable<String, String[]>(0);

	public static void renderLivingAt(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isRenderLivingAtModded)
			renderPlayerAPI.renderLivingAt(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);
		else
			target.localRenderLivingAt(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);
	}

	private void renderLivingAt(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3)
	{
		if(beforeRenderLivingAtHooks != null)
			for(int i = beforeRenderLivingAtHooks.length - 1; i >= 0 ; i--)
				beforeRenderLivingAtHooks[i].beforeRenderLivingAt(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);

		if(overrideRenderLivingAtHooks != null)
			overrideRenderLivingAtHooks[overrideRenderLivingAtHooks.length - 1].renderLivingAt(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);
		else
			renderPlayer.localRenderLivingAt(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);

		if(afterRenderLivingAtHooks != null)
			for(int i = 0; i < afterRenderLivingAtHooks.length; i++)
				afterRenderLivingAtHooks[i].afterRenderLivingAt(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);

	}

	protected RenderPlayerBase GetOverwrittenRenderLivingAt(RenderPlayerBase overWriter)
	{
		if (overrideRenderLivingAtHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderLivingAtHooks.length; i++)
			if(overrideRenderLivingAtHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderLivingAtHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderLivingAtHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderLivingAtHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderLivingAtHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeRenderLivingAtHooks;
	private RenderPlayerBase[] overrideRenderLivingAtHooks;
	private RenderPlayerBase[] afterRenderLivingAtHooks;

	public boolean isRenderLivingAtModded;

	private static final Map<String, String[]> allBaseBeforeRenderLivingAtSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderLivingAtInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderLivingAtSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderLivingAtInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderLivingAtSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderLivingAtInferiors = new Hashtable<String, String[]>(0);

	public static void renderLivingLabel(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, String paramString, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isRenderLivingLabelModded)
			renderPlayerAPI.renderLivingLabel(paramAbstractClientPlayer, paramString, paramDouble1, paramDouble2, paramDouble3, paramInt);
		else
			target.localRenderLivingLabel(paramAbstractClientPlayer, paramString, paramDouble1, paramDouble2, paramDouble3, paramInt);
	}

	private void renderLivingLabel(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, String paramString, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt)
	{
		if(beforeRenderLivingLabelHooks != null)
			for(int i = beforeRenderLivingLabelHooks.length - 1; i >= 0 ; i--)
				beforeRenderLivingLabelHooks[i].beforeRenderLivingLabel(paramAbstractClientPlayer, paramString, paramDouble1, paramDouble2, paramDouble3, paramInt);

		if(overrideRenderLivingLabelHooks != null)
			overrideRenderLivingLabelHooks[overrideRenderLivingLabelHooks.length - 1].renderLivingLabel(paramAbstractClientPlayer, paramString, paramDouble1, paramDouble2, paramDouble3, paramInt);
		else
			renderPlayer.localRenderLivingLabel(paramAbstractClientPlayer, paramString, paramDouble1, paramDouble2, paramDouble3, paramInt);

		if(afterRenderLivingLabelHooks != null)
			for(int i = 0; i < afterRenderLivingLabelHooks.length; i++)
				afterRenderLivingLabelHooks[i].afterRenderLivingLabel(paramAbstractClientPlayer, paramString, paramDouble1, paramDouble2, paramDouble3, paramInt);

	}

	protected RenderPlayerBase GetOverwrittenRenderLivingLabel(RenderPlayerBase overWriter)
	{
		if (overrideRenderLivingLabelHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderLivingLabelHooks.length; i++)
			if(overrideRenderLivingLabelHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderLivingLabelHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderLivingLabelHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderLivingLabelHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderLivingLabelHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeRenderLivingLabelHooks;
	private RenderPlayerBase[] overrideRenderLivingLabelHooks;
	private RenderPlayerBase[] afterRenderLivingLabelHooks;

	public boolean isRenderLivingLabelModded;

	private static final Map<String, String[]> allBaseBeforeRenderLivingLabelSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderLivingLabelInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderLivingLabelSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderLivingLabelInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderLivingLabelSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderLivingLabelInferiors = new Hashtable<String, String[]>(0);

	public static void renderModel(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isRenderModelModded)
			renderPlayerAPI.renderModel(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
		else
			target.localRenderModel(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
	}

	private void renderModel(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
	{
		if(beforeRenderModelHooks != null)
			for(int i = beforeRenderModelHooks.length - 1; i >= 0 ; i--)
				beforeRenderModelHooks[i].beforeRenderModel(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);

		if(overrideRenderModelHooks != null)
			overrideRenderModelHooks[overrideRenderModelHooks.length - 1].renderModel(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
		else
			renderPlayer.localRenderModel(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);

		if(afterRenderModelHooks != null)
			for(int i = 0; i < afterRenderModelHooks.length; i++)
				afterRenderModelHooks[i].afterRenderModel(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);

	}

	protected RenderPlayerBase GetOverwrittenRenderModel(RenderPlayerBase overWriter)
	{
		if (overrideRenderModelHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderModelHooks.length; i++)
			if(overrideRenderModelHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderModelHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderModelHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderModelHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderModelHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeRenderModelHooks;
	private RenderPlayerBase[] overrideRenderModelHooks;
	private RenderPlayerBase[] afterRenderModelHooks;

	public boolean isRenderModelModded;

	private static final Map<String, String[]> allBaseBeforeRenderModelSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderModelInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderModelSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderModelInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderModelSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderModelInferiors = new Hashtable<String, String[]>(0);

	public static void renderMultipass(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isRenderMultipassModded)
			renderPlayerAPI.renderMultipass(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
		else
			target.localRenderMultipass(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
	}

	private void renderMultipass(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
		if(beforeRenderMultipassHooks != null)
			for(int i = beforeRenderMultipassHooks.length - 1; i >= 0 ; i--)
				beforeRenderMultipassHooks[i].beforeRenderMultipass(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

		if(overrideRenderMultipassHooks != null)
			overrideRenderMultipassHooks[overrideRenderMultipassHooks.length - 1].renderMultipass(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
		else
			renderPlayer.localRenderMultipass(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

		if(afterRenderMultipassHooks != null)
			for(int i = 0; i < afterRenderMultipassHooks.length; i++)
				afterRenderMultipassHooks[i].afterRenderMultipass(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

	}

	protected RenderPlayerBase GetOverwrittenRenderMultipass(RenderPlayerBase overWriter)
	{
		if (overrideRenderMultipassHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderMultipassHooks.length; i++)
			if(overrideRenderMultipassHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderMultipassHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderMultipassHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderMultipassHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderMultipassHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeRenderMultipassHooks;
	private RenderPlayerBase[] overrideRenderMultipassHooks;
	private RenderPlayerBase[] afterRenderMultipassHooks;

	public boolean isRenderMultipassModded;

	private static final Map<String, String[]> allBaseBeforeRenderMultipassSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderMultipassInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderMultipassSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderMultipassInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderMultipassSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderMultipassInferiors = new Hashtable<String, String[]>(0);

	public static void renderName(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isRenderNameModded)
			renderPlayerAPI.renderName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);
		else
			target.localRenderName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);
	}

	private void renderName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3)
	{
		if(beforeRenderNameHooks != null)
			for(int i = beforeRenderNameHooks.length - 1; i >= 0 ; i--)
				beforeRenderNameHooks[i].beforeRenderName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);

		if(overrideRenderNameHooks != null)
			overrideRenderNameHooks[overrideRenderNameHooks.length - 1].renderName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);
		else
			renderPlayer.localRenderName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);

		if(afterRenderNameHooks != null)
			for(int i = 0; i < afterRenderNameHooks.length; i++)
				afterRenderNameHooks[i].afterRenderName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);

	}

	protected RenderPlayerBase GetOverwrittenRenderName(RenderPlayerBase overWriter)
	{
		if (overrideRenderNameHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderNameHooks.length; i++)
			if(overrideRenderNameHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderNameHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderNameHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderNameHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderNameHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeRenderNameHooks;
	private RenderPlayerBase[] overrideRenderNameHooks;
	private RenderPlayerBase[] afterRenderNameHooks;

	public boolean isRenderNameModded;

	private static final Map<String, String[]> allBaseBeforeRenderNameSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderNameInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderNameSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderNameInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderNameSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderNameInferiors = new Hashtable<String, String[]>(0);

	public static void renderRightArm(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isRenderRightArmModded)
			renderPlayerAPI.renderRightArm(paramAbstractClientPlayer);
		else
			target.localRenderRightArm(paramAbstractClientPlayer);
	}

	private void renderRightArm(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		if(beforeRenderRightArmHooks != null)
			for(int i = beforeRenderRightArmHooks.length - 1; i >= 0 ; i--)
				beforeRenderRightArmHooks[i].beforeRenderRightArm(paramAbstractClientPlayer);

		if(overrideRenderRightArmHooks != null)
			overrideRenderRightArmHooks[overrideRenderRightArmHooks.length - 1].renderRightArm(paramAbstractClientPlayer);
		else
			renderPlayer.localRenderRightArm(paramAbstractClientPlayer);

		if(afterRenderRightArmHooks != null)
			for(int i = 0; i < afterRenderRightArmHooks.length; i++)
				afterRenderRightArmHooks[i].afterRenderRightArm(paramAbstractClientPlayer);

	}

	protected RenderPlayerBase GetOverwrittenRenderRightArm(RenderPlayerBase overWriter)
	{
		if (overrideRenderRightArmHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderRightArmHooks.length; i++)
			if(overrideRenderRightArmHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderRightArmHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderRightArmHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderRightArmHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderRightArmHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeRenderRightArmHooks;
	private RenderPlayerBase[] overrideRenderRightArmHooks;
	private RenderPlayerBase[] afterRenderRightArmHooks;

	public boolean isRenderRightArmModded;

	private static final Map<String, String[]> allBaseBeforeRenderRightArmSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderRightArmInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderRightArmSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderRightArmInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderRightArmSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderRightArmInferiors = new Hashtable<String, String[]>(0);

	public static void rotateCorpse(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isRotateCorpseModded)
			renderPlayerAPI.rotateCorpse(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3);
		else
			target.localRotateCorpse(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3);
	}

	private void rotateCorpse(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3)
	{
		if(beforeRotateCorpseHooks != null)
			for(int i = beforeRotateCorpseHooks.length - 1; i >= 0 ; i--)
				beforeRotateCorpseHooks[i].beforeRotateCorpse(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3);

		if(overrideRotateCorpseHooks != null)
			overrideRotateCorpseHooks[overrideRotateCorpseHooks.length - 1].rotateCorpse(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3);
		else
			renderPlayer.localRotateCorpse(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3);

		if(afterRotateCorpseHooks != null)
			for(int i = 0; i < afterRotateCorpseHooks.length; i++)
				afterRotateCorpseHooks[i].afterRotateCorpse(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3);

	}

	protected RenderPlayerBase GetOverwrittenRotateCorpse(RenderPlayerBase overWriter)
	{
		if (overrideRotateCorpseHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRotateCorpseHooks.length; i++)
			if(overrideRotateCorpseHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRotateCorpseHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRotateCorpseHookTypes = new LinkedList<String>();
	private final static List<String> overrideRotateCorpseHookTypes = new LinkedList<String>();
	private final static List<String> afterRotateCorpseHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeRotateCorpseHooks;
	private RenderPlayerBase[] overrideRotateCorpseHooks;
	private RenderPlayerBase[] afterRotateCorpseHooks;

	public boolean isRotateCorpseModded;

	private static final Map<String, String[]> allBaseBeforeRotateCorpseSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRotateCorpseInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRotateCorpseSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRotateCorpseInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRotateCorpseSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRotateCorpseInferiors = new Hashtable<String, String[]>(0);

	public static boolean setBrightness(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat, boolean paramBoolean)
	{
		boolean _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isSetBrightnessModded)
			_result = renderPlayerAPI.setBrightness(paramAbstractClientPlayer, paramFloat, paramBoolean);
		else
			_result = target.localSetBrightness(paramAbstractClientPlayer, paramFloat, paramBoolean);
		return _result;
	}

	private boolean setBrightness(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat, boolean paramBoolean)
	{
		if(beforeSetBrightnessHooks != null)
			for(int i = beforeSetBrightnessHooks.length - 1; i >= 0 ; i--)
				beforeSetBrightnessHooks[i].beforeSetBrightness(paramAbstractClientPlayer, paramFloat, paramBoolean);

		boolean _result;
		if(overrideSetBrightnessHooks != null)
			_result = overrideSetBrightnessHooks[overrideSetBrightnessHooks.length - 1].setBrightness(paramAbstractClientPlayer, paramFloat, paramBoolean);
		else
			_result = renderPlayer.localSetBrightness(paramAbstractClientPlayer, paramFloat, paramBoolean);

		if(afterSetBrightnessHooks != null)
			for(int i = 0; i < afterSetBrightnessHooks.length; i++)
				afterSetBrightnessHooks[i].afterSetBrightness(paramAbstractClientPlayer, paramFloat, paramBoolean);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenSetBrightness(RenderPlayerBase overWriter)
	{
		if (overrideSetBrightnessHooks == null)
			return overWriter;

		for(int i = 0; i < overrideSetBrightnessHooks.length; i++)
			if(overrideSetBrightnessHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideSetBrightnessHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeSetBrightnessHookTypes = new LinkedList<String>();
	private final static List<String> overrideSetBrightnessHookTypes = new LinkedList<String>();
	private final static List<String> afterSetBrightnessHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeSetBrightnessHooks;
	private RenderPlayerBase[] overrideSetBrightnessHooks;
	private RenderPlayerBase[] afterSetBrightnessHooks;

	public boolean isSetBrightnessModded;

	private static final Map<String, String[]> allBaseBeforeSetBrightnessSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeSetBrightnessInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetBrightnessSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetBrightnessInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetBrightnessSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetBrightnessInferiors = new Hashtable<String, String[]>(0);

	public static boolean setDoRenderBrightness(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		boolean _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isSetDoRenderBrightnessModded)
			_result = renderPlayerAPI.setDoRenderBrightness(paramAbstractClientPlayer, paramFloat);
		else
			_result = target.localSetDoRenderBrightness(paramAbstractClientPlayer, paramFloat);
		return _result;
	}

	private boolean setDoRenderBrightness(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		if(beforeSetDoRenderBrightnessHooks != null)
			for(int i = beforeSetDoRenderBrightnessHooks.length - 1; i >= 0 ; i--)
				beforeSetDoRenderBrightnessHooks[i].beforeSetDoRenderBrightness(paramAbstractClientPlayer, paramFloat);

		boolean _result;
		if(overrideSetDoRenderBrightnessHooks != null)
			_result = overrideSetDoRenderBrightnessHooks[overrideSetDoRenderBrightnessHooks.length - 1].setDoRenderBrightness(paramAbstractClientPlayer, paramFloat);
		else
			_result = renderPlayer.localSetDoRenderBrightness(paramAbstractClientPlayer, paramFloat);

		if(afterSetDoRenderBrightnessHooks != null)
			for(int i = 0; i < afterSetDoRenderBrightnessHooks.length; i++)
				afterSetDoRenderBrightnessHooks[i].afterSetDoRenderBrightness(paramAbstractClientPlayer, paramFloat);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenSetDoRenderBrightness(RenderPlayerBase overWriter)
	{
		if (overrideSetDoRenderBrightnessHooks == null)
			return overWriter;

		for(int i = 0; i < overrideSetDoRenderBrightnessHooks.length; i++)
			if(overrideSetDoRenderBrightnessHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideSetDoRenderBrightnessHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeSetDoRenderBrightnessHookTypes = new LinkedList<String>();
	private final static List<String> overrideSetDoRenderBrightnessHookTypes = new LinkedList<String>();
	private final static List<String> afterSetDoRenderBrightnessHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeSetDoRenderBrightnessHooks;
	private RenderPlayerBase[] overrideSetDoRenderBrightnessHooks;
	private RenderPlayerBase[] afterSetDoRenderBrightnessHooks;

	public boolean isSetDoRenderBrightnessModded;

	private static final Map<String, String[]> allBaseBeforeSetDoRenderBrightnessSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeSetDoRenderBrightnessInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetDoRenderBrightnessSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetDoRenderBrightnessInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetDoRenderBrightnessSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetDoRenderBrightnessInferiors = new Hashtable<String, String[]>(0);

	public static void setModelVisibilities(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isSetModelVisibilitiesModded)
			renderPlayerAPI.setModelVisibilities(paramAbstractClientPlayer);
		else
			target.localSetModelVisibilities(paramAbstractClientPlayer);
	}

	private void setModelVisibilities(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		if(beforeSetModelVisibilitiesHooks != null)
			for(int i = beforeSetModelVisibilitiesHooks.length - 1; i >= 0 ; i--)
				beforeSetModelVisibilitiesHooks[i].beforeSetModelVisibilities(paramAbstractClientPlayer);

		if(overrideSetModelVisibilitiesHooks != null)
			overrideSetModelVisibilitiesHooks[overrideSetModelVisibilitiesHooks.length - 1].setModelVisibilities(paramAbstractClientPlayer);
		else
			renderPlayer.localSetModelVisibilities(paramAbstractClientPlayer);

		if(afterSetModelVisibilitiesHooks != null)
			for(int i = 0; i < afterSetModelVisibilitiesHooks.length; i++)
				afterSetModelVisibilitiesHooks[i].afterSetModelVisibilities(paramAbstractClientPlayer);

	}

	protected RenderPlayerBase GetOverwrittenSetModelVisibilities(RenderPlayerBase overWriter)
	{
		if (overrideSetModelVisibilitiesHooks == null)
			return overWriter;

		for(int i = 0; i < overrideSetModelVisibilitiesHooks.length; i++)
			if(overrideSetModelVisibilitiesHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideSetModelVisibilitiesHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeSetModelVisibilitiesHookTypes = new LinkedList<String>();
	private final static List<String> overrideSetModelVisibilitiesHookTypes = new LinkedList<String>();
	private final static List<String> afterSetModelVisibilitiesHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeSetModelVisibilitiesHooks;
	private RenderPlayerBase[] overrideSetModelVisibilitiesHooks;
	private RenderPlayerBase[] afterSetModelVisibilitiesHooks;

	public boolean isSetModelVisibilitiesModded;

	private static final Map<String, String[]> allBaseBeforeSetModelVisibilitiesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeSetModelVisibilitiesInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetModelVisibilitiesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetModelVisibilitiesInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetModelVisibilitiesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetModelVisibilitiesInferiors = new Hashtable<String, String[]>(0);

	public static void setRenderOutlines(IRenderPlayerAPI target, boolean paramBoolean)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isSetRenderOutlinesModded)
			renderPlayerAPI.setRenderOutlines(paramBoolean);
		else
			target.localSetRenderOutlines(paramBoolean);
	}

	private void setRenderOutlines(boolean paramBoolean)
	{
		if(beforeSetRenderOutlinesHooks != null)
			for(int i = beforeSetRenderOutlinesHooks.length - 1; i >= 0 ; i--)
				beforeSetRenderOutlinesHooks[i].beforeSetRenderOutlines(paramBoolean);

		if(overrideSetRenderOutlinesHooks != null)
			overrideSetRenderOutlinesHooks[overrideSetRenderOutlinesHooks.length - 1].setRenderOutlines(paramBoolean);
		else
			renderPlayer.localSetRenderOutlines(paramBoolean);

		if(afterSetRenderOutlinesHooks != null)
			for(int i = 0; i < afterSetRenderOutlinesHooks.length; i++)
				afterSetRenderOutlinesHooks[i].afterSetRenderOutlines(paramBoolean);

	}

	protected RenderPlayerBase GetOverwrittenSetRenderOutlines(RenderPlayerBase overWriter)
	{
		if (overrideSetRenderOutlinesHooks == null)
			return overWriter;

		for(int i = 0; i < overrideSetRenderOutlinesHooks.length; i++)
			if(overrideSetRenderOutlinesHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideSetRenderOutlinesHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeSetRenderOutlinesHookTypes = new LinkedList<String>();
	private final static List<String> overrideSetRenderOutlinesHookTypes = new LinkedList<String>();
	private final static List<String> afterSetRenderOutlinesHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeSetRenderOutlinesHooks;
	private RenderPlayerBase[] overrideSetRenderOutlinesHooks;
	private RenderPlayerBase[] afterSetRenderOutlinesHooks;

	public boolean isSetRenderOutlinesModded;

	private static final Map<String, String[]> allBaseBeforeSetRenderOutlinesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeSetRenderOutlinesInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetRenderOutlinesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetRenderOutlinesInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetRenderOutlinesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetRenderOutlinesInferiors = new Hashtable<String, String[]>(0);

	public static boolean setScoreTeamColor(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		boolean _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isSetScoreTeamColorModded)
			_result = renderPlayerAPI.setScoreTeamColor(paramAbstractClientPlayer);
		else
			_result = target.localSetScoreTeamColor(paramAbstractClientPlayer);
		return _result;
	}

	private boolean setScoreTeamColor(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		if(beforeSetScoreTeamColorHooks != null)
			for(int i = beforeSetScoreTeamColorHooks.length - 1; i >= 0 ; i--)
				beforeSetScoreTeamColorHooks[i].beforeSetScoreTeamColor(paramAbstractClientPlayer);

		boolean _result;
		if(overrideSetScoreTeamColorHooks != null)
			_result = overrideSetScoreTeamColorHooks[overrideSetScoreTeamColorHooks.length - 1].setScoreTeamColor(paramAbstractClientPlayer);
		else
			_result = renderPlayer.localSetScoreTeamColor(paramAbstractClientPlayer);

		if(afterSetScoreTeamColorHooks != null)
			for(int i = 0; i < afterSetScoreTeamColorHooks.length; i++)
				afterSetScoreTeamColorHooks[i].afterSetScoreTeamColor(paramAbstractClientPlayer);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenSetScoreTeamColor(RenderPlayerBase overWriter)
	{
		if (overrideSetScoreTeamColorHooks == null)
			return overWriter;

		for(int i = 0; i < overrideSetScoreTeamColorHooks.length; i++)
			if(overrideSetScoreTeamColorHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideSetScoreTeamColorHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeSetScoreTeamColorHookTypes = new LinkedList<String>();
	private final static List<String> overrideSetScoreTeamColorHookTypes = new LinkedList<String>();
	private final static List<String> afterSetScoreTeamColorHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeSetScoreTeamColorHooks;
	private RenderPlayerBase[] overrideSetScoreTeamColorHooks;
	private RenderPlayerBase[] afterSetScoreTeamColorHooks;

	public boolean isSetScoreTeamColorModded;

	private static final Map<String, String[]> allBaseBeforeSetScoreTeamColorSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeSetScoreTeamColorInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetScoreTeamColorSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetScoreTeamColorInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetScoreTeamColorSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetScoreTeamColorInferiors = new Hashtable<String, String[]>(0);

	public static boolean shouldRender(IRenderPlayerAPI target, net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, net.minecraft.client.renderer.culling.ICamera paramICamera, double paramDouble1, double paramDouble2, double paramDouble3)
	{
		boolean _result;
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isShouldRenderModded)
			_result = renderPlayerAPI.shouldRender(paramAbstractClientPlayer, paramICamera, paramDouble1, paramDouble2, paramDouble3);
		else
			_result = target.localShouldRender(paramAbstractClientPlayer, paramICamera, paramDouble1, paramDouble2, paramDouble3);
		return _result;
	}

	private boolean shouldRender(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, net.minecraft.client.renderer.culling.ICamera paramICamera, double paramDouble1, double paramDouble2, double paramDouble3)
	{
		if(beforeShouldRenderHooks != null)
			for(int i = beforeShouldRenderHooks.length - 1; i >= 0 ; i--)
				beforeShouldRenderHooks[i].beforeShouldRender(paramAbstractClientPlayer, paramICamera, paramDouble1, paramDouble2, paramDouble3);

		boolean _result;
		if(overrideShouldRenderHooks != null)
			_result = overrideShouldRenderHooks[overrideShouldRenderHooks.length - 1].shouldRender(paramAbstractClientPlayer, paramICamera, paramDouble1, paramDouble2, paramDouble3);
		else
			_result = renderPlayer.localShouldRender(paramAbstractClientPlayer, paramICamera, paramDouble1, paramDouble2, paramDouble3);

		if(afterShouldRenderHooks != null)
			for(int i = 0; i < afterShouldRenderHooks.length; i++)
				afterShouldRenderHooks[i].afterShouldRender(paramAbstractClientPlayer, paramICamera, paramDouble1, paramDouble2, paramDouble3);

		return _result;
	}

	protected RenderPlayerBase GetOverwrittenShouldRender(RenderPlayerBase overWriter)
	{
		if (overrideShouldRenderHooks == null)
			return overWriter;

		for(int i = 0; i < overrideShouldRenderHooks.length; i++)
			if(overrideShouldRenderHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideShouldRenderHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeShouldRenderHookTypes = new LinkedList<String>();
	private final static List<String> overrideShouldRenderHookTypes = new LinkedList<String>();
	private final static List<String> afterShouldRenderHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeShouldRenderHooks;
	private RenderPlayerBase[] overrideShouldRenderHooks;
	private RenderPlayerBase[] afterShouldRenderHooks;

	public boolean isShouldRenderModded;

	private static final Map<String, String[]> allBaseBeforeShouldRenderSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeShouldRenderInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideShouldRenderSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideShouldRenderInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterShouldRenderSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterShouldRenderInferiors = new Hashtable<String, String[]>(0);

	public static void transformHeldFull3DItemLayer(IRenderPlayerAPI target)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isTransformHeldFull3DItemLayerModded)
			renderPlayerAPI.transformHeldFull3DItemLayer();
		else
			target.localTransformHeldFull3DItemLayer();
	}

	private void transformHeldFull3DItemLayer()
	{
		if(beforeTransformHeldFull3DItemLayerHooks != null)
			for(int i = beforeTransformHeldFull3DItemLayerHooks.length - 1; i >= 0 ; i--)
				beforeTransformHeldFull3DItemLayerHooks[i].beforeTransformHeldFull3DItemLayer();

		if(overrideTransformHeldFull3DItemLayerHooks != null)
			overrideTransformHeldFull3DItemLayerHooks[overrideTransformHeldFull3DItemLayerHooks.length - 1].transformHeldFull3DItemLayer();
		else
			renderPlayer.localTransformHeldFull3DItemLayer();

		if(afterTransformHeldFull3DItemLayerHooks != null)
			for(int i = 0; i < afterTransformHeldFull3DItemLayerHooks.length; i++)
				afterTransformHeldFull3DItemLayerHooks[i].afterTransformHeldFull3DItemLayer();

	}

	protected RenderPlayerBase GetOverwrittenTransformHeldFull3DItemLayer(RenderPlayerBase overWriter)
	{
		if (overrideTransformHeldFull3DItemLayerHooks == null)
			return overWriter;

		for(int i = 0; i < overrideTransformHeldFull3DItemLayerHooks.length; i++)
			if(overrideTransformHeldFull3DItemLayerHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideTransformHeldFull3DItemLayerHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeTransformHeldFull3DItemLayerHookTypes = new LinkedList<String>();
	private final static List<String> overrideTransformHeldFull3DItemLayerHookTypes = new LinkedList<String>();
	private final static List<String> afterTransformHeldFull3DItemLayerHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeTransformHeldFull3DItemLayerHooks;
	private RenderPlayerBase[] overrideTransformHeldFull3DItemLayerHooks;
	private RenderPlayerBase[] afterTransformHeldFull3DItemLayerHooks;

	public boolean isTransformHeldFull3DItemLayerModded;

	private static final Map<String, String[]> allBaseBeforeTransformHeldFull3DItemLayerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeTransformHeldFull3DItemLayerInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideTransformHeldFull3DItemLayerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideTransformHeldFull3DItemLayerInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterTransformHeldFull3DItemLayerSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterTransformHeldFull3DItemLayerInferiors = new Hashtable<String, String[]>(0);

	public static void unsetBrightness(IRenderPlayerAPI target)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isUnsetBrightnessModded)
			renderPlayerAPI.unsetBrightness();
		else
			target.localUnsetBrightness();
	}

	private void unsetBrightness()
	{
		if(beforeUnsetBrightnessHooks != null)
			for(int i = beforeUnsetBrightnessHooks.length - 1; i >= 0 ; i--)
				beforeUnsetBrightnessHooks[i].beforeUnsetBrightness();

		if(overrideUnsetBrightnessHooks != null)
			overrideUnsetBrightnessHooks[overrideUnsetBrightnessHooks.length - 1].unsetBrightness();
		else
			renderPlayer.localUnsetBrightness();

		if(afterUnsetBrightnessHooks != null)
			for(int i = 0; i < afterUnsetBrightnessHooks.length; i++)
				afterUnsetBrightnessHooks[i].afterUnsetBrightness();

	}

	protected RenderPlayerBase GetOverwrittenUnsetBrightness(RenderPlayerBase overWriter)
	{
		if (overrideUnsetBrightnessHooks == null)
			return overWriter;

		for(int i = 0; i < overrideUnsetBrightnessHooks.length; i++)
			if(overrideUnsetBrightnessHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideUnsetBrightnessHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeUnsetBrightnessHookTypes = new LinkedList<String>();
	private final static List<String> overrideUnsetBrightnessHookTypes = new LinkedList<String>();
	private final static List<String> afterUnsetBrightnessHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeUnsetBrightnessHooks;
	private RenderPlayerBase[] overrideUnsetBrightnessHooks;
	private RenderPlayerBase[] afterUnsetBrightnessHooks;

	public boolean isUnsetBrightnessModded;

	private static final Map<String, String[]> allBaseBeforeUnsetBrightnessSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeUnsetBrightnessInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideUnsetBrightnessSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideUnsetBrightnessInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterUnsetBrightnessSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterUnsetBrightnessInferiors = new Hashtable<String, String[]>(0);

	public static void unsetScoreTeamColor(IRenderPlayerAPI target)
	{
		RenderPlayerAPI renderPlayerAPI = target.getRenderPlayerAPI();
		if(renderPlayerAPI != null && renderPlayerAPI.isUnsetScoreTeamColorModded)
			renderPlayerAPI.unsetScoreTeamColor();
		else
			target.localUnsetScoreTeamColor();
	}

	private void unsetScoreTeamColor()
	{
		if(beforeUnsetScoreTeamColorHooks != null)
			for(int i = beforeUnsetScoreTeamColorHooks.length - 1; i >= 0 ; i--)
				beforeUnsetScoreTeamColorHooks[i].beforeUnsetScoreTeamColor();

		if(overrideUnsetScoreTeamColorHooks != null)
			overrideUnsetScoreTeamColorHooks[overrideUnsetScoreTeamColorHooks.length - 1].unsetScoreTeamColor();
		else
			renderPlayer.localUnsetScoreTeamColor();

		if(afterUnsetScoreTeamColorHooks != null)
			for(int i = 0; i < afterUnsetScoreTeamColorHooks.length; i++)
				afterUnsetScoreTeamColorHooks[i].afterUnsetScoreTeamColor();

	}

	protected RenderPlayerBase GetOverwrittenUnsetScoreTeamColor(RenderPlayerBase overWriter)
	{
		if (overrideUnsetScoreTeamColorHooks == null)
			return overWriter;

		for(int i = 0; i < overrideUnsetScoreTeamColorHooks.length; i++)
			if(overrideUnsetScoreTeamColorHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideUnsetScoreTeamColorHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeUnsetScoreTeamColorHookTypes = new LinkedList<String>();
	private final static List<String> overrideUnsetScoreTeamColorHookTypes = new LinkedList<String>();
	private final static List<String> afterUnsetScoreTeamColorHookTypes = new LinkedList<String>();

	private RenderPlayerBase[] beforeUnsetScoreTeamColorHooks;
	private RenderPlayerBase[] overrideUnsetScoreTeamColorHooks;
	private RenderPlayerBase[] afterUnsetScoreTeamColorHooks;

	public boolean isUnsetScoreTeamColorModded;

	private static final Map<String, String[]> allBaseBeforeUnsetScoreTeamColorSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeUnsetScoreTeamColorInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideUnsetScoreTeamColorSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideUnsetScoreTeamColorInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterUnsetScoreTeamColorSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterUnsetScoreTeamColorInferiors = new Hashtable<String, String[]>(0);

	
	protected final IRenderPlayerAPI renderPlayer;

	private final static Set<String> keys = new HashSet<String>();
	private final static Map<String, String> keysToVirtualIds = new HashMap<String, String>();
	private final static Set<Class<?>> dynamicTypes = new HashSet<Class<?>>();

	private final static Map<Class<?>, Map<String, Method>> virtualDynamicHookMethods = new HashMap<Class<?>, Map<String, Method>>();

	private final static Map<Class<?>, Map<String, Method>> beforeDynamicHookMethods = new HashMap<Class<?>, Map<String, Method>>();
	private final static Map<Class<?>, Map<String, Method>> overrideDynamicHookMethods = new HashMap<Class<?>, Map<String, Method>>();
	private final static Map<Class<?>, Map<String, Method>> afterDynamicHookMethods = new HashMap<Class<?>, Map<String, Method>>();

	private final static List<String> beforeLocalConstructingHookTypes = new LinkedList<String>();
	private final static List<String> afterLocalConstructingHookTypes = new LinkedList<String>();

	private static final Map<String, List<String>> beforeDynamicHookTypes = new Hashtable<String, List<String>>(0);
	private static final Map<String, List<String>> overrideDynamicHookTypes = new Hashtable<String, List<String>>(0);
	private static final Map<String, List<String>> afterDynamicHookTypes = new Hashtable<String, List<String>>(0);

	private RenderPlayerBase[] beforeLocalConstructingHooks;
	private RenderPlayerBase[] afterLocalConstructingHooks;

	private final Map<RenderPlayerBase, String> baseObjectsToId = new Hashtable<RenderPlayerBase, String>();
	private final Map<String, RenderPlayerBase> allBaseObjects = new Hashtable<String, RenderPlayerBase>();
	private final Set<String> unmodifiableAllBaseIds = Collections.unmodifiableSet(allBaseObjects.keySet());

	private static final Map<String, Constructor<?>> allBaseConstructors = new Hashtable<String, Constructor<?>>();
	private static final Set<String> unmodifiableAllIds = Collections.unmodifiableSet(allBaseConstructors.keySet());

	private static final Map<String, String[]> allBaseBeforeLocalConstructingSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeLocalConstructingInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterLocalConstructingSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterLocalConstructingInferiors = new Hashtable<String, String[]>(0);

	private static final Map<String, Map<String, String[]>> allBaseBeforeDynamicSuperiors = new Hashtable<String, Map<String, String[]>>(0);
	private static final Map<String, Map<String, String[]>> allBaseBeforeDynamicInferiors = new Hashtable<String, Map<String, String[]>>(0);
	private static final Map<String, Map<String, String[]>> allBaseOverrideDynamicSuperiors = new Hashtable<String, Map<String, String[]>>(0);
	private static final Map<String, Map<String, String[]>> allBaseOverrideDynamicInferiors = new Hashtable<String, Map<String, String[]>>(0);
	private static final Map<String, Map<String, String[]>> allBaseAfterDynamicSuperiors = new Hashtable<String, Map<String, String[]>>(0);
	private static final Map<String, Map<String, String[]>> allBaseAfterDynamicInferiors = new Hashtable<String, Map<String, String[]>>(0);

	private static boolean initialized = false;
}
