package top.kwseeker.labs;

public interface Human {

    /**
     * 介绍
     */
    void introduce();

    void work();

    default void alive() {
        System.out.println("exec alive");
    }
}
