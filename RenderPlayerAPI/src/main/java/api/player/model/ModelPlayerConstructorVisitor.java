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

import org.objectweb.asm.*;

public final class ModelPlayerConstructorVisitor extends MethodVisitor
{
	private final boolean isObfuscated;
	private final int parameterOffset;

	public ModelPlayerConstructorVisitor(MethodVisitor paramMethodVisitor, boolean isObfuscated, int parameterOffset)
	{
		super(262144, paramMethodVisitor);
		this.isObfuscated = isObfuscated;
		this.parameterOffset = parameterOffset;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
	{
		if(name.equals("<init>") && owner.equals(isObfuscated ? "bqj" : "net/minecraft/client/model/ModelPlayer"))
		{
			desc = desc.substring(0, desc.indexOf(")")) + "Ljava/lang/String;)V";
			mv.visitVarInsn(Opcodes.ALOAD, parameterOffset);
		}
		super.visitMethodInsn(opcode, owner, name, desc, itf);
		if(name.equals("<init>") && owner.equals(isObfuscated ? "bpx" : "net/minecraft/client/model/ModelBiped"))
		{
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitVarInsn(Opcodes.FLOAD, 1);
			mv.visitLdcInsn(0F);
			mv.visitLdcInsn(64);
			mv.visitLdcInsn(64);
			mv.visitVarInsn(Opcodes.ILOAD, 2);
			mv.visitVarInsn(Opcodes.ALOAD, 3);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "api/player/model/ModelPlayerAPI", "create", "(Lapi/player/model/IModelPlayerAPI;FFIIZLjava/lang/String;)Lapi/player/model/ModelPlayerAPI;", false);
			mv.visitFieldInsn(Opcodes.PUTFIELD, isObfuscated ? "bqj" : "net/minecraft/client/model/ModelPlayer", "modelPlayerAPI", "Lapi/player/model/ModelPlayerAPI;");

			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "api/player/model/ModelPlayerAPI", "beforeLocalConstructing", "(Lapi/player/model/IModelPlayerAPI;)V", false);
		}
	}

	@Override
	public void visitInsn(int opcode)
	{
		if(opcode == Opcodes.RETURN)
		{
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "api/player/model/ModelPlayerAPI", "afterLocalConstructing", "(Lapi/player/model/IModelPlayerAPI;)V", false);
		}
		super.visitInsn(opcode);
	}
}
