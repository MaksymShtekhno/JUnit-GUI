import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestSammlung2{

    MethodsToTest methodsToTest = new MethodsToTest();

    @Test
    public void multTest1(){
        assertEquals( 1, methodsToTest.mult(1,1));
    }

    @Test
    public void multTest2(){
        assertEquals( 10, methodsToTest.mult(10,10));
    }

    @Test
    public void multTest3(){
        assertEquals( -100, methodsToTest.mult(-1,100));
    }

    @Ignore
    public void multTest4(){
        assertEquals( 10, methodsToTest.mult(1,0));
    }

    @Test
    public void multTest5(){
        assertEquals( 0, methodsToTest.mult(934592,0));
    }

}
