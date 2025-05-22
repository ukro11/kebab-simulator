package kebab_simulator.test;

import KAGO_framework.control.ViewController;
import kebab_simulator.control.ProgramController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Test {

    private final String type;
    protected final Logger logger = LoggerFactory.getLogger(Test.class);
    protected static ViewController viewController;
    protected static ProgramController programController;

    public Test(String type) {
        this.type = type;
    }

    public static void setup(ViewController viewController) {
        Test.viewController = viewController;
        Test.programController = viewController.getProgramController();
    }

    protected void startTest(String description) {
        System.out.println("\n");
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("------------------------------------- [Kebab Simulator 2.0] -------------------------------------");
        System.out.printf("Starting %s:\n", this.type);
        System.out.println("   > " + description);
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}
