package com.cs.copy.tron.service;

import cn.hutool.core.codec.Base58;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tron.trident.core.key.KeyPair;
import org.tron.trident.core.utils.Sha256Hash;
import org.tron.trident.proto.Chain;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.proto.Response.TransactionExtention;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Tron区块链客户端
 * 用于与TronGrid API交互，实现Stake 2.0能量租赁功能
 */
@Slf4j
@Component("tronV2Client")
public class TronV2Client {

    @Value("${tron.fullnode.url:https://api.trongrid.io}")
    private String fullNodeUrl;

    @Value("${tron.api.key:}")
    private String apiKey;

    /**
     * 检查Tron地址是否已激活
     * @param address Tron地址 (Base58格式)
     * @return true-已激活，false-未激活
     */
    public boolean isAddressActivated(String address) {
        try {
            String url = fullNodeUrl + "/wallet/getaccount";
            JSONObject param = new JSONObject();
            param.set("address", address);
            param.set("visible", true);

            JSONObject result = post(url, param);

            // 如果返回结果包含address字段，说明账户已激活
            // 未激活的账户会返回空对象或不包含address字段
            if (result != null && result.containsKey("address")) {
                log.info("地址已激活: {}", address);
                return true;
            } else {
                log.info("地址未激活: {}", address);
                return false;
            }
        } catch (Exception e) {
            log.error("检查地址激活状态失败: {}", address, e);
            return false;
        }
    }

    /**
     * 获取账户资源信息
     * @param address Tron地址 (Base58格式)
     * @return 资源信息JSON
     */
    public JSONObject getAccountResource(String address) {
        String url = fullNodeUrl + "/wallet/getaccountresource";
        JSONObject param = new JSONObject();
        param.set("address", address);
        param.set("visible", true);
        return post(url, param);
    }

    /**
     * 获取全网能量价格
     * 计算每1 TRX（1,000,000 SUN）能获得的能量点数
     * 公式: energyPerTrx = totalEnergyLimit / totalEnergyWeight
     *
     * @return 每1 TRX能获得的能量点数，如果获取失败返回0
     */
    public float getEnergyPricePerTrx() {
        try {
            // 调用任意账户的 getaccountresource 来获取全网能量信息
            // 使用系统账户地址（可以使用任何地址，因为我们只需要全局参数）
            String url = fullNodeUrl + "/wallet/getaccountresource";
            JSONObject param = new JSONObject();
            param.set("address", "T9yD14Nj9j7xAB4dbGeiX9h8unkKHxuWwb"); // 使用一个公共地址
            param.set("visible", true);

            JSONObject result = post(url, param);

            if (result != null && result.containsKey("TotalEnergyLimit") && result.containsKey("TotalEnergyWeight")) {
                long totalEnergyLimit = result.getLong("TotalEnergyLimit");
                long totalEnergyWeight = result.getLong("TotalEnergyWeight");

                if (totalEnergyWeight > 0) {
                    // 计算每1 TRX能获得的能量
                    float energyPerTrx = totalEnergyLimit / totalEnergyWeight;
                    log.info("全网能量价格 - TotalEnergyLimit: {}, TotalEnergyWeight: {}, 每1TRX能量: {}",
                            totalEnergyLimit, totalEnergyWeight, energyPerTrx);
                    return energyPerTrx;
                } else {
                    log.warn("TotalEnergyWeight为0，无法计算能量价格");
                }
            } else {
                log.warn("无法获取全网能量信息: {}", result);
            }
        } catch (Exception e) {
            log.error("获取能量价格失败", e);
        }
        return 0;
    }

    /**
     * 计算指定TRX数量能获得的能量
     *
     * @param trxAmount TRX数量（单位：SUN，1 TRX = 1,000,000 SUN）
     * @return 能获得的能量点数
     */
    public long calculateEnergyByTrx(long trxAmount, long energyPerTrx) {
        if (energyPerTrx > 0) {
            return trxAmount * energyPerTrx / 1_000_000; // 转换为TRX单位
        }
        return 0;
    }

    public long calculateTrxByEnergy(long energyAmount, long energyPerTrx) {
        if (energyPerTrx > 0) {
            return energyAmount * 1_000_000 / energyPerTrx; // 转换为SUN单位
        }
        return 0;
    }

    /**
     * 查询指定账号的转账记录
     * @param address 接收地址 (Base58格式)
     * @param minTimestamp 最小时间戳（毫秒）
     * @return 交易数组
     */
    public JSONArray getTransactions(String address, long minTimestamp) {
        // 使用TronGrid V1 API查询转入交易
        String url = String.format("%s/v1/accounts/%s/transactions?only_to=true&limit=50&min_timestamp=%d",
                fullNodeUrl.replace("/wallet", ""), address, minTimestamp);

        try {
            HttpResponse response = HttpRequest.get(url)
                    .header("TRON-PRO-API-KEY", apiKey)
                    .timeout(10000)
                    .execute();

            if (response.isOk()) {
                JSONObject json = JSONUtil.parseObj(response.body());
                if (json.getBool("success", false)) {
                    return json.getJSONArray("data");
                } else {
                    log.warn("TronGrid API 返回失败: {}", json);
                }
            } else {
                log.error("TronGrid API HTTP错误: {} {}", response.getStatus(), response.body());
            }
        } catch (Exception e) {
            log.error("TronGrid API调用异常", e);
        }
        return new JSONArray();
    }

