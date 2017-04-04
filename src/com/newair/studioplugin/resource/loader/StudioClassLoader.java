package com.newair.studioplugin.resource.loader;

import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLClassLoader;

public class StudioClassLoader extends URLClassLoader {
	
	public StudioClassLoader(URL[] urls) {
		super(urls);
	}

	public StudioClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public URL getResource(String name) {
		URL url = findResource(name);
		if (url == null) {
			url = super.getResource(name);
		}
		return url;
	}

	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> clasz = null;
		try {
			clasz = super.loadClass(name, resolve);
		} catch (Exception localException1) {
		}
		if ((clasz == null) && (name.startsWith("["))) {
			int index = name.indexOf("L");
			String str = name.substring(0, index);
			String componentClassName = name.substring(index + 1, name.length() - 1);
			int[] dimes = new int[str.length()];
			for (int i = 0; i < dimes.length; i++) {
				dimes[i] = 0;
			}
			try {
				Class<?> componentType = loadClass(componentClassName);
				clasz = Array.newInstance(componentType, dimes).getClass();
			} catch (Exception localException2) {
			}
		}

		if (clasz == null) {
			throw new ClassNotFoundException(name);
		}
		return clasz;
	}
}