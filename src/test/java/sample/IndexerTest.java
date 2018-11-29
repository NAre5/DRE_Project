package sample;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

public class IndexerTest {

    @org.junit.Test
    public void intToBytes() {
//        int number = 128;
//        int num_of_bytes = 2;
        System.out.println(Arrays.toString(ByteBuffer.allocate(4).putInt(126).array()));
        assertEquals(Arrays.toString(new byte[]{0,126}),Arrays.toString(Indexer.intToBytes(126,2)));
        System.out.println(Arrays.toString(ByteBuffer.allocate(4).putInt(128).array()));
        assertEquals(Arrays.toString(new byte[]{0,-128}),Arrays.toString(Indexer.intToBytes(128,2)));
        System.out.println(Arrays.toString(ByteBuffer.allocate(4).putInt(256).array()));
        assertEquals(Arrays.toString(new byte[]{1,0}),Arrays.toString(Indexer.intToBytes(256,2)));
        System.out.println(Arrays.toString(ByteBuffer.allocate(4).putInt(65836).array()));
        assertEquals(Arrays.toString(new byte[]{1,1,44}),Arrays.toString(Indexer.intToBytes(65836,3)));
    }
}