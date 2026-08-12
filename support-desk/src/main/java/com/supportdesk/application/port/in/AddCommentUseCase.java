package com.supportdesk.application.port.in;

import com.supportdesk.application.command.AddCommentCommand;
import com.supportdesk.domain.model.Comment;
import org.springframework.stereotype.Service;

@Service
public interface AddCommentUseCase {
    Comment addComment(AddCommentCommand command);
}