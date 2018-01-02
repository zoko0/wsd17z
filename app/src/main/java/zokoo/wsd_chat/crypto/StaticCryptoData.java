package zokoo.wsd_chat.crypto;

/**
 * Created by Piotyr-Szef on 17.12.2017.
 */

public class StaticCryptoData {
    static private byte[] priveteKey;
    static private byte[] publicKey;
    static private byte[] password;
    static private byte[] decodedData;

    public static byte[] getPublicKey() {
        return publicKey;
    }

    public static void setPublicKey(byte[] publicKey) {
        StaticCryptoData.publicKey = publicKey;
    }

    public static byte[] getPassword() {
        return password;
    }

    public static void setPassword(byte[] password) {
        StaticCryptoData.password = password;
    }

    public static byte[] getDecodedData() {
        return decodedData;
    }

    public static void setDecodedData(byte[] decodedData) {
        StaticCryptoData.decodedData = decodedData;
    }

    public static byte[] getPriveteKey() {
        return priveteKey;
    }

    public static void setPriveteKey(byte[] priveteKey) {
        StaticCryptoData.priveteKey = priveteKey;
    }
}
