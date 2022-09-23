package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional( readOnly = true)
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService( PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    public Player findById( Long id) {
        return playerRepository.findById( id).orElse( null);
    }

    public List< Player> findAllWithCriterions (
            String namePattern, String titlePattern,
            String race, String profession,
            // Race race, Profession profession,
            Boolean banned,
            Long after, Long before,
            Integer minExperience, Integer maxExperience,
            Integer minLevel, Integer maxLevel,
            Pageable pageable) {

        //System.out.println( "3 pageable = " + pageable);
        return playerRepository.findAll(
                new PlayerSpecification(
                        namePattern, titlePattern,
                        // race.toString( ), profession.toString( ),
                        race, profession,
                        banned,
                        after, before,
                        minExperience, maxExperience,
                        minLevel, maxLevel
                        ),
                pageable)
                .getContent( );
    }

    public long countWithCriterions(
            String namePattern, String titlePattern,
            String race, String profession,
            // Race race, Profession profession,
            Boolean banned,
            Long after, Long before,
            Integer minExperience, Integer maxExperience,
            Integer minLevel, Integer maxLevel) {

        return playerRepository.count(
                new PlayerSpecification(
                        namePattern, titlePattern,
                        // race.toString( ), profession.toString( ),
                        race, profession,
                        banned,
                        after, before,
                        minExperience, maxExperience,
                        minLevel, maxLevel
                ));
    }

    @Transactional
    public void insert( Player player) {
        // player.setId( null);
        player.checkNullFieldsForInserting( );
        if ( player.getBanned( ) == null) {
            player.setBanned( false);
        }
        player.checkAllFieldsValidAndCalcLevels( );
        playerRepository.save( player);
    }

    @Transactional
    public Player update( long id, Player updatedPlayer) {
        Player player = playerRepository.findById( id).orElse(null);

        if ( player == null) {
            throw new RuntimeException( "Player with id is equal " + id + " not found!");
        }

        if ( updatedPlayer.areAllFieldsNulls( )) {
            return player;
        }
        updatedPlayer.setId( id);
        updatedPlayer.fillVoidFieldsFromAnotherPlayer( player);
        updatedPlayer.checkAllFieldsValidAndCalcLevels( );
        playerRepository.save( updatedPlayer);
        return updatedPlayer;
    }

    @Transactional
    public void deleteById( long id) {
        playerRepository.deleteById( id);
    }
}
