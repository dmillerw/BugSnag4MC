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
	public static final String[] FIELD_DESC = new String[] {"field_71513_a", "description"};
	public static final String[] FIELD_THROW = new String[] {"field_71511_b", "cause"};
	public static final String[] METHOD_POPULATE = new String[] {"func_71504_g", "populateEnvironment"};
	public static final String[] METHOD_GET = new String[] {"func_71502_e", "getCompleteReport"};
	
	public static void log(String message) {
		System.out.println("[" + BS4MCCore.ID + "] " + message);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (name.equals(CLASS_CRASHREPORT[OBFUSCATED])) {
			return transformCrashReport(name, bytes, true);
		} else if (name.equals(CLASS_CRASHREPORT[FML_MAPPING])) {
			return transformCrashReport(name, bytes, false);
		}
		
		return bytes;
	}

	private byte[] transformCrashReport(String name, byte[] bytes, boolean obfuscated) {
		log("Transforming class: " + name);
		
		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		
		String constructorMethod = "<init>"; // BECAUSE I CAN! THAT'S WHY!
		String targetClassDesc = "(Ljava/lang/String;Ljava/lang/Throwable;)V"; // Paramaters of String and Throwable, returns void
		
		String stringDesc = ("Ljava/lang/String;");
		String throwDesc = ("Ljava/lang/Throwable;");
		
		Iterator<MethodNode> methods = cn.methods.iterator();
		while (methods.hasNext()) {
			MethodNode method = methods.next();
			
			if (method.name.equals(constructorMethod) && method.desc.equalsIgnoreCase(targetClassDesc)) {
				log("Found CrashReport constructor! Preparing to inject!");

				InsnList methodInstructions = method.instructions;
				
				for (int i=0; i<methodInstructions.size(); i++) {
					AbstractInsnNode node = methodInstructions.get(i);
					
					if (node.getOpcode() == Opcodes.INVOKESPECIAL && node.getType() == AbstractInsnNode.METHOD_INSN) {
						MethodInsnNode methodNode = (MethodInsnNode) node;
						
						if (methodNode.name.equals(METHOD_POPULATE[obfuscated ? OBFUSCATED : FML_MAPPING]) && methodNode.desc.equals("()V")) {
							log("Found target node! Preparing to inject!");
							
							methodInstructions.insert(new VarInsnNode(Opcodes.ALOAD, 0));
							methodInstructions.insert(new FieldInsnNode(Opcodes.GETFIELD, CLASS_CRASHREPORT[obfuscated ? OBFUSCATED : FML_MAPPING].replace(".", "/"), FIELD_DESC[obfuscated ? OBFUSCATED : FML_MAPPING], stringDesc));
							methodInstructions.insert(new VarInsnNode(Opcodes.ALOAD, 0));
							methodInstructions.insert(new FieldInsnNode(Opcodes.GETFIELD, CLASS_CRASHREPORT[obfuscated ? OBFUSCATED : FML_MAPPING].replace(".", "/"), FIELD_THROW[obfuscated ? OBFUSCATED : FML_MAPPING], throwDesc));
							methodInstructions.insert(new VarInsnNode(Opcodes.ALOAD, 0));
							methodInstructions.insert(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASS_CRASHREPORT[obfuscated ? OBFUSCATED : FML_MAPPING].replace(".", "/"), METHOD_GET[obfuscated ? OBFUSCATED : FML_MAPPING], stringDesc));
							methodInstructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/dmillerw/bugSnag4MC/core/CrashReportHandler", "handleCrashReport", "(Ljava/lang/String;Ljava/lang/Throwable;Ljava/lang/String;)V"));
							
							break;
						}
					}
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		cn.accept(writer);
		writer.visitEnd();
		return writer.toByteArray();
	}
	
}