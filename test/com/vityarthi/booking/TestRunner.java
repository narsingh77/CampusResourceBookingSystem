package com.vityarthi.booking;

/**
 * Standalone test runner executing all suites.
 */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("*************************************************************");
        System.out.println("  STARTING AUTOMATED TEST RUNNER: CampusResourceHub         ");
        System.out.println("*************************************************************");

        try {
            BookingServiceTest.runAllTests();
            ConcurrencyBookingTest.runAllTests();

            System.out.println("\n=======================================================");
            System.out.println("  ALL TEST SUITES EXECUTED SUCCESSFULLY! (5/5 PASSED)  ");
            System.out.println("=======================================================\n");
        } catch (Throwable t) {
            System.err.println("\n[TEST SUITE FAILURE]: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }
}
