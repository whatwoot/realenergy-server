package com.cs.copy;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cs.copy.global.constants.Gkey;
import com.cs.oksdk.OkxMultiSdkApi;
import com.cs.oksdk.OkxSdkApi;
import com.cs.oksdk.config.prop.OkxProperties;
import com.cs.oksdk.dto.CopyMember;
import com.cs.oksdk.enums.InstrumentType;
import com.cs.oksdk.enums.MgnMode;
import com.cs.oksdk.enums.PosMode;
import com.cs.oksdk.enums.PositionsSide;
import com.cs.oksdk.reponse.*;
import com.cs.oksdk.request.*;
import com.cs.web.util.BeanCopior;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cs.sp.common.WebAssert.expect;

/**
 * @authro fun
 * @date 2025/11/27 00:27
 */
@Slf4j
public class OkxSdkApiTest {

    @Test
    public void test() {
        String apikey = "3d1e428c-c4fa-49b6-8dfd-b37d2985474b";
        String apiSecret = "8BCEE4B0D57DCFC20F14AE980BD9352A";
        String pwd = "Quzm!104OKEX";

        OkxProperties prop = new OkxProperties();
        prop.setApikey(apikey);
        prop.setSecret(apiSecret);
        prop.setPassphrase(pwd);
        prop.setTestnet(true);

        OkxSdkApi okxApiHelper = new OkxSdkApi(prop);
//        okxApiHelper
//        String status = okxApiHelper.getStatus();
//        log.info("status: {}", status);
//        String balance = okxApiHelper.getBalance(null);
//        log.info("balance: {}", balance);
//        OrderRequest req = OrderRequest.builder()
//                .instId("ETH-USDT-SWAP")
//                .tdMode(TdMode.ISOLATED)
//                .side(Side.BUY)
//                .posSide(PositionsSide.LONG)
//                .ordType(OrderType.MARKET)
//                .sz(BigDecimal.ONE)
//                .build();
//        String order = okxApiHelper.order(req);
//        log.info("order: {}", order);

//        String instruments = okxApiHelper.instruments(IntrumentsRequest.builder()
//                        .instType(InstrumentType.SWAP)
//                .build());
//        log.info(instruments);


    }

    @Test
    public void prodTest() {
        // prod-跟
//        String apikey = "edf850c3-d82a-42c6-b110-70eae8c6322f";
//        String apiSecret = "246F95E62850B82C45350FC65F187F7B";
//        String pwd = "Dong900624@";
        //
        // String apikey = "09ecb38c-f2d2-48fa-b8fd-fa573d755478";
        // String apiSecret = "5B4E8A6BEC4867204424DCFCA72848BC";
        // String pwd = "YDH131419ydh@";

        String apikey = "966ace91-97c2-4aee-bd0f-58d7371ed34b";
        String apiSecret = "B070C78D4EFCB198C081FDCBA8F46F73";
        String pwd = "Lnb770104.";

        boolean testEnv = false;

        OkxProperties prop = new OkxProperties();
        prop.setApikey(apikey);
        prop.setSecret(apiSecret);
        prop.setPassphrase(pwd);
        prop.setTestnet(testEnv);

        CopyMember user = BeanCopior.map(prop, CopyMember.class);

        OkxMultiSdkApi okxApiHelper = new OkxMultiSdkApi(testEnv);

//        PositionsRes positions = okxApiHelper.positions(user, PositionsRequest.builder()
//                .instType(InstrumentType.SWAP)
//                .build());
//        log.info("positions: {}", JSONObject.toJSONString(positions));

//        AccountConfigRes accountConfigRes = okxApiHelper.accountConfig(user);
//        log.info("accountConfigRes:{}", JSONObject.toJSONString(accountConfigRes));
//        PositionsRes positions = okxApiHelper.positions(user, PositionsRequest.builder()
//                .build());
        BalanceRes balances = okxApiHelper.balances(user, BalanceRequest.builder().ccy(Gkey.USDT).build());
        log.info("positions: {}", JSONObject.toJSONString(balances));
    }

