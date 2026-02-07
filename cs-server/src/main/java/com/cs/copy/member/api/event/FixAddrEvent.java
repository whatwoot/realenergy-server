package com.cs.copy.member.api.event;

import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.enums.ChainEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/9/29 21:09
 */
@Getter
public class FixAddrEvent extends ApplicationEvent {
    private Member member;
    private ChainEnum chainEnum;

    public FixAddrEvent(Object source, Member member, ChainEnum chainEnum) {
        super(source);
        this.member = member;
        this.chainEnum = chainEnum;
    }
}
