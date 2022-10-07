package fr.ing.secu.leakybank.dao;

import fr.ing.secu.leakybank.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsersDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Maps USERS table result set to User beans
     */
    private RowMapper<User> userRowMapper = (rs, rowNum) -> new User(rs.getString("LOGIN"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"), rs.getBoolean("IS_ADMIN"));

    /**
     * Login a user
     *
     * @return Details on the user if login credentials are valid, else return null
     * an exception
     */
    public Optional<User> login(String login, String password) {
        final String sqlQuery = "select LOGIN, FIRST_NAME, LAST_NAME, IS_ADMIN from users where login= ? and password= ?";

        try {
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, new Object[]{login, password}, userRowMapper));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<User> findByUserName(String login) {
        final String sqlQuery = "select LOGIN, FIRST_NAME, LAST_NAME, IS_ADMIN from users where login= ?";

        try {
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, new Object[]{login}, userRowMapper));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    /**
     * Return the list of non-admin users
     */
    public List<User> findUsers() {
        final String sqlQuery = "select LOGIN, FIRST_NAME, LAST_NAME, IS_ADMIN from users where IS_ADMIN = false";

        return jdbcTemplate.query(sqlQuery, userRowMapper);
    }

    public void deleteUser(String login) {
        final String sqlQuery = "delete from USERS where login= ?";
        jdbcTemplate.update(sqlQuery, preparedStatement -> preparedStatement.setString(1, login));
    }
}
