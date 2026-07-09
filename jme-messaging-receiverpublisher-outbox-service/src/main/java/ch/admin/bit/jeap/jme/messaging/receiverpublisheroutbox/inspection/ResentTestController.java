package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.inspection;


import ch.admin.bit.jeap.jme.messaging.common.MessageContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inspect/resenttest")
@Transactional(readOnly = true)
class ResentTestController {

    private final ResentTestRepository resentTestRepository;

    @GetMapping
    List<MessageContext> getMessages() {
        return resentTestRepository.getMessages();
    }

}
