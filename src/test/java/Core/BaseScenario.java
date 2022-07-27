package Core;

import com.CODEXIS.PopularDocuments.utils.PostgreSQLDriverBuilder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

@ExtendWith(SpringExtension.class)
public abstract class BaseScenario {
    private PostgreSQLContainer<?> postgres;
    private Connection connection;

    protected ResultSet performQuery(String sql) throws SQLException {
        Statement statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        statement.execute(sql);
        return statement.getResultSet();
    }

    @PostConstruct
    public void setUp() throws SQLException, IOException {
        this.postgres = new PostgreSQLContainer<>(PostgreSQLContainer.IMAGE);
        this.postgres.start();
        this.connection = this.postgres.createConnection("");
        // Okay I couldn't get field injection to work with the database connection, so I decided to do it this way
        PostgreSQLDriverBuilder.PostgreSQLDriver.replaceConnection(connection);
        try(Scanner scan = new Scanner(new FileReader(".//src//queries//dump.sql"))){
            scan.useDelimiter(";");
            while (scan.hasNext()) {
                performQuery(scan.next().trim());
            }
        };
    }
}
