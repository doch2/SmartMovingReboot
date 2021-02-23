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

package api.player.model;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.lang.ref.*;
import java.lang.reflect.*;

public final class ModelPlayerAPI
{
	private final static Class<?>[] Class = new Class[] { ModelPlayerAPI.class };
	private final static Class<?>[] Classes = new Class[] { ModelPlayerAPI.class, String.class };

	private static boolean isCreated;
	private static final Logger logger = Logger.getLogger("ModelPlayerAPI");

	private static void log(String text)
	{
		System.out.println(text);
		logger.fine(text);
	}

	public static void register(String id, Class<?> baseClass)
	{
		register(id, baseClass, null);
	}

	public static void register(String id, Class<?> baseClass, ModelPlayerBaseSorting baseSorting)
	{
		try
		{
			register(baseClass, id, baseSorting);
		}
		catch(RuntimeException exception)
		{
			if(id != null)
				log("Model Player: failed to register id '" + id + "'");
			else
				log("Model Player: failed to register ModelPlayerBase");

			throw exception;
		}
	}

	private static void register(Class<?> baseClass, String id, ModelPlayerBaseSorting baseSorting)
	{
		if(!isCreated)
		{
			try
			{
				Method mandatory = net.minecraft.client.model.ModelPlayer.class.getMethod("getModelPlayerBase", String.class);
				if (mandatory.getReturnType() != ModelPlayerBase.class)
					throw new NoSuchMethodException(ModelPlayerBase.class.getName() + " " + net.minecraft.client.model.ModelPlayer.class.getName() + ".getModelPlayerBase(" + String.class.getName() + ")");
			}
			catch(NoSuchMethodException exception)
			{
				String[] errorMessageParts = new String[]
				{
					"========================================",
					"The API \"Model Player\" version " + api.player.forge.RenderPlayerAPIPlugin.VERSION + " of the mod \"Render Player API Core " + api.player.forge.RenderPlayerAPIPlugin.VERSION + "\" can not be created!",
					"----------------------------------------",
					"Mandatory member method \"{0} getModelPlayerBase({3})\" not found in class \"{1}\".",
					"There are three scenarios this can happen:",
					"* Minecraft Forge is missing a Render Player API Core which Minecraft version matches its own.",
					"  Download and install the latest Render Player API Core for the Minecraft version you were trying to run.",
					"* The code of the class \"{2}\" of Render Player API Core has been modified beyond recognition by another Minecraft Forge coremod.",
					"  Try temporary deinstallation of other core mods to find the culprit and deinstall it permanently to fix this specific problem.",
					"* Render Player API Core has not been installed correctly.",
					"  Deinstall Render Player API Core and install it again following the installation instructions in the readme file.",
					"========================================"
				};

				String baseModelPlayerClassName = ModelPlayerBase.class.getName();
				String targetClassName = net.minecraft.client.model.ModelPlayer.class.getName();
				String targetClassFileName = targetClassName.replace(".", File.separator);
				String stringClassName = String.class.getName();

				for(int i=0; i<errorMessageParts.length; i++)
					errorMessageParts[i] = MessageFormat.format(errorMessageParts[i], baseModelPlayerClassName, targetClassName, targetClassFileName, stringClassName);

				for(String errorMessagePart : errorMessageParts)
					logger.severe(errorMessagePart);

				for(String errorMessagePart : errorMessageParts)
					System.err.println(errorMessagePart);

				String errorMessage = "\n\n";
				for(String errorMessagePart : errorMessageParts)
					errorMessage += "\t" + errorMessagePart + "\n";

				throw new RuntimeException(errorMessage, exception);
			}

			log("Model Player " + api.player.forge.RenderPlayerAPIPlugin.VERSION + " Created");
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
				throw new IllegalArgumentException("Can not find necessary constructor with one argument of type '" + ModelPlayerAPI.class.getName() + "' and eventually a second argument of type 'String' in the class '" + baseClass.getName() + "'", t);
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

			addSorting(id, allBaseBeforeGetArmForSideSuperiors, baseSorting.getBeforeGetArmForSideSuperiors());
			addSorting(id, allBaseBeforeGetArmForSideInferiors, baseSorting.getBeforeGetArmForSideInferiors());
			addSorting(id, allBaseOverrideGetArmForSideSuperiors, baseSorting.getOverrideGetArmForSideSuperiors());
			addSorting(id, allBaseOverrideGetArmForSideInferiors, baseSorting.getOverrideGetArmForSideInferiors());
			addSorting(id, allBaseAfterGetArmForSideSuperiors, baseSorting.getAfterGetArmForSideSuperiors());
			addSorting(id, allBaseAfterGetArmForSideInferiors, baseSorting.getAfterGetArmForSideInferiors());

			addSorting(id, allBaseBeforeGetMainHandSuperiors, baseSorting.getBeforeGetMainHandSuperiors());
			addSorting(id, allBaseBeforeGetMainHandInferiors, baseSorting.getBeforeGetMainHandInferiors());
			addSorting(id, allBaseOverrideGetMainHandSuperiors, baseSorting.getOverrideGetMainHandSuperiors());
			addSorting(id, allBaseOverrideGetMainHandInferiors, baseSorting.getOverrideGetMainHandInferiors());
			addSorting(id, allBaseAfterGetMainHandSuperiors, baseSorting.getAfterGetMainHandSuperiors());
			addSorting(id, allBaseAfterGetMainHandInferiors, baseSorting.getAfterGetMainHandInferiors());

			addSorting(id, allBaseBeforeGetRandomModelBoxSuperiors, baseSorting.getBeforeGetRandomModelBoxSuperiors());
			addSorting(id, allBaseBeforeGetRandomModelBoxInferiors, baseSorting.getBeforeGetRandomModelBoxInferiors());
			addSorting(id, allBaseOverrideGetRandomModelBoxSuperiors, baseSorting.getOverrideGetRandomModelBoxSuperiors());
			addSorting(id, allBaseOverrideGetRandomModelBoxInferiors, baseSorting.getOverrideGetRandomModelBoxInferiors());
			addSorting(id, allBaseAfterGetRandomModelBoxSuperiors, baseSorting.getAfterGetRandomModelBoxSuperiors());
			addSorting(id, allBaseAfterGetRandomModelBoxInferiors, baseSorting.getAfterGetRandomModelBoxInferiors());

			addSorting(id, allBaseBeforeGetTextureOffsetSuperiors, baseSorting.getBeforeGetTextureOffsetSuperiors());
			addSorting(id, allBaseBeforeGetTextureOffsetInferiors, baseSorting.getBeforeGetTextureOffsetInferiors());
			addSorting(id, allBaseOverrideGetTextureOffsetSuperiors, baseSorting.getOverrideGetTextureOffsetSuperiors());
			addSorting(id, allBaseOverrideGetTextureOffsetInferiors, baseSorting.getOverrideGetTextureOffsetInferiors());
			addSorting(id, allBaseAfterGetTextureOffsetSuperiors, baseSorting.getAfterGetTextureOffsetSuperiors());
			addSorting(id, allBaseAfterGetTextureOffsetInferiors, baseSorting.getAfterGetTextureOffsetInferiors());

			addSorting(id, allBaseBeforePostRenderArmSuperiors, baseSorting.getBeforePostRenderArmSuperiors());
			addSorting(id, allBaseBeforePostRenderArmInferiors, baseSorting.getBeforePostRenderArmInferiors());
			addSorting(id, allBaseOverridePostRenderArmSuperiors, baseSorting.getOverridePostRenderArmSuperiors());
			addSorting(id, allBaseOverridePostRenderArmInferiors, baseSorting.getOverridePostRenderArmInferiors());
			addSorting(id, allBaseAfterPostRenderArmSuperiors, baseSorting.getAfterPostRenderArmSuperiors());
			addSorting(id, allBaseAfterPostRenderArmInferiors, baseSorting.getAfterPostRenderArmInferiors());

			addSorting(id, allBaseBeforeRenderSuperiors, baseSorting.getBeforeRenderSuperiors());
			addSorting(id, allBaseBeforeRenderInferiors, baseSorting.getBeforeRenderInferiors());
			addSorting(id, allBaseOverrideRenderSuperiors, baseSorting.getOverrideRenderSuperiors());
			addSorting(id, allBaseOverrideRenderInferiors, baseSorting.getOverrideRenderInferiors());
			addSorting(id, allBaseAfterRenderSuperiors, baseSorting.getAfterRenderSuperiors());
			addSorting(id, allBaseAfterRenderInferiors, baseSorting.getAfterRenderInferiors());

			addSorting(id, allBaseBeforeRenderCapeSuperiors, baseSorting.getBeforeRenderCapeSuperiors());
			addSorting(id, allBaseBeforeRenderCapeInferiors, baseSorting.getBeforeRenderCapeInferiors());
			addSorting(id, allBaseOverrideRenderCapeSuperiors, baseSorting.getOverrideRenderCapeSuperiors());
			addSorting(id, allBaseOverrideRenderCapeInferiors, baseSorting.getOverrideRenderCapeInferiors());
			addSorting(id, allBaseAfterRenderCapeSuperiors, baseSorting.getAfterRenderCapeSuperiors());
			addSorting(id, allBaseAfterRenderCapeInferiors, baseSorting.getAfterRenderCapeInferiors());

			addSorting(id, allBaseBeforeRenderDeadmau5HeadSuperiors, baseSorting.getBeforeRenderDeadmau5HeadSuperiors());
			addSorting(id, allBaseBeforeRenderDeadmau5HeadInferiors, baseSorting.getBeforeRenderDeadmau5HeadInferiors());
			addSorting(id, allBaseOverrideRenderDeadmau5HeadSuperiors, baseSorting.getOverrideRenderDeadmau5HeadSuperiors());
			addSorting(id, allBaseOverrideRenderDeadmau5HeadInferiors, baseSorting.getOverrideRenderDeadmau5HeadInferiors());
			addSorting(id, allBaseAfterRenderDeadmau5HeadSuperiors, baseSorting.getAfterRenderDeadmau5HeadSuperiors());
			addSorting(id, allBaseAfterRenderDeadmau5HeadInferiors, baseSorting.getAfterRenderDeadmau5HeadInferiors());

			addSorting(id, allBaseBeforeSetLivingAnimationsSuperiors, baseSorting.getBeforeSetLivingAnimationsSuperiors());
			addSorting(id, allBaseBeforeSetLivingAnimationsInferiors, baseSorting.getBeforeSetLivingAnimationsInferiors());
			addSorting(id, allBaseOverrideSetLivingAnimationsSuperiors, baseSorting.getOverrideSetLivingAnimationsSuperiors());
			addSorting(id, allBaseOverrideSetLivingAnimationsInferiors, baseSorting.getOverrideSetLivingAnimationsInferiors());
			addSorting(id, allBaseAfterSetLivingAnimationsSuperiors, baseSorting.getAfterSetLivingAnimationsSuperiors());
			addSorting(id, allBaseAfterSetLivingAnimationsInferiors, baseSorting.getAfterSetLivingAnimationsInferiors());

			addSorting(id, allBaseBeforeSetModelAttributesSuperiors, baseSorting.getBeforeSetModelAttributesSuperiors());
			addSorting(id, allBaseBeforeSetModelAttributesInferiors, baseSorting.getBeforeSetModelAttributesInferiors());
			addSorting(id, allBaseOverrideSetModelAttributesSuperiors, baseSorting.getOverrideSetModelAttributesSuperiors());
			addSorting(id, allBaseOverrideSetModelAttributesInferiors, baseSorting.getOverrideSetModelAttributesInferiors());
			addSorting(id, allBaseAfterSetModelAttributesSuperiors, baseSorting.getAfterSetModelAttributesSuperiors());
			addSorting(id, allBaseAfterSetModelAttributesInferiors, baseSorting.getAfterSetModelAttributesInferiors());

			addSorting(id, allBaseBeforeSetRotationAnglesSuperiors, baseSorting.getBeforeSetRotationAnglesSuperiors());
			addSorting(id, allBaseBeforeSetRotationAnglesInferiors, baseSorting.getBeforeSetRotationAnglesInferiors());
			addSorting(id, allBaseOverrideSetRotationAnglesSuperiors, baseSorting.getOverrideSetRotationAnglesSuperiors());
			addSorting(id, allBaseOverrideSetRotationAnglesInferiors, baseSorting.getOverrideSetRotationAnglesInferiors());
			addSorting(id, allBaseAfterSetRotationAnglesSuperiors, baseSorting.getAfterSetRotationAnglesSuperiors());
			addSorting(id, allBaseAfterSetRotationAnglesInferiors, baseSorting.getAfterSetRotationAnglesInferiors());

			addSorting(id, allBaseBeforeSetTextureOffsetSuperiors, baseSorting.getBeforeSetTextureOffsetSuperiors());
			addSorting(id, allBaseBeforeSetTextureOffsetInferiors, baseSorting.getBeforeSetTextureOffsetInferiors());
			addSorting(id, allBaseOverrideSetTextureOffsetSuperiors, baseSorting.getOverrideSetTextureOffsetSuperiors());
			addSorting(id, allBaseOverrideSetTextureOffsetInferiors, baseSorting.getOverrideSetTextureOffsetInferiors());
			addSorting(id, allBaseAfterSetTextureOffsetSuperiors, baseSorting.getAfterSetTextureOffsetSuperiors());
			addSorting(id, allBaseAfterSetTextureOffsetInferiors, baseSorting.getAfterSetTextureOffsetInferiors());

			addSorting(id, allBaseBeforeSetVisibleSuperiors, baseSorting.getBeforeSetVisibleSuperiors());
			addSorting(id, allBaseBeforeSetVisibleInferiors, baseSorting.getBeforeSetVisibleInferiors());
			addSorting(id, allBaseOverrideSetVisibleSuperiors, baseSorting.getOverrideSetVisibleSuperiors());
			addSorting(id, allBaseOverrideSetVisibleInferiors, baseSorting.getOverrideSetVisibleInferiors());
			addSorting(id, allBaseAfterSetVisibleSuperiors, baseSorting.getAfterSetVisibleSuperiors());
			addSorting(id, allBaseAfterSetVisibleInferiors, baseSorting.getAfterSetVisibleInferiors());

		}

		addMethod(id, baseClass, beforeLocalConstructingHookTypes, "beforeLocalConstructing", float.class, float.class, int.class, int.class, boolean.class);
		addMethod(id, baseClass, afterLocalConstructingHookTypes, "afterLocalConstructing", float.class, float.class, int.class, int.class, boolean.class);


		addMethod(id, baseClass, beforeGetArmForSideHookTypes, "beforeGetArmForSide", net.minecraft.util.EnumHandSide.class);
		addMethod(id, baseClass, overrideGetArmForSideHookTypes, "getArmForSide", net.minecraft.util.EnumHandSide.class);
		addMethod(id, baseClass, afterGetArmForSideHookTypes, "afterGetArmForSide", net.minecraft.util.EnumHandSide.class);

		addMethod(id, baseClass, beforeGetMainHandHookTypes, "beforeGetMainHand", net.minecraft.entity.Entity.class);
		addMethod(id, baseClass, overrideGetMainHandHookTypes, "getMainHand", net.minecraft.entity.Entity.class);
		addMethod(id, baseClass, afterGetMainHandHookTypes, "afterGetMainHand", net.minecraft.entity.Entity.class);

		addMethod(id, baseClass, beforeGetRandomModelBoxHookTypes, "beforeGetRandomModelBox", java.util.Random.class);
		addMethod(id, baseClass, overrideGetRandomModelBoxHookTypes, "getRandomModelBox", java.util.Random.class);
		addMethod(id, baseClass, afterGetRandomModelBoxHookTypes, "afterGetRandomModelBox", java.util.Random.class);

		addMethod(id, baseClass, beforeGetTextureOffsetHookTypes, "beforeGetTextureOffset", String.class);
		addMethod(id, baseClass, overrideGetTextureOffsetHookTypes, "getTextureOffset", String.class);
		addMethod(id, baseClass, afterGetTextureOffsetHookTypes, "afterGetTextureOffset", String.class);

		addMethod(id, baseClass, beforePostRenderArmHookTypes, "beforePostRenderArm", float.class, net.minecraft.util.EnumHandSide.class);
		addMethod(id, baseClass, overridePostRenderArmHookTypes, "postRenderArm", float.class, net.minecraft.util.EnumHandSide.class);
		addMethod(id, baseClass, afterPostRenderArmHookTypes, "afterPostRenderArm", float.class, net.minecraft.util.EnumHandSide.class);

		addMethod(id, baseClass, beforeRenderHookTypes, "beforeRender", net.minecraft.entity.Entity.class, float.class, float.class, float.class, float.class, float.class, float.class);
		addMethod(id, baseClass, overrideRenderHookTypes, "render", net.minecraft.entity.Entity.class, float.class, float.class, float.class, float.class, float.class, float.class);
		addMethod(id, baseClass, afterRenderHookTypes, "afterRender", net.minecraft.entity.Entity.class, float.class, float.class, float.class, float.class, float.class, float.class);

		addMethod(id, baseClass, beforeRenderCapeHookTypes, "beforeRenderCape", float.class);
		addMethod(id, baseClass, overrideRenderCapeHookTypes, "renderCape", float.class);
		addMethod(id, baseClass, afterRenderCapeHookTypes, "afterRenderCape", float.class);

		addMethod(id, baseClass, beforeRenderDeadmau5HeadHookTypes, "beforeRenderDeadmau5Head", float.class);
		addMethod(id, baseClass, overrideRenderDeadmau5HeadHookTypes, "renderDeadmau5Head", float.class);
		addMethod(id, baseClass, afterRenderDeadmau5HeadHookTypes, "afterRenderDeadmau5Head", float.class);

		addMethod(id, baseClass, beforeSetLivingAnimationsHookTypes, "beforeSetLivingAnimations", net.minecraft.entity.EntityLivingBase.class, float.class, float.class, float.class);
		addMethod(id, baseClass, overrideSetLivingAnimationsHookTypes, "setLivingAnimations", net.minecraft.entity.EntityLivingBase.class, float.class, float.class, float.class);
		addMethod(id, baseClass, afterSetLivingAnimationsHookTypes, "afterSetLivingAnimations", net.minecraft.entity.EntityLivingBase.class, float.class, float.class, float.class);

		addMethod(id, baseClass, beforeSetModelAttributesHookTypes, "beforeSetModelAttributes", net.minecraft.client.model.ModelBase.class);
		addMethod(id, baseClass, overrideSetModelAttributesHookTypes, "setModelAttributes", net.minecraft.client.model.ModelBase.class);
		addMethod(id, baseClass, afterSetModelAttributesHookTypes, "afterSetModelAttributes", net.minecraft.client.model.ModelBase.class);

		addMethod(id, baseClass, beforeSetRotationAnglesHookTypes, "beforeSetRotationAngles", float.class, float.class, float.class, float.class, float.class, float.class, net.minecraft.entity.Entity.class);
		addMethod(id, baseClass, overrideSetRotationAnglesHookTypes, "setRotationAngles", float.class, float.class, float.class, float.class, float.class, float.class, net.minecraft.entity.Entity.class);
		addMethod(id, baseClass, afterSetRotationAnglesHookTypes, "afterSetRotationAngles", float.class, float.class, float.class, float.class, float.class, float.class, net.minecraft.entity.Entity.class);

		addMethod(id, baseClass, beforeSetTextureOffsetHookTypes, "beforeSetTextureOffset", String.class, int.class, int.class);
		addMethod(id, baseClass, overrideSetTextureOffsetHookTypes, "setTextureOffset", String.class, int.class, int.class);
		addMethod(id, baseClass, afterSetTextureOffsetHookTypes, "afterSetTextureOffset", String.class, int.class, int.class);

		addMethod(id, baseClass, beforeSetVisibleHookTypes, "beforeSetVisible", boolean.class);
		addMethod(id, baseClass, overrideSetVisibleHookTypes, "setVisible", boolean.class);
		addMethod(id, baseClass, afterSetVisibleHookTypes, "afterSetVisible", boolean.class);


		addDynamicMethods(id, baseClass);

		addDynamicKeys(id, baseClass, beforeDynamicHookMethods, beforeDynamicHookTypes);
		addDynamicKeys(id, baseClass, overrideDynamicHookMethods, overrideDynamicHookTypes);
		addDynamicKeys(id, baseClass, afterDynamicHookMethods, afterDynamicHookTypes);

		initialize();

		for(IModelPlayerAPI instance : getAllInstancesList())
			instance.getModelPlayerAPI().attachModelPlayerBase(id);

		System.out.println("Model Player: registered " + id);
		logger.fine("Model Player: registered class '" + baseClass.getName() + "' with id '" + id + "'");

		initialized = false;
	}

