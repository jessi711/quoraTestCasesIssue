package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;


@Service
public class QuestionService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);

        if (userAuthTokenEntity == null) throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        //if(!userAuthTokenEntity.getUser().getRole().equalsIgnoreCase("admin")) throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        if (userAuthTokenEntity.getLogoutAt() != null) throw new AuthorizationFailedException("ATHR-002", "User is signed out");

        questionEntity.setUser(userAuthTokenEntity.getUser());
        //questionDao.createQuestion(questionEntity);
     System.out.println(questionEntity.getContent()+" gupta");
        return questionDao.createQuestion(questionEntity);
    }

    public List<QuestionEntity> getAllQuestion(final String authorization)
        throws AuthorizationFailedException{
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);

        if (userAuthTokenEntity == null) throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        if (userAuthTokenEntity.getLogoutAt() != null) throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        return questionDao.getAllQuestion();

    }
    @Transactional(propagation = Propagation.REQUIRED)
     public QuestionEntity updateQuestion(final String uuId,final String content, final String authorization) throws AuthorizationFailedException, InvalidQuestionException{
         UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
         if (userAuthTokenEntity == null) throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
         if (userAuthTokenEntity.getLogoutAt()!=null) throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to edit an question");

         if(!userAuthTokenEntity.getUser().getUuid().equalsIgnoreCase(questionDao.getQuestion(uuId).getUser().getUuid()))  throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
         if(questionDao.getQuestion(uuId) == null) throw new InvalidQuestionException("ANS-001","Entered question uuid does not exist");

         QuestionEntity questionEntity = new QuestionEntity();
         questionEntity.setId(questionDao.getQuestion(uuId).getId());
         questionEntity.setUuid(uuId);
         questionEntity.setContent(content);
         questionEntity.setDate(ZonedDateTime.now());
         questionEntity.setUser(userAuthTokenEntity.getUser());

         return questionDao.update(questionEntity);

     }
}
