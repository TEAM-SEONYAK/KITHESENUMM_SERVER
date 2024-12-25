package org.sopt.seonyakServer.domain.senior.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.seonyakServer.domain.senior.dto.SeniorCardProfileResponse;
import org.sopt.seonyakServer.domain.senior.dto.SeniorFilterResponse;
import org.sopt.seonyakServer.domain.senior.dto.SeniorNameResponse;
import org.sopt.seonyakServer.domain.senior.dto.SeniorProfileRequest;
import org.sopt.seonyakServer.domain.senior.dto.SeniorProfileResponse;
import org.sopt.seonyakServer.domain.senior.model.PreferredTimeList;
import org.sopt.seonyakServer.domain.senior.service.SeniorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SeniorController {

    private final SeniorService seniorService;

    @PatchMapping("/senior/profile")
    public ResponseEntity<Void> patchProfile(
            @RequestBody SeniorProfileRequest seniorProfileRequest
    ) {
        seniorService.patchSeniorProfile(seniorProfileRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/senior/time/{seniorId}")
    public ResponseEntity<PreferredTimeList> getPreferredTime(
            @PathVariable final Long seniorId
    ) {
        return ResponseEntity.ok(seniorService.getSeniorPreferredTime(seniorId));
    }

    @GetMapping("/senior/search")
    public ResponseEntity<SeniorFilterResponse> searchFieldPosition(
            @RequestParam(required = false) final List<String> field,
            @RequestParam(required = false) final List<String> position
    ) {
        return ResponseEntity.ok(seniorService.searchSeniorFieldPosition(field, position));
    }

    @GetMapping("/senior/{seniorId}")
    public ResponseEntity<SeniorProfileResponse> getSeniorProfile(
            @PathVariable final Long seniorId
    ) {
        return ResponseEntity.ok(seniorService.getSeniorProfile(seniorId));
    }

    @GetMapping("/senior/card/{seniorId}")
    public ResponseEntity<SeniorCardProfileResponse> getSeniorCardProfile(
            @PathVariable final Long seniorId
    ) {
        return ResponseEntity.ok(seniorService.getSeniorCardProfile(seniorId));
    }

    @GetMapping("/auth/token/validate")
    public ResponseEntity<SeniorNameResponse> validTokenExpired() {
        return ResponseEntity.ok(seniorService.validTokenExpired());
    }
}