	public static boolean unregister(String id)
	{
		if(id == null)
			return false;

		Constructor<?> constructor = allBaseConstructors.remove(id);
		if(constructor == null)
			return false;

		for(IModelPlayerAPI instance : getAllInstancesList())
			instance.getModelPlayerAPI().detachModelPlayerBase(id);

		beforeLocalConstructingHookTypes.remove(id);
		afterLocalConstructingHookTypes.remove(id);

		allBaseBeforeGetArmForSideSuperiors.remove(id);
		allBaseBeforeGetArmForSideInferiors.remove(id);
		allBaseOverrideGetArmForSideSuperiors.remove(id);
		allBaseOverrideGetArmForSideInferiors.remove(id);
		allBaseAfterGetArmForSideSuperiors.remove(id);
		allBaseAfterGetArmForSideInferiors.remove(id);

		beforeGetArmForSideHookTypes.remove(id);
		overrideGetArmForSideHookTypes.remove(id);
		afterGetArmForSideHookTypes.remove(id);

		allBaseBeforeGetMainHandSuperiors.remove(id);
		allBaseBeforeGetMainHandInferiors.remove(id);
		allBaseOverrideGetMainHandSuperiors.remove(id);
		allBaseOverrideGetMainHandInferiors.remove(id);
		allBaseAfterGetMainHandSuperiors.remove(id);
		allBaseAfterGetMainHandInferiors.remove(id);

		beforeGetMainHandHookTypes.remove(id);
		overrideGetMainHandHookTypes.remove(id);
		afterGetMainHandHookTypes.remove(id);

		allBaseBeforeGetRandomModelBoxSuperiors.remove(id);
		allBaseBeforeGetRandomModelBoxInferiors.remove(id);
		allBaseOverrideGetRandomModelBoxSuperiors.remove(id);
		allBaseOverrideGetRandomModelBoxInferiors.remove(id);
		allBaseAfterGetRandomModelBoxSuperiors.remove(id);
		allBaseAfterGetRandomModelBoxInferiors.remove(id);

		beforeGetRandomModelBoxHookTypes.remove(id);
		overrideGetRandomModelBoxHookTypes.remove(id);
		afterGetRandomModelBoxHookTypes.remove(id);

		allBaseBeforeGetTextureOffsetSuperiors.remove(id);
		allBaseBeforeGetTextureOffsetInferiors.remove(id);
		allBaseOverrideGetTextureOffsetSuperiors.remove(id);
		allBaseOverrideGetTextureOffsetInferiors.remove(id);
		allBaseAfterGetTextureOffsetSuperiors.remove(id);
		allBaseAfterGetTextureOffsetInferiors.remove(id);

		beforeGetTextureOffsetHookTypes.remove(id);
		overrideGetTextureOffsetHookTypes.remove(id);
		afterGetTextureOffsetHookTypes.remove(id);

		allBaseBeforePostRenderArmSuperiors.remove(id);
		allBaseBeforePostRenderArmInferiors.remove(id);
		allBaseOverridePostRenderArmSuperiors.remove(id);
		allBaseOverridePostRenderArmInferiors.remove(id);
		allBaseAfterPostRenderArmSuperiors.remove(id);
		allBaseAfterPostRenderArmInferiors.remove(id);

		beforePostRenderArmHookTypes.remove(id);
		overridePostRenderArmHookTypes.remove(id);
		afterPostRenderArmHookTypes.remove(id);

		allBaseBeforeRenderSuperiors.remove(id);
		allBaseBeforeRenderInferiors.remove(id);
		allBaseOverrideRenderSuperiors.remove(id);
		allBaseOverrideRenderInferiors.remove(id);
		allBaseAfterRenderSuperiors.remove(id);
		allBaseAfterRenderInferiors.remove(id);

		beforeRenderHookTypes.remove(id);
		overrideRenderHookTypes.remove(id);
		afterRenderHookTypes.remove(id);

		allBaseBeforeRenderCapeSuperiors.remove(id);
		allBaseBeforeRenderCapeInferiors.remove(id);
		allBaseOverrideRenderCapeSuperiors.remove(id);
		allBaseOverrideRenderCapeInferiors.remove(id);
		allBaseAfterRenderCapeSuperiors.remove(id);
		allBaseAfterRenderCapeInferiors.remove(id);

		beforeRenderCapeHookTypes.remove(id);
		overrideRenderCapeHookTypes.remove(id);
		afterRenderCapeHookTypes.remove(id);

		allBaseBeforeRenderDeadmau5HeadSuperiors.remove(id);
		allBaseBeforeRenderDeadmau5HeadInferiors.remove(id);
		allBaseOverrideRenderDeadmau5HeadSuperiors.remove(id);
		allBaseOverrideRenderDeadmau5HeadInferiors.remove(id);
		allBaseAfterRenderDeadmau5HeadSuperiors.remove(id);
		allBaseAfterRenderDeadmau5HeadInferiors.remove(id);

		beforeRenderDeadmau5HeadHookTypes.remove(id);
		overrideRenderDeadmau5HeadHookTypes.remove(id);
		afterRenderDeadmau5HeadHookTypes.remove(id);

		allBaseBeforeSetLivingAnimationsSuperiors.remove(id);
		allBaseBeforeSetLivingAnimationsInferiors.remove(id);
		allBaseOverrideSetLivingAnimationsSuperiors.remove(id);
		allBaseOverrideSetLivingAnimationsInferiors.remove(id);
		allBaseAfterSetLivingAnimationsSuperiors.remove(id);
		allBaseAfterSetLivingAnimationsInferiors.remove(id);

		beforeSetLivingAnimationsHookTypes.remove(id);
		overrideSetLivingAnimationsHookTypes.remove(id);
		afterSetLivingAnimationsHookTypes.remove(id);

		allBaseBeforeSetModelAttributesSuperiors.remove(id);
		allBaseBeforeSetModelAttributesInferiors.remove(id);
		allBaseOverrideSetModelAttributesSuperiors.remove(id);
		allBaseOverrideSetModelAttributesInferiors.remove(id);
		allBaseAfterSetModelAttributesSuperiors.remove(id);
		allBaseAfterSetModelAttributesInferiors.remove(id);

		beforeSetModelAttributesHookTypes.remove(id);
		overrideSetModelAttributesHookTypes.remove(id);
		afterSetModelAttributesHookTypes.remove(id);

		allBaseBeforeSetRotationAnglesSuperiors.remove(id);
		allBaseBeforeSetRotationAnglesInferiors.remove(id);
		allBaseOverrideSetRotationAnglesSuperiors.remove(id);
		allBaseOverrideSetRotationAnglesInferiors.remove(id);
		allBaseAfterSetRotationAnglesSuperiors.remove(id);
		allBaseAfterSetRotationAnglesInferiors.remove(id);

		beforeSetRotationAnglesHookTypes.remove(id);
		overrideSetRotationAnglesHookTypes.remove(id);
		afterSetRotationAnglesHookTypes.remove(id);

		allBaseBeforeSetTextureOffsetSuperiors.remove(id);
		allBaseBeforeSetTextureOffsetInferiors.remove(id);
		allBaseOverrideSetTextureOffsetSuperiors.remove(id);
		allBaseOverrideSetTextureOffsetInferiors.remove(id);
		allBaseAfterSetTextureOffsetSuperiors.remove(id);
		allBaseAfterSetTextureOffsetInferiors.remove(id);

		beforeSetTextureOffsetHookTypes.remove(id);
		overrideSetTextureOffsetHookTypes.remove(id);
		afterSetTextureOffsetHookTypes.remove(id);

		allBaseBeforeSetVisibleSuperiors.remove(id);
		allBaseBeforeSetVisibleInferiors.remove(id);
		allBaseOverrideSetVisibleSuperiors.remove(id);
		allBaseOverrideSetVisibleInferiors.remove(id);
		allBaseAfterSetVisibleSuperiors.remove(id);
		allBaseAfterSetVisibleInferiors.remove(id);

		beforeSetVisibleHookTypes.remove(id);
		overrideSetVisibleHookTypes.remove(id);
		afterSetVisibleHookTypes.remove(id);

		for(IModelPlayerAPI instance : getAllInstancesList())
			instance.getModelPlayerAPI().updateModelPlayerBases();

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

		log("ModelPlayerAPI: unregistered id '" + id + "'");

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
			boolean isOverridden = method.getDeclaringClass() != ModelPlayerBase.class;
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

	public static ModelPlayerAPI create(IModelPlayerAPI modelPlayer, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean, String type)
	{
		if(allBaseConstructors.size() > 0 && !initialized)
			initialize();
		return new ModelPlayerAPI(modelPlayer, paramFloat1, paramFloat2, paramInt1, paramInt2, paramBoolean, type);
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

		sortBases(beforeGetArmForSideHookTypes, allBaseBeforeGetArmForSideSuperiors, allBaseBeforeGetArmForSideInferiors, "beforeGetArmForSide");
		sortBases(overrideGetArmForSideHookTypes, allBaseOverrideGetArmForSideSuperiors, allBaseOverrideGetArmForSideInferiors, "overrideGetArmForSide");
		sortBases(afterGetArmForSideHookTypes, allBaseAfterGetArmForSideSuperiors, allBaseAfterGetArmForSideInferiors, "afterGetArmForSide");

		sortBases(beforeGetMainHandHookTypes, allBaseBeforeGetMainHandSuperiors, allBaseBeforeGetMainHandInferiors, "beforeGetMainHand");
		sortBases(overrideGetMainHandHookTypes, allBaseOverrideGetMainHandSuperiors, allBaseOverrideGetMainHandInferiors, "overrideGetMainHand");
		sortBases(afterGetMainHandHookTypes, allBaseAfterGetMainHandSuperiors, allBaseAfterGetMainHandInferiors, "afterGetMainHand");

		sortBases(beforeGetRandomModelBoxHookTypes, allBaseBeforeGetRandomModelBoxSuperiors, allBaseBeforeGetRandomModelBoxInferiors, "beforeGetRandomModelBox");
		sortBases(overrideGetRandomModelBoxHookTypes, allBaseOverrideGetRandomModelBoxSuperiors, allBaseOverrideGetRandomModelBoxInferiors, "overrideGetRandomModelBox");
		sortBases(afterGetRandomModelBoxHookTypes, allBaseAfterGetRandomModelBoxSuperiors, allBaseAfterGetRandomModelBoxInferiors, "afterGetRandomModelBox");

		sortBases(beforeGetTextureOffsetHookTypes, allBaseBeforeGetTextureOffsetSuperiors, allBaseBeforeGetTextureOffsetInferiors, "beforeGetTextureOffset");
		sortBases(overrideGetTextureOffsetHookTypes, allBaseOverrideGetTextureOffsetSuperiors, allBaseOverrideGetTextureOffsetInferiors, "overrideGetTextureOffset");
		sortBases(afterGetTextureOffsetHookTypes, allBaseAfterGetTextureOffsetSuperiors, allBaseAfterGetTextureOffsetInferiors, "afterGetTextureOffset");

		sortBases(beforePostRenderArmHookTypes, allBaseBeforePostRenderArmSuperiors, allBaseBeforePostRenderArmInferiors, "beforePostRenderArm");
		sortBases(overridePostRenderArmHookTypes, allBaseOverridePostRenderArmSuperiors, allBaseOverridePostRenderArmInferiors, "overridePostRenderArm");
		sortBases(afterPostRenderArmHookTypes, allBaseAfterPostRenderArmSuperiors, allBaseAfterPostRenderArmInferiors, "afterPostRenderArm");

		sortBases(beforeRenderHookTypes, allBaseBeforeRenderSuperiors, allBaseBeforeRenderInferiors, "beforeRender");
		sortBases(overrideRenderHookTypes, allBaseOverrideRenderSuperiors, allBaseOverrideRenderInferiors, "overrideRender");
		sortBases(afterRenderHookTypes, allBaseAfterRenderSuperiors, allBaseAfterRenderInferiors, "afterRender");

		sortBases(beforeRenderCapeHookTypes, allBaseBeforeRenderCapeSuperiors, allBaseBeforeRenderCapeInferiors, "beforeRenderCape");
		sortBases(overrideRenderCapeHookTypes, allBaseOverrideRenderCapeSuperiors, allBaseOverrideRenderCapeInferiors, "overrideRenderCape");
		sortBases(afterRenderCapeHookTypes, allBaseAfterRenderCapeSuperiors, allBaseAfterRenderCapeInferiors, "afterRenderCape");

		sortBases(beforeRenderDeadmau5HeadHookTypes, allBaseBeforeRenderDeadmau5HeadSuperiors, allBaseBeforeRenderDeadmau5HeadInferiors, "beforeRenderDeadmau5Head");
		sortBases(overrideRenderDeadmau5HeadHookTypes, allBaseOverrideRenderDeadmau5HeadSuperiors, allBaseOverrideRenderDeadmau5HeadInferiors, "overrideRenderDeadmau5Head");
		sortBases(afterRenderDeadmau5HeadHookTypes, allBaseAfterRenderDeadmau5HeadSuperiors, allBaseAfterRenderDeadmau5HeadInferiors, "afterRenderDeadmau5Head");

		sortBases(beforeSetLivingAnimationsHookTypes, allBaseBeforeSetLivingAnimationsSuperiors, allBaseBeforeSetLivingAnimationsInferiors, "beforeSetLivingAnimations");
		sortBases(overrideSetLivingAnimationsHookTypes, allBaseOverrideSetLivingAnimationsSuperiors, allBaseOverrideSetLivingAnimationsInferiors, "overrideSetLivingAnimations");
		sortBases(afterSetLivingAnimationsHookTypes, allBaseAfterSetLivingAnimationsSuperiors, allBaseAfterSetLivingAnimationsInferiors, "afterSetLivingAnimations");

		sortBases(beforeSetModelAttributesHookTypes, allBaseBeforeSetModelAttributesSuperiors, allBaseBeforeSetModelAttributesInferiors, "beforeSetModelAttributes");
		sortBases(overrideSetModelAttributesHookTypes, allBaseOverrideSetModelAttributesSuperiors, allBaseOverrideSetModelAttributesInferiors, "overrideSetModelAttributes");
		sortBases(afterSetModelAttributesHookTypes, allBaseAfterSetModelAttributesSuperiors, allBaseAfterSetModelAttributesInferiors, "afterSetModelAttributes");

		sortBases(beforeSetRotationAnglesHookTypes, allBaseBeforeSetRotationAnglesSuperiors, allBaseBeforeSetRotationAnglesInferiors, "beforeSetRotationAngles");
		sortBases(overrideSetRotationAnglesHookTypes, allBaseOverrideSetRotationAnglesSuperiors, allBaseOverrideSetRotationAnglesInferiors, "overrideSetRotationAngles");
		sortBases(afterSetRotationAnglesHookTypes, allBaseAfterSetRotationAnglesSuperiors, allBaseAfterSetRotationAnglesInferiors, "afterSetRotationAngles");

		sortBases(beforeSetTextureOffsetHookTypes, allBaseBeforeSetTextureOffsetSuperiors, allBaseBeforeSetTextureOffsetInferiors, "beforeSetTextureOffset");
		sortBases(overrideSetTextureOffsetHookTypes, allBaseOverrideSetTextureOffsetSuperiors, allBaseOverrideSetTextureOffsetInferiors, "overrideSetTextureOffset");
		sortBases(afterSetTextureOffsetHookTypes, allBaseAfterSetTextureOffsetSuperiors, allBaseAfterSetTextureOffsetInferiors, "afterSetTextureOffset");

		sortBases(beforeSetVisibleHookTypes, allBaseBeforeSetVisibleSuperiors, allBaseBeforeSetVisibleInferiors, "beforeSetVisible");
		sortBases(overrideSetVisibleHookTypes, allBaseOverrideSetVisibleSuperiors, allBaseOverrideSetVisibleInferiors, "overrideSetVisible");
		sortBases(afterSetVisibleHookTypes, allBaseAfterSetVisibleSuperiors, allBaseAfterSetVisibleInferiors, "afterSetVisible");

		initialized = true;
	}

	private static List<IModelPlayerAPI> getAllInstancesList()
	{
		List<IModelPlayerAPI> result = new ArrayList<IModelPlayerAPI>();
		for(Iterator<WeakReference<IModelPlayerAPI>> iterator = allInstances.iterator(); iterator.hasNext();)
		{
			IModelPlayerAPI instance = iterator.next().get();
			if(instance != null)
				result.add(instance);
			else
				iterator.remove();
		}
		return result;
	}

	private static List<WeakReference<IModelPlayerAPI>> allInstances = new ArrayList<WeakReference<IModelPlayerAPI>>();

	public static net.minecraft.client.model.ModelBiped[] getAllInstances()
	{
		List<IModelPlayerAPI> allInstances = getAllInstancesList();
		return allInstances.toArray(new net.minecraft.client.model.ModelBiped[allInstances.size()]);
	}

	public static void beforeLocalConstructing(IModelPlayerAPI modelPlayer)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		if(modelPlayerAPI != null)
			modelPlayerAPI.load();

		allInstances.add(new WeakReference<IModelPlayerAPI>(modelPlayer));

		if(modelPlayerAPI != null)
			modelPlayerAPI.beforeLocalConstructing();
	}

