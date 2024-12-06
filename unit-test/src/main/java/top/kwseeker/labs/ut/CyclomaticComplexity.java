package top.kwseeker.labs.ut;

/**
 * JaCoCo Cyclomatic Complexity 圈复杂度测试
 * 测试报告显示这个类 info() 方法的圈复杂度是 9，怎么计算出来的？
 */
public class CyclomaticComplexity {

    public static String info(String name, int age, int gender) {
        StringBuilder info = new StringBuilder(name);
        info.append(": ");

        if (gender == 1) {
            info.append("male");
        } else if (gender == 2) {
            info.append("female");
        } else {
            info.append("unknown");
        }

        if (age <= 1) {
            info.append(", infant");
        } else if (age <= 12) {
            info.append(", child");
        } else if (age <= 18) {
            info.append(", teenager");
        } else if (age <= 35) {
            info.append(", young adult");
        } else if (age <= 65) {
            info.append(", middle age");
        } else {
            info.append(", senior");
        }

        return info.toString();
    }
}
