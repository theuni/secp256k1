package org.bitcoin;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.math.BigInteger;
import com.google.common.base.Preconditions;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static org.bitcoin.NativeSecp256k1Util.*;

/**
 * This class holds native methods to handle ECDSA verification.
 * You can find an example library that can be used for this at
 * https://github.com/sipa/secp256k1
 */
//TODO fixup of travis test errors https://travis-ci.org/bitcoin/secp256k1/jobs/79025947
public class NativeSecp256k1 {

    private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static final Lock r = rwl.readLock();
    private static final Lock w = rwl.writeLock();
    private static ThreadLocal<ByteBuffer> nativeECDSABuffer = new ThreadLocal<ByteBuffer>();
    /**
     * Verifies the given secp256k1 signature in native code.
     * Calling when enabled == false is undefined (probably library not loaded)
     * 
     * @param data The data which was signed, must be exactly 32 bytes
     * @param signature The signature
     * @param pub The public key which did the signing
     */
    public static boolean verify(byte[] data, byte[] signature, byte[] pub) throws AssertFailException{
        Preconditions.checkArgument(data.length == 32 && signature.length <= 520 && pub.length <= 520);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(520);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(data);
        byteBuff.put(signature);
        byteBuff.put(pub);

        byte[][] retByteArray;

        r.lock();
        try {
          boolean result = secp256k1_ecdsa_verify(byteBuff, Secp256k1Context.getContext(), signature.length, pub.length) == 1;
          return result;
        } finally {
          r.unlock();
        }
    }

    /**
     * recover the given secp256k1 pubkey in native code.
     * 
     * @param data The data which was signed, must be exactly 32 bytes
     * @param signature The signature
     * @param compressed whether to recover a compressed pubkey
     * @param pub The public key which did the signing
     */
    //TODO recoverCompact()
    public static byte[] recoverCompact(byte[] data, byte[] signature,int compressed, int recID) throws AssertFailException{
        Preconditions.checkArgument(data.length == 32 && signature.length == 64 && (compressed == 0 || compressed == 1));

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(32 + 64);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(data);
        byteBuff.put(signature);

        byte[][] retByteArray = null;//secp256k1_ecdsa_recover_compact(byteBuff, Secp256k1Context, compressed, recID);

        byte[] pubArr = retByteArray[0];
        int pubLen = new BigInteger(new byte[] { retByteArray[1][0] }).intValue();
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(pubArr.length,pubLen, "Got bad signature length." );

        assertEquals(retVal,1, "Failed return value check.");

        return pubArr;
    }

    /**
     * libsecp256k1 Create an ECDSA signature. 
     * 
     * @param data Message hash, 32 bytes
     * @param key Secret key, 32 bytes
     * 
     * Return values
     * @param sig byte array of signature
     */
    
    public static byte[] sign(byte[] data, byte[] sec) throws AssertFailException{
        Preconditions.checkArgument(data.length == 32 && sec.length <= 32);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(32 + 32);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(data);
        byteBuff.put(sec);

        byte[][] retByteArray;

        r.lock();
        try {
          retByteArray = secp256k1_ecdsa_sign(byteBuff, Secp256k1Context.getContext());
        } finally {
          r.unlock();
        }

        byte[] sigArr = retByteArray[0];
        int sigLen = new BigInteger(new byte[] { retByteArray[1][0] }).intValue();
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(sigArr.length,sigLen, "Got bad signature length." );

        if( retVal == 0 ) sigArr = new byte[0];

        return sigArr;
    } 

    /**
     * libsecp256k1 Seckey Verify - returns 1 if valid, 0 if invalid
     * 
     * @param seckey ECDSA Secret key, 32 bytes
     */
    
    public static boolean secKeyVerify(byte[] seckey) {
        Preconditions.checkArgument(seckey.length == 32);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(seckey.length);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(seckey);

        r.lock();
        try {
          return secp256k1_ec_seckey_verify(byteBuff,Secp256k1Context.getContext()) == 1;
        } finally {
          r.unlock();
        }
    } 


    /**
     * libsecp256k1 Compute Pubkey - computes public key from secret key
     * 
     * @param seckey ECDSA Secret key, 32 bytes
     * @param compressed 1 to return compressed key, 0 for uncompressed
     * 
     * Return values
     * @param pubkey ECDSA Public key, 33 or 65 bytes
     */
    
