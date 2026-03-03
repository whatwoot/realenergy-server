package com.cs.energy.member.api.event;

import com.cs.energy.member.api.entity.InviteLevel;
import com.cs.energy.member.api.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/12/19 01:26
 */
@Getter
public class InviteLvEvent extends ApplicationEvent {
    private Member member;
    private InviteLevel nextLeve;

    public InviteLvEvent(Object source, Member member, InviteLevel nextLeve) {
        super(source);
        this.member = member;
        this.nextLeve = nextLeve;
    }
}
