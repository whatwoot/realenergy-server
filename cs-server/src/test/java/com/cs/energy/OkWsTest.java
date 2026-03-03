package com.cs.energy;

import com.cs.web.spring.helper.hashids.HashidsHelper;
import com.cs.web.spring.helper.hashids.HashidsProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.UUID;

/**
 * @authro fun
 * @date 2025/11/24 16:29
 */
@Slf4j
public class OkWsTest {
    @Test
    public void test() {

    }
    @Test
    public void test2() {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
        System.out.println(uuid.toString().replace("-", ""));
    }

    @Test
    public void test3() {
        // salt: a09ba52e14914655911a88c0fd17ac65
        // minLength: 6
        // alphabet: ABCD2345EFGHJK6789MNPQRSTUVWXYZ
        HashidsProperties prop = new HashidsProperties();
        prop.setSalt("a09ba52e14914655911a88c0fd17ac65");
        prop.setMinLength(6);
        prop.setAlphabet("ABCD2345EFGHJK6789MNPQRSTUVWXYZ");
        HashidsHelper hashidsHelper = new HashidsHelper(prop);
        String encode = hashidsHelper.encode(1L);
        log.info("1 {} ", encode);
    }
}
