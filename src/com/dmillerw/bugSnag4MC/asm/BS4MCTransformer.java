package com.dmillerw.bugSnag4MC.asm;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.dmillerw.bugSnag4MC.BS4MCCore;

public class BS4MCTransformer implements IClassTransformer {

	public static final int OBFUSCATED = 0;
	public static final int FML_MAPPING = 1;
	
	public static final String[] CLASS_CRASHREPORT = new String[] {"b", "net.minecraft.crash.CrashReport"};
	public static final String[] METHOD_TARGET_NAME = new String[] {"h", "populateEnvironment"};
	
	public static void log(String message) {
		System.out.println("[" + BS4MCCore.ID + "] " + message);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (name.equals(CLASS_CRASHREPORT[OBFUSCATED])) {
			transformCrashReport(name, bytes, true);
		} else if (name.equals(CLASS_CRASHREPORT[FML_MAPPING])) {
			transformCrashReport(name, bytes, false);
		}
		
		return bytes;
	}

	private byte[] transformCrashReport(String name, byte[] bytes, boolean obfuscated) {
		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		
		String targetMethod = "<init>"; // BECAUSE I CAN! THAT'S WHY!
		String targetDesc = "(Ljava/lang/String;Ljava/lang/Throwable;)V"; // Paramaters of String and Throwable, returns void
		String targetFieldDesc = "()V";
		
		Iterator<MethodNode> methods = cn.methods.iterator();
		while (methods.hasNext()) {
			MethodNode method = methods.next();
			int putIndex = -1;
			
			if (method.name.equals(targetMethod) && method.desc.equalsIgnoreCase(targetDesc)) {
				log("Found CrashReport constructor! Preparing to inject!");
			
				AbstractInsnNode currentNode = null;
				AbstractInsnNode targetNode = null;
				Iterator<AbstractInsnNode> nodes = method.instructions.iterator();
				int index = -1;
				
				while (nodes.hasNext()) {
					index++;
					currentNode = nodes.next();
					
					// If is invoking self method
					if (currentNode.getOpcode() == Opcodes.INVOKESPECIAL && currentNode.getType() == AbstractInsnNode.METHOD_INSN) {
						MethodInsnNode methodNode = (MethodInsnNode) currentNode;
						
						if (methodNode.name.equals(METHOD_TARGET_NAME[obfuscated ? OBFUSCATED : FML_MAPPING]) && methodNode.desc.equals(targetFieldDesc)) {
							targetNode = currentNode;
							putIndex = index;
							break;
						}
					}
				}
				
				if (targetNode == null || putIndex == -1) {
					log("Something went wrong! Abandoning class!");
				}
				
				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/dmillerw/bugSnag4MC/core/CrashReportHandler", "test", "()V"));
				method.instructions.insert(targetNode, inject);
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(writer);
        return writer.toByteArray();
	}
	
}
