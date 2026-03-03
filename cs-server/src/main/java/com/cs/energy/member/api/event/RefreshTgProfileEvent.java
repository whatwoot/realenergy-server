package com.cs.energy.member.api.event;

import com.cs.energy.member.api.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/9/29 21:09
 */
@Getter
public class RefreshTgProfileEvent extends ApplicationEvent {
    private Member member;

    public RefreshTgProfileEvent(Object source, Member member) {
        super(source);
        this.member = member;
    }
}
