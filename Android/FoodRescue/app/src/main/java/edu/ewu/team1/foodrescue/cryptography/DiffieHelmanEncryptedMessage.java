package edu.ewu.team1.foodrescue.cryptography;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Note to reader:
 * The variable names in this class may seem to be against best practices,
 * however I assure you it is for a good reason.
 * All variables in this class are named to match up with the variables used in the
 * Diffie-Helman Protocol explanation, which can be found at
 * http://mathworld.wolfram.com/Diffie-HellmanProtocol.html
 *
 * These variable names do appear to be standardized across all Diffie-Helman reference material
 */
public class DiffieHelmanEncryptedMessage implements Runnable{

    /*
    3072-bit MODP Group
    This group is assigned id 15.
    This prime is: 2^3072 - 2^3008 - 1 + 2^64 * { [2^2942 pi] + 1690314 }
    The generator is: 2.

    So as per http://mathworld.wolfram.com/Diffie-HellmanProtocol.html
    g = 2
    p = The big lad right below
    */
    private static final BigInteger p = new BigInteger(
            "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1" +
                    "29024E088A67CC74020BBEA63B139B22514A08798E3404DD" +
                    "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245" +
                    "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED" +
                    "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D" +
                    "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F" +
                    "83655D23DCA3AD961C62F356208552BB9ED529077096966D" +
                    "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B" +
                    "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9" +
                    "DE2BCBF6955817183995497CEA956AE515D2261898FA0510" +
                    "15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64" +
                    "ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7" +
                    "ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B" +
                    "F12FFA06D98A0864D87602733EC86A64521F2B18177B200C" +
                    "BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31" +
                    "43DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF", 16);
    private static final BigInteger generator = new BigInteger("2", 10);
    private static final int bits = 540;
    private RequestQueue queue;
    private String payload;
    private BigInteger a;
    private BigInteger A;
    private BigInteger B;
    private BigInteger K;
    private static String ENCRYPTION_ALGORITHM = "AES";

    public DiffieHelmanEncryptedMessage(Context context, String payload){
        queue = Volley.newRequestQueue(context);
        this.payload = payload;
    }

    private static BigInteger getRandomBits(SecureRandom r) {
        BigInteger candidate = new BigInteger(bits, r);
        while (candidate.equals(BigInteger.ZERO)) {
            candidate = new BigInteger(bits, r);
        }
        return candidate;
    }

    //This code will run Async from main thread
    //run() -> askForB() -> calculateK() -> encryptPayload() -> sendMessage()
    @Override
    public void run() {
        a = getRandomBits(new SecureRandom());
        A = generator.modPow(a, p);
        //This code will run async from this thread
        askForB();
    }

    private void askForB() {
        String url = "bradstephensoncode.org/FoodRescue/DiffieHelman1.php?A="+A.toString(16);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        B = new BigInteger(response.getString("B"), 16);
                        calculateK();
                    } catch (JSONException error) {
                        Log.e("Crypto Exchange Error:", error.toString());
                    }
                },
                error -> Log.e("Crypto Exchange Error:", error.toString())
        );
        queue.add(jsonRequest);
    }

    private void calculateK(){
        K = B.modPow(a, p);
    }

    private void entryptPayload(){
        try {
            byte[] keyBytes = K.toString(16).getBytes();
            Key key = new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = cipher.doFinal(payload.getBytes());

        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException |
                IllegalBlockSizeException |
                BadPaddingException error) {
            Log.e("Crypto Exchange Error:", error.toString());
        }
    }

}