    @Test
    public void testClose() {
        // 测-国-跟
        String apikey = "99909b30-7b9b-47ab-a2ac-6eb5f025d143";
        String apiSecret = "B2871AC8252C6B4B2C477E8CA09FF8EA";
        String pwd = "Qq123456@";

//        String apikey = "195dbc5c-312a-493c-badc-35307060523c";
//        String apiSecret = "4814338B0792D7E0BEB26A2FFD43FB6C";
//        String pwd = "Lcw123456@";
//        String apikey = "c014754c-2e9c-489f-b28a-253aac9cb7e1";
//        String apiSecret = "2B169C5DCA059305045E65AA0CD9B13F";
//        String pwd = "Femman557788@";
        boolean testEnv = true;

        OkxProperties prop = new OkxProperties();
        prop.setApikey(apikey);
        prop.setSecret(apiSecret);
        prop.setPassphrase(pwd);
        prop.setTestnet(testEnv);

        CopyMember user = BeanCopior.map(prop, CopyMember.class);

        OkxMultiSdkApi okxApiHelper = new OkxMultiSdkApi(testEnv);
        ClosePositionRes closePositionRes = okxApiHelper.closePosition(user, ClosePositionRequest.builder()
                .instId("ETH-USDT-SWAP")
                .mgnMode(MgnMode.CROSS)
                .posSide(PositionsSide.LONG)
                .build());
        log.info("s: {}", JSONObject.toJSONString(closePositionRes));
    }