    /**
     * 代理资源 (Stake 2.0 delegateresource)
     * 把能量代理给指定账号
     *
     * @param fromAddr 能量提供方地址 (Base58)
     * @param toAddr 能量接收方地址 (Base58)
     * @param energyAmount 代理的能量数量
     * @param privateKey 提供方私钥
     * @return 交易Hash，失败返回null
     */
    public String delegateResource(String fromAddr, String toAddr, long energyAmount, String privateKey) {
        String url = fullNodeUrl + "/wallet/delegateresource";

        JSONObject param = new JSONObject();
        param.set("owner_address", fromAddr);
        param.set("receiver_address", toAddr);
        param.set("balance", energyAmount);
        param.set("resource", "ENERGY");
        param.set("lock", false);
        param.set("visible", true);
        param.set("permission_id", 3);  // 使用 active_permission (ID=2)
        //https://nile.trongrid.io/v1/accounts/TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN

        JSONObject tx = post(url, param);

        if (tx != null && !tx.containsKey("Error") && tx.containsKey("txID")) {
            log.info("创建代理资源交易成功: {} -> {}, 能量: {}", fromAddr, toAddr, energyAmount);
            return signAndBroadcast(tx, privateKey);
        }

        log.error("创建代理资源交易失败: {}", tx);
        return null;

//        try {
////            fromAddr = "TFEWqCSUFs6tthfyLWFjpUepSqWPWKDZQx";
//            ApiWrapper client = ApiWrapper.ofNile(privateKey);
//            TransactionExtention txnExt = client.delegateResource(fromAddr, energyAmount, 1, toAddr, false);
//            if(txnExt == null || !txnExt.getResult().getResult()) {
//                log.info("交易构造失败: {}", txnExt.getResult().getMessage().toStringUtf8());
//                return null;
//            }
//            Chain.Transaction signedTxn = client.signTransaction(txnExt);
//            return client.broadcastTransaction(signedTxn);
////            return new String(txHash);
//        }catch (Exception e){
//            log.info("创建代理资源交易失败: {}", e.getMessage());
//            return null;
//        }
    }

    /**
     * 取消代理资源 (Stake 2.0 undelegateresource)
     * 收回代理给指定账号的能量
     *
     * @param fromAddr 能量提供方地址 (Base58)
     * @param toAddr 能量接收方地址 (Base58)
     * @param energyAmount 取消代理的能量数量
     * @param privateKey 提供方私钥
     * @return 交易Hash，失败返回null
     */
    public String unDelegateResource(String fromAddr, String toAddr, long energyAmount, String privateKey) {
        String url = fullNodeUrl + "/wallet/undelegateresource";

        JSONObject param = new JSONObject();
        param.set("owner_address", fromAddr);
        param.set("receiver_address", toAddr);
        param.set("balance", energyAmount);
        param.set("resource", "ENERGY");
        param.set("visible", true);

        JSONObject tx = post(url, param);

        if (tx != null && !tx.containsKey("Error") && tx.containsKey("txID")) {
            log.info("创建取消代理资源交易成功: {} <- {}, 能量: {}", fromAddr, toAddr, energyAmount);
            return signAndBroadcast(tx, privateKey);
        }

        log.error("创建取消代理资源交易失败: {}", tx);
        return null;
    }

    /**
     * 获取账户的权限信息
     */
    public void printAccountPermissions(String address) {
        try {
            String url = fullNodeUrl + "/wallet/getaccount";
            JSONObject param = new JSONObject();
            param.set("address", address);
            param.set("visible", true);

            JSONObject account = post(url, param);

            log.info("=== 账户权限信息: {} ===", address);
            log.info("Owner Permission: {}", account.getJSONObject("owner_permission"));
            log.info("Active Permissions: {}", account.getJSONArray("active_permission"));
        } catch (Exception e) {
            log.error("获取账户权限失败", e);
        }
    }


