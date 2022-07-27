package Core;

import com.CODEXIS.PopularDocuments.controller.DocumentController;
import com.CODEXIS.PopularDocuments.repository.DocumentRepository;
import com.CODEXIS.PopularDocuments.repository.UserRepository;
import com.CODEXIS.PopularDocuments.repository.VisitRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.ResultSet;
import java.sql.SQLException;

@WebMvcTest(value = DocumentController.class)
@Import(DocumentController.class)
@ComponentScan({"com.CODEXIS.PopularDocuments.repository"})
public abstract class BaseRestScenario extends BaseScenario {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected DocumentRepository documentRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected VisitRepository visitRepository;

    protected ResultSet performQuery(String sql) throws SQLException {
        return super.performQuery(sql);
    }
}
