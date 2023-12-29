package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.jupiter.api.Assertions.*;

class HelloSpELTest {

    private static ExpressionParser parser;

    @BeforeAll
    public static void init() {
        parser = new SpelExpressionParser();
    }

    //文字表达式 ------------------------------------------------------------------
    
    @Test
    public void test1() {
        Expression expr1 = parser.parseExpression("'Hello SpEL!'");
        assertEquals("Hello SpEL!", expr1.getValue());
    }

    //方法调用 ------------------------------------------------------------------

    @Test
    public void test2() {
        Expression expr2 = parser.parseExpression(" 'Hello SpEL'.concat('!') ");
        System.out.println("expr2 value: " + expr2.getValue());
        String str2 = "Hello SpEL".concat("!");
        System.out.println("str2 value: " + str2);
        assertEquals(str2, expr2.getValue());
    }

    //访问属性 ------------------------------------------------------------------

    @Test
    public void test3() {
        //String没有bytes属性,只有getBytes()方法，或许SpEL是通过Getter方法访问的属性
        Expression expr3 = parser.parseExpression(" 'Hello SpEL'.bytes.length ");
        Expression expr31 = parser.parseExpression(" 'Hello SpEL'.getBytes().length ");
        assertEquals(10, expr3.getValue(Integer.class));
        assertEquals(10, expr31.getValue(Integer.class));
    }

    @Test
    public void test31() {
        //String没有bytes属性,只有getBytes()方法，或许SpEL是通过Getter方法访问的属性
        Expression expr3 = parser.parseExpression(" 'Hello SpEL'.bytes.length ");
        Expression expr31 = parser.parseExpression(" 'Hello SpEL'.getBytes().length ");
        assertEquals(10, expr3.getValue(Integer.class));
        assertEquals(10, expr31.getValue(Integer.class));
    }

    //调用构造方法 ------------------------------------------------------------------

    //读取一个不存在的属性，会转换成调用其getter方法
    //比如：这里访问 student 这个boolean类型属性，但是实际根对象中并不存在这个属性，会调用isStudent()方法
    @Test
    public void test4() {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Person arvin = new Person("Arvin", 18);
        context.setRootObject(arvin);
        
        //根对象arvin中不存在student属性，实际会调用isStudent()方法
        Expression expr = parser.parseExpression("student");
        Boolean isStudent = expr.getValue(context, Boolean.class);
        
        assertEquals(Boolean.FALSE, isStudent);
    }

    //除了“java.lang”包下的类其他所有类都需要全限定名
    @Test
    public void test4_1() {
        assertThrows(SpelEvaluationException.class, () -> {
            Expression expr = parser.parseExpression(" new Person('Arvin', 18).name ");
            System.out.println("expr value: " + expr.getValue());
        });
    }

    @Test
    public void test4_2() {
        Expression expr = parser.parseExpression(" new top.kwseeker.spring.expression.example01.HelloSpELTest.Person('Arvin', 18).name ");
        assertEquals("Arvin", expr.getValue());
    }

    //也可以通过rootObject访问自定义类的方法
    @Test
    public void test4_3() {
        Person person = new Person("Arvin", 18);
        Expression exp = parser.parseExpression("name"); // Parse name as an expression

        String name = (String) exp.getValue(person);
        assertEquals("Arvin", name);

        exp = parser.parseExpression("name == 'Arvin'");
        Boolean result = exp.getValue(person, Boolean.class);
        
        assertNotNull(result);
        assertTrue(result);
    }

    static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean isStudent() {
            System.out.println("calling isStudent()");
            return false;
        }
    }
}