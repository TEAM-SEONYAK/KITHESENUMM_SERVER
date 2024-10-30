package org.sopt.seonyakServer.domain.university.repository;

import java.util.Optional;
import org.sopt.seonyakServer.domain.university.model.UnivCode;
import org.springframework.data.repository.CrudRepository;

public interface UnivCodeRepository extends CrudRepository<UnivCode, String> {
    Optional<UnivCode> findByUnivMail(final String univMail);
}
