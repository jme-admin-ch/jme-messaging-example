package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.inspection;


import ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.Declaration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inspect/declaration")
@Transactional(readOnly = true)
class DeclarationInspectionController {

    private final DeclarationInspectionRepository declarationInspectionRepository;

    @GetMapping
    List<String> listTodaysDeclarations() {
        return declarationInspectionRepository.
                findByCreatedAtAfterOrderByCreatedAtDesc(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)).stream()
                .map(Declaration::toString)
                .collect(Collectors.toList());
    }

}
