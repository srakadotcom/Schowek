package pl.memexurer.mschowek.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class BinaryUtils {
    private BinaryUtils() {
        ;
    }

    public static Map<Integer, Integer> readIntegerMap(byte[] data) throws IOException {
        Map<Integer, Integer> schowki = new HashMap<>();

        try(DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data))) {
            int size = dataInputStream.readByte();
            for(int i = 0; i < size; i++)
                schowki.put((int) dataInputStream.readByte(), dataInputStream.readInt());
        }

        return schowki;
    }

    public static byte[] write(Map<Integer, Integer> integerMap) throws IOException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(baos)) {
            outputStream.writeByte(integerMap.size());
            for(Map.Entry<Integer, Integer> entry: integerMap.entrySet()) {
                outputStream.writeByte(entry.getKey());
                outputStream.writeInt(entry.getValue());
            }

            return baos.toByteArray();
        }
    }
}
