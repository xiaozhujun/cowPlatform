package org.whut.platform.fundamental.bcs;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.auth.BCSSignCondition;
import com.baidu.inf.iis.bcs.http.HttpMethodName;
import com.baidu.inf.iis.bcs.model.*;
import com.baidu.inf.iis.bcs.policy.Policy;
import com.baidu.inf.iis.bcs.policy.PolicyAction;
import com.baidu.inf.iis.bcs.policy.PolicyEffect;
import com.baidu.inf.iis.bcs.policy.Statement;
import com.baidu.inf.iis.bcs.request.*;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;
import org.whut.platform.fundamental.config.FundamentalConfigProvider;
import org.whut.platform.fundamental.logger.PlatformLogger;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-2-15
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
public class BcsProxy {

    private static final PlatformLogger logger = PlatformLogger.getLogger(BcsProxy.class);

    static String host = FundamentalConfigProvider.get("bae.bcs.host");
    static String accessKey =  FundamentalConfigProvider.get("bae.bcs.accessKey");
    static String secretKey =  FundamentalConfigProvider.get("bae.bcs.secretKey");
    static String bucket =  FundamentalConfigProvider.get("bae.bcs.bucket");
    // ----------------------------------------
    static String object = "/dir1/dir001/first-object2";
    static File destFile = new File("test");

    private static BaiduBCS baiduBCS;
    private static BaiduBCS iniBCS(){
        if(baiduBCS==null){
            synchronized(new Date()) {
                if(baiduBCS==null){
                    BCSCredentials credentials = new BCSCredentials(accessKey, secretKey);
                    baiduBCS = new BaiduBCS(credentials, host);
                    baiduBCS.setDefaultEncoding("UTF-8");
                }
            }
        }
        return baiduBCS;
    }

    //上传文件到百度云，并返回可以访问的云盘路径
    public static String uploadFile(File file,String destPath){
        iniBCS();
        PutObjectRequest request = new PutObjectRequest(bucket, destPath, file);
        request.setAcl(X_BS_ACL.PublicRead);
        ObjectMetadata metadata = new ObjectMetadata();
        request.setMetadata(metadata);
        BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putObject(request);
        ObjectMetadata objectMetadata = response.getResult();
        String webPath = "http://"+host+"/"+bucket+destPath;
        logger.info("x-bs-request-id: " + response.getRequestId());
        logger.info(objectMetadata.toString());
        logger.info("file path: "+webPath);
        return webPath;
    }

    //获取指定路径文件后缀
    private static String getSuffix(String filePath){
        if(filePath!=null&&!filePath.trim().equals("")){
            String[]  array = filePath.split("\\.");
            if(array.length>0){
                return  array[array.length-1];
            }
        }
        return "";
    }

    //将指定文件流写入临时文件
    private static File createTempFile(InputStream ins,String destPath){
        File tempFile = null;
        try{
            if(ins!=null&&destPath!=null){
                String suffix = getSuffix(destPath);
                tempFile = File.createTempFile("bcs-temp",suffix);

                OutputStream os = new FileOutputStream(tempFile);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                ins.close();
            }

        }catch (IOException ie){
            logger.error(ie.getMessage());
        }
        return tempFile;

    }

    //上传文件到百度云，并返回可以访问的云盘路径
    public static String uploadFile(InputStream inputStream,String destPath){
        iniBCS();
        File tempFile = createTempFile(inputStream,destPath);
        PutObjectRequest request = new PutObjectRequest(bucket, destPath,tempFile);
        request.setAcl(X_BS_ACL.PublicRead);
        BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putObject(request);
        ObjectMetadata objectMetadata = response.getResult();
        String webPath = "http://"+host+"/"+bucket+destPath;
        logger.info("x-bs-request-id: " + response.getRequestId());
        logger.info(objectMetadata.toString());
        logger.info("file path: "+webPath);
        return webPath;
    }

