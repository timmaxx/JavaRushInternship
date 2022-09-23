package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class PlayerSpecification implements Specification< Player> {
    String namePattern;
    String titlePattern;
    String race;
    String profession;

    Boolean banned;
    Long after;
    Long before;
    Integer minExperience;
    Integer maxExperience;
    Integer minLevel;
    Integer maxLevel;

    PlayerSpecification (
            String namePattern, String titlePattern,
            String race, String profession,
            Boolean banned,
            Long after, Long before,
            Integer minExperience, Integer maxExperience,
            Integer minLevel, Integer maxLevel
    ) {
        this.namePattern = namePattern;
        this.titlePattern = titlePattern;
        this.race = race;
        this.profession = profession;
        this.banned = banned;
        this.after = after;
        this.before = before;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public Predicate toPredicate(
            Root< Player> root,
            CriteriaQuery< ?> query,
            CriteriaBuilder criteriaBuilder) {
        Predicate predicate;
        Predicate predicate2 = criteriaBuilder.conjunction();

        if ( namePattern != null && !namePattern.isEmpty( )) {
            predicate = criteriaBuilder.like( root.get( "name"), "%" + namePattern + "%");
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }
        if ( titlePattern != null && !titlePattern.isEmpty( )) {
            predicate = criteriaBuilder.like( root.get( "title"), "%" + titlePattern + "%");
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }
        if ( race != null) {
            predicate = criteriaBuilder.equal( root.get( "race"), Race.valueOf( race));
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }
        if ( profession != null) {
            predicate = criteriaBuilder.equal( root.get( "profession"), Profession.valueOf( profession));
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }
        if ( banned != null) {
            predicate = criteriaBuilder.equal( root.get( "banned"), banned);
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }
        //   Обрати внимание. 8.
        //   При передаче границ диапазонов (параметры с именами, которые начинаются на «min» или «max»)
        // границы нужно использовать включительно.
        // Будем использовать методы с именами, заканчивающимися на "OrEqualTo".
        if ( after != null) {
            predicate = criteriaBuilder.greaterThanOrEqualTo( root.get( "birthday"), new Date( after));
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }
        if ( before != null) {
            predicate = criteriaBuilder.lessThanOrEqualTo( root.get( "birthday"), new Date( before));
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }
        if ( minExperience != null) {
            predicate = criteriaBuilder.greaterThanOrEqualTo( root.get( "experience"), minExperience);
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }
        if ( maxExperience != null) {
            predicate = criteriaBuilder.lessThanOrEqualTo( root.get( "experience"), maxExperience);
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }
        if ( minLevel != null) {
            predicate = criteriaBuilder.greaterThanOrEqualTo( root.get( "level"), minLevel);
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }
        if ( maxLevel != null) {
            predicate = criteriaBuilder.lessThanOrEqualTo( root.get( "level"), maxLevel);
            predicate2 = criteriaBuilder.and( predicate2, predicate);
        }

        return predicate2;
    }
}
