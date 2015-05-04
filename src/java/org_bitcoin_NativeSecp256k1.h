/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_bitcoin_NativeSecp256k1 */

#ifndef _Included_org_bitcoin_NativeSecp256k1
#define _Included_org_bitcoin_NativeSecp256k1
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_bitcoin_NativeSecp256k1
 * Method:    secp256k1_init_context
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_bitcoin_NativeSecp256k1_secp256k1_1init_1context
  (JNIEnv *, jclass);

/*
 * Class:     org_bitcoin_NativeSecp256k1
 * Method:    secp256k1_destroy_context
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_bitcoin_NativeSecp256k1_secp256k1_1destroy_1context
  (JNIEnv *, jclass, jlong);

/*
 * Class:     org_bitcoin_NativeSecp256k1
 * Method:    secp256k1_ecdsa_verify
 * Signature: (Ljava/nio/ByteBuffer;J)I
 */
JNIEXPORT jint JNICALL Java_org_bitcoin_NativeSecp256k1_secp256k1_1ecdsa_1verify
  (JNIEnv *, jclass, jobject, jlong);

/*
 * Class:     org_bitcoin_NativeSecp256k1
 * Method:    secp256k1_ecdsa_sign
 * Signature: (Ljava/nio/ByteBuffer;J)[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_bitcoin_NativeSecp256k1_secp256k1_1ecdsa_1sign
  (JNIEnv *, jclass, jobject, jlong);

/*
 * Class:     org_bitcoin_NativeSecp256k1
 * Method:    secp256k1_ec_seckey_verify
 * Signature: (Ljava/nio/ByteBuffer;J)I
 */
JNIEXPORT jint JNICALL Java_org_bitcoin_NativeSecp256k1_secp256k1_1ec_1seckey_1verify
  (JNIEnv *, jclass, jobject, jlong);

/*
 * Class:     org_bitcoin_NativeSecp256k1
 * Method:    secp256k1_ec_pubkey_verify
 * Signature: (Ljava/nio/ByteBuffer;J)I
 */
JNIEXPORT jint JNICALL Java_org_bitcoin_NativeSecp256k1_secp256k1_1ec_1pubkey_1verify
  (JNIEnv *, jclass, jobject, jlong);

/*
 * Class:     org_bitcoin_NativeSecp256k1
 * Method:    secp256k1_ec_pubkey_create
 * Signature: (Ljava/nio/ByteBuffer;J)[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_bitcoin_NativeSecp256k1_secp256k1_1ec_1pubkey_1create
  (JNIEnv *, jclass, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif
