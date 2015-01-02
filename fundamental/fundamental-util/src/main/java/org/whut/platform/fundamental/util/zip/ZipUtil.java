package org.whut.platform.fundamental.util.zip;

import org.whut.platform.fundamental.logger.PlatformLogger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-31
 * Time: 下午4:17
 * To change this template use File | Settings | File Templates.
 */
public class ZipUtil {
    private static final PlatformLogger logger = PlatformLogger.getLogger(ZipUtil.class);

    public static void upzip(String zipFilePath,String outDir){
        try {
            File file = new File(zipFilePath);//压缩文件
            ZipFile zipFile = new ZipFile(file);//实例化ZipFile，每一个zip压缩文件都可以表示为一个ZipFile
            //实例化一个Zip压缩文件的ZipInputStream对象，可以利用该类的getNextEntry()方法依次拿到每一个ZipEntry对象
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file), Charset.forName("UTF-8"));
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                File temp = new File(outDir + fileName);
                if (! temp.getParentFile().exists())
                    temp.getParentFile().mkdirs();
                OutputStream os = new FileOutputStream(temp);
                //通过ZipFile的getInputStream方法拿到具体的ZipEntry的输入流
                InputStream is = zipFile.getInputStream(zipEntry);
                int len = 0;
                while ((len = is.read()) != -1)
                    os.write(len);
                    os.close();
                    is.close();
            }
            zipInputStream.close();
        }catch (IOException ioe){
            logger.error(ioe.getMessage());
        }



    }
}
