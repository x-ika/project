package ika.games.admin.util;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class JarUnloadTest {

    private static final class DynamicLoader extends URLClassLoader {
        public DynamicLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        public void addURL(URL url) {
            if (!Arrays.asList(getURLs()).contains(url)) {
                super.addURL(url);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Class[] clazz = new Class[99];
        int z = 0;
        URLConnection c1 = null;
        URLConnection c2 = null;

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = in.readLine()) != null) {
            try {
                if (line.equals("1")) {
                    if (c1 != null) {
                        ((JarURLConnection) c1).getJarFile().close();
                        ((JarURLConnection) c2).getJarFile().close();
                    }
                    URL jarUrl1 = new URL("jar:file:D:\\ika\\projects\\Gelwins\\Games\\build\\tester\\m.jar!/");
                    URL jarUrl2 = new URL("jar:file:D:\\ika\\projects\\Gelwins\\Games\\build\\tester\\zz.jar!/");
                    c1 = jarUrl1.openConnection();
                    c2 = jarUrl2.openConnection();

                    DynamicLoader classLoader = new DynamicLoader(new URL[0], ClassLoader.getSystemClassLoader());
                    classLoader.addURL(jarUrl1);
                    classLoader.addURL(jarUrl2);
                    clazz[z++] = classLoader.loadClass("m.M");
                }
                if (line.startsWith("p")) {
                    clazz[Integer.parseInt(line.split(" ")[1])].getMethods()[0].invoke(null);
                }
                if (line.equals("c")) {
                    Arrays.fill(clazz, null);
                    z = 0;
                    System.gc();
                }
                if (line.equals("i")) {
                    JarUnloadTest.printMemoryUsage();
                }
                if (line.equals("o")) {
                    ((JarURLConnection) c1).getJarFile().close();
                    ((JarURLConnection) c2).getJarFile().close();
                    System.out.println("closed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void printMemoryUsage() {
        long t = Runtime.getRuntime().totalMemory() >> 20;
        long f = Runtime.getRuntime().freeMemory() >> 20;
        System.out.println(t + " " + f);
    }

}
