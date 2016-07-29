package sample.microasset.api;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.*;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import sample.WebTestSupport;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.RegCashOut;
import sample.microasset.usecase.AssetService;

/**
 * AssetFacadeFacadeExporter の単体検証です。
 * <p>low: 簡易な正常系検証が中心
 */
@WebMvcTest(AssetFacadeExporter.class)
public class AssetFacadeExporterTest extends WebTestSupport {

    @MockBean
    private AssetService service;
    
    @Override
    protected String prefix() {
        return AssetFacadeExporter.Path;
    }

    @Test
    public void 未処理の振込依頼情報を検索します() {
        given(service.findUnprocessedCashOut()).willReturn(resultCashOuts());
        performGet(AssetFacadeExporter.PathFindUnprocessedCashOut,
            JsonExpects.success()
                .match("$[0].currency", "JPY")
                .match("$[0].absAmount", 3000)
                .match("$[1].absAmount", 4000));
    }

    private List<CashInOut> resultCashOuts() {
        return Arrays.asList(
                fixturesAsset.cio("sample", "3000", true),
                fixturesAsset.cio("sample", "4000", true));
    }

    @Test
    public void 振込出金依頼をします() {
        given(service.withdraw(any(RegCashOut.class))).willReturn(1L);
        performJsonPost(
          uriBuilder(AssetFacadeExporter.PathWithdraw).build(),
          "{\"accountId\": \"sample\", \"currency\": \"JPY\", \"absAmount\": \"1000\"}",
          JsonExpects.success()
        );
    }
    
}