    @Test
    public void test2() {
        // 测-j-带
//        String apikey = "3d1e428c-c4fa-49b6-8dfd-b37d2985474b";
//        String apiSecret = "8BCEE4B0D57DCFC20F14AE980BD9352A";
//        String pwd = "Quzm!104OKEX";

//        String apikey = "cbb50b93-bca8-44c3-be52-31796f3ee124";
//        String apiSecret = "1AD51A2E447E41E35D21B1F4B835713B";
//        String pwd = "Qq123456&";

//        String apikey = "55f1e673-75a3-4657-858a-ac8b0eaef95f";
//        String apiSecret = "AA4480F52543C6C41859015A48945E68";
//        String pwd = "Zj112233$";

//        String apikey = "e97e94f0-9d54-4489-a80d-040673988ccc";
//        String apiSecret = "CA1253C739C92A35CA5887ABB805C115";
//        String pwd = "Qq123456&";

//        String apikey = "393c4091-8acc-4e56-bc28-a831b3602b39";
//        String apiSecret = "4798B0FD8E46B2B5DFD374B8AA5D83CF";
//        String pwd = "Qa12345678.";

        // prod-带-uni
//        String apikey = "0b6cf4b0-4c1c-482a-b26c-601d64f2e7eb";
//        String apiSecret = "3248758CF697EE6270CDCA920E1B2CB9";
//        String pwd = "Qq123456@";
        //
        /**
         Qq123456@

         子1 sol
         5AXPLK37RQEIOXX5
         apikey = "ec526382-97fd-424b-a757-5025e9a99875"
         secretkey = "C0F44192D1297D30DA32855B7B5D3024"
         IP = ""
         备注名 = "子1"
         权限 = "读取/交易"


         子2  sui
         W6FLGSBXCIH52IUN
         Qq123456@
         apikey = "bca2de5b-105b-4462-b4d5-3ba57d3c5261"
         secretkey = "B3DEC6363AC68CF7E51ECD413179C781"
         IP = ""
         备注名 = "子2"
         权限 = "读取/交易"

         子3 eth
         HLP46LNH44MOEGKR
         Qq123456@
         apikey = "c4c0af7f-77f4-42a8-ad1f-9a77a05ef9fe"
         secretkey = "3591A1B620163A93E08C74AFB79848B0"
         IP = ""
         API key name = "子3"
         Permissions = "Read/Trade"

         */

        // prod-跟
//        String apikey = "edf850c3-d82a-42c6-b110-70eae8c6322f";
//        String apiSecret = "246F95E62850B82C45350FC65F187F7B";
//        String pwd = "Dong900624@";
        // {"apiKey":"99909b30-7b9b-47ab-a2ac-6eb5f025d143","secretKey":"B2871AC8252C6B4B2C477E8CA09FF8EA","API name":"测试环境","IP":"0","Permissions":"只读, 交易"}
        // 测-国-跟
        String apikey = "99909b30-7b9b-47ab-a2ac-6eb5f025d143";
        String apiSecret = "B2871AC8252C6B4B2C477E8CA09FF8EA";
        String pwd = "Qq123456@";

//        String apikey = "195dbc5c-312a-493c-badc-35307060523c";
//        String apiSecret = "4814338B0792D7E0BEB26A2FFD43FB6C";
//        String pwd = "Lcw123456@";
//        String apikey = "c014754c-2e9c-489f-b28a-253aac9cb7e1";
//        String apiSecret = "2B169C5DCA059305045E65AA0CD9B13F";
//        String pwd = "Femman557788@";

        boolean testEnv = true;

        OkxProperties prop = new OkxProperties();
        prop.setApikey(apikey);
        prop.setSecret(apiSecret);
        prop.setPassphrase(pwd);
        prop.setTestnet(testEnv);

        CopyMember user = BeanCopior.map(prop, CopyMember.class);

        OkxMultiSdkApi okxApiHelper = new OkxMultiSdkApi(testEnv);
//        String status = okxApiHelper.getStatus();
//        log.info("status: {}", status);
//        BalanceRes balances = okxApiHelper.balances(user, null);
//        log.info("balance: {}", JSONObject.toJSONString(balances));
//        OrderRequest req = OrderRequest.builder()
//                .instId("ETH-USDT-SWAP")
//                .tdMode(TdMode.ISOLATED)
//                .side(Side.BUY)
//                .posSide(PositionsSide.LONG)
//                .ordType(OrderType.MARKET)
//                .sz(BigDecimal.ONE)
//                .build();
//        String order = okxApiHelper.order(map, req);
//        log.info("order: {}", order);
//        ClosePositionRequest closePos = ClosePositionRequest.builder()
//                .instId("ETH-USDT-SWAP")
//                .mgnMode(MgnMode.ISOLATED)
//                .posSide(PositionsSide.LONG)
//                .build();
//        String s = okxApiHelper.closePosition(map, closePos);
//        log.info("s: {}", s);

        // 开 3089566235744739328
        // 平 3089695002320375808
//        GetOrderRes order1 = okxApiHelper.getOrder(user, GetOrderRequest.builder()
//                .instId("ETH-USDT-SWAP")
////                .ordId("3127339409269932032")
//                .ordId("3144888759458025472")
//                .build());
//        log.info("open: {}", JSONObject.toJSONString(order1));
//        order = okxApiHelper.getOrder(user, GetOrderRequest.builder()
//                .instId("ETH-USDT-SWAP")
//                .ordId("3098146944713428992")
//                .build());
//        log.info("open: {}", JSONObject.toJSONString(order));
//        order = okxApiHelper.getOrder(user, GetOrderRequest.builder()
//                .instId("ETH-USDT-SWAP")
//                .ordId("3098147181842599936")
//                .build());
//        log.info("open: {}", JSONObject.toJSONString(order));

//        order = okxApiHelper.getOrder(user, GetOrderRequest.builder()
//                .instId("ETH-USDT-SWAP")
//                .ordId("3098099761377120256")
//                .build());
//        log.info("close: {}", JSONObject.toJSONString(order));

//        {"code":"0","data":[{"clOrdId":"","ordId":"3077990043711524864","sCode":"0","sMsg":"Order placed","tag":"","ts":"1764233651588"}],"inTime":"1764233651588557","msg":"","outTime":"1764233651589733"}

//        OrderRequest req = OrderRequest.builder()
//                .instId("ETH-USDT-SWAP")
//                .tdMode(TdMode.ISOLATED.value())
//                .side(Side.BUY.value())
//                .posSide(PositionsSide.LONG.value())
//                .ordType(OrderType.MARKET.value())
//                .sz(BigDecimal.ONE)
////                .reduceOnly(true)
//                .build();
//        OrderRes order = okxApiHelper.order(user, req);
//        log.info("order: {}", JSONObject.toJSONString(order));

//        PositionsRes positions = okxApiHelper.positions(user, PositionsRequest.builder()
//                .instType(InstrumentType.SWAP)
//                .build());
//        log.info("positions: {}", JSONObject.toJSONString(positions));
//        LeverageInfoRes leverageInfo = okxApiHelper.leverageInfo(user, LeverageInfoRequest.builder()
//                .instId("ETH-USDT-SWAP")
//                .mgnMode(MgnMode.ISOLATED.value())
//                .build());
//        log.info("s: {}", JSONObject.toJSONString(leverageInfo));

//        InstrumentRes instruments = okxApiHelper.instruments(InstrumentsRequest.builder()
//                .instType(InstrumentType.SWAP)
//                .build());
//        log.info("instruments: {}", instruments);


//        SetLeverageInfoRes setLeverageInfoRes = okxApiHelper.setLeverage(user, SetLeverageRequest.builder()
//                .instId("BTC-USDT-SWAP")
//                .mgnMode(MgnMode.ISOLATED.value())
//                .posSide(PositionsSide.SHORT.value())
//                .lever("20")
//                .build());
//        log.info("setLeverage {}", JSONObject.toJSONString(setLeverageInfoRes));

//        SetPositionModeRes setPositionModeRes = okxApiHelper.setPositionMode(user, SetPositionModeRequest.builder()
//                .posMode(PosMode.LONG_SHORT.value())
//                .build());
//        log.info("setPositionMode: {}", JSONObject.toJSONString(setPositionModeRes));
//        SetAccountLevelRes setAccountLevelRes = okxApiHelper.setAccountLevel(user, SetAccountLevelRequest.builder()
//                .acctLv("2")
//                .build());
//        log.info("setAccountLevelRes: {}", JSON.toJSONString(setAccountLevelRes));

//        AccountConfigRes accountConfigRes = okxApiHelper.accountConfig(user);
//        log.info("accountConfigRes: {}", JSON.toJSONString(accountConfigRes));
//        BalanceRes balances = okxApiHelper.balances(user, BalanceRequest.builder()
//                .ccy(Gkey.USDT)
//                .build());
//        log.info("balances: {}", JSON.toJSONString(balances));
//        InstrumentRes instruments = okxApiHelper.instruments(InstrumentsRequest.builder()
//                .instType(InstrumentType.SWAP)
//                .build());
//        log.info("{}", JSON.toJSONString(instruments));
//
//        BigDecimal small = BigDecimal.ONE;
//        for (InstrumentRes.Data data : instruments.getData()) {
//            if (small.compareTo(new BigDecimal(data.getLotSz())) >= 0) {
//                small = new BigDecimal(data.getLotSz());
//            }
//        }
//        log.info("small {}", small.stripTrailingZeros().toPlainString());
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        Long time = new Date().getTime();
//        1766817641911
//        String instId = "UNI-USDT-SWAP";
//        String instId = "BTC-USDT-SWAP";
        String instId = "ETH-USDT-SWAP";
        PositionsHisRes res = okxApiHelper.positionsHis(user, PositionsHisRequest.builder()
//                .instId("ETH-USDT-SWAP")
                .instId(instId)
                .mgnMode(MgnMode.CROSS.value())
//                        .before("1766247066971")
//                        .after("1766247066972")
//                        .before("1766850232731")
//                        .before("1767034395100")
                .before("1767046106700")
                .limit("100")
                .build());
        log.info("positionsHis {}", JSONObject.toJSONString(res));
        GetOrderRes orderHis;
        for (PositionsHisRes.Data datum : res.getData()) {
            orderHis = okxApiHelper.getOrderHis(user, GetOrderHisRequest.builder()
                    .instType(InstrumentType.SWAP)
                    .instId(instId)
                    .state("filled")
                    .begin(String.valueOf(datum.getCTime() - Gkey.SECOND_MILLISECOND)) // 因为pos 的ctime是order的uTime
                    .end(datum.getUTime().toString())
                    .build());
            if (orderHis.isOk()) {
                log.info("orderHis 【{}】, pos {}, r: {} of p:{} f:{} ff:{} lp:{}, sp:{}, size {}", datum.getType(),
                        datum.getPosId(),
                        datum.getRealizedPnl(),
                        datum.getPnl(),
                        datum.getFee(),
                        datum.getFundingFee(),
                        datum.getLiqPenalty(),
                        datum.getSettledPnl(),
                        orderHis.getData().size()
                );
                for (GetOrderRes.Data order : orderHis.getData()) {
                    if (order.getPosSide().equals(datum.getPosSide()) && order.getUTime() >= datum.getCTime() && datum.getUTime() >= order.getUTime()) {
                        log.info("{} {} {} {} {} {} {} {} {}, fee {} {}, win {}",
                                order.getOrdId(),
                                order.getCTime(),
                                sdf.format(new Date(order.getCTime())),
                                order.getInstId(),
                                order.getTdMode(), order.getSide(), order.getPosSide(), order.getAccFillSz(), order.getLever(),
                                order.getFee(),
                                order.getFeeCcy(),
                                order.getPnl()
                        );
                    }
                }
            }
        }

//        GetOrderRes order = okxApiHelper.getOrderHis(user, GetOrderHisRequest.builder()
//                        .instType(InstrumentType.SWAP)
//                .instId("ETH-USDT-SWAP")
//                .state("filled")
////                .begin("1765699277862")
////                        1765704377290
////                .begin("1765704377290")
//                .end("1766135781450")
//
////                .limit(10)
//                .build());
//        log.info("open: {}", JSONObject.toJSONString(order));
//        PositionsHisRes fills = okxApiHelper.fills(user, FillsRequest.builder()
//                        .instType(InstrumentType.SWAP)
//                        .ccy("USDT")
//                        .type("8")
//                        .begin("1765381044839")
//                        .end("1765619960236")
//                .build());
//        log.info("fills: {}", JSONObject.toJSONString(fills));
//        AssetBillsRes bills = okxApiHelper.assetBills(user, AssetBillsRequest.builder()
//                .type("8")
//                .build());
//        log.info("bills: {}", JSON.toJSONString(bills));

//        AccountBillsRes abills = okxApiHelper.accountBills(user, AccountBillsRequest.builder()
//                        .instType(InstrumentType.SWAP)
//                        .ccy("USDT")
//                .build());
//        log.info("bills: {}", JSON.toJSONString(abills));

    }

