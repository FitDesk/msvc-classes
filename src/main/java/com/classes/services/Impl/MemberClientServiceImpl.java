package com.classes.services.Impl;

import com.classes.dtos.external.MemberInfoDTO;
import com.classes.services.MemberClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberClientServiceImpl implements MemberClientService {
    
    private final RestTemplate restTemplate;
    private static final String MEMBERS_SERVICE_URL = "http://msvc-members";
    
    @Override
    public MemberInfoDTO getMemberInfo(UUID memberId) {
        try {
            String url = MEMBERS_SERVICE_URL + "/members/" + memberId;
            log.debug("🔗 Llamando al microservicio de members: {}", url);
            
            ResponseEntity<MemberInfoDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    MemberInfoDTO.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("❌ Miembro {} no encontrado en el microservicio de members", memberId);
            return null;
        } catch (Exception e) {
            log.error("❌ Error al consultar el microservicio de members para el miembro {}: {}", 
                    memberId, e.getMessage());
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
