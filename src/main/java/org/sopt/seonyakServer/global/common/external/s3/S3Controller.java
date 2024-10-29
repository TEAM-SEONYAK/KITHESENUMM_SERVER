package org.sopt.seonyakServer.global.common.external.s3;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.seonyakServer.global.common.external.s3.dto.PreSignedUrlResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;

    @GetMapping("/image/profile")
    public ResponseEntity<PreSignedUrlResponse> getProfilePreSignedUrl() {
        return ResponseEntity.ok(s3Service.getImageUploadPreSignedUrl());
    }

    @GetMapping("/image/businesscard")
    public ResponseEntity<PreSignedUrlResponse> getBusinesscardPreSignedUrl() {
        return ResponseEntity.ok(s3Service.getUploadBusinesscardPreSignedUrl());
    }

    @PatchMapping("/profile-image")
    public ResponseEntity<Void> uploadProfileImage(
            @RequestParam("profileImage") @Valid MultipartFile profileImage
    ) {
        s3Service.uploadProfile(profileImage);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/businesscard-image")
    public ResponseEntity<Void> uploadBusinessCardImage(
            @RequestParam("businessCardImage") @Valid MultipartFile businessCardImage
    ) {
        s3Service.uploadBusinessCard(businessCardImage);
        return ResponseEntity.ok().build();
    }
}
