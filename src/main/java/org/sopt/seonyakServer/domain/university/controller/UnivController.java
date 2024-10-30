package org.sopt.seonyakServer.domain.university.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.seonyakServer.domain.university.dto.SearchDeptResponse;
import org.sopt.seonyakServer.domain.university.dto.SearchUnivResponse;
import org.sopt.seonyakServer.domain.university.dto.UnivVerifyCodeRequest;
import org.sopt.seonyakServer.domain.university.dto.UnivVerifyRequest;
import org.sopt.seonyakServer.domain.university.service.UnivService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class UnivController {

    private final UnivService univService;

    @GetMapping("search/univ")
    public ResponseEntity<SearchUnivResponse> searchUniv(
            @RequestParam final String univName
    ) {
        return ResponseEntity.ok(univService.searchUniv(univName));
    }

    @GetMapping("search/dept")
    public ResponseEntity<List<SearchDeptResponse>> searchDept(
            @RequestParam final String univName,
            @RequestParam final String deptName
    ) {
        return ResponseEntity.ok(univService.searchDept(univName, deptName));
    }

    @PostMapping("/univ/verify")
    public ResponseEntity<Void> verify(
            @RequestBody UnivVerifyRequest univVerifyRequest
    ) {
        univService.verifyEmail(univVerifyRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/univ/verifycode")
    public ResponseEntity<Void> verifyCode(
            @RequestBody UnivVerifyCodeRequest univVerifyCodeRequest
    ) {
        univService.verifyCode(univVerifyCodeRequest);
        return ResponseEntity.ok().build();
    }
}
