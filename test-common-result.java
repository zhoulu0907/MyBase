import com.cmsr.onebase.framework.common.pojo.CommonResult;

public class TestCommonResult {
    public static void main(String[] args) {
        CommonResult<String> result = CommonResult.success("Test");
        System.out.println("CommonResult test successful: " + result);
    }
}
