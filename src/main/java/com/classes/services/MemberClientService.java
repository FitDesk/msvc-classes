package com.classes.services;

import com.classes.dtos.external.MemberInfoDTO;

import java.util.List;
import java.util.UUID;

public interface MemberClientService {

    MemberInfoDTO getMemberInfo(UUID memberId);
    List<MemberInfoDTO> getMembersInfo(List<UUID> memberIds);

}
