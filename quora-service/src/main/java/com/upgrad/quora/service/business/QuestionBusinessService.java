package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDAO questionDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) throws NullPointerException {
        questionEntity = questionDAO.createQuestion(questionEntity);
        if (questionEntity.getId() != null)
            return questionEntity;
        else
            return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestion() throws NullPointerException {
        List<QuestionEntity> questionEntityList = questionDAO.getAllQuestion();
        return questionEntityList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionbyUser(String user_uuid) throws NullPointerException {
        List<QuestionEntity> questionEntityList = questionDAO.getAllQuestionByUser(user_uuid);
        return questionEntityList;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity getQuestionByUuid(String uuid) throws NullPointerException {
        QuestionEntity questionEntity1 = questionDAO.getQuestionByUuid(uuid);
        return questionEntity1;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestion(QuestionEntity questionEntity) throws NullPointerException, AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity1 = getQuestionByUuid(questionEntity.getUuid());
        if (questionEntity1 != null) {
            if (questionEntity1.getUser() != null && questionEntity1.getUser().getId() == questionEntity.getUser().getId()) {
                questionEntity1 = questionDAO.updateQuestion(questionEntity);
            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
            }
        } else {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        return questionEntity1;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(QuestionEntity questionEntity) throws NullPointerException, AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity1 = getQuestionByUuid(questionEntity.getUuid());
        if (questionEntity1 != null) {
            if (questionEntity1.getUser() != null && (questionEntity1.getUser().getId() == questionEntity.getUser().getId()
                    || !questionEntity1.getUser().getRole().equals("nonadmin"))) {
                questionDAO.deleteQuestion(questionEntity);
            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            }
        } else {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        return questionEntity1;
    }

}