    private boolean geDiff10(Long a, Long b) {
        return true;
    }
// 3080790391614427136
// 3080790391681536000

    @Test
    public void test3() {

        String jsonString = JSONObject.toJSONString(MgnMode.CROSS);
        log.info("jsonString: {}", jsonString);
    }

    @Test
    public void test4() {
        CopyMember user = new CopyMember();
        user.setApikey("sdfsdf");
        user.setSecret("");
        user.setPassphrase("");

        OkxMultiSdkApi okxApiHelper = new OkxMultiSdkApi(true);
        AccountConfigRes accountConfigRes = okxApiHelper.accountConfig(user);
        log.info("accountConfigRes: {}", JSONObject.toJSONString(accountConfigRes));
    }

    @Test
    public void checkAccontProd() {
//        String apikey = "966ace91-97c2-4aee-bd0f-58d7371ed34b";
//        String secret = "B070C78D4EFCB198C081FDCBA8F46F73";
//        String pwd = "Lnb770104.";

        String apikey = "4c228b5e-4e09-4d06-837c-3e643ecb909e";
        String secret = "39E1CD4E8AF08C440DFA9FC3755F2A5C";
        String pwd = "Ddm@198388";


        CopyMember user = new CopyMember();
        user.setApikey(apikey);
        user.setSecret(secret);
        user.setPassphrase(pwd);

        OkxMultiSdkApi okxApiHelper = new OkxMultiSdkApi(false);
//        BigDecimal bigDecimal = checkAccountAndBalance(okxApiHelper, user);
//        log.info("usdt: {}", bigDecimal);
        AccountConfigRes accountConfigRes = okxApiHelper.accountConfig(user);
        log.info("accountConfigRes: {}", JSONObject.toJSONString(accountConfigRes));
    }

