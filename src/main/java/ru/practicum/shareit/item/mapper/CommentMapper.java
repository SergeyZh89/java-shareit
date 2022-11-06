package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                commentDto.getItemId(),
                commentDto.getAuthorId(),
                commentDto.getAuthorName(),
                commentDto.getCreated());
    }

    public static ItemDto.ItemComments toItemComment(Comment comment) {
        return new ItemDto.ItemComments(comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthorId(),
                comment.getAuthorName(),
                comment.getCreated());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthorId(),
                comment.getAuthorName(),
                comment.getCreated());
    }
}