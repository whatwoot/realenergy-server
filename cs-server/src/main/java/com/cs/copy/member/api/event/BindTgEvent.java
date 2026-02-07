package com.cs.copy.member.api.event;

import com.cs.copy.member.api.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/9/29 21:09
 */
@Getter
public class BindTgEvent extends ApplicationEvent {
    private Member member;

    public BindTgEvent(Object source, Member member) {
        super(source);
        this.member = member;
    }
}
