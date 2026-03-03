package com.cs.energy.member.api.event;

import com.cs.energy.member.api.entity.MemberWallet;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2025/2/19 00:11
 */
@Getter
public class AddWalletEvent extends ApplicationEvent {

    private MemberWallet memberWallet;

    public AddWalletEvent(Object source, MemberWallet memberWallet) {
        super(source);
        this.memberWallet = memberWallet;
    }
}
