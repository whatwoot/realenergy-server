package com.cs.energy.evm.api.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.cs.sp.common.WebAssert.expect;

/**
 * @author dolyw.com
 * @date 2018/8/9 15:45
 */
@Service
public class EthSignUtil {

    private static final Logger log = LoggerFactory.getLogger(EthSignUtil.class);
    public static final String WRAP_LINE = "\n";

    private static final String MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    public static void checkFullSign(String addr, String full, String sign) {
        boolean signValid = EthSignUtil.isSignatureValid(addr, sign, full);
        if (!signValid) {
            log.info("CheckSign failed {}.\n{}=>{}", addr, full, sign);
        }
        // 时间误差
        expect(signValid, "chk.invalid.sign");
    }

    public static String verifyMessage(String message, String signature) {
        return recoverAddress(hashMessage(message), signature);
    }

    public static String hashMessage(String message) {
        return Hash.sha3(
                Numeric.toHexStringNoPrefix(
                        (MESSAGE_PREFIX + message.length() + message).getBytes(StandardCharsets.UTF_8)));
    }

    public static String recoverAddress(String digest, String signature) {
        Sign.SignatureData signatureData = getSignatureData(signature);
        int header = 0;
        for (byte b : signatureData.getV()) {
            header = (header << 8) + (b & 0xFF);
        }
        if (header < 27 || header > 34) {
            return null;
        }

        int recId = header - 27;
        BigInteger key = Sign.recoverFromSignature(
                recId,
                new ECDSASignature(
                        new BigInteger(1, signatureData.getR()), new BigInteger(1, signatureData.getS())),
                Numeric.hexStringToByteArray(digest));
        if (key == null) {
            return null;
        }
        return ("0x" + Keys.getAddress(key)).trim();
    }