    private BigDecimal checkAccountAndBalance(OkxMultiSdkApi okxMultiSdkApi, CopyMember member) {

        AccountConfigRes configRes = okxMultiSdkApi.accountConfig(member);
        log.info("AccountConfig: {} {}", member.getId(), JSONObject.toJSONString(configRes));
        expect(configRes.isOk(), configRes.getMsg());
        AccountConfigRes.Data config = configRes.getData().get(0);

        if (!PosMode.LONG_SHORT.eq(config.getPosMode())) {
            // 设置合约开仓模型
            SetPositionModeRes modeRes = okxMultiSdkApi.setPositionMode(member, SetPositionModeRequest.builder()
                    .posMode(PosMode.LONG_SHORT.value())
                    .build());
            log.info("positionMode: {} {}", member.getId(), JSONObject.toJSONString(modeRes));
            expect(modeRes.isOk(), modeRes.getMsg());
        }

        // 非合约模式就切换
        if (!Gkey.OKX_CA_ACC_LEVEL.equals(config.getAcctLv())) {
            // 设置
            SetAccountLevelRes levelRes = okxMultiSdkApi.setAccountLevel(member, SetAccountLevelRequest.builder()
                    .acctLv(Gkey.OKX_CA_ACC_LEVEL)
                    .build());
            expect(levelRes.isOk(), levelRes.getMsg());
            log.info("accountLevel: {} {}", member.getId(), JSONObject.toJSONString(levelRes));
        }
        // 获取余额
        BalanceRes balances = okxMultiSdkApi.balances(member, BalanceRequest.builder()
                .ccy(Gkey.USDT)
                .build());
        expect(balances.isOk(), balances.getMsg());

        BigDecimal balance = BigDecimal.ZERO;
        if (!balances.getData().isEmpty() && !balances.getData().get(0).getDetails().isEmpty()) {
            balance = balances.getData().get(0).getDetails().get(0).getAvailBal();
        }
        return balance;
    }


