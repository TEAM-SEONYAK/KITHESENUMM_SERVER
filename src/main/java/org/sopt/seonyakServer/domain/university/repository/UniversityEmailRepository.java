package org.sopt.seonyakServer.domain.university.repository;

import java.util.Optional;
import org.sopt.seonyakServer.domain.university.model.UniversityEmail;
import org.sopt.seonyakServer.global.exception.enums.ErrorType;
import org.sopt.seonyakServer.global.exception.model.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityEmailRepository extends JpaRepository<UniversityEmail, Long> {
    Optional<UniversityEmail> findUniversityEmailByUnivName(String univName);

    boolean existsByUnivName(String univName);

    default UniversityEmail findUniversityEmailByUnivNameOrThrow(String univName) {
        return findUniversityEmailByUnivName(univName)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_UNIV_EMAIL_DOMAIN_ERROR));
    }
}
