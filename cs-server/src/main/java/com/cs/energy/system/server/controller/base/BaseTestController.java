package com.cs.energy.system.server.controller.base;

import com.cs.sp.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import static com.cs.sp.common.WebAssert.hasPermission;

/**
 * @authro fun
 * @date 2025/5/23 15:41
 */
public class BaseTestController {

    @Autowired
    private Environment env;

    protected void only4Test() {
        hasPermission(!env.acceptsProfiles(Profiles.of(Constant.PROD)));
    }
}
