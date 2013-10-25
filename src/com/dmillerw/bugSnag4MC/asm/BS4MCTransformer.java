package com.dmillerw.bugSnag4MC.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.CheckClassAdapter;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class BS4MCTransformer implements IClassTransformer {

	public static final String targetClassName = "com/dmillerw/bugSnag4MC/core/CrashReportHandler";
	public static final String targetClassType = "L" + targetClassName + ";";
	public static final String targetMethodName = "handleCrashReport";
	public static final String targetMethodType = "(%s%s%s)V";
	
	public HashMap<String, String> typeMap = new HashMap<String, String>();
	public HashMap<String, String> srgMappings = new HashMap<String, String>();
	
	private FMLDeobfuscatingRemapper mapper;
	
	public boolean obfuscated;
	
	public BS4MCTransformer() {
		srgMappings.put("description", "field_71513_a");
		srgMappings.put("cause", "field_71511_b");
		srgMappings.put("populateEnvironment", "func_71504_g");
		srgMappings.put("getCompleteReport", "func_71502_e");
		
		this.mapper = FMLDeobfuscatingRemapper.INSTANCE;
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if ("net.minecraft.crash.CrashReport".equals(transformedName)) {
			this.obfuscated = !name.equals(transformedName);
			return transformCrashReport(name, bytes);
		}
		
		return bytes; // If fails for whatever reason, just return original class
	}

	private byte[] transformCrashReport(String name, byte[] bytes) {
		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		
		MethodNode targetNode = null;
		
		// Fill field type mappings
		for (FieldNode field : cn.fields) {
			String srgName = getNodeSrgName(name, field);
		
			if (getMappedName("description") == srgName || getMappedName("cause") == srgName) {
				this.typeMap.put(srgName, field.desc);
			}
		}
		
		// Either find target, or store method type mappings
		for (MethodNode method : cn.methods) {
			String srgName = getNodeSrgName(name, method);
			
			if (method.name.equals("<init>")) {
				targetNode = method;
			} else if (getMappedName("getCompleteReport").equals(srgName)) {
				this.typeMap.put(srgName, method.desc);
			}
		}
		
		// Finally, go back to target and inject
//		String descriptionType = this.typeMap.get(getMappedName("description"));
		String descriptionType = "Ljava/lang/String;";
//		String causeType = this.typeMap.get(getMappedName("cause"));
		String causeType = "Ljava/lang/Throwable;";
		String getFullType = this.typeMap.get(getMappedName("getCompleteReport"));
		
		int index = 0;
		
		// While not at target
		while (!isMethodWithName(targetNode.instructions.get(index), "populateEnvironment")) {
			++index;
		}
		
		// Once at target
		String ownerName = name.replace(".", "/");
		InsnList injectionList = new InsnList();
		
//		injectionList.insert(new VarInsnNode(ALOAD, 0));
//		injectionList.insert(new FieldInsnNode(GETFIELD, ownerName, getMappedName("description"), descriptionType));
//		injectionList.insert(new VarInsnNode(ALOAD, 0));
//		injectionList.insert(new FieldInsnNode(GETFIELD, ownerName, getMappedName("cause"), causeType));
		
		// Apparently have to fill the injection list backwards. Not even going to question it.
		injectionList.insert(new MethodInsnNode(INVOKESTATIC, targetClassName, targetMethodName, String.format(targetMethodType, descriptionType, causeType, descriptionType)));
		injectionList.insert(new MethodInsnNode(INVOKEVIRTUAL, ownerName, getMappedName("getCompleteReport"), getFullType));
		injectionList.insert(new VarInsnNode(ALOAD, 0));
		injectionList.insert(new VarInsnNode(ALOAD, 2)); // Get second constructor param (Throwable)
		injectionList.insert(new VarInsnNode(ALOAD, 1)); // Get first constructor param (String)
		
		// Inject instructions into target method
		targetNode.instructions.insert(targetNode.instructions.get(index), injectionList);
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		
		return cw.toByteArray();
	}
	
	private String getNodeSrgName(MethodInsnNode node) {
		return mapper.mapMethodName(node.owner, node.name, node.desc);
	}
	
	private String getNodeSrgName(String className, MethodNode node) {
		return mapper.mapMethodName(className, node.name, node.desc);
	}
	
	private String getNodeSrgName(String className, FieldNode node) {
		return mapper.mapFieldName(className, node.name, node.desc);
	}
	
	private boolean isMethodWithName(AbstractInsnNode node, String name) {
		if (node.getType() == AbstractInsnNode.METHOD_INSN) {
			String srgName = getNodeSrgName((MethodInsnNode) node);
			return srgName.equals(getMappedName(name));
		}
		
		return false;
	}
	
	private String getMappedName(String name) {
		if (this.obfuscated && this.srgMappings.containsKey(name)) {
			return srgMappings.get(name);
		}
		
		return name;
	}
	
}