    @Test
    public void test5() throws IOException {

        String follows = "/Users/fun/workspace/ywlx/platform-copy/2-db/follow.1.txt";
        String followsAll = new String(Files.readAllBytes(new File(follows).toPath()), StandardCharsets.UTF_8);
        String[] followAllLines = followsAll.split("\n");

        String[] lineArr;
        Map<String, BigDecimal> apiKeyBalMap = new HashMap<>();
        for (String followAllLine : followAllLines) {
            lineArr = followAllLine.split(" ");
            apiKeyBalMap.put(lineArr[0], new BigDecimal(lineArr[1]));
        }
        log.info("apiKeyBalMap: {}", apiKeyBalMap.size());
        longBal(apiKeyBalMap);
    }

    private void longBal(Map<String, BigDecimal> apiKeyBalMap) throws IOException {

        String path = "/Users/fun/workspace/ywlx/platform-copy/2-db/a.txt";
        String all = new String(Files.readAllBytes(new File(path).toPath()), StandardCharsets.UTF_8);
        String[] split = all.split("\n");
        String[] arr;

        OkxMultiSdkApi okxApiHelper = new OkxMultiSdkApi(false);
        CopyMember member;
        BalanceRes balances;
        BigDecimal usdt;

        StringJoiner sj = new StringJoiner("\n");

        BigDecimal rate = BigDecimal.valueOf(0.80);

        List<String> skip = Arrays.asList("43df7f31-c722-4631-87bd-ca8fb39b97b0",
                "fbfce822-5117-472f-9d70-371ca19f1041",
                "edf850c3-d82a-42c6-b110-70eae8c6322f",
                "20fa95c2-4572-47e4-b39b-7559ead0ce3c",
                "697d9bd6-1a8b-40e0-aaa3-9c454c5a68ad"
                );

        Boolean doClose = true;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal reduceTotal = BigDecimal.ZERO;
        PositionsRes positions;
        StringJoiner closeSj = new StringJoiner("\n");
        for (String line : split) {
            arr = line.split(" ");
            log.info("arr: {} {}", arr.length, JSONArray.toJSONString(arr));
            //
            if (!apiKeyBalMap.containsKey(arr[0])) {
                continue;
            }
            if(skip.contains(arr[0])) {
                continue;
            }
            member = new CopyMember();
            member.setApikey(arr[0]);
            member.setSecret(arr[1]);
            member.setPassphrase(arr[2]);
            balances = okxApiHelper.balances(member, BalanceRequest.builder().ccy("USDT").build());
            if (balances.getData().isEmpty() || balances.getData().get(0).getDetails().isEmpty()) {
                usdt = BigDecimal.ZERO;
            } else {
                usdt = balances.getData().get(0).getDetails().get(0).getEq();
            }
            sj.add(member.getApikey() + " " + apiKeyBalMap.get(member.getApikey()).stripTrailingZeros().toPlainString() + " " + usdt.stripTrailingZeros().toPlainString());
            log.info("sj: {} {} {}", member.getApikey(), apiKeyBalMap.get(member.getApikey()).stripTrailingZeros().toPlainString(), usdt.stripTrailingZeros().toPlainString());

            total = total.add(apiKeyBalMap.get(member.getApikey()).subtract(usdt));
            if (usdt.divide(apiKeyBalMap.get(member.getApikey()), 4, RoundingMode.FLOOR).compareTo(rate) < 0) {
                reduceTotal = reduceTotal.add(apiKeyBalMap.get(member.getApikey()).subtract(usdt));
                positions = okxApiHelper.positions(member, PositionsRequest.builder().instType(InstrumentType.SWAP).build());
                if (positions.isOk() && !positions.getData().isEmpty()) {
                    ClosePositionRes closePositionRes;
                    for (PositionsRes.Data pos : positions.getData()) {
//                        closeSj.add(StrUtil.format("close {} {} {} {}", member.getApikey(), pos.getInstId(), pos.getMgnMode(), pos.getPosSide()));
                        if(doClose){
                            closePositionRes = okxApiHelper.closePosition(member, ClosePositionRequest.builder()
                                    .instId(pos.getInstId())
                                    .mgnMode(MgnMode.of(pos.getMgnMode()))
                                    .posSide(PositionsSide.of(pos.getPosSide()))
                                    .tag("a48295876e71BCDE")
                                    .build());
                            if(closePositionRes.isOk()){
                                closeSj.add(StrUtil.format("close {} {} {} {}", member.getApikey(), pos.getInstId(), pos.getMgnMode(), pos.getPosSide()));
                            }
                        }else{
                            closeSj.add(StrUtil.format("close {} {} {} {}", member.getApikey(), pos.getInstId(), pos.getMgnMode(), pos.getPosSide()));
                        }
                    }
                }
            }
        }

        log.info("Full: {} \n {}", sj.length(), sj.toString());
        log.info("closeSj {}", closeSj.toString());
        log.info("Total {}", total.stripTrailingZeros().toPlainString());
        log.info("reduceTotal {}", reduceTotal.stripTrailingZeros().toPlainString());
    }

