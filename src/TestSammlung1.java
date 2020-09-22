import org.junit.*;

import static org.junit.Assert.*;
public class TestSammlung1{

    MethodsToTest methodsToTest = new MethodsToTest();

    @Ignore
    public void sumTest1(){
        assertEquals( 2, methodsToTest.sum(1,1));
    }

    @Test
    public void sumTest2(){
        assertEquals( 0, methodsToTest.sum(-1,1));
    }

    @Test
    public void sumTest3(){
        assertEquals( 0, methodsToTest.sum(0,1));
    }

    @Test
    public  void sumTest4(){
        assertEquals( 512, methodsToTest.sum(256,256));
    }

    @AfterClass
    public void sumTest5(){
        assertEquals( 3, methodsToTest.sum(1,1));
    }

}