    /**
     * 签名并广播交易
     *
     * @param transaction 未签名的交易JSON
     * @param privateKey 私钥
     * @return 交易Hash，失败返回null
     */
    private String signAndBroadcast(JSONObject transaction, String privateKey) {
//        printAccountPermissions("TJtWE6PuHM7UQrPDDRab3oP5dVqiV1EMSN");

        try {
            // Step 1: 签名交易
//            String signUrl = fullNodeUrl + "/wallet/gettransactionsign";
//            JSONObject signParam = new JSONObject();
//            signParam.set("transaction", transaction);
//            signParam.set("privateKey", privateKey);
//
//            JSONObject signedTx = post(signUrl, signParam);

            KeyPair keyPair = new KeyPair(privateKey);

//            // 1. 构造 Transaction 对象
//            Chain.Transaction.Builder txBuilder = Chain.Transaction.newBuilder();
//
//            // 2. 解析 raw_data
//            Chain.Transaction.raw.Builder rawBuilder = Chain.Transaction.raw.newBuilder();
//            JSONObject rawData = transaction.getJSONObject("raw_data");
//            JsonFormat.parser().ignoringUnknownFields().merge(rawData.toString(), rawBuilder);
//
//            // 3. 设置 raw_data 到交易
//            txBuilder.setRawData(rawBuilder.build());
//            Chain.Transaction txProto = txBuilder.build();
//
//            // 4. 计算签名
//            byte[] rawDataBytes = txProto.getRawData().toByteArray();
//            byte[] hash = Sha256Hash.hash(true, rawDataBytes);
//            byte[] signature = KeyPair.signTransaction(hash, keyPair);
//
//            // 5. 添加签名
//            Chain.Transaction signedTx = txProto.toBuilder()
//                    .addSignature(ByteString.copyFrom(signature))
//                    .build();

            // 1. 使用 raw_data_hex 字段（TronGrid 返回的已编码数据）
            String rawDataHex = transaction.getStr("raw_data_hex");
            if (StringUtils.isBlank(rawDataHex)) {
                log.error("交易缺少 raw_data_hex 字段");
                return null;
            }

            // 2. 计算签名
            byte[] rawDataBytes = HexUtil.decodeHex(rawDataHex);
            byte[] hash = Sha256Hash.hash(true, rawDataBytes);
            byte[] signature = KeyPair.signTransaction(hash, keyPair);

            // 3. 构造签名后的交易 JSON
            JSONObject signedTx = JSONUtil.parseObj(transaction.toString());
            JSONArray signatures = new JSONArray();
            signatures.add(HexUtil.encodeHexStr(signature));
            signedTx.set("signature", signatures);

            // Step 2: 广播交易
            String broadcastUrl = fullNodeUrl + "/wallet/broadcasttransaction";
//            String txJson = JsonFormat.printer().includingDefaultValueFields().print(signedTx);
//            JSONObject broadcastParam = JSONUtil.parseObj(txJson);
//            JSONObject broadcastResult = post(broadcastUrl, broadcastParam);
            JSONObject broadcastResult = post(broadcastUrl, signedTx);

            if (broadcastResult != null && broadcastResult.getBool("result", false)) {
                String txId = transaction.getStr("txID");
                log.info("交易广播成功: {}", txId);
                return txId;
            } else {
                log.error("交易广播失败: {}", broadcastResult);
                return null;
            }
        } catch (Exception e) {
            log.error("签名广播交易异常", e);
            return null;
        }
    }

    /**
     * POST请求
     */
    private JSONObject post(String url, JSONObject param) {
        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("TRON-PRO-API-KEY", apiKey)
                    .header("Content-Type", "application/json")
                    .body(param.toString())
                    .timeout(15000)
                    .execute();

            if (response.isOk()) {
                return JSONUtil.parseObj(response.body());
            } else {
                log.error("Tron API HTTP错误: {} {} - {}", url, response.getStatus(), response.body());
            }
        } catch (Exception e) {
            log.error("Tron API调用异常: {}", url, e);
        }
        return null;
    }

    /**
     * Base58地址转Hex格式
     */
    public String toHex(String base58) {
        if (StrUtil.isBlank(base58)) {
            return null;
        }
        // 如果已经是Hex格式（以41开头）
        if (base58.startsWith("41") && base58.length() == 42) {
            return base58;
        }
        try {
            byte[] bytes = Base58.decode(base58);
            return HexUtil.encodeHexStr(Arrays.copyOf(bytes,21));
        } catch (Exception e) {
            log.warn("Base58转Hex失败: {}", base58);
            return base58;
        }
    }

    /**
     * Hex地址转Base58格式
     */
    public String toBase58(String hex) {
        if (StrUtil.isBlank(hex)) {
            return null;
        }
        // 如果已经是Base58格式
        if (hex.startsWith("T")) {
            return hex;
        }
        try {
            byte[] rawAddr = HexUtil.decodeHex(hex);
            // 2. 计算双重 SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash1 = digest.digest(rawAddr);
            byte[] hash2 = digest.digest(hash1);

            // 3. 获取前 4 字节校验和并拼接 (21 + 4 = 25 bytes)
            byte[] result = new byte[25];
            System.arraycopy(rawAddr, 0, result, 0, 21);
            System.arraycopy(hash2, 0, result, 21, 4);

            // 4. Base58 编码 (需调用 Base58 库)
            return Base58.encode(result);
        } catch (Exception e) {
            log.warn("Hex转Base58失败: {}", hex);
            return hex;
        }
    }
}