    /**
     * @param args
     * @throws URISyntaxException
     * @throws IOException
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
       iniBCS();
        try {
            // -------------bucket-------------
//            listBucket(baiduBCS);
            // createBucket(baiduBCS);
            // deleteBucket(baiduBCS);
            // getBucketPolicy(baiduBCS);
            // putBucketPolicyByPolicy(baiduBCS);
            // putBucketPolicyByX_BS_ACL(baiduBCS, X_BS_ACL.PublicControl);
            // listObject(baiduBCS);
            // ------------object-------------
            File file = new File("D:/照片/me/me.jpg");
            FileInputStream inputStream = new FileInputStream(file);
            uploadFile(inputStream,"/dir1/001/me2.jpg");
//            putObjectByFile(baiduBCS);
            // putObjectByInputStream(baiduBCS);
            // getObjectWithDestFile(baiduBCS);
            // putSuperfile(baiduBCS);
            // deleteObject(baiduBCS);
            // getObjectMetadata(baiduBCS);
            // setObjectMetadata(baiduBCS);
            // copyObject(baiduBCS, bucket, object + "_copy" +
            // (System.currentTimeMillis()));
            // getObjectPolicy(baiduBCS);
            // putObjectPolicyByPolicy(baiduBCS);
            // putObjectPolicyByX_BS_ACL(baiduBCS, X_BS_ACL.PublicControl);

            // ------------common------------------
            // generateUrl(BaiduBCS baiduBCS);
        } catch (BCSServiceException e) {
            logger.error("Bcs return:" + e.getBcsErrorCode() + ", " + e.getBcsErrorMessage() + ", RequestId=" + e.getRequestId());
        } catch (BCSClientException e) {
            e.printStackTrace();
        }
    }

    public static void generateUrl(BaiduBCS baiduBCS) {
        GenerateUrlRequest generateUrlRequest = new GenerateUrlRequest(HttpMethodName.GET, bucket, object);
        generateUrlRequest.setBcsSignCondition(new BCSSignCondition());
        generateUrlRequest.getBcsSignCondition().setIp("1.1.1.1");
        generateUrlRequest.getBcsSignCondition().setTime(123455L);
        generateUrlRequest.getBcsSignCondition().setSize(123455L);
        System.out.println(baiduBCS.generateUrl(generateUrlRequest));
    }

    public static void copyObject(BaiduBCS baiduBCS, String destBucket, String destObject) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/jpeg");
        baiduBCS.copyObject(new Resource(bucket, object), new Resource(destBucket, destObject), objectMetadata);
        baiduBCS.copyObject(new Resource(bucket, object), new Resource(destBucket, destObject), null);
        baiduBCS.copyObject(new Resource(bucket, object), new Resource(destBucket, destObject));
    }

    private static void createBucket(BaiduBCS baiduBCS) {
        // baiduBCS.createBucket(bucket);
        baiduBCS.createBucket(new CreateBucketRequest(bucket, X_BS_ACL.PublicRead));
    }

    private static void deleteBucket(BaiduBCS baiduBCS) {
        baiduBCS.deleteBucket(bucket);
    }

    public static void deleteObject(BaiduBCS baiduBCS) {
        Empty result = baiduBCS.deleteObject(bucket, object).getResult();
        logger.info(result.toString());
    }

    private static void getBucketPolicy(BaiduBCS baiduBCS) {
        BaiduBCSResponse<Policy> response = baiduBCS.getBucketPolicy(bucket);

        logger.info("After analyze: " + response.getResult().toJson());
        logger.info("Origianal str: " + response.getResult().getOriginalJsonStr());
    }

    public static void getObjectMetadata(BaiduBCS baiduBCS) {
        ObjectMetadata objectMetadata = baiduBCS.getObjectMetadata(bucket, object).getResult();
        logger.info(objectMetadata.toString());
    }

    private static void getObjectPolicy(BaiduBCS baiduBCS) {
        BaiduBCSResponse<Policy> response = baiduBCS.getObjectPolicy(bucket, object);
        logger.info("After analyze: " + response.getResult().toJson());
        logger.info("Origianal str: " + response.getResult().getOriginalJsonStr());
    }

    private static void getObjectWithDestFile(BaiduBCS baiduBCS) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, object);
        baiduBCS.getObject(getObjectRequest, destFile);
    }

    private static void listBucket(BaiduBCS baiduBCS) {
        ListBucketRequest listBucketRequest = new ListBucketRequest();
        BaiduBCSResponse<List<BucketSummary>> response = baiduBCS.listBucket(listBucketRequest);
        logger.info("bucket size: "+response.getResult().size());
        for (BucketSummary bucket : response.getResult()) {
            logger.info(bucket.toString());
        }
    }

    private static void listObject(BaiduBCS baiduBCS) {
        ListObjectRequest listObjectRequest = new ListObjectRequest(bucket);
        listObjectRequest.setStart(0);
        listObjectRequest.setLimit(20);
        // ------------------by dir
        {
            // prefix must start with '/' and end with '/'
            // listObjectRequest.setPrefix("/1/");
            // listObjectRequest.setListModel(2);
        }
        // ------------------only object
        {
            // prefix must start with '/'
            // listObjectRequest.setPrefix("/1/");
        }
        BaiduBCSResponse<ObjectListing> response = baiduBCS.listObject(listObjectRequest);
        logger.info("we get [" + response.getResult().getObjectSummaries().size() + "] object record.");
        for (ObjectSummary os : response.getResult().getObjectSummaries()) {
            logger.info(os.toString());
        }
    }

    private static void putBucketPolicyByPolicy(BaiduBCS baiduBCS) {
        Policy policy = new Policy();
        Statement st1 = new Statement();
        st1.addAction(PolicyAction.all).addAction(PolicyAction.get_object);
        st1.addUser("zhengkan").addUser("zhangyong01");
        st1.addResource(bucket + "/111").addResource(bucket + "/111");
        st1.setEffect(PolicyEffect.allow);
        policy.addStatements(st1);
        baiduBCS.putBucketPolicy(bucket, policy);
    }

    private static void putBucketPolicyByX_BS_ACL(BaiduBCS baiduBCS, X_BS_ACL acl) {
        baiduBCS.putBucketPolicy(bucket, acl);
    }

    public static void putObjectByFile(BaiduBCS baiduBCS) {
        PutObjectRequest request = new PutObjectRequest(bucket, object, createSampleFile());
        ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setContentType("text/html");
        request.setMetadata(metadata);
        BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putObject(request);
        ObjectMetadata objectMetadata = response.getResult();
        logger.info("x-bs-request-id: " + response.getRequestId());
        logger.info(objectMetadata.toString());
    }

    public static void putObjectByInputStream(BaiduBCS baiduBCS) throws FileNotFoundException {
        File file = createSampleFile();
        InputStream fileContent = new FileInputStream(file);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("text/html");
        objectMetadata.setContentLength(file.length());
        PutObjectRequest request = new PutObjectRequest(bucket, object, fileContent, objectMetadata);
        ObjectMetadata result = baiduBCS.putObject(request).getResult();
        logger.info(result.toString());
    }

    private static void putObjectPolicyByPolicy(BaiduBCS baiduBCS) {
        Policy policy = new Policy();
        Statement st1 = new Statement();
        st1.addAction(PolicyAction.all).addAction(PolicyAction.get_object);
        st1.addUser("zhengkan").addUser("zhangyong01");
        st1.addResource(bucket + object).addResource(bucket + object);
        st1.setEffect(PolicyEffect.allow);
        policy.addStatements(st1);
        baiduBCS.putObjectPolicy(bucket, object, policy);
    }

    private static void putObjectPolicyByX_BS_ACL(BaiduBCS baiduBCS, X_BS_ACL acl) {
        baiduBCS.putObjectPolicy(bucket, object, acl);
    }

    public static void putSuperfile(BaiduBCS baiduBCS) {
        List<SuperfileSubObject> subObjectList = new ArrayList<SuperfileSubObject>();
        // 0
        BaiduBCSResponse<ObjectMetadata> response1 = baiduBCS.putObject(bucket, object + "_part0", createSampleFile());
        subObjectList.add(new SuperfileSubObject(bucket, object + "_part0", response1.getResult().getETag()));
        // 1
        BaiduBCSResponse<ObjectMetadata> response2 = baiduBCS.putObject(bucket, object + "_part1", createSampleFile());
        subObjectList.add(new SuperfileSubObject(bucket, object + "_part1", response2.getResult().getETag()));
        // put superfile
        PutSuperfileRequest request = new PutSuperfileRequest(bucket, object + "_superfile", subObjectList);
        BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putSuperfile(request);
        ObjectMetadata objectMetadata = response.getResult();
        logger.info("x-bs-request-id: " + response.getRequestId());
        logger.info(objectMetadata.toString());
    }

    public static void setObjectMetadata(BaiduBCS baiduBCS) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("text/html12");
        baiduBCS.setObjectMetadata(bucket, object, objectMetadata);
    }

    private static File createSampleFile() {
        try {
            File file = File.createTempFile("java-sdk-", ".txt");
            file.deleteOnExit();

            Writer writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write("niuniu \n");
            writer.write("01234567890123456789\n");
            writer.write("01234567890123456789\n");
            writer.write("01234567890123456789\n");
            writer.write("01234567890123456789\n");
            writer.close();

            return file;
        } catch (IOException e) {
            logger.error("tmp file create failed.");
            return null;
        }
    }
}
