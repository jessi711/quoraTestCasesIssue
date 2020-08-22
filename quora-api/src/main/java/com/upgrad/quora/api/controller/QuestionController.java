package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody QuestionRequest questionRequest,
                                                       @RequestHeader("authorization")
                                                        String authorization)
         throws AuthorizationFailedException, UserNotFoundException {
        QuestionEntity questionEntity = new QuestionEntity();
        //adding data to the entity object for db
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        System.out.println("jessi --- "+questionEntity.getContent()+ " "+questionEntity.getUuid());
        QuestionEntity createQuestionEntity = questionService.createQuestion(questionEntity, authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createQuestionEntity.getUuid()).status("Question Successfully Added");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

 }

    @RequestMapping(method = RequestMethod.GET, path="/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionEntity>> getAllQuestion(@RequestHeader ("authorization") String authorization)
            throws AuthorizationFailedException, UserNotFoundException
    {
         List<QuestionEntity> questionEntity = questionService.getAllQuestion(authorization);
         return new ResponseEntity<List<QuestionEntity>>(questionEntity, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path="/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(@RequestHeader ("authorization") String authorization,
                                                             @PathVariable ("questionId") final String uuId, final QuestionEditRequest questionEditRequest)
                throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException
    {
        final QuestionEntity editQuestion = questionService.updateQuestion(uuId,questionEditRequest.getContent(), authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editQuestion.getUuid()).status("Question Edited");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
}
