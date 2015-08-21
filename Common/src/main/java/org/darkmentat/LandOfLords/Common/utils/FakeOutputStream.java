package org.darkmentat.LandOfLords.Common.utils;

import java.io.IOException;
import java.io.OutputStream;



/*
 *  This is class for some very dirty hack. Protobuf classes can write delimited messages only to OutputStream,
 *  but akka cat send only byte arrays. So this class for applying next hack:
 *
 *  1. Create FakeOutputStream;
 *  2. Let some protobuf generated class write delimited messages to it;
 *  3. Reading from it written ByteArray
 *  4. Sending this array to akka tcp actor
 *  5. ???
 *  6. PROFIT?
 *
 */
public class FakeOutputStream extends OutputStream {
    private byte[] mArray;

    @Override public void write(int b) throws IOException {}
    @Override public void write(byte[] b, int off, int len) throws IOException {
        mArray = b;
    }

    public byte[] getByteArray(){
        return mArray;
    }
}