    @Test
    public void test6() throws IOException {
        String follows = "/Users/fun/workspace/ywlx/platform-copy/2-db/follow.1.txt";
        String followsAll = new String(Files.readAllBytes(new File(follows).toPath()), StandardCharsets.UTF_8);
        String[] followAllLines = followsAll.split("\n");
        String[] lineArr;
        Map<String, BigDecimal> apiKeyBalMap = new HashMap<>();
        for (String followAllLine : followAllLines) {
            lineArr = followAllLine.split(" ");
            apiKeyBalMap.put(lineArr[0], new BigDecimal(lineArr[1]));
        }
        log.info("apiKeyBalMap: {}", apiKeyBalMap.size());

        Set<String> allKeys = apiKeyBalMap.keySet();

        String path = "/Users/fun/workspace/ywlx/platform-copy/2-db/a.txt";
        String all = new String(Files.readAllBytes(new File(path).toPath()), StandardCharsets.UTF_8);
        String[] split = all.split("\n");
        String[] arr;

        for (String line2 : split) {
            arr = line2.split(" ");
            if (allKeys.contains(arr[0])) {
                allKeys.remove(arr[0]);
            }
        }
        log.info("allKeys: {}", JSONArray.toJSONString(allKeys));
    }

    @Test
    public void test7() throws IOException {
        Map<String, BigDecimal> apikeyBalMap = new HashMap<>();
        apikeyBalMap.put("7f6905e1-4cb2-43dc-a331-5604127e17ed", BigDecimal.valueOf(8000));
        longBal(apikeyBalMap);
    }

