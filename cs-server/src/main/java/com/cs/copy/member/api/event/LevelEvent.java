package com.cs.copy.member.api.event;

import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.entity.TeamLevel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/9/29 21:09
 */
@Getter
public class LevelEvent extends ApplicationEvent {
    private Member member;
    private TeamLevel level;

    public LevelEvent(Object source, Member member, TeamLevel level) {
        super(source);
        this.member = member;
        this.level = level;
    }
}
