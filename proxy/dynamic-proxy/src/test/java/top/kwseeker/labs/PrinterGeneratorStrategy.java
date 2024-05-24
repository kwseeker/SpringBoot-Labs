package top.kwseeker.labs;

import net.sf.cglib.core.ClassGenerator;
import net.sf.cglib.core.DefaultGeneratorStrategy;

import java.io.FileOutputStream;
import java.io.IOException;

public class PrinterGeneratorStrategy extends DefaultGeneratorStrategy {

    @Override
    public byte[] generate(ClassGenerator cg) throws Exception {
        byte[] generate = super.generate(cg);   //这里获取的是字节码, 从内容可以看到搞了至少两层代理，TODO 有空再看细节
        try (FileOutputStream fos = new FileOutputStream("src/test/java/top/kwseeker/labs/CGLibProxy.class")) {
            fos.write(generate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return generate;
    }
}
