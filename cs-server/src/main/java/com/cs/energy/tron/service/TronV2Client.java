package com.cs.energy.tron.service;

import cn.hutool.core.codec.Base58;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tron.trident.core.key.KeyPair;
import org.tron.trident.core.utils.Sha256Hash;

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
     * @param privateKey 提供方私钥（可以是资源账号本身或被授权的代理账号）
     * @return 交易Hash，失败返回null
     */
    public String delegateResource(String fromAddr, String toAddr, long energyAmount, String privateKey, Integer permissionId) {
        // 1. 从私钥获取签名者地址
        KeyPair signerKeyPair = new KeyPair(privateKey);
        String signerAddress = signerKeyPair.toBase58CheckAddress();

        log.info("======================================");
        log.info("代理资源请求: owner={}, receiver={}, amount={}, signer={}",
                fromAddr, toAddr, energyAmount, signerAddress);

//        // 2. 检查签名者是否有权限，并获取对应的 permission_id
//        int permissionId = -1;
//        if (fromAddr.equals(signerAddress)) {
//            // 如果是资源账号自己签名，使用 owner permission (ID=0)
//            permissionId = 0;
//            log.info("✓ 使用资源账号本身签名，permission_id=0");
//        } else {
//            // 如果是代理账号签名，需要查找其 permission_id
//            log.info("使用代理账号签名，正在查询权限...");
//
//            // 先打印账户权限结构
//            printAccountPermissions(fromAddr);
//
//            permissionId = getPermissionIdForSigner(fromAddr, signerAddress);
//            if (permissionId == -1) {
//                log.error("❌ 签名者 {} 没有权限操作资源账号 {}", signerAddress, fromAddr);
//                log.error("❌ 解决方案: 请在资源账号 {} 的多签名设置中添加代理账号 {}", fromAddr, signerAddress);
//                log.error("❌ 步骤: 1. 使用TronLink登录资源账号 2. 进入设置 -> Multi-Signature 3. 添加地址 {}", signerAddress);
//                return null;
//            }
//            log.info("✓ 使用代理账号签名，permission_id={}", permissionId);
//        }

        String url = fullNodeUrl + "/wallet/delegateresource";

        JSONObject param = new JSONObject();
        param.set("owner_address", fromAddr);
        param.set("receiver_address", toAddr);
        param.set("balance", energyAmount);
        param.set("resource", "ENERGY");
        param.set("lock", false);
        param.set("visible", true);
        param.set("Permission_id", permissionId);

//        // 只有当使用非 owner permission 时才设置 permission_id
//        // 注意：Tron API 文档显示字段名是 "Permission_id"（大写P）
//        if (permissionId != 0) {
//            param.set("Permission_id", permissionId);  // 使用大写 P
//            log.info("设置 Permission_id={} 到交易参数（注意大写P）", permissionId);
//        }
//
//        log.info("创建交易参数: {}", param);
        JSONObject tx = post(url, param);

        if (tx != null && !tx.containsKey("Error") && tx.containsKey("txID")) {
            log.info("✓ 创建代理资源交易成功: {} -> {}, 能量: {}", fromAddr, toAddr, energyAmount);
            log.info("交易详情: txID={}", tx.getStr("txID"));

            // 打印完整交易结构用于调试
            log.info("完整交易结构: {}", tx.toString());

            // 检查 Permission_id 的位置
            if (tx.containsKey("raw_data")) {
                JSONObject rawData = tx.getJSONObject("raw_data");
                log.info("raw_data 内容: {}", rawData.toString());

                if (rawData.containsKey("contract")) {
                    JSONArray contracts = rawData.getJSONArray("contract");
                    log.info("contract 数组大小: {}", contracts.size());

                    if (contracts != null && !contracts.isEmpty()) {
                        JSONObject contract = contracts.getJSONObject(0);
                        log.info("contract[0] 完整内容: {}", contract.toString());

                        // 尝试不同的字段名
                        if (contract.containsKey("Permission_id")) {
                            log.info("✓ 找到 Permission_id (大写P): {}", contract.getInt("Permission_id"));
                        } else if (contract.containsKey("permission_id")) {
                            log.info("✓ 找到 permission_id (小写p): {}", contract.getInt("permission_id"));
                        } else {
                            log.warn("⚠ contract[0] 中不包含 Permission_id 字段");
                            log.warn("⚠ contract[0] 的所有键: {}", contract.keySet());
                        }

                        // 检查 parameter 中是否有
                        if (contract.containsKey("parameter")) {
                            JSONObject parameter = contract.getJSONObject("parameter");
                            log.info("parameter 内容: {}", parameter.toString());
                        }
                    }
                }
            }

            return signAndBroadcast(tx, privateKey);
        }

        log.error("❌ 创建代理资源交易失败: {}", tx);
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
     * @param privateKey 提供方私钥（可以是资源账号本身或被授权的代理账号）
     * @return 交易Hash，失败返回null
     */
    public String unDelegateResource(String fromAddr, String toAddr, long energyAmount, String privateKey) {
        // 1. 从私钥获取签名者地址
        KeyPair signerKeyPair = new KeyPair(privateKey);
        String signerAddress = signerKeyPair.toBase58CheckAddress();

        log.info("取消代理资源请求: owner={}, receiver={}, amount={}, signer={}",
                fromAddr, toAddr, energyAmount, signerAddress);

//        // 2. 检查签名者是否有权限，并获取对应的 permission_id
//        int permissionId = -1;
//        if (fromAddr.equals(signerAddress)) {
//            // 如果是资源账号自己签名，使用 owner permission (ID=0)
//            permissionId = 0;
//            log.info("使用资源账号本身签名，permission_id=0");
//        } else {
//            // 如果是代理账号签名，需要查找其 permission_id
//            permissionId = getPermissionIdForSigner(fromAddr, signerAddress);
//            if (permissionId == -1) {
//                log.error("签名者 {} 没有权限操作资源账号 {}", signerAddress, fromAddr);
//                return null;
//            }
//            log.info("使用代理账号签名，permission_id={}", permissionId);
//        }

        String url = fullNodeUrl + "/wallet/undelegateresource";

        JSONObject param = new JSONObject();
        param.set("owner_address", fromAddr);
        param.set("receiver_address", toAddr);
        param.set("balance", energyAmount);
        param.set("resource", "ENERGY");
        param.set("visible", true);
        param.set("Permission_id", 3);

//        // 只有当使用非 owner permission 时才设置 permission_id
//        // 注意：Tron API 文档显示字段名是 "Permission_id"（大写P）
//        if (permissionId != 0) {
//            param.set("Permission_id", permissionId);  // 使用大写 P
//            log.info("设置 Permission_id={} 到交易参数（注意大写P）", permissionId);
//        }

//        log.info("创建取消代理交易参数: {}", param);
        JSONObject tx = post(url, param);

        if (tx != null && !tx.containsKey("Error") && tx.containsKey("txID")) {
            log.info("✓ 创建取消代理资源交易成功: {} <- {}, 能量: {}", fromAddr, toAddr, energyAmount);
            log.info("交易详情: txID={}", tx.getStr("txID"));

            // 打印交易中的 permission_id 信息用于调试
            if (tx.containsKey("raw_data")) {
                JSONObject rawData = tx.getJSONObject("raw_data");
                if (rawData.containsKey("contract")) {
                    JSONArray contracts = rawData.getJSONArray("contract");
                    if (contracts != null && contracts.size() > 0) {
                        JSONObject contract = contracts.getJSONObject(0);
                        if (contract.containsKey("Permission_id")) {
                            log.info("✓ 交易中包含 Permission_id: {}", contract.getInt("Permission_id"));
                        } else {
                            log.warn("⚠ 交易中不包含 Permission_id 字段");
                        }
                    }
                }
            }

            return signAndBroadcast(tx, privateKey);
        }

        log.error("❌ 创建取消代理资源交易失败: {}", tx);
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

            if (account != null && !account.isEmpty()) {
                log.info("账户信息: {}", account);

                if (account.containsKey("owner_permission")) {
                    log.info("Owner Permission: {}", account.getJSONObject("owner_permission"));
                } else {
                    log.warn("⚠ 账户没有 owner_permission");
                }

                if (account.containsKey("active_permission")) {
                    log.info("Active Permissions: {}", account.getJSONArray("active_permission"));
                } else {
                    log.warn("⚠ 账户没有 active_permission");
                }
            } else {
                log.error("❌ 无法获取账户信息或账户不存在");
            }

            log.info("=================================");
        } catch (Exception e) {
            log.error("获取账户权限失败", e);
        }
    }

    /**
     * 诊断方法：测试代理账号是否有权限
     * @param resourceAddress 资源账号地址
     * @param agentAddress 代理账号地址
     */
    public void diagnosePermission(String resourceAddress, String agentAddress) {
        log.info("========================================");
        log.info("权限诊断");
        log.info("资源账号: {}", resourceAddress);
        log.info("代理账号: {}", agentAddress);
        log.info("========================================");

        printAccountPermissions(resourceAddress);

        int permId = getPermissionIdForSigner(resourceAddress, agentAddress);
        if (permId == -1) {
            log.error("❌ 诊断结果: 代理账号 {} 没有权限操作资源账号 {}", agentAddress, resourceAddress);
            log.error("❌ 需要执行以下步骤:");
            log.error("   1. 使用 TronLink 钱包导入资源账号 {} 的私钥", resourceAddress);
            log.error("   2. 进入钱包设置 -> Multi-Signature");
            log.error("   3. 点击 'Add Permission' 或修改现有的 Active Permission");
            log.error("   4. 在 'Keys' 中添加代理账号地址: {}", agentAddress);
            log.error("   5. 设置权限阈值为 1, 地址权重为 1");
            log.error("   6. 确认并广播交易");
        } else if (permId == 0) {
            log.info("✓ 诊断结果: 代理账号即为资源账号本身，使用 owner permission");
        } else {
            log.info("✓ 诊断结果: 代理账号有权限，permission_id={}", permId);
        }

        log.info("========================================");
    }

    /**
     * 获取账户信息
     */
    public JSONObject getAccountInfo(String address) {
        try {
            String url = fullNodeUrl + "/wallet/getaccount";
            JSONObject param = new JSONObject();
            param.set("address", address);
            param.set("visible", true);
            return post(url, param);
        } catch (Exception e) {
            log.error("获取账户信息失败: {}", address, e);
            return null;
        }
    }

    /**
     * 检查签名地址是否在账户的权限列表中
     * @param ownerAddress 资源所有者地址
     * @param signerAddress 签名者地址
     * @return 如果签名者被授权则返回对应的 permission_id，否则返回 -1
     */
    public int getPermissionIdForSigner(String ownerAddress, String signerAddress) {
        try {
            JSONObject account = getAccountInfo(ownerAddress);
            if (account == null || account.isEmpty()) {
                log.error("❌ 无法获取账户信息: {}", ownerAddress);
                return -1;
            }

            log.info("查询账户权限: owner={}, signer={}", ownerAddress, signerAddress);
            log.debug("账户信息: {}", account);

            // 检查 owner_permission (ID=0)
            if (account.containsKey("owner_permission")) {
                JSONObject ownerPerm = account.getJSONObject("owner_permission");
                log.info("检查 owner_permission: {}", ownerPerm);
                if (containsAddress(ownerPerm, signerAddress)) {
                    log.info("✓ 签名者 {} 在 owner_permission 中", signerAddress);
                    return 0;
                }
            } else {
                log.warn("⚠ 账户没有 owner_permission 字段");
            }

            // 检查 active_permission (通常 ID=2)
            if (account.containsKey("active_permission")) {
                JSONArray activePerms = account.getJSONArray("active_permission");
                log.info("检查 active_permission 数组，共 {} 个权限", activePerms.size());

                for (int i = 0; i < activePerms.size(); i++) {
                    JSONObject perm = activePerms.getJSONObject(i);
                    log.info("检查 active_permission[{}]: {}", i, perm);

                    if (containsAddress(perm, signerAddress)) {
                        int permId = perm.getInt("id", 2);
                        log.info("✓ 签名者 {} 在 active_permission[{}] 中, permission_id={}",
                                signerAddress, i, permId);
                        return permId;
                    }
                }
                log.warn("⚠ 签名者 {} 不在任何 active_permission 中", signerAddress);
            } else {
                log.warn("⚠ 账户没有 active_permission 字段");
            }

            log.error("❌ 签名者 {} 不在账户 {} 的任何权限列表中", signerAddress, ownerAddress);
            log.error("❌ 请确保已在 Tron 钱包中为资源账号设置了多签名权限");
            return -1;
        } catch (Exception e) {
            log.error("❌ 检查权限失败", e);
            return -1;
        }
    }

    /**
     * 检查权限对象中是否包含指定地址
     */
    private boolean containsAddress(JSONObject permission, String address) {
        if (!permission.containsKey("keys")) {
            log.debug("权限对象不包含 keys 字段: {}", permission);
            return false;
        }

        JSONArray keys = permission.getJSONArray("keys");
        log.debug("检查 keys 数组（共 {} 个）中是否包含地址 {}", keys.size(), address);

        for (int i = 0; i < keys.size(); i++) {
            JSONObject key = keys.getJSONObject(i);
            String keyAddress = key.getStr("address");
            log.debug("  keys[{}]: address={}, weight={}", i, keyAddress, key.getInt("weight", 1));

            if (address.equals(keyAddress)) {
                log.info("✓ 找到匹配的地址: {}", address);
                return true;
            }
        }

        log.debug("✗ 未找到匹配的地址: {}", address);
        return false;
    }


    /**
     * 签名并广播交易
     *
     * @param transaction 未签名的交易JSON
     * @param privateKey 私钥
     * @return 交易Hash，失败返回null
     */
    private String signAndBroadcast(JSONObject transaction, String privateKey) {
        try {
            KeyPair keyPair = new KeyPair(privateKey);
            String signerAddress = keyPair.toBase58CheckAddress();

            // 1. 使用 raw_data_hex 字段（TronGrid 返回的已编码数据）
            String rawDataHex = transaction.getStr("raw_data_hex");
            if (StringUtils.isBlank(rawDataHex)) {
                log.error("交易缺少 raw_data_hex 字段");
                return null;
            }

            // 2. 检查交易中是否有 permission_id
            JSONObject rawData = transaction.getJSONObject("raw_data");
            Integer permissionId = null;
            if (rawData != null && rawData.containsKey("contract")) {
                JSONArray contracts = rawData.getJSONArray("contract");
                if (contracts != null && contracts.size() > 0) {
                    JSONObject contract = contracts.getJSONObject(0);
                    if (contract.containsKey("Permission_id")) {
                        permissionId = contract.getInt("Permission_id");
                        log.info("交易包含 Permission_id: {}, 签名者: {}", permissionId, signerAddress);
                    }
                }
            }

            // 3. 计算签名
            byte[] rawDataBytes = HexUtil.decodeHex(rawDataHex);
            byte[] hash = Sha256Hash.hash(true, rawDataBytes);
            byte[] signature = KeyPair.signTransaction(hash, keyPair);

            log.info("交易签名: txID={}, 签名者={}, 签名={}",
                    transaction.getStr("txID"), signerAddress, HexUtil.encodeHexStr(signature));

            // 4. 构造签名后的交易 JSON
            JSONObject signedTx = JSONUtil.parseObj(transaction.toString());
            JSONArray signatures = new JSONArray();
            signatures.add(HexUtil.encodeHexStr(signature));
            signedTx.set("signature", signatures);

            // 5. 广播交易
            String broadcastUrl = fullNodeUrl + "/wallet/broadcasttransaction";
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