    private static Sign.SignatureData getSignatureData(String signature) {
        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }
        byte[] r = (byte[]) Arrays.copyOfRange(signatureBytes, 0, 32);
        byte[] s = (byte[]) Arrays.copyOfRange(signatureBytes, 32, 64);
        return new Sign.SignatureData(v, r, s);
    }

    public static boolean isSignatureValid(final String address, final String signature, final String message) {
        log.debug("isSignatureValid invoked for Address {} with Signature {} and Message {} ", address, signature,
                message);
        if (address == null || address.trim().length() == 0) {
            return false;
        }
        return address.equalsIgnoreCase(verifyMessage(message, signature));
    }

    public static String sign(String priv, Type... values) {
        String encodeMsg = TypeEncoder.encode(new DynamicStruct(values));
        byte[] bytes = Numeric.hexStringToByteArray(Hash.sha3(String.format("0x%s", encodeMsg)));
        Credentials credentials = Credentials.create(priv);
        // eq with solidity keccak256(byte32)
        Sign.SignatureData signatureData = Sign.signPrefixedMessage(bytes, credentials.getEcKeyPair());
        // 将签名数据转换为十六进制字符串
        return String.format("0x%s%s%s", Numeric.toHexStringNoPrefix(signatureData.getR()),
                Numeric.toHexStringNoPrefix(signatureData.getS()),
                Numeric.toHexStringNoPrefix(signatureData.getV()));
    }

    public static String getToken(String salt, String contract, String wallet) {
        String encode = TypeEncoder.encode(new DynamicStruct(
                new Utf8String(salt),
                new Address(contract),
                new Address(wallet)
        ));
        return Hash.sha3(String.format("0x%s", encode));
    }

    // 通用的 abi.encode 实现
    public static String abiEncode(Type... parameters) {
        StringBuilder result = new StringBuilder();
        for (Type parameter : parameters) {
            result.append(TypeEncoder.encode(parameter));
        }
        return result.toString();
    }


    public static byte[] encodePackedAddressArray(List<Address> addresses) {
        byte[] result = new byte[addresses.size() * 32]; // 每个address编码为32字节
        for (int i = 0; i < addresses.size(); i++) {
            byte[] addrBytes = Numeric.hexStringToByteArray(addresses.get(i).getValue());
            System.arraycopy(addrBytes, 0, result, i * 32 + (32 - addrBytes.length), addrBytes.length);
        }
        return result;
    }

    public static String encodeDynamicArr(DynamicArray arr) {
        String encode = FunctionEncoder.encode(new Function(
                "f",
                Arrays.asList(arr),
                Collections.emptyList()));
        return encode.substring(10);
    }

    public static byte[] encodePackedAddressArray(String[] addresses) {
        byte[] result = new byte[addresses.length * 32]; // 每个address编码为32字节
        for (int i = 0; i < addresses.length; i++) {
            byte[] addrBytes = Numeric.hexStringToByteArray(addresses[i]);
            System.arraycopy(addrBytes, 0, result, i * 32 + (32 - addrBytes.length), addrBytes.length);
        }
        return result;
    }

    public static byte[] encodePackedUint256Array(List<Uint256> amounts) {
        byte[] result = new byte[amounts.size() * 32]; // 每个uint256为32字节
        for (int i = 0; i < amounts.size(); i++) {
            byte[] amountBytes = amounts.get(i).getValue().toByteArray();
            if (amountBytes.length > 32) {
                amountBytes = Arrays.copyOfRange(amountBytes, amountBytes.length - 32, amountBytes.length);
            }
            System.arraycopy(amountBytes, 0, result, i * 32 + (32 - amountBytes.length), amountBytes.length);
        }
        return result;
    }

    // 编码uint256[]数组为abi.encodePacked格式
    public static byte[] encodePackedUint256Array(BigInteger[] amounts) {
        byte[] result = new byte[amounts.length * 32]; // 每个uint256为32字节
        for (int i = 0; i < amounts.length; i++) {
            byte[] amountBytes = amounts[i].toByteArray();
            if (amountBytes.length > 32) {
                amountBytes = Arrays.copyOfRange(amountBytes, amountBytes.length - 32, amountBytes.length);
            }
            System.arraycopy(amountBytes, 0, result, i * 32 + (32 - amountBytes.length), amountBytes.length);
        }
        return result;
    }

    public static byte[] encodeEip191Header(byte[] domainSeparator, byte[] structHash) {
        // 1. 将\x19\x01转换为字节数组
        byte[] prefix = new byte[]{0x19, 0x01};

        // 2. 拼接三个部分（不添加任何额外字节）
        byte[] result = new byte[prefix.length + domainSeparator.length + structHash.length];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(domainSeparator, 0, result, prefix.length, domainSeparator.length);
        System.arraycopy(structHash, 0, result, prefix.length + domainSeparator.length, structHash.length);
        return result;
    }

    // 通用的 abi.encodePacked 实现
    public static String abiEncodePacked(Object... parameters) {
        List<byte[]> byteArrays = new ArrayList<>();

        for (Object param : parameters) {
            if (param == null) {
                continue;
            }

            if (param instanceof String) {
                // 字符串直接编码为UTF-8字节
                byteArrays.add(((String) param).getBytes(StandardCharsets.UTF_8));
            } else if (param instanceof byte[]) {
                // 字节数组直接使用
                byteArrays.add((byte[]) param);
            } else if (param instanceof Address) {
                // 地址类型
                byteArrays.add(Numeric.hexStringToByteArray(((Address) param).getValue()));
            } else if (param instanceof Bool) {
                // 布尔类型
                byteArrays.add(new byte[]{((Bool) param).getValue() ? (byte) 1 : (byte) 0});
            } else if (param instanceof NumericType) {
                // 数值类型 (uint/int)
                BigInteger value = ((NumericType) param).getValue();
                byteArrays.add(encodeNumeric(value));
            } else if (param instanceof Type) {
                // 其他web3j类型
                byteArrays.add(TypeEncoder.encode((Type) param).getBytes(StandardCharsets.UTF_8));
            } else if (param instanceof Number) {
                // Java原生数值类型
                if (param instanceof Integer || param instanceof Long) {
                    byteArrays.add(encodeNumeric(BigInteger.valueOf(((Number) param).longValue())));
                } else if (param instanceof BigInteger) {
                    byteArrays.add(encodeNumeric((BigInteger) param));
                }
            } else if (param instanceof Boolean) {
                // Java原生布尔类型
                byteArrays.add(new byte[]{(Boolean) param ? (byte) 1 : (byte) 0});
            } else {
                throw new IllegalArgumentException("Unsupported type: " + param.getClass().getName());
            }
        }

        // 合并所有字节数组
        byte[] result = concatenateByteArrays(byteArrays);
        return Numeric.toHexString(result);
    }

    // 数值编码辅助方法
    private static byte[] encodeNumeric(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length > 32) {
            // 处理超过32字节的大数
            byte[] trimmed = new byte[32];
            System.arraycopy(bytes, bytes.length - 32, trimmed, 0, 32);
            return trimmed;
        } else if (bytes.length < 32) {
            // 不足32字节前面补零
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 32 - bytes.length, bytes.length);
            return padded;
        }
        return bytes;
    }

    // 合并字节数组辅助方法
    private static byte[] concatenateByteArrays(List<byte[]> byteArrays) {
        int totalLength = byteArrays.stream().mapToInt(arr -> arr.length).sum();
        byte[] result = new byte[totalLength];
        int currentIndex = 0;

        for (byte[] bytes : byteArrays) {
            System.arraycopy(bytes, 0, result, currentIndex, bytes.length);
            currentIndex += bytes.length;
        }

        return result;
    }

    public static String eip712(DynamicStruct dynamicStruct, String domainSper, String priv) {
        return eip712(dynamicStruct, domainSper, Credentials.create(priv));
    }

    public static String eip712(DynamicStruct dynamicStruct, String domainSper, Credentials credentials) {
        String encodeAll = TypeEncoder.encode(dynamicStruct);
        byte[] msg = encodeEip191Header(Numeric.hexStringToByteArray(domainSper), Hash.sha3(Numeric.hexStringToByteArray(encodeAll)));
        Sign.SignatureData signatureData = Sign.signMessage(msg, credentials.getEcKeyPair());
        return abiEncodePacked(signatureData.getR(), signatureData.getS(), signatureData.getV());
    }

}
