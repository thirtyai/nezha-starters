/*
 * Copyright (c) kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirtyai.nezha.i18n;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.common.NezhaConstant;
import com.thirtyai.nezha.common.i18n.I18nWrapper;
import com.thirtyai.nezha.common.util.YamlUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

/**
 * i18n generator
 *
 * @author kyleju
 */
@RequiredArgsConstructor
@Slf4j
public class I18nGenerator {
	public static final String VM_LOAD_PATH_KEY = "resource.loader.file.class";
	public static final String VM_LOAD_PATH_VALUE = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";
	public static final String I18NS_CONFIGURATION_FILE_NAME = "I18nsConfiguration";
	@NonNull
	private final I18nGeneratorProperties properties;

	public void doGenerate() {
		doGenerate(null);
	}

	public void doGenerate(String moduleFolderName) {
		if (this.properties.lists.size() > 0) {
			this.properties.lists.forEach(i18nModule -> {
				Assert.notNull(i18nModule, "status generator is null.");
				Assert.notBlank(i18nModule.getClsPackage(), "clsPackage not set.");
				try {
					generateMainJava(i18nModule, moduleFolderName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			try {
				generateSprigFactories(this.properties.lists, moduleFolderName);
				generateConfigJava(this.properties.lists, moduleFolderName);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("i18 modules is empty.");
		}
	}

	private void generateSprigFactories(List<I18nGeneratorProperties.I18nModule> i18nModules, String moduleFolderName) throws IOException {
		String outputFolder = Paths.get(getClassPath(moduleFolderName), "/src/main/resources/META-INF/").toString();
		File dir = new File(outputFolder);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String outputUri = Paths.get(outputFolder, "spring.factories").toString();
		write(getObjects(i18nModules), "spring.factories.vm", outputUri);
	}

	private void generateConfigJava(List<I18nGeneratorProperties.I18nModule> i18nModules, String moduleFolderName) throws IOException {
		String outputFolder = Paths.get(getClassPath(moduleFolderName), "/src/main/java/" + i18nModules.get(0).getClsPackage().replace(".", "/")).toString();
		File dir = new File(outputFolder);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String outputUri = Paths.get(outputFolder, I18NS_CONFIGURATION_FILE_NAME + ".java").toString();

		write(getObjects(i18nModules), "i18nconfiguration.java.vm", outputUri);
	}

	private void generateMainJava(I18nGeneratorProperties.I18nModule i18nModule, String moduleFolderName) throws IOException {

		String outputFolder = Paths.get(getClassPath(moduleFolderName), "/src/main/java/" + i18nModule.getClsPackage().replace(".", "/")).toString();
		File dir = new File(outputFolder);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String outputUri = Paths.get(outputFolder, i18nModule.getClsName() + ".java").toString();
		write(getObjects(i18nModule), "status_template.vm", outputUri);
	}

	private void write(Map<String, Object> objectMap, String templateName, String outputUri) {
		Template templateObj = getTemplate(templateName);
		try (
			FileOutputStream fos = new FileOutputStream(outputUri);
			OutputStreamWriter ow = new OutputStreamWriter(fos, StandardCharsets.UTF_8.name());
			BufferedWriter writer = new BufferedWriter(ow)) {
			templateObj.merge(new VelocityContext(objectMap), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Template getTemplate(String templateName) {
		Properties p = new Properties();
		p.setProperty(VM_LOAD_PATH_KEY, VM_LOAD_PATH_VALUE);
		p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, "");
		p.setProperty(Velocity.ENCODING_DEFAULT, StandardCharsets.UTF_8.name());
		p.setProperty(Velocity.INPUT_ENCODING, StandardCharsets.UTF_8.name());
		p.setProperty("resource.loader.file.unicode", StandardCharsets.UTF_8.name());
		VelocityEngine velocityEngine = new VelocityEngine(p);
		return velocityEngine.getTemplate(templateName);
	}

	private Map<String, Object> getObjects(List<I18nGeneratorProperties.I18nModule> i18nModules) throws IOException {
		Map<String, Object> data = MapUtil.createMap(HashMap.class);
		if (i18nModules != null && i18nModules.size() > 0) {
			List<String> lists = new ArrayList<>();
			StrBuilder strBuilder = StrBuilder.create();
			data.put("clsPackage", i18nModules.get(0).getClsPackage());
			i18nModules.forEach(item -> {
				String temp = "import " + item.getClsPackage() + StrUtil.DOT + item.getClsName() + ";";
				if (!lists.contains(temp)) {
					lists.add("import " + item.getClsPackage() + StrUtil.DOT + item.getClsName() + ";");
					strBuilder.append("	" + "@Bean\n");
					strBuilder.append("	" + "public " + item.getClsName() + " init" + item.getClsName() + "() {" + " return " + item.getClsName() + ".self();" + " }\n");
				}
			});
			if (lists.size() >= 1) {
				lists.remove(0);
			}
			data.put("imports", String.join("\n", lists));
			data.put("beans", strBuilder.toString());
			data.put("i18nsConfigName", I18NS_CONFIGURATION_FILE_NAME);
		}
		return data;
	}

	private Map<String, Object> getObjects(I18nGeneratorProperties.I18nModule i18nModule) throws IOException {
		Map<String, Object> data = MapUtil.createMap(HashMap.class);
		data.put("clsPackage", i18nModule.getClsPackage());
		data.put("clsName", i18nModule.getClsName());
		data.put("defaultLanguage", i18nModule.getDefaultLanguage());
		data.put("resourceFolder", i18nModule.getResourceFolder());
		ClassPathResource resource = new ClassPathResource(i18nModule.getFullResourceFolder() + File.separator + "zh_CN" + NezhaConstant.NEZHA_YML_FILE_EXT_NAME);
		I18nWrapper i18nWrapper = YamlUtil.parse(resource.getStream(), I18nWrapper.class);
		if (i18nWrapper != null && i18nWrapper.i18ns != null && i18nWrapper.i18ns.size() > 0) {
			String objectInfo = "";
			String staticFieldInfo = "";
			int i = 1;
			for (I18nWrapper.I18nItem item : i18nWrapper.i18ns) {
				objectInfo += "	/**\n";
				objectInfo += "	 * " + item.getCode() + " = " + item.getDesc() + "\n";
				objectInfo += "	 */\n";

				objectInfo += "	public static final I18n " + StrUtil.upperFirst(item.getName()) + " = new " + i18nModule.getClsName() + "( \"" + StrUtil.upperFirst(item.getName()) + "\", \"" + item.getCode() + "\", \"" + item.getDesc() + "\" );\n";
				staticFieldInfo += "	/**\n";
				staticFieldInfo += "	 * " + item.getCode() + " = " + item.getDesc() + "\n";
				staticFieldInfo += "	 */\n";
				staticFieldInfo += "	" + "public static final String " + item.getName().toUpperCase() + "_NAME = \"" + StrUtil.upperFirst(item.getName()) + StrUtil.DOT + "\" + I18N_IDENTIFY_NAME;\n";
				i++;
			}
			data.put("objectsInfo", objectInfo);
			data.put("staticFieldsInfo", staticFieldInfo);
		}
		return data;
	}

	private String getClassPath(String rootPath) {
		String classPath = ClassUtil.getClassPath();
		if (StrUtil.isNotBlank(classPath)) {
			if (StrUtil.isNotBlank(rootPath)) {
				return FileUtil.getParent(FileUtil.file(classPath), 3).getPath() + File.separator + rootPath;
			} else {
				return FileUtil.getParent(FileUtil.file(classPath), 2).getPath();
			}
		}
		return "";
	}
}
