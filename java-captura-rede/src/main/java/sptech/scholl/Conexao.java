package sptech.scholl;

import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;

public class Conexao {
    private DataSource dataSource;
    public Conexao() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/spot_bd");// o host e o banco!
        basicDataSource.setUsername("user_SPOT");
        basicDataSource.setPassword("senha");

        dataSource = basicDataSource;

    }

    public DataSource getDataSource() {
        return dataSource;
    }
}