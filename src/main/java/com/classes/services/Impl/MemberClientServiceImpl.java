package com.classes.services.Impl;

import com.classes.clients.MemberFeignClient;
import com.classes.dtos.external.MemberInfoDTO;
import com.classes.services.MemberClientService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberClientServiceImpl implements MemberClientService {
    
    private final MemberFeignClient memberFeignClient;
    
    @Override
    public MemberInfoDTO getMemberInfo(UUID memberId) {
        try {
            log.info("🔗 Consultando información del miembro {} usando Feign", memberId);
            MemberInfoDTO result = memberFeignClient.getMemberInfo(memberId);
            log.info("✅ Información recibida: {} {}", result.getFirstName(), result.getLastName());
            return result;
        } catch (FeignException.NotFound e) {
            log.error("❌ Miembro {} no encontrado en msvc-members (404)", memberId);
            return null;
        } catch (FeignException e) {
            log.error("❌ Error Feign al consultar miembro {}: {} - {}",
                    memberId, e.status(), e.getMessage());
            log.error("❌ Response body: {}", e.contentUTF8());
            return null;
        } catch (Exception e) {
            log.error("❌ Error inesperado al consultar miembro {}: {}",
                    memberId, e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public List<MemberInfoDTO> getMembersInfo(List<UUID> memberIds) {
        List<MemberInfoDTO> members = new ArrayList<>();
        for (UUID memberId : memberIds) {
            MemberInfoDTO member = getMemberInfo(memberId);
            if (member != null) {
                members.add(member);
            }
        }
        return members;
    }
}
