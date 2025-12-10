package cmsr.controller;

import cmsr.common.util.R;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class OssController {

    private static final Logger log = LoggerFactory.getLogger(OssController.class);

    @Autowired(required = false)
    private OSS ossClient;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String enpoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${spring.cloud.alicloud.secret-key}")
    private String accessKeySecret;

    @RequestMapping("/oss/policy")
    public R policy() {

        ossClient = new OSSClient(enpoint, accessId, accessKeySecret);

        //https://gulimall-gsp.oss-cn-shanghai.aliyuncs.com/p1.jpg
        String host = "https://" + bucket + "." + enpoint;
        String formatDate = new SimpleDateFormat("yyy-MM-dd").format(new Date());
        String dir = formatDate + "/";
        LinkedHashMap<String, String> resMap = null;
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConditions = new PolicyConditions();
            policyConditions.addConditionItem(PolicyConditions.COND_CACHE_CONTROL, 0, 1048576000);
            policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConditions);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);


            resMap = new LinkedHashMap<>();
            resMap.put("accessid", accessId);
            resMap.put("policy", encodedPolicy);
            resMap.put("signature", postSignature);
            resMap.put("dir", dir);
            resMap.put("host", host);
            resMap.put("expire", String.valueOf(expireEndTime / 1000));


        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return R.ok().put("data", resMap);
    }


    @RequestMapping("/oss/upload")
    public R upload(@RequestBody Map map) throws FileNotFoundException {

        System.out.println(map);

        OSS ossClient = new OSSClientBuilder().build(enpoint, accessId, accessKeySecret);

        InputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\image\\pf.jpg");

        PutObjectResult putObjectResult = ossClient.putObject("gulimall-gsp", "pf.jpg", inputStream);

        ossClient.shutdown();

        System.out.println("上传成功...");

        return R.ok();
    }
}
