package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.util.List;

@RestController
@RequestMapping( "/rest")
public class PlayerController {
    private Connection conn;
    private final PlayerService playerService;

    @Autowired
    public PlayerController( PlayerService playerService) {
        this.playerService = playerService;
    }

    // 1. Get players list
    @GetMapping( "/players")
    public ResponseEntity< List< Player>> getPlayersWithCriterions(
            @RequestParam( value = "name", required = false) String name,
            @RequestParam( value = "title", required = false) String title,
            @RequestParam( value = "race", required = false) String race,
            //@RequestParam( value = "race", required = false) Race race,
            @RequestParam( value = "profession", required = false) String profession,
            //@RequestParam( value = "profession", required = false) Profession profession,
            @RequestParam( value = "after", required = false) Long after,
            @RequestParam( value = "before", required = false) Long before,
            @RequestParam( value = "banned", required = false) Boolean banned,
            @RequestParam( value = "minExperience", required = false) Integer minExperience,
            @RequestParam( value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam( value = "minLevel", required = false) Integer minLevel,
            @RequestParam( value = "maxLevel", required = false) Integer maxLevel,

            // Обрати внимание. 4. Если параметр order не указан – нужно использовать значение PlayerOrder.ID.
            @RequestParam( value = "order", required = false/*, defaultValue = "PlayerOrder.ID"*/) PlayerOrder order,
            // Обрати внимание. 5. Если параметр pageNumber не указан – нужно использовать значение 0.
            @RequestParam( value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            // Обрати внимание. 6. Если параметр pageSize не указан – нужно использовать значение 3.
            @RequestParam( value = "pageSize", required = false, defaultValue = "3") Integer pageSize
    ) {

        PageRequest pageRequest = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by( ( order == null) ?
                        PlayerOrder.ID.getFieldName( ) :
                        order.getFieldName( ) )
        );
        List< Player> playerList = playerService.findAllWithCriterions(
                name, title,
                race, profession,
                banned,
                after, before,
                minExperience, maxExperience,
                minLevel, maxLevel,
                pageRequest
        );

        HttpStatus httpStatus = HttpStatus.OK;
        ResponseEntity< List< Player>> responseEntity = new ResponseEntity( playerList, httpStatus);

        return responseEntity;
    }

    // 2. Get players count
    @RequestMapping( "/players/count")
    public ResponseEntity< Long> getCountWithCriterions(
            @RequestParam( value = "name", required = false) String name,
            @RequestParam( value = "title", required = false) String title,
            // @RequestParam( value = "race", required = false) Race race,
            @RequestParam( value = "race", required = false) String race,
            // @RequestParam( value = "profession", required = false) Profession profession,
            @RequestParam( value = "profession", required = false) String profession,
            @RequestParam( value = "after", required = false) Long after,
            @RequestParam( value = "before", required = false) Long before,
            @RequestParam( value = "banned", required = false) Boolean banned,
            @RequestParam( value = "minExperience", required = false) Integer minExperience,
            @RequestParam( value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam( value = "minLevel", required = false) Integer minLevel,
            @RequestParam( value = "maxLevel", required = false) Integer maxLevel
    ) {
        Long count = playerService.countWithCriterions(
                name, title,
                race, profession,
                banned,
                after, before,
                minExperience, maxExperience,
                minLevel, maxLevel
        );
        HttpStatus httpStatus = HttpStatus.OK;
        ResponseEntity< Long> responseEntity = new ResponseEntity( count, httpStatus);

        return responseEntity;
    }

    // 3. Create player
    @PostMapping( "/players")
    public ResponseEntity< Player> create (
            @RequestBody Player player
    ) {
        ResponseEntity< Player> responseEntity;
        HttpStatus httpStatus;
        // System.out.println( "public ResponseEntity< Player> create");
        // System.out.println( "player = " + player);
        try {
            playerService.insert( player);
            httpStatus = HttpStatus.OK;
        } catch ( RuntimeException re) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        responseEntity = new ResponseEntity< Player>( player, httpStatus);
        return responseEntity;
    }

    // 4. Get player
    @GetMapping( "/players/{id}")
    public ResponseEntity< Player> getPlayerById(
            @PathVariable( "id") Long id
            // @RequestParam( value = "id") Long id
    ) {
        //   Ели поступит запрос, где вместо id целого и положительного поступит что-то иное,
        // то входа в метод не произойдёт, но возникнет исключение (которое можно-было обработать и самостоятельно,
        // но для данного задания это не нужно - Spring сам сделает так, как надо) и выставится HttpStatus.BAD_REQUEST.
        //   Смотри:
        // Обрати внимание.
        // 7. Не валидным считается id, если он:
        // 7.1. не числовой
        // 7.2. не целое число

        ResponseEntity< Player> responseEntity;
        Player player = null;
        HttpStatus httpStatus;

        if ( id <= 0) {
            // Обрати внимание.
            // 7. Не валидным считается id, если он:
            // 7.3. не положительный
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            player = playerService.findById( id);

            if ( player != null) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.NOT_FOUND;
            }
        }
        responseEntity = new ResponseEntity< Player>( player, httpStatus);
        return responseEntity;
    }

    // 5. Update player
    @PostMapping( "/players/{id}")
    public ResponseEntity< Player> update(
            @PathVariable( "id") Long id,
            //@RequestParam( value = "id") Long id,
            @RequestBody Player player
    ) {
        ResponseEntity< Player> responseEntity;
        HttpStatus httpStatus;

        if ( id <= 0) {
            // Обрати внимание. 7. Не валидным считается id, если он:
            // 7.3. не положительный
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            try {
                player = playerService.update( id, player);
                httpStatus = HttpStatus.OK;
            } catch ( RuntimeException re) {
                if ( re.getMessage().contains( "Player with id is equal")) {
                    httpStatus = HttpStatus.NOT_FOUND;
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        }
        responseEntity = new ResponseEntity< Player>( player, httpStatus);
        return responseEntity;
    }

    // 6. Delete player
    @DeleteMapping( "/players/{id}")
    public ResponseEntity deleteOne(
            @PathVariable( "id") Long id
            // @RequestParam( value = "id") Integer id
    ) {
        ResponseEntity< Player> responseEntity;
        HttpStatus httpStatus;

        if ( id <= 0) {
            // Обрати внимание.
            // 7. Не валидным считается id, если он:
            // 7.3. не положительный
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            try {
                playerService.deleteById( id);
                httpStatus = HttpStatus.OK;
            } catch ( RuntimeException re) {
                httpStatus = HttpStatus.NOT_FOUND;
            }
        }
        responseEntity = new ResponseEntity( null, httpStatus);
        return responseEntity;
    }
}
