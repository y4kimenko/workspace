package by.diplom.workspace.worker.notification;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
@RequiredArgsConstructor
public class UserNotificationController {

}
