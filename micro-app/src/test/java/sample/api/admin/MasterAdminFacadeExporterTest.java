package sample.api.admin;

import static org.mockito.BDDMockito.*;
import java.util.*;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import sample.WebTestSupport;
import sample.usecase.MasterAdminService;

/**
 * MasterAdminFacadeExporter の単体検証です。
 * <p>low: 簡易な正常系検証が中心
 */
@WebMvcTest(MasterAdminFacadeExporter.class)
public class MasterAdminFacadeExporterTest extends WebTestSupport {

    @MockBean
    private MasterAdminService service;
    
    @Override
    protected String prefix() {
        return MasterAdminFacadeExporter.Path;
    }

    @Test
    public void 社員を取得します() throws Exception {
        given(service.getStaff(any()))
            .willReturn(Optional.of(fixtures.staff("example")));
        performGet("/staff/example/",
            JsonExpects.success()
                .match("$.id", "example")
                .match("$.name", "example"));
        
        given(service.getStaff(any()))
            .willReturn(Optional.empty());
        performGet("/staff/empty/",
            JsonExpects.success().emptyContents());
    }

}
