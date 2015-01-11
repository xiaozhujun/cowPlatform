package org.whut.platform.fundamental.util.zip;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.whut.platform.fundamental.logger.PlatformLogger;

import java.io.*;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-31
 * Time: 下午4:17
 * 利用Apache ant.jar中的zip包进行Zip压缩和解压
 */
public class ZipUtil {
    private static final PlatformLogger logger = PlatformLogger.getLogger(ZipUtil.class);

    /**
     * 压缩zip文件
     * @param file 压缩的文件对象
     * @param out 输出ZIP流
     * @param dir 相对父目录名称
     * @param boo 是否把空目录压缩进去
     */
    public static void zip(File file,ZipOutputStream out,String dir,boolean boo) throws IOException {

        if(file.isDirectory()){//是目录

            File []listFile = file.listFiles();//得出目录下所有的文件对象

            if(listFile.length == 0 && boo){//空目录压缩

                out.putNextEntry(new ZipEntry(dir + file.getName() + "/"));//将实体放入输出ZIP流中
                return;
            }else{

                for(File cfile: listFile){

                    zip(cfile,out,dir + file.getName() + "/",boo);//递归压缩
                }
            }

        }else if(file.isFile()){//是文件

           logger.info("压缩." + dir + file.getName() + "/");

            byte[] bt = new byte[2048*2];

            ZipEntry ze = new ZipEntry(dir+file.getName());//构建压缩实体
            //设置压缩前的文件大小
            ze.setSize(file.length());

            out.putNextEntry(ze);////将实体放入输出ZIP流中

            FileInputStream fis = null;

            try{

                fis = new FileInputStream(file);

                int i=0;

                while((i = fis.read(bt)) != -1) {//循环读出并写入输出Zip流中

                    out.write(bt, 0, i);
                }

            }catch(IOException ex){
                throw new IOException("写入压缩文件出现异常",ex);
            }finally{

                try{
                    if (fis != null)
                        fis.close();//关闭输入流

                }catch(IOException ex){

                    throw new IOException("关闭输入流出现异常");
                }

            }
        }

    }

    /**
     * 解压缩zipFile
     * @param file 要解压的zip文件对象
     * @param outputDir 要解压到某个指定的目录下
     * @throws IOException
     */
    public static void unzip(String file,String outputDir) throws IOException {


        ZipFile zipFile = null;

        try {

            zipFile =  new ZipFile(file);

            createDirectory(outputDir,null);//创建输出目录

            Enumeration<?> enums = zipFile.getEntries();

            while(enums.hasMoreElements()){

                ZipEntry entry = (ZipEntry) enums.nextElement();

                logger.info("解压." +  entry.getName());

                if(entry.isDirectory()){//是目录

                    createDirectory(outputDir,entry.getName());//创建空目录

                }else{//是文件

                    File tmpFile = new File(outputDir + "/" + entry.getName());

                    createDirectory(tmpFile.getParent() + "/",null);//创建输出目录

                    InputStream in = null;

                    OutputStream out = null;

                    try{
                        in = zipFile.getInputStream(entry);;

                        out = new FileOutputStream(tmpFile);

                        int length = 0;

                        byte[] b = new byte[2048];

                        while((length = in.read(b)) != -1){
                            out.write(b, 0, length);
                        }

                    }catch(IOException ex){
                        throw ex;
                    }finally{
                        if(in!=null)
                            in.close();
                        if(out!=null)
                            out.close();
                    }

                }

            }

        } catch (IOException e) {
            throw new IOException("解压缩文件出现异常",e);
        } finally{
            try{
                if(zipFile != null){
                    zipFile.close();
                }
            }catch(IOException ex){
                throw new IOException("关闭zipFile出现异常",ex);
            }
        }


    }

    /**
     * 构建目录
     * @param outputDir
     * @param subDir
     */
    public static void createDirectory(String outputDir,String subDir){

        File file = new File(outputDir);

        if(!(subDir == null || subDir.trim().equals(""))){//子目录不为空

            file = new File(outputDir + "/" + subDir);
        }

        if(!file.exists()){

            file.mkdirs();
        }

    }

    /**
     * 清理文件(目录或文件)
     * @param file
     */
    public static void deleteDirectory(File file){

        if(file.isFile()){

            file.delete();//清理文件
        }else{

            File list[] = file.listFiles();

            if(list!=null){

                for(File f: list){
                    deleteDirectory(f);
                }
                file.delete();//清理目录
            }

        }

    }

}
