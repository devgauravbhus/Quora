package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;
    @Autowired
    private CommonBusinessService commonBusinessService;
    //This endpoint is used to create Question in the QuoraApplication. Any user can go and access this endpoint and create a question
    //This endpoint requests for the attributes in QuestionRequest and accessToken in the authorization header	    //This endpoint requests for the attributes in QuestionRequest and accessToken in the authorization heade
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQustion(QuestionRequest questionRequest, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {

        if (authorization == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Checking for the user associated to the access found in the header , returns exception if not found.
        UserAuthTokenEntity userAuthEntity = commonBusinessService.getUser(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if ((userAuthEntity.getLogoutAt() != null)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details.");
            } else {
                QuestionEntity questionEntity = new QuestionEntity();
                questionEntity.setContent(questionRequest.getContent());
                questionEntity.setDate(LocalDateTime.now());
                questionEntity.setUser(userAuthEntity.getUser());
                questionEntity.setUuid(UUID.randomUUID().toString());
                questionEntity = questionBusinessService.createQuestion(questionEntity);
                QuestionResponse questionResponse = new QuestionResponse();
                questionResponse.setId(questionEntity.getUuid());
                questionResponse.setStatus("QUESTION CREATED");
                return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
            }
        }


    }
    //This endpoint is see all the questions posted in the quora application
    //This endpoint is accessed by any user by just providing the access token as input in the authorization header	    //This endpoint is accessed by any user by just providing the access token as input in the authorization header
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> getAllQustion(@RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionDetailsResponse> responseList = new ArrayList<QuestionDetailsResponse>();
        if (authorization == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Checking for the user associated to the access found in the header , returns exception if not found.
        UserAuthTokenEntity userAuthEntity = commonBusinessService.getUser(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            /*
                Need to add this condition as we have needed to check wehther user's token is expired or not
                (userAuthEntity.getExpiresAt() != null && LocalDateTime.now().isAfter(userAuthEntity.getExpiresAt())) ||
             */
            if ((userAuthEntity.getLogoutAt() != null)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details.");
            } else {
                List<QuestionEntity> list = questionBusinessService.getAllQuestion();
                for (QuestionEntity q : list) {
                    QuestionDetailsResponse response = new QuestionDetailsResponse();
                    response.setId(q.getUuid());
                    response.setContent(q.getContent());
                    responseList.add(response);
                }
            }
        }
        System.out.println("here");
        return new ResponseEntity(responseList, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> updateQuestion(QuestionEditRequest questionEditRequest, @PathVariable("questionId") String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {
        QuestionEditResponse questionResponse = new QuestionEditResponse();
        if (authorization == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Checking for the user associated to the access found in the header , returns exception if not found.
        UserEntity userAuthEntity = commonBusinessService.getUser(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            /*
                Need to add this condition as we have needed to check wehther user's token is expired or not
                (userAuthEntity.getExpiresAt() != null && LocalDateTime.now().isAfter(userAuthEntity.getExpiresAt())) ||
             */
            if ((userAuthEntity.getLogoutAt() != null)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details.");
            } else {
                QuestionEntity questionEntity = new QuestionEntity();
                questionEntity.setUuid(questionId);
                questionEntity.setContent(questionEditRequest.getContent());
                questionEntity.setUser(userAuthEntity.getUser());
                questionEntity = questionBusinessService.updateQuestion(questionEntity);
                if (questionEntity != null) {
                    questionResponse.setId(questionEntity.getUuid());
                    questionResponse.setStatus("QUESTION EDITED");
                }
            }
            return new ResponseEntity<QuestionEditResponse>(questionResponse, HttpStatus.OK);
        }
    }
    //The admin or the owner of the Question has a privilege of deleting the question
    //This endpoint requests for the questionUuid to be deleted and the questionowner or admin accesstoken in the authorization header	    //This endpoint requests for the questionUuid to be deleted and the questionowner or admin accesstoken in the authorization header
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {
        QuestionDeleteResponse questionResponse = new QuestionDeleteResponse();
        if (authorization == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Checking for the user associated to the access found in the header , returns exception if not found.
        UserAuthTokenEntity userAuthEntity = commonBusinessService.getUser();
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            /*
                Need to add this condition as we have needed to check wehther user's token is expired or not
                (userAuthEntity.getExpiresAt() != null && LocalDateTime.now().isAfter(userAuthEntity.getExpiresAt())) ||
             */
            if ((userAuthEntity.getLogoutAt() != null)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details.");
            } else {
                QuestionEntity questionEntity = new QuestionEntity();
                questionEntity.setUuid(questionId);
                questionEntity.setUser(userAuthEntity.getUser());
                questionEntity = questionBusinessService.deleteQuestion(questionEntity);
                if (questionEntity != null) {
                    questionResponse.setId(questionEntity.getUuid());
                    questionResponse.setStatus("QUESTION DELETED");
                }
            }
            return new ResponseEntity<QuestionDeleteResponse>(questionResponse, HttpStatus.OK);
        }

    }

    //**getAllQuestionsByUser**//
    //This method returns all the questions posted by user as a list and can be accessed by an user.
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> getAllQustion(@PathVariable("userId") String userId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionDetailsResponse> responseList = new ArrayList<QuestionDetailsResponse>();
        UserEntity userEntity = commonBusinessService.getUser(userId, authorization);
        //getUserByUuid(userId);
        if (authorization == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Checking for the user associated to the access found in the header , returns exception if not found.
        UserAuthTokenEntity userAuthEntity = commonBusinessService.getUser(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            /*
                Need to add this condition as we have needed to check wehther user's token is expired or not
                (userAuthEntity.getExpiresAt() != null && LocalDateTime.now().isAfter(userAuthEntity.getExpiresAt())) ||
             */
            if ((userAuthEntity.getLogoutAt() != null)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details.");
            } else {
                List<QuestionEntity> list = questionBusinessService.getAllQuestion();
                for (QuestionEntity q : list) {
                    QuestionDetailsResponse response = new QuestionDetailsResponse();
                    response.setId(q.getUuid());
                    response.setContent(q.getContent());
                    responseList.add(response);
                }
            }
        }
        System.out.println("here");
        return new ResponseEntity(responseList, HttpStatus.OK);

    }
}
