package com.upgrad.quora.service.dao;

import antlr.LexerSharedInputState;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }
    public List<QuestionEntity> getAllQuestion() {
        try {
            return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
        }
        catch (NoResultException nre) {
            return null;
        }
    }
    public QuestionEntity getQuestion(final String uuId) {
        try {
            return entityManager.createNamedQuery("questionById", QuestionEntity.class).setParameter("uuid", uuId).getSingleResult();
        }catch (NoResultException nre) {
            return null;
        }
    }
    public QuestionEntity update(final QuestionEntity questionEntity) {
        return entityManager.merge(questionEntity);
    }
}