	public static void afterLocalConstructing(IModelPlayerAPI modelPlayer)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		if(modelPlayerAPI != null)
			modelPlayerAPI.afterLocalConstructing();
	}

	public static ModelPlayerBase getModelPlayerBase(IModelPlayerAPI modelPlayer, String baseId)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		if(modelPlayerAPI != null)
			return modelPlayerAPI.getModelPlayerBase(baseId);
		return null;
	}

	public static Set<String> getModelPlayerBaseIds(IModelPlayerAPI modelPlayer)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		Set<String> result = null;
		if(modelPlayerAPI != null)
			result = modelPlayerAPI.getModelPlayerBaseIds();
		else
			result = Collections.<String>emptySet();
		return result;
	}

	public static float getExpandParameter(IModelPlayerAPI modelPlayer)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		if(modelPlayerAPI != null)
			return modelPlayerAPI.paramFloat1;
		return 0;
	}

	public static float getYOffsetParameter(IModelPlayerAPI modelPlayer)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		if(modelPlayerAPI != null)
			return modelPlayerAPI.paramFloat2;
		return 0;
	}

	public static int getTextureWidthParameter(IModelPlayerAPI modelPlayer)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		if(modelPlayerAPI != null)
			return modelPlayerAPI.paramInt1;
		return 0;
	}

	public static int getTextureHeightParameter(IModelPlayerAPI modelPlayer)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		if(modelPlayerAPI != null)
			return modelPlayerAPI.paramInt2;
		return 0;
	}

	public static boolean getSmallArmsParameter(IModelPlayerAPI modelPlayer)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		if(modelPlayerAPI != null)
			return modelPlayerAPI.paramBoolean;
		return false;
	}

	public static String getModelPlayerType(IModelPlayerAPI modelPlayer)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.type != null)
			return modelPlayerAPI.type;
		return "other";
	}

	public static Object dynamic(IModelPlayerAPI modelPlayer, String key, Object[] parameters)
	{
		ModelPlayerAPI modelPlayerAPI = modelPlayer.getModelPlayerAPI();
		if(modelPlayerAPI != null)
			return modelPlayerAPI.dynamic(key, parameters);
		return null;
	}

	private static void sortBases(List<String> list, Map<String, String[]> allBaseSuperiors, Map<String, String[]> allBaseInferiors, String methodName)
	{
		new ModelPlayerBaseSorter(list, allBaseSuperiors, allBaseInferiors, methodName).Sort();
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

	private ModelPlayerAPI(IModelPlayerAPI modelPlayer, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean, String type)
	{
		this.modelPlayer = modelPlayer;
		this.paramFloat1 = paramFloat1;
		this.paramFloat2 = paramFloat2;
		this.paramInt1 = paramInt1;
		this.paramInt2 = paramInt2;
		this.paramBoolean = paramBoolean;
		this.type = type;
	}

	private void load()
	{
		Iterator<String> iterator = allBaseConstructors.keySet().iterator();
		while(iterator.hasNext())
		{
			String id = iterator.next();
			ModelPlayerBase toAttach = createModelPlayerBase(id);
			toAttach.beforeBaseAttach(false);
			allBaseObjects.put(id, toAttach);
			baseObjectsToId.put(toAttach, id);
		}

		beforeLocalConstructingHooks = create(beforeLocalConstructingHookTypes);
		afterLocalConstructingHooks = create(afterLocalConstructingHookTypes);

		updateModelPlayerBases();

		iterator = allBaseObjects.keySet().iterator();
		while(iterator.hasNext())
			allBaseObjects.get(iterator.next()).afterBaseAttach(false);
	}

	private ModelPlayerBase createModelPlayerBase(String id)
	{
		Constructor<?> contructor = allBaseConstructors.get(id);

		ModelPlayerBase base;
		try
		{
			if(contructor.getParameterTypes().length == 1)
				base = (ModelPlayerBase)contructor.newInstance(this);
			else
				base = (ModelPlayerBase)contructor.newInstance(this, id);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Exception while creating a ModelPlayerBase of type '" + contructor.getDeclaringClass() + "'", e);
		}
		return base;
	}

	private void updateModelPlayerBases()
	{
		beforeGetArmForSideHooks = create(beforeGetArmForSideHookTypes);
		overrideGetArmForSideHooks = create(overrideGetArmForSideHookTypes);
		afterGetArmForSideHooks = create(afterGetArmForSideHookTypes);
		isGetArmForSideModded =
			beforeGetArmForSideHooks != null ||
			overrideGetArmForSideHooks != null ||
			afterGetArmForSideHooks != null;

		beforeGetMainHandHooks = create(beforeGetMainHandHookTypes);
		overrideGetMainHandHooks = create(overrideGetMainHandHookTypes);
		afterGetMainHandHooks = create(afterGetMainHandHookTypes);
		isGetMainHandModded =
			beforeGetMainHandHooks != null ||
			overrideGetMainHandHooks != null ||
			afterGetMainHandHooks != null;

		beforeGetRandomModelBoxHooks = create(beforeGetRandomModelBoxHookTypes);
		overrideGetRandomModelBoxHooks = create(overrideGetRandomModelBoxHookTypes);
		afterGetRandomModelBoxHooks = create(afterGetRandomModelBoxHookTypes);
		isGetRandomModelBoxModded =
			beforeGetRandomModelBoxHooks != null ||
			overrideGetRandomModelBoxHooks != null ||
			afterGetRandomModelBoxHooks != null;

		beforeGetTextureOffsetHooks = create(beforeGetTextureOffsetHookTypes);
		overrideGetTextureOffsetHooks = create(overrideGetTextureOffsetHookTypes);
		afterGetTextureOffsetHooks = create(afterGetTextureOffsetHookTypes);
		isGetTextureOffsetModded =
			beforeGetTextureOffsetHooks != null ||
			overrideGetTextureOffsetHooks != null ||
			afterGetTextureOffsetHooks != null;

		beforePostRenderArmHooks = create(beforePostRenderArmHookTypes);
		overridePostRenderArmHooks = create(overridePostRenderArmHookTypes);
		afterPostRenderArmHooks = create(afterPostRenderArmHookTypes);
		isPostRenderArmModded =
			beforePostRenderArmHooks != null ||
			overridePostRenderArmHooks != null ||
			afterPostRenderArmHooks != null;

		beforeRenderHooks = create(beforeRenderHookTypes);
		overrideRenderHooks = create(overrideRenderHookTypes);
		afterRenderHooks = create(afterRenderHookTypes);
		isRenderModded =
			beforeRenderHooks != null ||
			overrideRenderHooks != null ||
			afterRenderHooks != null;

		beforeRenderCapeHooks = create(beforeRenderCapeHookTypes);
		overrideRenderCapeHooks = create(overrideRenderCapeHookTypes);
		afterRenderCapeHooks = create(afterRenderCapeHookTypes);
		isRenderCapeModded =
			beforeRenderCapeHooks != null ||
			overrideRenderCapeHooks != null ||
			afterRenderCapeHooks != null;

		beforeRenderDeadmau5HeadHooks = create(beforeRenderDeadmau5HeadHookTypes);
		overrideRenderDeadmau5HeadHooks = create(overrideRenderDeadmau5HeadHookTypes);
		afterRenderDeadmau5HeadHooks = create(afterRenderDeadmau5HeadHookTypes);
		isRenderDeadmau5HeadModded =
			beforeRenderDeadmau5HeadHooks != null ||
			overrideRenderDeadmau5HeadHooks != null ||
			afterRenderDeadmau5HeadHooks != null;

		beforeSetLivingAnimationsHooks = create(beforeSetLivingAnimationsHookTypes);
		overrideSetLivingAnimationsHooks = create(overrideSetLivingAnimationsHookTypes);
		afterSetLivingAnimationsHooks = create(afterSetLivingAnimationsHookTypes);
		isSetLivingAnimationsModded =
			beforeSetLivingAnimationsHooks != null ||
			overrideSetLivingAnimationsHooks != null ||
			afterSetLivingAnimationsHooks != null;

		beforeSetModelAttributesHooks = create(beforeSetModelAttributesHookTypes);
		overrideSetModelAttributesHooks = create(overrideSetModelAttributesHookTypes);
		afterSetModelAttributesHooks = create(afterSetModelAttributesHookTypes);
		isSetModelAttributesModded =
			beforeSetModelAttributesHooks != null ||
			overrideSetModelAttributesHooks != null ||
			afterSetModelAttributesHooks != null;

		beforeSetRotationAnglesHooks = create(beforeSetRotationAnglesHookTypes);
		overrideSetRotationAnglesHooks = create(overrideSetRotationAnglesHookTypes);
		afterSetRotationAnglesHooks = create(afterSetRotationAnglesHookTypes);
		isSetRotationAnglesModded =
			beforeSetRotationAnglesHooks != null ||
			overrideSetRotationAnglesHooks != null ||
			afterSetRotationAnglesHooks != null;

		beforeSetTextureOffsetHooks = create(beforeSetTextureOffsetHookTypes);
		overrideSetTextureOffsetHooks = create(overrideSetTextureOffsetHookTypes);
		afterSetTextureOffsetHooks = create(afterSetTextureOffsetHookTypes);
		isSetTextureOffsetModded =
			beforeSetTextureOffsetHooks != null ||
			overrideSetTextureOffsetHooks != null ||
			afterSetTextureOffsetHooks != null;

		beforeSetVisibleHooks = create(beforeSetVisibleHookTypes);
		overrideSetVisibleHooks = create(overrideSetVisibleHookTypes);
		afterSetVisibleHooks = create(afterSetVisibleHookTypes);
		isSetVisibleModded =
			beforeSetVisibleHooks != null ||
			overrideSetVisibleHooks != null ||
			afterSetVisibleHooks != null;

	}

	private void attachModelPlayerBase(String id)
	{
        ModelPlayerBase toAttach = createModelPlayerBase(id);
		toAttach.beforeBaseAttach(true);
		allBaseObjects.put(id, toAttach);
		updateModelPlayerBases();
		toAttach.afterBaseAttach(true);
	}

	private void detachModelPlayerBase(String id)
	{
		ModelPlayerBase toDetach = allBaseObjects.get(id);
		toDetach.beforeBaseDetach(true);
		allBaseObjects.remove(id);
		toDetach.afterBaseDetach(true);
	}

	private ModelPlayerBase[] create(List<String> types)
	{
		if(types.isEmpty())
			return null;

		ModelPlayerBase[] result = new ModelPlayerBase[types.size()];
		for(int i = 0; i < result.length; i++)
			result[i] = getModelPlayerBase(types.get(i));
		return result;
	}

	private void beforeLocalConstructing()
	{
		if(beforeLocalConstructingHooks != null)
			for(int i = beforeLocalConstructingHooks.length - 1; i >= 0 ; i--)
				beforeLocalConstructingHooks[i].beforeLocalConstructing(paramFloat1, paramFloat2, paramInt1, paramInt2, paramBoolean);
		beforeLocalConstructingHooks = null;
	}

	private void afterLocalConstructing()
	{
		if(afterLocalConstructingHooks != null)
			for(int i = 0; i < afterLocalConstructingHooks.length; i++)
				afterLocalConstructingHooks[i].afterLocalConstructing(paramFloat1, paramFloat2, paramInt1, paramInt2, paramBoolean);
		afterLocalConstructingHooks = null;
	}

	public ModelPlayerBase getModelPlayerBase(String id)
	{
		return allBaseObjects.get(id);
	}

	public Set<String> getModelPlayerBaseIds()
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

	public Object dynamicOverwritten(String key, Object[] parameters, ModelPlayerBase overwriter)
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

		return execute(getModelPlayerBase(id), method, parameters);
	}

	private void executeAll(String key, Object[] parameters, Map<String, List<String>> dynamicHookTypes, Map<Class<?>, Map<String, Method>> dynamicHookMethods, boolean reverse)
	{
		List<String> beforeIds = dynamicHookTypes.get(key);
		if(beforeIds == null)
			return;

		for(int i= reverse ? beforeIds.size() - 1 : 0; reverse ? i >= 0 : i < beforeIds.size(); i = i + (reverse ? -1 : 1))
		{
			String id = beforeIds.get(i);
			ModelPlayerBase base = getModelPlayerBase(id);
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

	private Object execute(ModelPlayerBase base, Method method, Object[] parameters)
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

	public static net.minecraft.client.model.ModelRenderer getArmForSide(IModelPlayerAPI target, net.minecraft.util.EnumHandSide paramEnumHandSide)
	{
		net.minecraft.client.model.ModelRenderer _result;
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isGetArmForSideModded)
			_result = modelPlayerAPI.getArmForSide(paramEnumHandSide);
		else
			_result = target.localGetArmForSide(paramEnumHandSide);
		return _result;
	}

	private net.minecraft.client.model.ModelRenderer getArmForSide(net.minecraft.util.EnumHandSide paramEnumHandSide)
	{
		if(beforeGetArmForSideHooks != null)
			for(int i = beforeGetArmForSideHooks.length - 1; i >= 0 ; i--)
				beforeGetArmForSideHooks[i].beforeGetArmForSide(paramEnumHandSide);

		net.minecraft.client.model.ModelRenderer _result;
		if(overrideGetArmForSideHooks != null)
			_result = overrideGetArmForSideHooks[overrideGetArmForSideHooks.length - 1].getArmForSide(paramEnumHandSide);
		else
			_result = modelPlayer.localGetArmForSide(paramEnumHandSide);

		if(afterGetArmForSideHooks != null)
			for(int i = 0; i < afterGetArmForSideHooks.length; i++)
				afterGetArmForSideHooks[i].afterGetArmForSide(paramEnumHandSide);

		return _result;
	}

	protected ModelPlayerBase GetOverwrittenGetArmForSide(ModelPlayerBase overWriter)
	{
		if (overrideGetArmForSideHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetArmForSideHooks.length; i++)
			if(overrideGetArmForSideHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetArmForSideHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetArmForSideHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetArmForSideHookTypes = new LinkedList<String>();
	private final static List<String> afterGetArmForSideHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeGetArmForSideHooks;
	private ModelPlayerBase[] overrideGetArmForSideHooks;
	private ModelPlayerBase[] afterGetArmForSideHooks;

	public boolean isGetArmForSideModded;

	private static final Map<String, String[]> allBaseBeforeGetArmForSideSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetArmForSideInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetArmForSideSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetArmForSideInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetArmForSideSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetArmForSideInferiors = new Hashtable<String, String[]>(0);

	public static net.minecraft.util.EnumHandSide getMainHand(IModelPlayerAPI target, net.minecraft.entity.Entity paramEntity)
	{
		net.minecraft.util.EnumHandSide _result;
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isGetMainHandModded)
			_result = modelPlayerAPI.getMainHand(paramEntity);
		else
			_result = target.localGetMainHand(paramEntity);
		return _result;
	}

	private net.minecraft.util.EnumHandSide getMainHand(net.minecraft.entity.Entity paramEntity)
	{
		if(beforeGetMainHandHooks != null)
			for(int i = beforeGetMainHandHooks.length - 1; i >= 0 ; i--)
				beforeGetMainHandHooks[i].beforeGetMainHand(paramEntity);

		net.minecraft.util.EnumHandSide _result;
		if(overrideGetMainHandHooks != null)
			_result = overrideGetMainHandHooks[overrideGetMainHandHooks.length - 1].getMainHand(paramEntity);
		else
			_result = modelPlayer.localGetMainHand(paramEntity);

		if(afterGetMainHandHooks != null)
			for(int i = 0; i < afterGetMainHandHooks.length; i++)
				afterGetMainHandHooks[i].afterGetMainHand(paramEntity);

		return _result;
	}

	protected ModelPlayerBase GetOverwrittenGetMainHand(ModelPlayerBase overWriter)
	{
		if (overrideGetMainHandHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetMainHandHooks.length; i++)
			if(overrideGetMainHandHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetMainHandHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetMainHandHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetMainHandHookTypes = new LinkedList<String>();
	private final static List<String> afterGetMainHandHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeGetMainHandHooks;
	private ModelPlayerBase[] overrideGetMainHandHooks;
	private ModelPlayerBase[] afterGetMainHandHooks;

	public boolean isGetMainHandModded;

	private static final Map<String, String[]> allBaseBeforeGetMainHandSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetMainHandInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetMainHandSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetMainHandInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetMainHandSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetMainHandInferiors = new Hashtable<String, String[]>(0);

	public static net.minecraft.client.model.ModelRenderer getRandomModelBox(IModelPlayerAPI target, java.util.Random paramRandom)
	{
		net.minecraft.client.model.ModelRenderer _result;
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isGetRandomModelBoxModded)
			_result = modelPlayerAPI.getRandomModelBox(paramRandom);
		else
			_result = target.localGetRandomModelBox(paramRandom);
		return _result;
	}

	private net.minecraft.client.model.ModelRenderer getRandomModelBox(java.util.Random paramRandom)
	{
		if(beforeGetRandomModelBoxHooks != null)
			for(int i = beforeGetRandomModelBoxHooks.length - 1; i >= 0 ; i--)
				beforeGetRandomModelBoxHooks[i].beforeGetRandomModelBox(paramRandom);

		net.minecraft.client.model.ModelRenderer _result;
		if(overrideGetRandomModelBoxHooks != null)
			_result = overrideGetRandomModelBoxHooks[overrideGetRandomModelBoxHooks.length - 1].getRandomModelBox(paramRandom);
		else
			_result = modelPlayer.localGetRandomModelBox(paramRandom);

		if(afterGetRandomModelBoxHooks != null)
			for(int i = 0; i < afterGetRandomModelBoxHooks.length; i++)
				afterGetRandomModelBoxHooks[i].afterGetRandomModelBox(paramRandom);

		return _result;
	}

	protected ModelPlayerBase GetOverwrittenGetRandomModelBox(ModelPlayerBase overWriter)
	{
		if (overrideGetRandomModelBoxHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetRandomModelBoxHooks.length; i++)
			if(overrideGetRandomModelBoxHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetRandomModelBoxHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetRandomModelBoxHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetRandomModelBoxHookTypes = new LinkedList<String>();
	private final static List<String> afterGetRandomModelBoxHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeGetRandomModelBoxHooks;
	private ModelPlayerBase[] overrideGetRandomModelBoxHooks;
	private ModelPlayerBase[] afterGetRandomModelBoxHooks;

	public boolean isGetRandomModelBoxModded;

	private static final Map<String, String[]> allBaseBeforeGetRandomModelBoxSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetRandomModelBoxInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetRandomModelBoxSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetRandomModelBoxInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetRandomModelBoxSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetRandomModelBoxInferiors = new Hashtable<String, String[]>(0);

	public static net.minecraft.client.model.TextureOffset getTextureOffset(IModelPlayerAPI target, String paramString)
	{
		net.minecraft.client.model.TextureOffset _result;
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isGetTextureOffsetModded)
			_result = modelPlayerAPI.getTextureOffset(paramString);
		else
			_result = target.localGetTextureOffset(paramString);
		return _result;
	}

	private net.minecraft.client.model.TextureOffset getTextureOffset(String paramString)
	{
		if(beforeGetTextureOffsetHooks != null)
			for(int i = beforeGetTextureOffsetHooks.length - 1; i >= 0 ; i--)
				beforeGetTextureOffsetHooks[i].beforeGetTextureOffset(paramString);

		net.minecraft.client.model.TextureOffset _result;
		if(overrideGetTextureOffsetHooks != null)
			_result = overrideGetTextureOffsetHooks[overrideGetTextureOffsetHooks.length - 1].getTextureOffset(paramString);
		else
			_result = modelPlayer.localGetTextureOffset(paramString);

		if(afterGetTextureOffsetHooks != null)
			for(int i = 0; i < afterGetTextureOffsetHooks.length; i++)
				afterGetTextureOffsetHooks[i].afterGetTextureOffset(paramString);

		return _result;
	}

	protected ModelPlayerBase GetOverwrittenGetTextureOffset(ModelPlayerBase overWriter)
	{
		if (overrideGetTextureOffsetHooks == null)
			return overWriter;

		for(int i = 0; i < overrideGetTextureOffsetHooks.length; i++)
			if(overrideGetTextureOffsetHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideGetTextureOffsetHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeGetTextureOffsetHookTypes = new LinkedList<String>();
	private final static List<String> overrideGetTextureOffsetHookTypes = new LinkedList<String>();
	private final static List<String> afterGetTextureOffsetHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeGetTextureOffsetHooks;
	private ModelPlayerBase[] overrideGetTextureOffsetHooks;
	private ModelPlayerBase[] afterGetTextureOffsetHooks;

	public boolean isGetTextureOffsetModded;

	private static final Map<String, String[]> allBaseBeforeGetTextureOffsetSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeGetTextureOffsetInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetTextureOffsetSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideGetTextureOffsetInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetTextureOffsetSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterGetTextureOffsetInferiors = new Hashtable<String, String[]>(0);

	public static void postRenderArm(IModelPlayerAPI target, float paramFloat, net.minecraft.util.EnumHandSide paramEnumHandSide)
	{
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isPostRenderArmModded)
			modelPlayerAPI.postRenderArm(paramFloat, paramEnumHandSide);
		else
			target.localPostRenderArm(paramFloat, paramEnumHandSide);
	}

	private void postRenderArm(float paramFloat, net.minecraft.util.EnumHandSide paramEnumHandSide)
	{
		if(beforePostRenderArmHooks != null)
			for(int i = beforePostRenderArmHooks.length - 1; i >= 0 ; i--)
				beforePostRenderArmHooks[i].beforePostRenderArm(paramFloat, paramEnumHandSide);

		if(overridePostRenderArmHooks != null)
			overridePostRenderArmHooks[overridePostRenderArmHooks.length - 1].postRenderArm(paramFloat, paramEnumHandSide);
		else
			modelPlayer.localPostRenderArm(paramFloat, paramEnumHandSide);

		if(afterPostRenderArmHooks != null)
			for(int i = 0; i < afterPostRenderArmHooks.length; i++)
				afterPostRenderArmHooks[i].afterPostRenderArm(paramFloat, paramEnumHandSide);

	}

	protected ModelPlayerBase GetOverwrittenPostRenderArm(ModelPlayerBase overWriter)
	{
		if (overridePostRenderArmHooks == null)
			return overWriter;

		for(int i = 0; i < overridePostRenderArmHooks.length; i++)
			if(overridePostRenderArmHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overridePostRenderArmHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforePostRenderArmHookTypes = new LinkedList<String>();
	private final static List<String> overridePostRenderArmHookTypes = new LinkedList<String>();
	private final static List<String> afterPostRenderArmHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforePostRenderArmHooks;
	private ModelPlayerBase[] overridePostRenderArmHooks;
	private ModelPlayerBase[] afterPostRenderArmHooks;

	public boolean isPostRenderArmModded;

	private static final Map<String, String[]> allBaseBeforePostRenderArmSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforePostRenderArmInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverridePostRenderArmSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverridePostRenderArmInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterPostRenderArmSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterPostRenderArmInferiors = new Hashtable<String, String[]>(0);

	public static void render(IModelPlayerAPI target, net.minecraft.entity.Entity paramEntity, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
	{
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isRenderModded)
			modelPlayerAPI.render(paramEntity, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
		else
			target.localRender(paramEntity, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
	}

	private void render(net.minecraft.entity.Entity paramEntity, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
	{
		if(beforeRenderHooks != null)
			for(int i = beforeRenderHooks.length - 1; i >= 0 ; i--)
				beforeRenderHooks[i].beforeRender(paramEntity, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);

		if(overrideRenderHooks != null)
			overrideRenderHooks[overrideRenderHooks.length - 1].render(paramEntity, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
		else
			modelPlayer.localRender(paramEntity, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);

		if(afterRenderHooks != null)
			for(int i = 0; i < afterRenderHooks.length; i++)
				afterRenderHooks[i].afterRender(paramEntity, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);

	}

	protected ModelPlayerBase GetOverwrittenRender(ModelPlayerBase overWriter)
	{
		if (overrideRenderHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderHooks.length; i++)
			if(overrideRenderHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeRenderHooks;
	private ModelPlayerBase[] overrideRenderHooks;
	private ModelPlayerBase[] afterRenderHooks;

	public boolean isRenderModded;

	private static final Map<String, String[]> allBaseBeforeRenderSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderInferiors = new Hashtable<String, String[]>(0);

	public static void renderCape(IModelPlayerAPI target, float paramFloat)
	{
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isRenderCapeModded)
			modelPlayerAPI.renderCape(paramFloat);
		else
			target.localRenderCape(paramFloat);
	}

	private void renderCape(float paramFloat)
	{
		if(beforeRenderCapeHooks != null)
			for(int i = beforeRenderCapeHooks.length - 1; i >= 0 ; i--)
				beforeRenderCapeHooks[i].beforeRenderCape(paramFloat);

		if(overrideRenderCapeHooks != null)
			overrideRenderCapeHooks[overrideRenderCapeHooks.length - 1].renderCape(paramFloat);
		else
			modelPlayer.localRenderCape(paramFloat);

		if(afterRenderCapeHooks != null)
			for(int i = 0; i < afterRenderCapeHooks.length; i++)
				afterRenderCapeHooks[i].afterRenderCape(paramFloat);

	}

	protected ModelPlayerBase GetOverwrittenRenderCape(ModelPlayerBase overWriter)
	{
		if (overrideRenderCapeHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderCapeHooks.length; i++)
			if(overrideRenderCapeHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderCapeHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderCapeHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderCapeHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderCapeHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeRenderCapeHooks;
	private ModelPlayerBase[] overrideRenderCapeHooks;
	private ModelPlayerBase[] afterRenderCapeHooks;

	public boolean isRenderCapeModded;

	private static final Map<String, String[]> allBaseBeforeRenderCapeSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderCapeInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderCapeSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderCapeInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderCapeSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderCapeInferiors = new Hashtable<String, String[]>(0);

	public static void renderDeadmau5Head(IModelPlayerAPI target, float paramFloat)
	{
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isRenderDeadmau5HeadModded)
			modelPlayerAPI.renderDeadmau5Head(paramFloat);
		else
			target.localRenderDeadmau5Head(paramFloat);
	}

	private void renderDeadmau5Head(float paramFloat)
	{
		if(beforeRenderDeadmau5HeadHooks != null)
			for(int i = beforeRenderDeadmau5HeadHooks.length - 1; i >= 0 ; i--)
				beforeRenderDeadmau5HeadHooks[i].beforeRenderDeadmau5Head(paramFloat);

		if(overrideRenderDeadmau5HeadHooks != null)
			overrideRenderDeadmau5HeadHooks[overrideRenderDeadmau5HeadHooks.length - 1].renderDeadmau5Head(paramFloat);
		else
			modelPlayer.localRenderDeadmau5Head(paramFloat);

		if(afterRenderDeadmau5HeadHooks != null)
			for(int i = 0; i < afterRenderDeadmau5HeadHooks.length; i++)
				afterRenderDeadmau5HeadHooks[i].afterRenderDeadmau5Head(paramFloat);

	}

	protected ModelPlayerBase GetOverwrittenRenderDeadmau5Head(ModelPlayerBase overWriter)
	{
		if (overrideRenderDeadmau5HeadHooks == null)
			return overWriter;

		for(int i = 0; i < overrideRenderDeadmau5HeadHooks.length; i++)
			if(overrideRenderDeadmau5HeadHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideRenderDeadmau5HeadHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeRenderDeadmau5HeadHookTypes = new LinkedList<String>();
	private final static List<String> overrideRenderDeadmau5HeadHookTypes = new LinkedList<String>();
	private final static List<String> afterRenderDeadmau5HeadHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeRenderDeadmau5HeadHooks;
	private ModelPlayerBase[] overrideRenderDeadmau5HeadHooks;
	private ModelPlayerBase[] afterRenderDeadmau5HeadHooks;

	public boolean isRenderDeadmau5HeadModded;

	private static final Map<String, String[]> allBaseBeforeRenderDeadmau5HeadSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeRenderDeadmau5HeadInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderDeadmau5HeadSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideRenderDeadmau5HeadInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderDeadmau5HeadSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterRenderDeadmau5HeadInferiors = new Hashtable<String, String[]>(0);

	public static void setLivingAnimations(IModelPlayerAPI target, net.minecraft.entity.EntityLivingBase paramEntityLivingBase, float paramFloat1, float paramFloat2, float paramFloat3)
	{
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isSetLivingAnimationsModded)
			modelPlayerAPI.setLivingAnimations(paramEntityLivingBase, paramFloat1, paramFloat2, paramFloat3);
		else
			target.localSetLivingAnimations(paramEntityLivingBase, paramFloat1, paramFloat2, paramFloat3);
	}

	private void setLivingAnimations(net.minecraft.entity.EntityLivingBase paramEntityLivingBase, float paramFloat1, float paramFloat2, float paramFloat3)
	{
		if(beforeSetLivingAnimationsHooks != null)
			for(int i = beforeSetLivingAnimationsHooks.length - 1; i >= 0 ; i--)
				beforeSetLivingAnimationsHooks[i].beforeSetLivingAnimations(paramEntityLivingBase, paramFloat1, paramFloat2, paramFloat3);

		if(overrideSetLivingAnimationsHooks != null)
			overrideSetLivingAnimationsHooks[overrideSetLivingAnimationsHooks.length - 1].setLivingAnimations(paramEntityLivingBase, paramFloat1, paramFloat2, paramFloat3);
		else
			modelPlayer.localSetLivingAnimations(paramEntityLivingBase, paramFloat1, paramFloat2, paramFloat3);

		if(afterSetLivingAnimationsHooks != null)
			for(int i = 0; i < afterSetLivingAnimationsHooks.length; i++)
				afterSetLivingAnimationsHooks[i].afterSetLivingAnimations(paramEntityLivingBase, paramFloat1, paramFloat2, paramFloat3);

	}

	protected ModelPlayerBase GetOverwrittenSetLivingAnimations(ModelPlayerBase overWriter)
	{
		if (overrideSetLivingAnimationsHooks == null)
			return overWriter;

		for(int i = 0; i < overrideSetLivingAnimationsHooks.length; i++)
			if(overrideSetLivingAnimationsHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideSetLivingAnimationsHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeSetLivingAnimationsHookTypes = new LinkedList<String>();
	private final static List<String> overrideSetLivingAnimationsHookTypes = new LinkedList<String>();
	private final static List<String> afterSetLivingAnimationsHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeSetLivingAnimationsHooks;
	private ModelPlayerBase[] overrideSetLivingAnimationsHooks;
	private ModelPlayerBase[] afterSetLivingAnimationsHooks;

	public boolean isSetLivingAnimationsModded;

	private static final Map<String, String[]> allBaseBeforeSetLivingAnimationsSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeSetLivingAnimationsInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetLivingAnimationsSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetLivingAnimationsInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetLivingAnimationsSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetLivingAnimationsInferiors = new Hashtable<String, String[]>(0);

	public static void setModelAttributes(IModelPlayerAPI target, net.minecraft.client.model.ModelBase paramModelBase)
	{
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isSetModelAttributesModded)
			modelPlayerAPI.setModelAttributes(paramModelBase);
		else
			target.localSetModelAttributes(paramModelBase);
	}

	private void setModelAttributes(net.minecraft.client.model.ModelBase paramModelBase)
	{
		if(beforeSetModelAttributesHooks != null)
			for(int i = beforeSetModelAttributesHooks.length - 1; i >= 0 ; i--)
				beforeSetModelAttributesHooks[i].beforeSetModelAttributes(paramModelBase);

		if(overrideSetModelAttributesHooks != null)
			overrideSetModelAttributesHooks[overrideSetModelAttributesHooks.length - 1].setModelAttributes(paramModelBase);
		else
			modelPlayer.localSetModelAttributes(paramModelBase);

		if(afterSetModelAttributesHooks != null)
			for(int i = 0; i < afterSetModelAttributesHooks.length; i++)
				afterSetModelAttributesHooks[i].afterSetModelAttributes(paramModelBase);

	}

	protected ModelPlayerBase GetOverwrittenSetModelAttributes(ModelPlayerBase overWriter)
	{
		if (overrideSetModelAttributesHooks == null)
			return overWriter;

		for(int i = 0; i < overrideSetModelAttributesHooks.length; i++)
			if(overrideSetModelAttributesHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideSetModelAttributesHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeSetModelAttributesHookTypes = new LinkedList<String>();
	private final static List<String> overrideSetModelAttributesHookTypes = new LinkedList<String>();
	private final static List<String> afterSetModelAttributesHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeSetModelAttributesHooks;
	private ModelPlayerBase[] overrideSetModelAttributesHooks;
	private ModelPlayerBase[] afterSetModelAttributesHooks;

	public boolean isSetModelAttributesModded;

	private static final Map<String, String[]> allBaseBeforeSetModelAttributesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeSetModelAttributesInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetModelAttributesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetModelAttributesInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetModelAttributesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetModelAttributesInferiors = new Hashtable<String, String[]>(0);

	public static void setRotationAngles(IModelPlayerAPI target, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, net.minecraft.entity.Entity paramEntity)
	{
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isSetRotationAnglesModded)
			modelPlayerAPI.setRotationAngles(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramEntity);
		else
			target.localSetRotationAngles(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramEntity);
	}

	private void setRotationAngles(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, net.minecraft.entity.Entity paramEntity)
	{
		if(beforeSetRotationAnglesHooks != null)
			for(int i = beforeSetRotationAnglesHooks.length - 1; i >= 0 ; i--)
				beforeSetRotationAnglesHooks[i].beforeSetRotationAngles(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramEntity);

		if(overrideSetRotationAnglesHooks != null)
			overrideSetRotationAnglesHooks[overrideSetRotationAnglesHooks.length - 1].setRotationAngles(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramEntity);
		else
			modelPlayer.localSetRotationAngles(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramEntity);

		if(afterSetRotationAnglesHooks != null)
			for(int i = 0; i < afterSetRotationAnglesHooks.length; i++)
				afterSetRotationAnglesHooks[i].afterSetRotationAngles(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramEntity);

	}

	protected ModelPlayerBase GetOverwrittenSetRotationAngles(ModelPlayerBase overWriter)
	{
		if (overrideSetRotationAnglesHooks == null)
			return overWriter;

		for(int i = 0; i < overrideSetRotationAnglesHooks.length; i++)
			if(overrideSetRotationAnglesHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideSetRotationAnglesHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeSetRotationAnglesHookTypes = new LinkedList<String>();
	private final static List<String> overrideSetRotationAnglesHookTypes = new LinkedList<String>();
	private final static List<String> afterSetRotationAnglesHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeSetRotationAnglesHooks;
	private ModelPlayerBase[] overrideSetRotationAnglesHooks;
	private ModelPlayerBase[] afterSetRotationAnglesHooks;

	public boolean isSetRotationAnglesModded;

	private static final Map<String, String[]> allBaseBeforeSetRotationAnglesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeSetRotationAnglesInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetRotationAnglesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetRotationAnglesInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetRotationAnglesSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetRotationAnglesInferiors = new Hashtable<String, String[]>(0);

	public static void setTextureOffset(IModelPlayerAPI target, String paramString, int paramInt1, int paramInt2)
	{
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isSetTextureOffsetModded)
			modelPlayerAPI.setTextureOffset(paramString, paramInt1, paramInt2);
		else
			target.localSetTextureOffset(paramString, paramInt1, paramInt2);
	}

	private void setTextureOffset(String paramString, int paramInt1, int paramInt2)
	{
		if(beforeSetTextureOffsetHooks != null)
			for(int i = beforeSetTextureOffsetHooks.length - 1; i >= 0 ; i--)
				beforeSetTextureOffsetHooks[i].beforeSetTextureOffset(paramString, paramInt1, paramInt2);

		if(overrideSetTextureOffsetHooks != null)
			overrideSetTextureOffsetHooks[overrideSetTextureOffsetHooks.length - 1].setTextureOffset(paramString, paramInt1, paramInt2);
		else
			modelPlayer.localSetTextureOffset(paramString, paramInt1, paramInt2);

		if(afterSetTextureOffsetHooks != null)
			for(int i = 0; i < afterSetTextureOffsetHooks.length; i++)
				afterSetTextureOffsetHooks[i].afterSetTextureOffset(paramString, paramInt1, paramInt2);

	}

	protected ModelPlayerBase GetOverwrittenSetTextureOffset(ModelPlayerBase overWriter)
	{
		if (overrideSetTextureOffsetHooks == null)
			return overWriter;

		for(int i = 0; i < overrideSetTextureOffsetHooks.length; i++)
			if(overrideSetTextureOffsetHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideSetTextureOffsetHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeSetTextureOffsetHookTypes = new LinkedList<String>();
	private final static List<String> overrideSetTextureOffsetHookTypes = new LinkedList<String>();
	private final static List<String> afterSetTextureOffsetHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeSetTextureOffsetHooks;
	private ModelPlayerBase[] overrideSetTextureOffsetHooks;
	private ModelPlayerBase[] afterSetTextureOffsetHooks;

	public boolean isSetTextureOffsetModded;

	private static final Map<String, String[]> allBaseBeforeSetTextureOffsetSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeSetTextureOffsetInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetTextureOffsetSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetTextureOffsetInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetTextureOffsetSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetTextureOffsetInferiors = new Hashtable<String, String[]>(0);

	public static void setVisible(IModelPlayerAPI target, boolean paramBoolean)
	{
		ModelPlayerAPI modelPlayerAPI = target.getModelPlayerAPI();
		if(modelPlayerAPI != null && modelPlayerAPI.isSetVisibleModded)
			modelPlayerAPI.setVisible(paramBoolean);
		else
			target.localSetVisible(paramBoolean);
	}

	private void setVisible(boolean paramBoolean)
	{
		if(beforeSetVisibleHooks != null)
			for(int i = beforeSetVisibleHooks.length - 1; i >= 0 ; i--)
				beforeSetVisibleHooks[i].beforeSetVisible(paramBoolean);

		if(overrideSetVisibleHooks != null)
			overrideSetVisibleHooks[overrideSetVisibleHooks.length - 1].setVisible(paramBoolean);
		else
			modelPlayer.localSetVisible(paramBoolean);

		if(afterSetVisibleHooks != null)
			for(int i = 0; i < afterSetVisibleHooks.length; i++)
				afterSetVisibleHooks[i].afterSetVisible(paramBoolean);

	}

	protected ModelPlayerBase GetOverwrittenSetVisible(ModelPlayerBase overWriter)
	{
		if (overrideSetVisibleHooks == null)
			return overWriter;

		for(int i = 0; i < overrideSetVisibleHooks.length; i++)
			if(overrideSetVisibleHooks[i] == overWriter)
				if(i == 0)
					return null;
				else
					return overrideSetVisibleHooks[i - 1];

		return overWriter;
	}

	private final static List<String> beforeSetVisibleHookTypes = new LinkedList<String>();
	private final static List<String> overrideSetVisibleHookTypes = new LinkedList<String>();
	private final static List<String> afterSetVisibleHookTypes = new LinkedList<String>();

	private ModelPlayerBase[] beforeSetVisibleHooks;
	private ModelPlayerBase[] overrideSetVisibleHooks;
	private ModelPlayerBase[] afterSetVisibleHooks;

	public boolean isSetVisibleModded;

	private static final Map<String, String[]> allBaseBeforeSetVisibleSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseBeforeSetVisibleInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetVisibleSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseOverrideSetVisibleInferiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetVisibleSuperiors = new Hashtable<String, String[]>(0);
	private static final Map<String, String[]> allBaseAfterSetVisibleInferiors = new Hashtable<String, String[]>(0);

	
	protected final IModelPlayerAPI modelPlayer;
	private final float paramFloat1;
	private final float paramFloat2;
	private final int paramInt1;
	private final int paramInt2;
	private final boolean paramBoolean;
	private final String type;

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

	private ModelPlayerBase[] beforeLocalConstructingHooks;
	private ModelPlayerBase[] afterLocalConstructingHooks;

	private final Map<ModelPlayerBase, String> baseObjectsToId = new Hashtable<ModelPlayerBase, String>();
	private final Map<String, ModelPlayerBase> allBaseObjects = new Hashtable<String, ModelPlayerBase>();
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
