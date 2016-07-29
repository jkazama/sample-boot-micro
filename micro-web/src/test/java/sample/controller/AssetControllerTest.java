package sample.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.*;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import sample.WebTestSupport;
import sample.api.AssetFacadeInvoker;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.RegCashOut;

/**
 * AssetController の単体検証です。
 * <p>low: 簡易な正常系検証が中心
 */
@WebMvcTest(AssetController.class)
public class AssetControllerTest extends WebTestSupport {

    @MockBean
    private AssetFacadeInvoker invoker;
    
    @Override
    protected String prefix() {
        return AssetFacadeInvoker.Path;
    }

    @Test
    public void 未処理の振込依頼情報を検索します() {
        given(invoker.findUnprocessedCashOut()).willReturn(resultCashOuts());
        performGet(AssetFacadeInvoker.PathFindUnprocessedCashOut,
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
        given(invoker.withdraw(any(RegCashOut.class)))
            .willReturn(ResponseEntity.status(HttpStatus.OK).body(1L));
        performPost(
          uriBuilder(AssetFacadeInvoker.PathWithdraw)
              .queryParam("accountId", "sample")
              .queryParam("currency", "JPY")
              .queryParam("absAmount", "1000")
              .build(),
          JsonExpects.success()
        );
    }
    
}
