package com.cs.energy.member.api.event;

import com.cs.energy.member.api.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @authro fun
 * @date 2025/12/25 22:38
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberWizardEndEvent {
    private transient Object source;
    private Member member;
}
