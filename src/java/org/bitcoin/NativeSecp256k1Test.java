package org.bitcoin;

import com.google.common.io.BaseEncoding;
import java.util.Arrays;
import java.math.BigInteger;
import javax.xml.bind.DatatypeConverter;
import static org.bitcoin.NativeSecp256k1Util.*;

/**
 * This class holds test cases defined for testing this library.
 */
public class NativeSecp256k1Test {

    //TODO improve comments/add more tests
    /**
      * This tests verify() for a valid signature
      */
    public static void testVerifyPos() throws AssertFailException{
        boolean result = false;
        byte[] data = BaseEncoding.base16().lowerCase().decode("CF80CD8AED482D5D1527D7DC72FCEFF84E6326592848447D2DC0B0E87DFC9A90".toLowerCase()); //sha256hash of "testing"
        byte[] sig = BaseEncoding.base16().lowerCase().decode("3044022079BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F817980220294F14E883B3F525B5367756C2A11EF6CF84B730B36C17CB0C56F0AAB2C98589".toLowerCase());
        byte[] pub = BaseEncoding.base16().lowerCase().decode("040A629506E1B65CD9D2E0BA9C75DF9C4FED0DB16DC9625ED14397F0AFC836FAE595DC53F8B0EFE61E703075BD9B143BAC75EC0E19F82A2208CAEB32BE53414C40".toLowerCase()); 

        result = NativeSecp256k1.verify( data, sig, pub);
        assertEquals( result, true , "testVerifyPos");
    }

    /**
      * This tests verify() for a non-valid signature
      */
    public static void testVerifyNeg() throws AssertFailException{
        boolean result = false;
        byte[] data = BaseEncoding.base16().lowerCase().decode("CF80CD8AED482D5D1527D7DC72FCEFF84E6326592848447D2DC0B0E87DFC9A91".toLowerCase()); //sha256hash of "testing"
        byte[] sig = BaseEncoding.base16().lowerCase().decode("3044022079BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F817980220294F14E883B3F525B5367756C2A11EF6CF84B730B36C17CB0C56F0AAB2C98589".toLowerCase());
        byte[] pub = BaseEncoding.base16().lowerCase().decode("040A629506E1B65CD9D2E0BA9C75DF9C4FED0DB16DC9625ED14397F0AFC836FAE595DC53F8B0EFE61E703075BD9B143BAC75EC0E19F82A2208CAEB32BE53414C40".toLowerCase()); 

        result = NativeSecp256k1.verify( data, sig, pub);
        //System.out.println(" TEST " + new BigInteger(1, resultbytes).toString(16));
        assertEquals( result, false , "testVerifyNeg");
    }

    /**
      * This tests secret key verify() for a valid secretkey
      */
    public static void testSecKeyVerifyPos() throws AssertFailException{
        boolean result = false;
        byte[] sec = BaseEncoding.base16().lowerCase().decode("67E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530".toLowerCase()); 

        result = NativeSecp256k1.secKeyVerify( sec );
        //System.out.println(" TEST " + new BigInteger(1, resultbytes).toString(16));
        assertEquals( result, true , "testSecKeyVerifyPos");
    }

    /**
      * This tests secret key verify() for a invalid secretkey
      */
    public static void testSecKeyVerifyNeg() throws AssertFailException{
        boolean result = false;
        byte[] sec = BaseEncoding.base16().lowerCase().decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF".toLowerCase());  

        result = NativeSecp256k1.secKeyVerify( sec );
        //System.out.println(" TEST " + new BigInteger(1, resultbytes).toString(16));
        assertEquals( result, false , "testSecKeyVerifyNeg");
    }

    /**
      * This tests public key create() for a valid secretkey
      */
    public static void testPubKeyCreatePos() throws AssertFailException{
        byte[] sec = BaseEncoding.base16().lowerCase().decode("67E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530".toLowerCase()); 

        byte[] resultArr = NativeSecp256k1.computePubkey( sec);
        String pubkeyString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( pubkeyString , "04C591A8FF19AC9C4E4E5793673B83123437E975285E7B442F4EE2654DFFCA5E2D2103ED494718C697AC9AEBCFD19612E224DB46661011863ED2FC54E71861E2A6" , "testPubKeyCreatePos");
    }

    /**
      * This tests public key create() for a invalid secretkey
      */
    public static void testPubKeyCreateNeg() throws AssertFailException{
       byte[] sec = BaseEncoding.base16().lowerCase().decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF".toLowerCase());  

       byte[] resultArr = NativeSecp256k1.computePubkey( sec);
       String pubkeyString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
       assertEquals( pubkeyString, "" , "testPubKeyCreateNeg");
    }

