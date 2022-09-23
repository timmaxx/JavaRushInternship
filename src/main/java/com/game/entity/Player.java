package com.game.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
// import javax.validation.constraints.*; // При допустимости использовать валидацию.

@Entity
@Table( name = "Player")
public class Player {
    final static int NAME_MAX_LENGTH = 12;
    final static int TITLE_MAX_LENGTH = 30;
    final static Integer MIN_EXPERIENCE = 0;
    final static Integer MAX_EXPERIENCE = 10_000_000;


    final static DateFormat format;
    final static Long MIN_BIRTHDAY;
    final static Long MAX_BIRTHDAY;
    final static String EXCEPTION_TEXT_FOR_WRONG_NAME;
    final static String EXCEPTION_TEXT_FOR_BIG_TITLE;
    final static String EXCEPTION_TEXT_FOR_WRONG_EXPERIENCE;
    final static String EXCEPTION_TEXT_FOR_NEGATIVE_BIRTHDAY = "Field 'birthday' should be positive or equal 0 (>= 01.01.1970)!";
    final static String EXCEPTION_TEXT_FOR_WRONG_BIRTHDAY;
    final static String EXCEPTION_TEXT_FOR_NULL_IN_KEY_FIELDS = "Fields: name, title, race, proffesion, birthday, experience should be no null!";

    static {
        format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        EXCEPTION_TEXT_FOR_WRONG_NAME = "Field 'name' should be not null, not empty and less or equal than " + NAME_MAX_LENGTH + " characters" + "!";
        EXCEPTION_TEXT_FOR_BIG_TITLE = "Field 'title' should be less or equal than " + TITLE_MAX_LENGTH + " characters" + "!";
        EXCEPTION_TEXT_FOR_WRONG_EXPERIENCE = "Field 'experience' should be more or equal " + MIN_EXPERIENCE + " and less or equal " + MAX_EXPERIENCE + "!";
        try {
            // Диапазон значений года 2000..3000 включительно
            //final static DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            MIN_BIRTHDAY = format.parse("01.01.2000").getTime( );
            MAX_BIRTHDAY = format.parse("01.01.3000").getTime( );
        } catch ( ParseException e) {
            throw new RuntimeException( e);
        }
        EXCEPTION_TEXT_FOR_WRONG_BIRTHDAY = "Field 'birthday' should be more or equal " + new Date( MIN_BIRTHDAY) + " and less or equal " + new Date( MAX_BIRTHDAY) + "!";
    }


    @Id
    @Column( name = "id", updatable = false, nullable = false)
    //@Column( name = "id")
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;                // ID игрока

    @Column( name = "name")
    //@Size( max = 12, message = "Name should be less or equal than 12 characters")
    private String name;            // Имя персонажа (до 12 знаков включительно)

    @Column( name = "title")
    //@Size( max = 30, message = "Title should be less or equal than 30 characters")
    private String title;           // Титул персонажа (до 30 знаков включительно)

    @Column( name = "race")
    @Enumerated( EnumType.STRING)
    private Race race;              // Расса персонажа

    @Column( name = "profession")
    // Если не сделать "@Enumerated( EnumType.STRING)", то будет ошибка:
    // org.springframework.web.util.NestedServletException: Request processing failed; nested exception is org.springframework.dao.DataIntegrityViolationException: Could not read entity state from ResultSet : EntityKey[com.game.entity.Player#14]; SQL [n/a]; nested exception is org.hibernate.exception.DataException: Could not read entity state from ResultSet : EntityKey[com.game.entity.Player#14]
    @Enumerated( EnumType.STRING)
    private Profession profession;  // Профессия персонажа

    @Column( name = "experience")
    private Integer experience;     // Опыт персонажа. Диапазон значений 0..10,000,000

    @Column( name = "level")
    private Integer level;          // Уровень персонажа

    @Column( name = "untilNextLevel")
    private Integer untilNextLevel; // Остаток опыта до следующего уровня

    // Сейчас birthday это Date. И тесты проходят.
    // Но:
    //   1. В Rest API для "Create player", “birthday”:[Long].
    //   2. В PlayerInfoTest birthday это Long.
    // Если сделать здесь Long, тогда нужно будет менять в коде:
    //   - отказ от @Temporal( TemporalType.DATE) и от @DateTimeFormat( pattern = "yyyy-MM-dd").
    //   - и при построении where (в PlayerSpecification) для after и before.
    //   - и при считывании из БД.
    //   - и при записи в БД (insert и update).
    @Column( name = "birthday")
    @Temporal( TemporalType.DATE)
    @DateTimeFormat( pattern = "yyyy-MM-dd")
    private Date birthday;    // Дата регистрации //Диапазон значений года 2000..3000 включительно
    // private Long birthday; //  Параметры даты между фронтом и сервером передаются в миллисекундах (тип Long) начиная с 01.01.1970.

    @Column( name = "banned")
    private Boolean banned;         // Забанен / не забанен


    public Player( ) {
        //setExperience(0);
    };

    public Player( Long id,
                   String name, String title,
                   Race race, Profession profession,
                   Integer experience,
                   // Integer level, Integer untilNextLevel,
                   Date birthday, // Long birthday,
                   Boolean banned) {
        this.id = id;
        setName( name);
        setTitle( title);
        this.race = race;
        this.profession = profession;
        setExperience( experience);
        setBirthday( birthday);
        setBanned( banned);
    }

    public Long getId( ) {
        return id;
    }

    public void setId( Long id) {
        this.id = id;
    }

    public String getName( ) { return name;}

    public void setName( String name) {
        checkNameValid( name);
        this.name = name;
    }