    @Test
    public void test8() throws IOException {
//        String id = "3230506911753428992";
        OkxMultiSdkApi okxApiHelper = new OkxMultiSdkApi(false);
        CopyMember user = new CopyMember();
        user.setApikey("bca2de5b-105b-4462-b4d5-3ba57d3c5261");
        user.setSecret("B3DEC6363AC68CF7E51ECD413179C781");
        user.setPassphrase("Qq123456@");



//        user.setApikey("c4c0af7f-77f4-42a8-ad1f-9a77a05ef9fe");
//        user.setSecret("3591A1B620163A93E08C74AFB79848B0");
//        user.setPassphrase("Qq123456@");

        GetOrderRes order1 = okxApiHelper.getOrder(user, GetOrderRequest.builder()
                .instId("SUI-USDT-SWAP")
                .ordId("3230506911753428992")
                .build());
        log.info("order1: {}", order1);
    }

    @Test
    public void test9() throws IOException {

        CopyMember user = new CopyMember();
        // 11169 8000
//        user.setApikey("7f288b30-fa3f-484a-a61c-c58f15a51023");
//        user.setSecret("52A9E360190DE337FC3668540D713A9E");
//        user.setPassphrase("Hs223166@");
        // 11166 1000
        user.setApikey("7f6905e1-4cb2-43dc-a331-5604127e17ed");
        user.setSecret("A2DA88AC8886F2F022A20F5F1952C3EE");
        user.setPassphrase("Yh666888$");

        OkxMultiSdkApi okxApiHelper = new OkxMultiSdkApi(false);
        AssetBillsRes assetBillsRes = okxApiHelper.assetBillsHis(user, AssetBillsRequest.builder().build());
        log.info("assetBillsRes: {}", assetBillsRes);
//        BalanceRes balances = okxApiHelper.balances(user, BalanceRequest.builder().ccy("USDT").build());
//        log.info("balances: {}", balances);
//        PositionsRes positions = okxApiHelper.positions(user, PositionsRequest.builder().instType(InstrumentType.SWAP).build());
//        log.info("positions: {}", positions);

    }
}








