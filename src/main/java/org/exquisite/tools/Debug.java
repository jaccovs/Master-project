package org.exquisite.tools;

/**
 * A ridiculous debug class
 *
 * @author Dietmar
 */
public class Debug {
    // Simply logs stuff to the console

    public static boolean DEBUGGING_ON = false;
    public static boolean QX_DEBUGGING = false;
    private static Object sync = new Object();

    /**
     * Print something to the console
     *
     * @param msg
     */
    public static void msg(String msg) {
        if (DEBUGGING_ON) {
            System.out.println(msg);
        }
    }

    public static void syncMsg(String msg) {
        synchronized (sync) {
            System.out.println(msg);
            System.out.flush();
        }
    }

}