    /**
      * This tests sign() for a valid secretkey
      */
    public static void testSignPos() throws AssertFailException{

        byte[] data = BaseEncoding.base16().lowerCase().decode("CF80CD8AED482D5D1527D7DC72FCEFF84E6326592848447D2DC0B0E87DFC9A90".toLowerCase()); //sha256hash of "testing" 
        byte[] sec = BaseEncoding.base16().lowerCase().decode("67E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530".toLowerCase());  

        byte[] resultArr = NativeSecp256k1.sign(data, sec);
        String sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString, "30440220182A108E1448DC8F1FB467D06A0F3BB8EA0533584CB954EF8DA112F1D60E39A202201C66F36DA211C087F3AF88B50EDF4F9BDAA6CF5FD6817E74DCA34DB12390C6E9" , "testSignPos");
    }

    /**
      * This tests sign() for a invalid secretkey
      */
    public static void testSignNeg() throws AssertFailException{
        byte[] data = BaseEncoding.base16().lowerCase().decode("CF80CD8AED482D5D1527D7DC72FCEFF84E6326592848447D2DC0B0E87DFC9A90".toLowerCase()); //sha256hash of "testing" 
        byte[] sec = BaseEncoding.base16().lowerCase().decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF".toLowerCase()); 

        byte[] resultArr = NativeSecp256k1.sign(data, sec);
        String sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString, "" , "testSignNeg");
    }

    /**
      * This tests private key export
      */
    public static void testPrivKeyExportComp() throws AssertFailException{
        byte[] sec = BaseEncoding.base16().lowerCase().decode("67E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530".toLowerCase()); 

        byte[] resultArr = NativeSecp256k1.privKeyExport( sec , 1);
        String sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "3081D3020101042067E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530A08185308182020101302C06072A8648CE3D0101022100FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F300604010004010704210279BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798022100FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141020101A12403220002C591A8FF19AC9C4E4E5793673B83123437E975285E7B442F4EE2654DFFCA5E2D" , "Case 12");

    }

    /**
      * This tests private key export
      */
    public static void testPrivKeyExportUncomp() throws AssertFailException{
        byte[] sec = BaseEncoding.base16().lowerCase().decode("67E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530".toLowerCase()); 

        byte[] resultArr = NativeSecp256k1.privKeyExport( sec , 0);
        String sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "30820113020101042067E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530A081A53081A2020101302C06072A8648CE3D0101022100FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F300604010004010704410479BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8022100FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141020101A14403420004C591A8FF19AC9C4E4E5793673B83123437E975285E7B442F4EE2654DFFCA5E2D2103ED494718C697AC9AEBCFD19612E224DB46661011863ED2FC54E71861E2A6" , "Case 13");

    }

    /**
      * This tests private key import
      */
    public static void testSecKeyImportPos() throws AssertFailException {
        byte[] sec = BaseEncoding.base16().lowerCase().decode("3081D3020101042067E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530A08185308182020101302C06072A8648CE3D0101022100FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F300604010004010704210279BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798022100FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141020101A12403220002C591A8FF19AC9C4E4E5793673B83123437E975285E7B442F4EE2654DFFCA5E2D".toLowerCase()); 

        byte[] resultArr = NativeSecp256k1.secKeyImport( sec );
        String sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);

        assertEquals( sigString , "67E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530" , "testSecKeyImport");
    }

    /**
      * This tests private key export
      */
    public static void testSecKeyImportPos2() throws AssertFailException {
        byte[] sec = BaseEncoding.base16().lowerCase().decode("30820113020101042067E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530A081A53081A2020101302C06072A8648CE3D0101022100FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F300604010004010704410479BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8022100FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141020101A14403420004C591A8FF19AC9C4E4E5793673B83123437E975285E7B442F4EE2654DFFCA5E2D2103ED494718C697AC9AEBCFD19612E224DB46661011863ED2FC54E71861E2A6".toLowerCase()); 

        byte[] resultArr = NativeSecp256k1.secKeyImport( sec );
        String sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "67E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530" , "testSecKeyImport2");
    }

    /**
      * This tests private key tweak-add
      */
    public static void testPrivKeyTweakAdd_1() throws AssertFailException {
        byte[] sec = BaseEncoding.base16().lowerCase().decode("67E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530".toLowerCase()); 
        byte[] data = BaseEncoding.base16().lowerCase().decode("3982F19BEF1615BCCFBB05E321C10E1D4CBA3DF0E841C2E41EEB6016347653C3".toLowerCase()); //sha256hash of "tweak"

        byte[] resultArr = NativeSecp256k1.privKeyTweakAdd( sec , data );
        String sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "A168571E189E6F9A7E2D657A4B53AE99B909F7E712D1C23CED28093CD57C88F3" , "testPrivKeyAdd_1");
    }

    /**
      * This tests private key tweak-mul
      */
    public static void testPrivKeyTweakMul_1() throws AssertFailException {
        byte[] sec = BaseEncoding.base16().lowerCase().decode("67E56582298859DDAE725F972992A07C6C4FB9F62A8FFF58CE3CA926A1063530".toLowerCase()); 
        byte[] data = BaseEncoding.base16().lowerCase().decode("3982F19BEF1615BCCFBB05E321C10E1D4CBA3DF0E841C2E41EEB6016347653C3".toLowerCase()); //sha256hash of "tweak"

        byte[] resultArr = NativeSecp256k1.privKeyTweakMul( sec , data );
        String sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "97F8184235F101550F3C71C927507651BD3F1CDB4A5A33B8986ACF0DEE20FFFC" , "testPrivKeyMul_1");
    }

    /**
      * This tests private key tweak-add uncompressed
      */
    public static void testPrivKeyTweakAdd_2() throws AssertFailException {
        byte[] pub = BaseEncoding.base16().lowerCase().decode("040A629506E1B65CD9D2E0BA9C75DF9C4FED0DB16DC9625ED14397F0AFC836FAE595DC53F8B0EFE61E703075BD9B143BAC75EC0E19F82A2208CAEB32BE53414C40".toLowerCase()); 
        byte[] data = BaseEncoding.base16().lowerCase().decode("3982F19BEF1615BCCFBB05E321C10E1D4CBA3DF0E841C2E41EEB6016347653C3".toLowerCase()); //sha256hash of "tweak"

        byte[] resultArr = NativeSecp256k1.pubKeyTweakAdd( pub , data );
        String sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "0411C6790F4B663CCE607BAAE08C43557EDC1A4D11D88DFCB3D841D0C6A941AF525A268E2A863C148555C48FB5FBA368E88718A46E205FABC3DBA2CCFFAB0796EF" , "testPrivKeyAdd_2");
    }

    /**
      * This tests private key tweak-mul uncompressed
      */
    public static void testPrivKeyTweakMul_2() throws AssertFailException {
        byte[] pub = BaseEncoding.base16().lowerCase().decode("040A629506E1B65CD9D2E0BA9C75DF9C4FED0DB16DC9625ED14397F0AFC836FAE595DC53F8B0EFE61E703075BD9B143BAC75EC0E19F82A2208CAEB32BE53414C40".toLowerCase()); 
        byte[] data = BaseEncoding.base16().lowerCase().decode("3982F19BEF1615BCCFBB05E321C10E1D4CBA3DF0E841C2E41EEB6016347653C3".toLowerCase()); //sha256hash of "tweak"

        byte[] resultArr = NativeSecp256k1.pubKeyTweakMul( pub , data );
        String sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "04E0FE6FE55EBCA626B98A807F6CAF654139E14E5E3698F01A9A658E21DC1D2791EC060D4F412A794D5370F672BC94B722640B5F76914151CFCA6E712CA48CC589" , "testPrivKeyMul_2");
    }

    /**
      * This tests seed randomization
      */
    public static void testRandomize() throws AssertFailException {
        byte[] seed = BaseEncoding.base16().lowerCase().decode("A441B15FE9A3CF56661190A0B93B9DEC7D04127288CC87250967CF3B52894D11".toLowerCase()); //sha256hash of "random"
        boolean result = NativeSecp256k1.randomize(seed);
        assertEquals( result, true, "testRandomize");
    }

    public static void testRecover() throws AssertFailException {

        /* TODO update this with functions from include/secp256k1_recovery.h 
        //Case 17
        data = BaseEncoding.base16().lowerCase().decode("CF80CD8AED482D5D1527D7DC72FCEFF84E6326592848447D2DC0B0E87DFC9A91".toLowerCase()); //sha256hash of "testing"
        sig = BaseEncoding.base16().lowerCase().decode("A33C093C80B84CA1AFBC8974EE3C42FC1CBC966CAE66612593CD1E44646BABFF00CB69703B98B0103AE22C7F9CCADD8DD98F9505BE7A66B1AE459576E930C4F6".toLowerCase());
        pub = BaseEncoding.base16().lowerCase().decode("040A629506E1B65CD9D2E0BA9C75DF9C4FED0DB16DC9625ED14397F0AFC836FAE595DC53F8B0EFE61E703075BD9B143BAC75EC0E19F82A2208CAEB32BE53414C40".toLowerCase()); 
        int recid = 1;

        resultArr = NativeSecp256k1.recoverCompact( data , sig , 0, recid );
        sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "04C591A8FF19AC9C4E4E5793673B83123437E975285E7B442F4EE2654DFFCA5E2D2103ED494718C697AC9AEBCFD19612E224DB46661011863ED2FC54E71861E2A6" , "Case 17");

        resultArr = NativeSecp256k1.recoverCompact( data , sig , 1, recid );
        sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "02C591A8FF19AC9C4E4E5793673B83123437E975285E7B442F4EE2654DFFCA5E2D" , "Case 18");
        */
    }

    public static void testRecoverCompact() throws AssertFailException {

        /* TODO update this with functions from include/secp256k1_recovery.h 
        //Case 17
        data = BaseEncoding.base16().lowerCase().decode("CF80CD8AED482D5D1527D7DC72FCEFF84E6326592848447D2DC0B0E87DFC9A91".toLowerCase()); //sha256hash of "testing"
        sig = BaseEncoding.base16().lowerCase().decode("A33C093C80B84CA1AFBC8974EE3C42FC1CBC966CAE66612593CD1E44646BABFF00CB69703B98B0103AE22C7F9CCADD8DD98F9505BE7A66B1AE459576E930C4F6".toLowerCase());
        pub = BaseEncoding.base16().lowerCase().decode("040A629506E1B65CD9D2E0BA9C75DF9C4FED0DB16DC9625ED14397F0AFC836FAE595DC53F8B0EFE61E703075BD9B143BAC75EC0E19F82A2208CAEB32BE53414C40".toLowerCase()); 
        int recid = 1;

        resultArr = NativeSecp256k1.recoverCompact( data , sig , 0, recid );
        sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "04C591A8FF19AC9C4E4E5793673B83123437E975285E7B442F4EE2654DFFCA5E2D2103ED494718C697AC9AEBCFD19612E224DB46661011863ED2FC54E71861E2A6" , "Case 17");

        resultArr = NativeSecp256k1.recoverCompact( data , sig , 1, recid );
        sigString = javax.xml.bind.DatatypeConverter.printHexBinary(resultArr);
        assertEquals( sigString , "02C591A8FF19AC9C4E4E5793673B83123437E975285E7B442F4EE2654DFFCA5E2D" , "Case 18");
        */
    }

    public static void main(String[] args) throws AssertFailException{


      System.out.println("\n libsecp256k1 enabled: " + Secp256k1Context.isEnabled() + "\n");

      if( Secp256k1Context.isEnabled() ) {

        //Test verify() success/fail
        testVerifyPos();
        testVerifyNeg();

        //Test secKeyVerify() success/fail
        testSecKeyVerifyPos();
        testSecKeyVerifyNeg();

        //Test computePubkey() success/fail
        testPubKeyCreatePos(); 
        testPubKeyCreateNeg();

        //Test sign() success/fail
        testSignPos(); 
        testSignNeg(); 

        //Test privKeyExport() compressed/uncomp
        //testPrivKeyExportComp(); //Now in /contrib
        //testPrivKeyExportUncomp(); //Now in /contrib

        //Test secKeyImport()/2 
        //testSecKeyImportPos();  //Now in /contrib
        //testSecKeyImportPos2();  //Now in /contrib

        //Test recovery //TODO
        //testRecoverCompact();
        //testRecover();
        //testCreateRecoverable();

        //Test ECDH //TODO
        //testECDHSecretGen();

        //Test Schnorr (partial support) //TODO
        //testSchnorrSign
        //testSchnorrVerify
        //testSchnorrRecovery

        //Test pubkeyCombine //TODO
        //test pubkeyCombine

        //Test privKeyTweakAdd() 1
        testPrivKeyTweakAdd_1();

        //Test privKeyTweakMul() 2
        testPrivKeyTweakMul_1();

        //Test privKeyTweakAdd() 3
        testPrivKeyTweakAdd_2();

        //Test privKeyTweakMul() 4
        testPrivKeyTweakMul_2();

        //Test randomize()
        testRandomize();

        NativeSecp256k1.cleanup();

        System.out.println(" All tests passed." );

      }
    }
}
