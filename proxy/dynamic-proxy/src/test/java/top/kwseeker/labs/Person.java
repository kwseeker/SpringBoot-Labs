package top.kwseeker.labs;

public class Person implements Human {

    private String name;

    public Person(String name) {
        this.name = name;
    }

    @Override
    public void introduce() {
        System.out.println("a person named " + name);
    }

    @Override
    public void work() {
        System.out.println("do some work");
    }

    public void outerMethod() {
        privateMethod();
        //protectedMethod();
        defaultMethod();
        staticMethod();
        staticProtectedMethod();
    }

    private void privateMethod() {
        System.out.println("exec privateMethod");
    }

    protected void protectedMethod() {
        System.out.println("exec protectedMethod");
    }

    protected void defaultMethod() {
        System.out.println("exec defaultMethod");
    }

    static void staticMethod() {
        System.out.println("exec staticMethod");
    }

    static protected void staticProtectedMethod() {
        System.out.println("exec staticProtectedMethod");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
