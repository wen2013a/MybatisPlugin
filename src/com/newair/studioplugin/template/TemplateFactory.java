package com.newair.studioplugin.template;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;

import com.newair.studioplugin.StudioActivator;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * 模板工厂类
 */
public class TemplateFactory {

	private static final Logger log = Logger.getLogger(TemplateFactory.class);

	private static TemplateFactory factory = getFactory();

	private Configuration tagcfg; // 标签模板配置

	private TemplateFactory() {
		/*URL url = this.getClass().getResource("/");
		try {
			url = FileLocator.toFileURL(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String path = "";
		String[] ss = url.getPath().split("/");
		for (int i = 0; i < ss.length - 1; i++) {
			if (StringUtils.isBlank(ss[i])) {
				continue;
			}
			if (StringUtils.isBlank(path)) {
				path = ss[i];
			} else {
				path = path + "/" + ss[i]; 
			}
		}
		path = path + "/" + "template";
		String path = StudioActivator.getResourcePath() + "template"; */
		tagcfg = new Configuration();
		try {
			URL url = StudioActivator.getDefault().getBundle().getResource("template");
			String resourcesPath = FileLocator.toFileURL(url).getPath();
			File resourcesDir = new File(resourcesPath);
			log.debug("模板文件路径：" + resourcesPath);
			tagcfg.setDirectoryForTemplateLoading(resourcesDir);
			tagcfg.setObjectWrapper(new DefaultObjectWrapper());
			tagcfg.setClassicCompatible(true);  //解决变量必须有值的问题
			tagcfg.setDefaultEncoding("UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("初始化模板错误！" + e.getMessage());
			log.error(e.getStackTrace());
		}
	}

	private static synchronized TemplateFactory myGetFactory() {
		if (factory == null) {
			factory = new TemplateFactory();
		}
		return factory;
	}

	private Template myGetTagTemplate(String templatename) throws Exception {
		Template temp = tagcfg.getTemplate(templatename);
		return temp;
	}

	public static TemplateFactory getFactory() {
		if (factory == null) {
			factory = myGetFactory();
		}
		return factory;
	}

	public static Template getTagTemplate(String tagname) throws Exception {
		try {
			Template template = factory.myGetTagTemplate(tagname);
			template.setEncoding("UTF-8");
			return template;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("获取模板错误！" + e.getMessage());
			log.error(e.getStackTrace());
		}
		return null;
	}
}
