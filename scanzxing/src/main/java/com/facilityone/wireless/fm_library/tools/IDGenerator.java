package com.facilityone.wireless.fm_library.tools;

/**
 * ID产生器
 */
public class IDGenerator {
    private static int seq = 0;
    private static long currentTime = 0;

    /**
     * 修改该方法为static类型，防止产生重复id
     * @return
     */
    synchronized public static String generatIdBySystemtime() {
        long t1 = System.currentTimeMillis();
        if (Math.abs(Math.abs(t1) - Math.abs(currentTime)) < 1) {
            if ((seq + 1) >= 100) {
                seq = 0;
            }
            seq++;
        }
        StringBuilder ID = new StringBuilder();
        ID.append(t1);
        ID.append(seq);
        long id = Long.parseLong(ID.toString());
        currentTime = t1;
        return id + "";
    }
}