    //TODO support 'compressed' arg
    public static byte[] computePubkey(byte[] seckey) throws AssertFailException{
        Preconditions.checkArgument(seckey.length == 32);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(seckey.length);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(seckey);

        byte[][] retByteArray;

        r.lock();
        try {
          retByteArray = secp256k1_ec_pubkey_create(byteBuff, Secp256k1Context.getContext());
        } finally {
          r.unlock();
        }

        byte[] pubArr = retByteArray[0];
        int pubLen = new BigInteger(new byte[] { retByteArray[1][0] }).intValue();
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(pubArr.length,pubLen, "Got bad pubkey length." );

        if( retVal == 0 ) pubArr = new byte[0];

        return pubArr;
    } 

    /**
     * libsecp256k1 Cleanup - This destroys the secp256k1 context object
     * This should be called at the end of the program for proper cleanup of the context.
     */
    public static synchronized void cleanup() {
        w.lock();
        try {
          secp256k1_destroy_context(Secp256k1Context.getContext());
        } finally {
          w.unlock();
        }
    }

    /**
     * libsecp256k1 Secret Key Import - Import a secret key in DER format.
     * 
     * @param seckey DER Sec key
     * @param compressed Compressed format
     */
    public static byte[] secKeyImport(byte[] seckey) throws AssertFailException{

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(seckey.length);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(seckey);

        byte[][] retByteArray;

        r.lock();
        try {
          retByteArray = secp256k1_ec_privkey_import(byteBuff,Secp256k1Context.getContext(), seckey.length);
        } finally {
          r.unlock();
        }

        byte[] privArr = retByteArray[0];

        int privLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(privArr.length,privLen, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return privArr;
    } 

    /**
     * libsecp256k1 Private Key Export - Export a private key in DER format.
     * 
     * @param seckey ECDSA Sec key, 33 or 65 bytes
     * @param compressed Compressed format
     */
    public static byte[] privKeyExport(byte[] privkey, int compressed) throws AssertFailException{
        Preconditions.checkArgument(privkey.length == 32 && (compressed == 0 || compressed == 1) );

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(privkey.length);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(privkey);

        byte[][] retByteArray;
        r.lock();
        try {
          retByteArray = secp256k1_ec_privkey_export(byteBuff,Secp256k1Context.getContext(), privkey.length, compressed );
        } finally {
          r.unlock();
        }

        byte[] privArr = retByteArray[0];

        int privLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(privArr.length, compressed == 1? 214 : 279, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return privArr;
    } 

    public static long cloneContext() {
       r.lock();
       try {
        return secp256k1_ctx_clone(Secp256k1Context.getContext());
       } finally { r.unlock(); }
    }

    /**
     * libsecp256k1 PrivKey Tweak-Mul - Tweak privkey by multiplying to it
     * 
     * @param tweak some bytes to tweak with
     * @param seckey 32-byte seckey
     */
    public static byte[] privKeyTweakMul(byte[] privkey, byte[] tweak) throws AssertFailException{
        Preconditions.checkArgument(privkey.length == 32);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(privkey.length + tweak.length);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(privkey);
        byteBuff.put(tweak);

        byte[][] retByteArray;
        r.lock();
        try {
          retByteArray = secp256k1_privkey_tweak_mul(byteBuff,Secp256k1Context.getContext());
        } finally {
          r.unlock();
        }

        byte[] privArr = retByteArray[0];

        int privLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(privArr.length, privLen, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return privArr;
    }

    /**
     * libsecp256k1 PrivKey Tweak-Add - Tweak privkey by adding to it
     * 
     * @param tweak some bytes to tweak with
     * @param seckey 32-byte seckey
     */
    public static byte[] privKeyTweakAdd(byte[] privkey, byte[] tweak) throws AssertFailException{
        Preconditions.checkArgument(privkey.length == 32);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(privkey.length + tweak.length);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(privkey);
        byteBuff.put(tweak);

        byte[][] retByteArray;
        r.lock();
        try {
          retByteArray = secp256k1_privkey_tweak_add(byteBuff,Secp256k1Context.getContext());
        } finally {
          r.unlock();
        }

        byte[] privArr = retByteArray[0];

        int privLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(privArr.length, privLen, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return privArr;
    }

    /**
     * libsecp256k1 PubKey Tweak-Add - Tweak pubkey by adding to it
     * 
     * @param tweak some bytes to tweak with
     * @param pubkey 32-byte seckey
     */
    public static byte[] pubKeyTweakAdd(byte[] pubkey, byte[] tweak) throws AssertFailException{
        Preconditions.checkArgument(pubkey.length == 33 || pubkey.length == 65);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(pubkey.length + tweak.length);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(pubkey);
        byteBuff.put(tweak);

        byte[][] retByteArray;
        r.lock();
        try {
          retByteArray = secp256k1_pubkey_tweak_add(byteBuff,Secp256k1Context.getContext(), pubkey.length);
        } finally {
          r.unlock();
        }

        byte[] pubArr = retByteArray[0];

        int pubLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(pubArr.length, pubLen, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return pubArr;
    }

    /**
     * libsecp256k1 PubKey Tweak-Mul - Tweak pubkey by multiplying to it
     * 
     * @param tweak some bytes to tweak with
     * @param pubkey 32-byte seckey
     */
    public static byte[] pubKeyTweakMul(byte[] pubkey, byte[] tweak) throws AssertFailException{
        Preconditions.checkArgument(pubkey.length == 33 || pubkey.length == 65);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(pubkey.length + tweak.length);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(pubkey);
        byteBuff.put(tweak);

        byte[][] retByteArray;
        r.lock();
        try {
          retByteArray = secp256k1_pubkey_tweak_mul(byteBuff,Secp256k1Context.getContext(), pubkey.length);
        } finally {
          r.unlock();
        }

        byte[] pubArr = retByteArray[0];

        int pubLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(pubArr.length, pubLen, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return pubArr;
    }

    /**
     * libsecp256k1 create ECDH secret - constant time ECDH calculation
     * 
     * @param seckey byte array of secret key used in exponentiaion
     * @param pubkey byte array of public key used in exponentiaion
     */
    //TODO schnorrSign, schnoorVerify, schnorrRecover()
    public static byte[] schnoorOps(byte[] pubkey, byte[] msg32) throws AssertFailException{
/*
        Preconditions.checkArgument(msg32.length == 32);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(pubkeys.length * 65);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(pubkey);

        byte[][] retByteArray;
        r.lock();
        try {
          retByteArray = secp256k1_ecdsa_recover(byteBuff,Secp256k1Context.getContext(), pubkey.length);
        } finally {
          r.unlock();
        }

        byte[] pubArr = retByteArray[0];

        int pubLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(pubArr.length, pubLen, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return pubArr;*/
  return new byte[0];
    }

    /**
     * libsecp256k1 create ECDH secret - constant time ECDH calculation
     * 
     * @param seckey byte array of secret key used in exponentiaion
     * @param pubkey byte array of public key used in exponentiaion
     */
    //TODO createECDHSecret()
    public static byte[] createECDHSecret(byte[] pubkey, byte[] msg32) throws AssertFailException{
/*
        Preconditions.checkArgument(msg32.length == 32);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(pubkeys.length * 65);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(pubkey);

        byte[][] retByteArray;
        r.lock();
        try {
          retByteArray = secp256k1_ecdsa_recover(byteBuff,Secp256k1Context.getContext(), pubkey.length);
        } finally {
          r.unlock();
        }

        byte[] pubArr = retByteArray[0];

        int pubLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(pubArr.length, pubLen, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return pubArr;
*/
  return new byte[0];
    }

    /**
     * libsecp256k1 createRecoverableSig - create recoverable signature
     * 
     * @param seckey byte array of secret key used in signing
     * @param msg32 32-byte signed message hash 
     */
    //TODO createRecoverableSig()
    public static byte[] createRecoverableSig(byte[] pubkey, byte[] msg32) throws AssertFailException{
/*
        Preconditions.checkArgument(msg32.length == 32);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(pubkeys.length * 65);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(pubkey);

        byte[][] retByteArray;
        r.lock();
        try {
          retByteArray = secp256k1_ecdsa_recover(byteBuff,Secp256k1Context.getContext(), pubkey.length);
        } finally {
          r.unlock();
        }

        byte[] pubArr = retByteArray[0];

        int pubLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(pubArr.length, pubLen, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return pubArr;
*/
  return new byte[0];
    }

    /**
     * libsecp256k1 Recover pubkey - recover pubkey from signature
     * 
     * @param sig byte array of signature
     * @param msg32 32-byte signed message hash 
     */
    //TODO recoverPubkey()
    public static byte[] recoverPubkey(byte[] pubkey, byte[] msg32) throws AssertFailException{


  return new byte[0];
/*
        Preconditions.checkArgument(msg32.length == 32);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(pubkeys.length * 65);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(pubkey);

        byte[][] retByteArray;
        r.lock();
        try {
          retByteArray = secp256k1_ecdsa_recover(byteBuff,Secp256k1Context.getContext(), pubkey.length);
        } finally {
          r.unlock();
        }

        byte[] pubArr = retByteArray[0];

        int pubLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(pubArr.length, pubLen, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return pubArr;
*/
    }

    /**
     * libsecp256k1 PubKey combine - add pubkeys together
     * 
     * @param pubkeys byte array of pubkeys
     * @param numKeys number of pubkeys to add
     */
    //TODO pubkeyCombine
    public static byte[] pubKeyCombine(byte[][] pubkeys, int numKeys) throws AssertFailException{
/*
        Preconditions.checkArgument(pubkeys.length > 0);
        for(int i = 0; i < pubkeys.length; i++) Preconditions.checkArgument(pubkeys[i].length == 65 || pubkeys[i].length == 33);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(pubkeys.length * 65);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(pubkey);

        byte[][] retByteArray;
        r.lock();
        try {
          retByteArray = secp256k1_pubkey_combine(byteBuff,Secp256k1Context.getContext(), pubkey.length);
        } finally {
          r.unlock();
        }

        byte[] pubArr = retByteArray[0];

        int pubLen = (byte) new BigInteger(new byte[] { retByteArray[1][0] }).intValue() & 0xFF;
        int retVal = new BigInteger(new byte[] { retByteArray[1][1] }).intValue();

        assertEquals(pubArr.length, pubLen, "Got bad pubkey length." );

        assertEquals(retVal,1, "Failed return value check.");

        return pubArr;
*/
  return new byte[0];
    }

    /**
     * libsecp256k1 randomize - updates the context randomization
     * 
     * @param seed 32-byte random seed
     */
    public static synchronized boolean randomize(byte[] seed) throws AssertFailException{
        Preconditions.checkArgument(seed.length == 32 || seed == null);

        ByteBuffer byteBuff = nativeECDSABuffer.get();
        if (byteBuff == null) {
            byteBuff = ByteBuffer.allocateDirect(seed.length);
            byteBuff.order(ByteOrder.nativeOrder());
            nativeECDSABuffer.set(byteBuff);
        }
        byteBuff.rewind();
        byteBuff.put(seed);

        w.lock();
        try {
          return secp256k1_context_randomize(byteBuff, Secp256k1Context.getContext()) == 1;
        } finally {
          w.unlock();
        }
    }

    private static native long secp256k1_ctx_clone(long context);

    private static native int secp256k1_context_randomize(ByteBuffer byteBuff, long context);

    private static native byte[][] secp256k1_privkey_tweak_add(ByteBuffer byteBuff, long context);

    private static native byte[][] secp256k1_privkey_tweak_mul(ByteBuffer byteBuff, long context);

    private static native byte[][] secp256k1_pubkey_tweak_add(ByteBuffer byteBuff, long context, int pubLen);

    private static native byte[][] secp256k1_pubkey_tweak_mul(ByteBuffer byteBuff, long context, int pubLen);

    private static native void secp256k1_destroy_context(long context);

    private static native int secp256k1_ecdsa_verify(ByteBuffer byteBuff, long context, int sigLen, int pubLen);

    private static native byte[][] secp256k1_ecdsa_sign(ByteBuffer byteBuff, long context);

    private static native int secp256k1_ec_seckey_verify(ByteBuffer byteBuff, long context);

    private static native byte[][] secp256k1_ec_pubkey_create(ByteBuffer byteBuff, long context);

    private static native byte[][] secp256k1_ec_privkey_export(ByteBuffer byteBuff, long context, int privLen, int compressed);

    private static native byte[][] secp256k1_ec_privkey_import(ByteBuffer byteBuff, long context, int privLen);

    private static native byte[][] secp256k1_ecdsa_signature_parse_der(ByteBuffer byteBuff, long context, int inputLen);

    //TODO sigNormalize()
    private static native byte[][] secp256k1_ecdsa_signature_normalize(ByteBuffer byteBuff, long context);

    private static native byte[][] secp256k1_ec_pubkey_parse(ByteBuffer byteBuff, long context, int inputLen);

    private static native long secp256k1_ecdsa_pubkey_combine(ByteBuffer byteBuff, long context, int keys);
}
