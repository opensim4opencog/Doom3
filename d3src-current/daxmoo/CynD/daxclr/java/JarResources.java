package daxclr.java;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * JarResources: JarResources maps all resources included in a
 * Zip or Jar file. Additionaly, it provides a method to extract one
 * as a blob.
 */
public final class JarResources {

    // jar resource mapping tables
    private Hashtable<String, Object> htSizes = new Hashtable<String, Object>(),
                                htJarContents = new Hashtable<String, Object>();

    // a jar file
    private String jarFileName;


    /**
     * creates a JarResources. It extracts all resources from a Jar
     * into an internal hashtable, keyed by resource names.
     * @param jarFileName1 a jar or zip file
     */
    public JarResources(String jarFileName1) {
        this.jarFileName = jarFileName1;
        init();
    }


    /**
     * Extracts a jar resource as a blob.
     * @param name a resource name.
     */
    public byte[] getResource(String name) {
        return (byte[]) htJarContents.get(name);
    }


    /** initializes internal hash tables with Jar file resources.  */
    private void init() {
        try {
            // extracts just sizes only.
            ZipFile zf = new ZipFile(jarFileName);
            for (Enumeration e = zf.entries(); e.hasMoreElements(); ) {
                ZipEntry ze = (ZipEntry) e.nextElement();
                htSizes.put(ze.getName(), new Integer((int) ze.getSize()));
            }
            zf.close();

            // extract resources and put them into the hashtable.
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(jarFileName)));

            for (ZipEntry ze = null; (ze = zis.getNextEntry()) != null; ) {
                if (ze.isDirectory()) {
                    continue;
                }

                int size = (int) ze.getSize();
                if (size == -1) { // -1 means unknown size.
                    size = ((Integer) htSizes.get(ze.getName())).intValue();
                }

                byte[] b = new byte[size];
                int rb = 0,
                         chunk = 0;
                while (((int) size - rb) > 0) {
                    chunk = zis.read(b, rb, (int) size - rb);
                    if (chunk == -1) {
                        break;
                    }
                    rb += chunk;
                }

                // add to internal resource hashtable
                htJarContents.put(ze.getName(), b);

            }
        } catch (NullPointerException e) {
            //nothing, done
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Dumps a zip entry into a string.
     * @param ze a ZipEntry
     */
    @SuppressWarnings("unused")
	private String dumpZipEntry(ZipEntry ze) {
        StringBuffer sb = new StringBuffer();
        if (ze.isDirectory()) {
            sb.append("d ");
        } else {
            sb.append("f ");
        }

        if (ze.getMethod() == ZipEntry.STORED) {
            sb.append("stored   ");
        } else {
            sb.append("defalted ");
        }

        sb.append(ze.getName());
        sb.append("\t");
        sb.append("" + ze.getSize());
        if (ze.getMethod() == ZipEntry.DEFLATED) {
            sb.append("/" + ze.getCompressedSize());
        }

        return (sb.toString());
    }

}
