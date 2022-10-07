package fr.ing.secu.leakybank.dao;

import fr.ing.secu.leakybank.model.AccountType;
import fr.ing.secu.leakybank.model.InternalAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DAO that manages internal accounts
 *
 * @author chouippea
 */
@Repository
public class AccountsDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Maps a result set row to a InternalAccount object
     */
    private RowMapper<InternalAccount> internalAccountMapper = (rs, rowNum) -> new InternalAccount(rs.getInt("ACCOUNT_NUMBER"), new AccountType(rs.getInt("ACCOUNT_TYPE"), rs.getString("LABEL")), rs.getBigDecimal("AVAILABLE_BALANCE"));

    /**
     * Return the list of internal accounts owned by the user
     *
     * @param userLogin login of the user
     * @return the list of internal accounts owned by the user
     */
    public List<InternalAccount> findInternalAccountsByUser(String userLogin) {
        final String sqlQuery = "select * from INTERNAL_ACCOUNTS account, INTERNAL_ACCOUNT_TYPES type where account.owner= ? and account.account_type = type.id";
        return jdbcTemplate.query(sqlQuery, preparedStatement -> preparedStatement.setString(1, userLogin), internalAccountMapper);

    }

    /**
     * Return an internal account from its account number
     *
     * @param accountNumber account number of the requested account
     * @return an internal account from its account number
     */
    public Optional<InternalAccount> findInternalAccountByAccountNumber(final int accountNumber) {
        final String sqlQuery = "select * from INTERNAL_ACCOUNTS account, INTERNAL_ACCOUNT_TYPES type where account.account_type=type.id and ACCOUNT_NUMBER= ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, new Object[]{accountNumber}, internalAccountMapper));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

}