    public static void checkNameValid(String name) {
        if ( name == null || name.isEmpty( ) || name.length( ) > NAME_MAX_LENGTH) {
            throw new RuntimeException( EXCEPTION_TEXT_FOR_WRONG_NAME);
        }
    }

    public String getTitle( ) {
        return title;
    }

    public void setTitle( String title) {
        checkTitleValid( title);
        this.title = title;
    }

    public static void checkTitleValid( String title) {
        if ( title != null && title.length( ) > TITLE_MAX_LENGTH) {
            throw new RuntimeException( EXCEPTION_TEXT_FOR_BIG_TITLE);
        }
    }

    public Race getRace( ) {
        return race;
    }

    public void setRace( Race race) {
        this.race = race;
    }

    public Profession getProfession( ) {
        return profession;
    }

    public void setProfession( Profession profession) {
        this.profession = profession;
    }

    public Integer getExperience( ) {
        if ( experience == null) {
            setExperience( 0);
        }
        return experience;
    }

    public void setExperience( Integer experience) {
        if ( experience == null) {
            this.experience = 0;
        } else {
            checkExperienceValid( experience);
            this.experience = experience;
        }
        setLevel( );
        setUntilNextLevel( );
    }

    public static void checkExperienceValid(Integer experience) {
        if ( experience < MIN_EXPERIENCE || experience > MAX_EXPERIENCE) {
            throw new RuntimeException( EXCEPTION_TEXT_FOR_WRONG_EXPERIENCE);
        }
    }

    public Integer getLevel( ) {
        if ( level == null) {
            setLevel( );
        }
        return level;
    }

    // Этот сеттер сделан приватным. И вызываться он должен из сеттера для experience.
    private void setLevel() {
        // this.level = Math.toIntExact(( Math.round(( Math.ceil( Math.sqrt( 2500 + 200 * experience) - 50) / 100)))) - 1;
        // this.level = new Double( Math.floor( Math.sqrt( 2500 + 200 * experience) - 50) / 100).longValue();
        this.level = Math.toIntExact( Math.round( ( Math.sqrt( 2500 + 200 * getExperience( )) - 50) / 100 - 0.5));
    }

    public Integer getUntilNextLevel( ) {
        if ( untilNextLevel == null) {
            setUntilNextLevel( );
        }
        return untilNextLevel;
    }

    // Этот сеттер сделан приватным. И вызываться он должен из сеттера для level.
    private void setUntilNextLevel( ) {
        this.untilNextLevel = 50 * ( getLevel( ) + 1) * ( getLevel( ) + 2) - getExperience( );
    }

    public Date getBirthday( ) {
        if ( birthday == null) {
            birthday = new Date( 0);
        }
        return birthday;
    }
    //public Long getBirthday( ) { return birthday;}

    public void setBirthday( Date birthday) {
        checkBirthdayValid( birthday);
        if ( birthday == null) {
            this.birthday = new Date( 0);
            return;
        }
        this.birthday = birthday;
    }
    //public void setBirthday( Long birthday) {this.birthday = birthday;}

    public static void checkBirthdayValid(Date birthday) {
        if ( birthday == null) {
            return;
        }

        if ( birthday.getTime( ) < 0) {
            // System.out.println( EXCEPTION_TEXT_FOR_NEGATIVE_BIRTHDAY);
            throw new RuntimeException( EXCEPTION_TEXT_FOR_NEGATIVE_BIRTHDAY);
        }

        if ( birthday.getTime( ) < MIN_BIRTHDAY || birthday.getTime( ) > MAX_BIRTHDAY) {
            // System.out.println( EXCEPTION_TEXT_FOR_WRONG_BIRTHDAY);
            throw new RuntimeException( EXCEPTION_TEXT_FOR_WRONG_BIRTHDAY);
        }

    }

    public Boolean getBanned( ) {
        return banned;
    }

    public void setBanned( Boolean banned) {
        this.banned = banned;
    }

    public void checkNullFieldsForInserting( ) {
        if ( name == null ||
                title == null ||
                race == null ||
                profession == null ||
                birthday == null ||
                experience == null
        ) {
            throw new RuntimeException( EXCEPTION_TEXT_FOR_NULL_IN_KEY_FIELDS);
        }
    }
    public void checkAllFieldsValidAndCalcLevels( ) {
        checkNameValid( name);
        checkTitleValid( title);
        checkBirthdayValid( birthday);
        checkExperienceValid( experience);
        setLevel( );
        setUntilNextLevel( );
    }

    public boolean areAllFieldsNulls( ) {
        return  name == null &&
                title == null &&
                race == null &&
                profession == null &&
                ( birthday == null || birthday.getTime( ) == 0) &&
                experience == null
                ;
    }

    public void fillVoidFieldsFromAnotherPlayer( Player player) {
        if ( name == null) { name = player.name;}
        if ( title == null) { title = player.title;}
        if ( race == null) { race = player.race;}
        if ( profession == null) { profession = player.profession;}
        if ( birthday == null || birthday.getTime( ) == 0) { setBirthday( player.birthday);}
        if ( experience == null) { setExperience ( player.experience);}
        if ( banned == null) { banned = player.banned;}
        checkAllFieldsValidAndCalcLevels( );
    }


    @Override
    public String toString( ) {

        return  super.toString() + "\n" +
                "Player{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", race=" + getRace() +
                ", profession=" + getProfession() +

                ", experience=" + getExperience() +
                ", level=" + getLevel() +
                ", untilNextLevel=" + getUntilNextLevel() +

                ", birthday=" + getBirthday( ) +

                ", banned=" + getBanned() +

                '}';
    }

}
