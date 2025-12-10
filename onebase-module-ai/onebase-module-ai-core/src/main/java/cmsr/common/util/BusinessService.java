package cmsr.common.util;

public class BusinessService {

    @Loggable(value = "[DEBUG]", trackTime = true)
    @RequiresRole({"ADMIN", "MANAGER","USER"})
    public void sensitiveOperation() {
        System.out.println("Performing sensitive operation...");
    }

    @Loggable
    public void normalOperation() {
        System.out.println("Performing normal operation...");
    }

}
